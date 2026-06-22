package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.LopHocPhan;
import com.nhom10.ooad.quanlysinhvien.Model.LopHPView; // Sử dụng lớp DTO tổng hợp mới
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TẦNG DAO DÙNG CHUNG CHO THỰC THỂ LỚP HỌC PHẦN (LOP HOC PHAN)
 * Phục vụ đồng thời:
 * - Giáo vụ: Mở lớp học phần mới (UC04.1) và Phân công giảng viên dạy (UC04.3)
 * - Sinh viên: Đăng ký, hủy môn và cập nhật biến động sĩ số hiện tại
 * - Giảng viên: Truy vấn danh sách lớp phụ trách để thực hiện nhập điểm
 */
public class LopHocPhanDAO {

    // ====================================================================
    // VÙNG CODE TÍCH HỢP MỚI PHỤC VỤ PHÂN HỆ GIÁO VỤ & GIẢNG VIÊN
    // ====================================================================

    /**
     * 🌟 THÊM MỚI: Lấy danh sách các mã lớp học phần mà một giảng viên cụ thể được phân công dạy
     * Dùng để đổ dữ liệu chính xác vào JComboBox bên giao diện nhập điểm của Giảng viên
     */
    public List<String> getMaLopHPByGiangVien(String maGV) {
        List<String> list = new ArrayList<>();
        if (maGV == null || maGV.trim().isEmpty()) {
            return list;
        }
        
        // Truy vấn lọc chuẩn xác theo Mã giảng viên đang đăng nhập hệ thống
        String sql = "SELECT MaLopHP FROM LopHocPhan WHERE MaGV = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maGV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("MaLopHP"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách mã lớp học phần theo giảng viên: " + maGV);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Kiểm tra xem Giảng viên đã bị trùng lịch dạy vào khung giờ này trong cùng học kỳ chưa
     * @return true nếu BỊ TRÙNG, false nếu lịch TRỐNG (Hợp lệ)
     */
    public boolean kiemTraTrungLichGiangVien(String maGV, String maHocKy, String lichHoc) {
        if (maGV == null || maGV.trim().isEmpty() || lichHoc == null || lichHoc.trim().isEmpty()) {
            return false; // Nếu chưa gán giảng viên hoặc lịch học trống thì bỏ qua check trùng
        }

        String sql = "SELECT COUNT(*) FROM LopHocPhan WHERE MaGV = ? AND MaHocKy = ? AND LichHoc = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maGV);
            ps.setString(2, maHocKy);
            ps.setString(3, lichHoc);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu COUNT > 0 (bị trùng lịch)
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi truy vấn kiểm tra trùng lịch giảng viên!");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * UC04.1: Thực hiện Mở một lớp học phần mới trong kỳ
     * Gỡ bỏ cột LichHoc khỏi SQL INSERT để không gây lỗi cấu trúc bảng DB
     */
    public boolean insertLopHocPhan(LopHocPhan lhp) {
        String sql = "INSERT INTO LopHocPhan (MaLopHP, MaHP, MaHocKy, MaGV, SiSoHienTai, SiSoToiDa, TrongSoQT, TrongSoCK) "
                   + "VALUES (?, ?, ?, ?, 0, ?, ?, ?)";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lhp.getMaLopHP());
            ps.setString(2, lhp.getMaHP());
            ps.setString(3, lhp.getMaHocKy());
            ps.setString(4, lhp.getMaGV().trim().isEmpty() ? null : lhp.getMaGV()); // Cho phép null nếu chưa phân giảng viên
            ps.setInt(5, lhp.getSiSoToiDa());
            ps.setDouble(6, lhp.getTrongSoQT());
            ps.setDouble(7, lhp.getTrongSoCK());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực thi nghiệp vụ mở lớp học phần mới dưới Database!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thực hiện Hủy / Xóa lớp học phần khỏi hệ thống cơ sở dữ liệu vĩnh viễn
     */
    public boolean deleteLopHocPhan(String maLopHP) {
        String sql = "DELETE FROM LopHocPhan WHERE MaLopHP = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLopHP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi hệ thống: Từ chối xóa lớp học phần " + maLopHP);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy toàn bộ danh sách lớp học phần hiện hành (Không phân biệt học kỳ)
     * Phục vụ dốc dữ liệu lên bảng chính phía dưới khi Giáo vụ ấn nút "Cập nhật bảng"
     */
    public List<LopHPView> getAllLopHPView() {
        List<LopHPView> list = new ArrayList<>();
        String sql = "SELECT lhp.*, hp.TenHP, hp.SoTC FROM LopHocPhan lhp " +
                     "JOIN HocPhan hp ON lhp.MaHP = hp.MaHP";
        
        try (Connection conn = DataBaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                LopHPView view = new LopHPView();
                view.setMaLopHP(rs.getString("MaLopHP"));
                view.setMaHP(rs.getString("MaHP"));
                view.setTenHP(rs.getString("TenHP"));
                view.setSoTC(rs.getInt("SoTC"));
                view.setMaHocKy(rs.getString("MaHocKy"));
                view.setMaGV(rs.getString("MaGV"));
                view.setSiSoHienTai(rs.getInt("SiSoHienTai"));
                view.setSiSoToiDa(rs.getInt("SiSoToiDa"));
                list.add(view);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi dốc toàn bộ danh sách lớp học phần hiện hành!");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * UC04.3: Thực hiện Phân công giảng viên phụ trách đứng lớp học phần
     */
    public boolean phanCongGiangVien(String maLopHP, String maGV) {
        String sql = "UPDATE LopHocPhan SET MaGV = ? WHERE MaLopHP = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maGV);
            ps.setString(2, maLopHP);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực hiện phân công giảng viên cho mã lớp: " + maLopHP);
            e.printStackTrace();
            return false;
        }
    }

    // ====================================================================
    // VÙNG CODE SẴN CÓ PHỤC VỤ PHÂN HỆ SINH VIÊN
    // ====================================================================

    public List<LopHocPhan> getAllOpen(String maHocKy) {
        List<LopHocPhan> list = new ArrayList<>();
        String sql = "SELECT * FROM LopHocPhan WHERE MaHocKy = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LopHocPhan lhp = new LopHocPhan(
                        rs.getString("MaLopHP"),
                        rs.getString("MaHP"),
                        rs.getString("MaHocKy"),
                        rs.getString("MaGV"),
                        rs.getInt("SiSoHienTai"),
                        rs.getInt("SiSoToiDa"),
                        rs.getDouble("TrongSoQT"),
                        rs.getDouble("TrongSoCK")
                    );
                    list.add(lhp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public LopHocPhan getByID(String maLopHP) {
        String sql = "SELECT * FROM LopHocPhan WHERE MaLopHP = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLopHP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LopHocPhan(
                        rs.getString("MaLopHP"),
                        rs.getString("MaHP"),
                        rs.getString("MaHocKy"),
                        rs.getString("MaGV"),
                        rs.getInt("SiSoHienTai"),
                        rs.getInt("SiSoToiDa"),
                        rs.getDouble("TrongSoQT"),
                        rs.getDouble("TrongSoCK")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSiSo(String maLopHP, int soLuong) {
        String sql = "UPDATE LopHocPhan SET SiSoHienTai = SiSoHienTai + ? WHERE MaLopHP = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maLopHP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LopHPView> getDanhSachLopHPView(String maHocKy, String keyword, String khoaVien) {
        List<LopHPView> list = new ArrayList<>();
        
        String sql = "SELECT lhp.*, hp.TenHP, hp.SoTC FROM LopHocPhan lhp " +
                     "JOIN HocPhan hp ON lhp.MaHP = hp.MaHP " +
                     "WHERE lhp.MaHocKy = ? ";
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "AND (lhp.MaHP LIKE ? OR hp.TenHP LIKE ? OR lhp.MaLopHP LIKE ?) ";
        }
        
        if (khoaVien != null && khoaVien.equals("Điện - Điện tử")) {
            sql += "AND lhp.MaHP LIKE 'EE%' ";
        } else if (khoaVien != null && khoaVien.equals("Công nghệ thông tin")) {
            sql += "AND (lhp.MaHP LIKE 'IT%' OR lhp.MaHP LIKE 'CS%') ";
        }

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            ps.setString(paramIndex++, maHocKy);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, likeKeyword);
                ps.setString(paramIndex++, likeKeyword);
                ps.setString(paramIndex++, likeKeyword);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LopHPView view = new LopHPView();
                    view.setMaLopHP(rs.getString("MaLopHP"));
                    view.setMaHP(rs.getString("MaHP"));
                    view.setTenHP(rs.getString("TenHP"));
                    view.setSoTC(rs.getInt("SoTC"));
                    view.setMaHocKy(rs.getString("MaHocKy"));
                    view.setMaGV(rs.getString("MaGV"));
                    view.setSiSoHienTai(rs.getInt("SiSoHienTai"));
                    view.setSiSoToiDa(rs.getInt("SiSoToiDa"));
                    list.add(view);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lọc danh sách LopHPView trong kỳ: " + maHocKy);
            e.printStackTrace();
        }
        return list;
    }

    public LopHPView getLopHPViewByID(String maLopHP) {
        String sql = "SELECT lhp.*, hp.TenHP, hp.SoTC FROM LopHocPhan lhp " +
                     "JOIN HocPhan hp ON lhp.MaHP = hp.MaHP WHERE lhp.MaLopHP = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLopHP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LopHPView view = new LopHPView();
                    view.setMaLopHP(rs.getString("MaLopHP"));
                    view.setMaHP(rs.getString("MaHP"));
                    view.setTenHP(rs.getString("TenHP"));
                    view.setSoTC(rs.getInt("SoTC"));
                    view.setMaHocKy(rs.getString("MaHocKy"));
                    view.setMaGV(rs.getString("MaGV"));
                    view.setSiSoHienTai(rs.getInt("SiSoHienTai"));
                    view.setSiSoToiDa(rs.getInt("SiSoToiDa"));
                    return view;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi quét chi tiết LopHPView cho mã lớp: " + maLopHP);
            e.printStackTrace();
        }
        return null;
    }
}