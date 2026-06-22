package com.nhom10.ooad.quanlysinhvien.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonHocPhi;
import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;

/**
 * HoaDonHocPhiDAO - tầng truy xuất dữ liệu (DAO) cho bảng HoaDonHocPhi.
 * Chứa các truy vấn SQL liên quan đến hóa đơn học phí.
 */
public class HoaDonHocPhiDAO {

    /**
     * Đếm tổng số tín chỉ 1 sinh viên đã đăng ký thành công trong 1 học kỳ.
     * JOIN: DangKyHoc -> LopHocPhan -> HocPhan, rồi SUM(SoTC).
     */
    public int getTongTinChi(String mssv, String maHocKy) {
        String sql =
            "SELECT ISNULL(SUM(hp.SoTC),0) AS TongTC "
          + "FROM DangKyHoc dk "
          + "JOIN LopHocPhan lhp ON dk.MaLopHP = lhp.MaLopHP "
          + "JOIN HocPhan hp     ON lhp.MaHP = hp.MaHP "
          + "WHERE dk.MSSV = ? AND lhp.MaHocKy = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("TongTC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Kiểm tra 1 sinh viên đã có hóa đơn của học kỳ này chưa (tránh tạo trùng). */
    public boolean daCoHoaDon(String mssv, String maHocKy) {
        String sql = "SELECT COUNT(*) FROM HoaDonHocPhi WHERE MSSV = ? AND MaHocKy = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 🌟 ĐÃ SỬA DỨT ĐIỂM: TÍNH HỌC PHÍ + TẠO HÓA ĐƠN dùng cơ chế quản lý Transaction tập trung.
     * Giải phóng hoàn toàn lỗi nghẽn port (Lock table) giúp hệ thống ghi nhận ngay lập tức xuống SQL Server.
     */
    public int tinhVaTaoHoaDon(String maHocKy, long donGia) {
        String sqlChon =
            "SELECT dk.MSSV, SUM(hp.SoTC) AS TongTC "
          + "FROM DangKyHoc dk "
          + "JOIN LopHocPhan lhp ON dk.MaLopHP = lhp.MaLopHP "
          + "JOIN HocPhan hp     ON lhp.MaHP = hp.MaHP "
          + "WHERE lhp.MaHocKy = ? "
          + "GROUP BY dk.MSSV";

        List<String[]> dsSV = new ArrayList<>(); 
        
        // Mở một kết nối lõi duy nhất cho toàn bộ tiến trình
        try (Connection con = DataBaseConnection.getConnection()) {
            
            // Bước 1: Quét và bóc tách danh sách sinh viên hợp lệ từ bộ đệm SQL
            try (PreparedStatement ps = con.prepareStatement(sqlChon)) {
                ps.setString(1, maHocKy);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dsSV.add(new String[]{ rs.getString("MSSV"), String.valueOf(rs.getInt("TongTC")) });
                    }
                }
            }

            if (dsSV.isEmpty()) return 0;

            // Thiết lập các câu lệnh xử lý song song trên cùng một kênh truyền (Connection)
            String sqlCheck = "SELECT COUNT(*) FROM HoaDonHocPhi WHERE MSSV = ? AND MaHocKy = ?";
            String sqlChen = "INSERT INTO HoaDonHocPhi (MaHoaDon, MSSV, MaHocKy, TongTinChi, DonGiaTinChi, TongTienNop, TrangThaiThanhToan) "
                           + "VALUES (?, ?, ?, ?, ?, ?, N'Chưa nộp')"; 
            
            int demMoi = 0;
            
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck);
                 PreparedStatement psChen = con.prepareStatement(sqlChen)) {
                
                // Kích hoạt chế độ Transaction để bảo toàn dữ liệu kế toán an toàn
                con.setAutoCommit(false);
                
                for (String[] sv : dsSV) {
                    String mssv = sv[0];
                    int soTC = Integer.parseInt(sv[1]);
                    
                    // Kiểm tra trùng ngay tại pipeline hiện tại
                    psCheck.setString(1, mssv);
                    psCheck.setString(2, maHocKy);
                    try (ResultSet rsCheck = psCheck.executeQuery()) {
                        if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                            continue; // Bỏ qua nếu sinh viên đã được khởi tạo hóa đơn trước đó
                        }
                    }

                    String maHoaDon = "HD-" + maHocKy + "-" + mssv; 
                    long tongTien = (long) soTC * donGia;

                    psChen.setString(1, maHoaDon);
                    psChen.setString(2, mssv);
                    psChen.setString(3, maHocKy);
                    psChen.setInt(4, soTC);
                    psChen.setLong(5, donGia);
                    psChen.setLong(6, tongTien);
                    
                    psChen.executeUpdate();
                    demMoi++;
                }
                
                // Thực thi đồng bộ đẩy toàn bộ hóa đơn xuống ổ đĩa cứng SQL Server
                con.commit();
            } catch (Exception ex) {
                con.rollback(); // Hoàn nguyên dữ liệu nếu xảy ra lỗi xung đột khóa ngoại
                ex.printStackTrace();
                throw ex;
            }
            
            return demMoi;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Tra cứu/lọc hóa đơn để hiển thị lên lưới JTable (UC07.2).
     */
    public List<HoaDonView> timHoaDon(String keyword, String maHocKy, String trangThai) {
        return getDanhSachHoaDon(keyword, maHocKy, trangThai);
    }

    /**
     * 🌟 ĐÃ CẬP NHẬT CHUẨN: Hàm xử lý truy vấn động cho JTable tra cứu.
     * Sửa đổi logic so sánh điều kiện TrangThaiThanhToan để khi ComboBox chọn "Tất cả" sẽ bỏ qua lọc trạng thái.
     */
    public List<HoaDonView> getDanhSachHoaDon(String keyword, String maHocKy, String trangThai) {
        List<HoaDonView> ds = new ArrayList<>();
        
        // Chuẩn hóa chuỗi rỗng nếu trạng thái là "Tất cả" hoặc null
        String checkTrangThai = (trangThai == null || "Tất cả".equalsIgnoreCase(trangThai)) ? "" : trangThai.trim();

        String sql =
            "SELECT hd.MaHoaDon, hd.MSSV, sv.HoTen, lh.TenLop, hd.MaHocKy, hd.TongTinChi, "
          + "       hd.DonGiaTinChi, hd.TongTienNop, hd.TrangThaiThanhToan, "
          + "       ISNULL((SELECT SUM(ls.SoTienNop) FROM LichSuThanhToan ls "
          + "               WHERE ls.MaHoaDon = hd.MaHoaDon),0) AS DaNop "
          + "FROM HoaDonHocPhi hd "
          + "JOIN SinhVien sv ON hd.MSSV = sv.MSSV "
          + "LEFT JOIN LopHoc   lh ON sv.MaLopQuanLy = lh.MaLopQuanLy "
          + "WHERE (sv.MSSV LIKE ? OR sv.HoTen LIKE ?) "
          + "  AND (? = '' OR hd.MaHocKy = ?) "
          + "  AND (? = '' OR hd.TrangThaiThanhToan = ?) "
          + "ORDER BY hd.MaHoaDon";
          
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, (maHocKy == null) ? "" : maHocKy.trim());
            ps.setString(4, (maHocKy == null) ? "" : maHocKy.trim());
            ps.setString(5, checkTrangThai);
            ps.setString(6, checkTrangThai);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(docHoaDonView(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy 1 hóa đơn theo mã (dùng khi tính toán số tiền đóng). */
    public HoaDonHocPhi getHoaDonById(String maHoaDon) {
        String sql = "SELECT * FROM HoaDonHocPhi WHERE MaHoaDon = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new HoaDonHocPhi(
                        rs.getString("MaHoaDon"), rs.getString("MSSV"), rs.getString("MaHocKy"),
                        rs.getInt("TongTinChi"), rs.getLong("DonGiaTinChi"), rs.getLong("TongTienNop"),
                        rs.getString("TrangThaiThanhToan"), String.valueOf(rs.getDate("NgayLapHoaDon")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tổng số tiền đã nộp tích lũy của 1 hóa đơn từ trước tới nay. */
    public long tongDaNop(String maHoaDon) {
        String sql = "SELECT ISNULL(SUM(SoTienNop),0) FROM LichSuThanhToan WHERE MaHoaDon = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Cập nhật trạng thái thanh toán mới của hóa đơn. */
    public void capNhatTrangThai(String maHoaDon, String trangThai) {
        String sql = "UPDATE HoaDonHocPhi SET TrangThaiThanhToan = ? WHERE MaHoaDon = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maHoaDon);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * LOGIC CHẶN NỢ: Sinh viên có đang còn nợ học phí không?
     * Trả về true nếu còn ít nhất 1 hóa đơn CHƯA ở trạng thái 'Đã nộp'.
     */
    public boolean coNoHocPhi(String mssv) {
        String sql = "SELECT COUNT(*) FROM HoaDonHocPhi "
                   + "WHERE MSSV = ? AND TrangThaiThanhToan <> N'Đã nộp'";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mssv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Đọc 1 dòng ResultSet thành HoaDonView. */
    static HoaDonView docHoaDonView(ResultSet rs) throws Exception {
        HoaDonView v = new HoaDonView();
        v.setMaHoaDon(rs.getString("MaHoaDon"));
        v.setMssv(rs.getString("MSSV"));
        v.setHoTen(rs.getString("HoTen"));
        v.setTenLop(rs.getString("TenLop"));
        v.setMaHocKy(rs.getString("MaHocKy"));
        v.setTongTinChi(rs.getInt("TongTinChi"));
        v.setDonGia(rs.getLong("DonGiaTinChi"));
        v.setTongTienNop(rs.getLong("TongTienNop"));
        long daNop = rs.getLong("DaNop");
        v.setDaNop(daNop);
        v.setConNo(rs.getLong("TongTienNop") - daNop);
        v.setTrangThai(rs.getString("TrangThaiThanhToan"));
        return v;
    }
}