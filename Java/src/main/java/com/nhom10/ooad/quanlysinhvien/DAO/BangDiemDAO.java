package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.BangDiem; // Sử dụng thực thể gốc cho giảng viên
import com.nhom10.ooad.quanlysinhvien.Model.DiemSoView; // Sử dụng lớp DTO tổng hợp hiển thị
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangDiemDAO {

    /**
     * UC06.1: Kéo toàn bộ bảng điểm cá nhân sử dụng DTO DiemSoView.
     * Sử dụng câu lệnh JOIN 3 bảng để tối ưu hóa hiệu năng, lấy trọn thông tin trong 1 câu truy vấn.
     */
    public List<DiemSoView> getDiemSinhVienNangCao(String mssv, String maHocKy) {
        List<DiemSoView> list = new ArrayList<>();
        String sql = "SELECT hp.MaHP, lhp.MaLopHP, hp.TenHP, hp.SoTC, lhp.TrongSoQT, lhp.TrongSoCK, bd.DiemQT, bd.DiemCK, bd.DiemTongKet, bd.TrangThai " +
                     "FROM BangDiem bd " +
                     "JOIN LopHocPhan lhp ON bd.MaLopHP = lhp.MaLopHP " +
                     "JOIN HocPhan hp ON lhp.MaHP = hp.MaHP " +
                     "WHERE bd.MSSV = ? AND lhp.MaHocKy = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mssv);
            ps.setString(2, maHocKy);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiemSoView dsv = new DiemSoView();
                    dsv.setMaHocPhan(rs.getString("MaHP"));
                    dsv.setMaLopHP(rs.getString("MaLopHP"));
                    dsv.setTenHocPhan(rs.getString("TenHP"));
                    dsv.setSoTinChi(rs.getInt("SoTC"));
                    dsv.setTrongSo(rs.getBigDecimal("TrongSoQT") + " - " + rs.getBigDecimal("TrongSoCK"));
                    
                    // --- ĐÃ SỬA: Thay thế việc ép kiểu (Double) trực tiếp bằng phương thức an toàn ---
                    dsv.setDiemQT(rs.getObject("DiemQT") != null ? rs.getDouble("DiemQT") : null);
                    dsv.setDiemCK(rs.getObject("DiemCK") != null ? rs.getDouble("DiemCK") : null);
                    dsv.setDiemTongKet(rs.getObject("DiemTongKet") != null ? rs.getDouble("DiemTongKet") : null);
                    // -------------------------------------------------------------------------------
                    
                    dsv.setTrangThai(rs.getString("TrangThai"));
                    
                    list.add(dsv);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tải dữ liệu bảng điểm DTO nâng cao cho MSSV: " + mssv);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Kiểm tra xem sinh viên đã học và đạt điểm qua môn tiên quyết chưa (Quy chế: >= 4.0)
     */
    public boolean checkMonTienQuyet(String mssv, String maHPTienQuyet) {
        String sql = "SELECT bd.DiemTongKet FROM BangDiem bd " +
                     "JOIN LopHocPhan lhp ON bd.MaLopHP = lhp.MaLopHP " +
                     "WHERE bd.MSSV = ? AND lhp.MaHP = ? AND bd.TrangThai = N'Chính thức'";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, maHPTienQuyet);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Sử dụng rs.getObject để kiểm tra NULL trước khi lấy giá trị
                    double diemTK = rs.getObject("DiemTongKet") != null ? rs.getDouble("DiemTongKet") : 0.0;
                    return diemTK >= 4.0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====================================================================
    // PHÂN HỆ XỬ LÝ DÀNH CHO GIẢNG VIÊN (ĐÌNH VIỆT)
    // ====================================================================

    /**
     * UC06.2 (Bổ trợ): Lấy danh sách toàn bộ bản ghi bảng điểm của 1 Lớp học phần
     * để hiển thị lên giao diện nhập liệu cho Giảng viên.
     */
    public List<BangDiem> getBangDiemByLopHP(String maLopHP) {
        List<BangDiem> list = new ArrayList<>();
        String sql = "SELECT * FROM BangDiem WHERE MaLopHP = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLopHP);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BangDiem bd = new BangDiem(
                        rs.getString("MaBangDiem"),
                        rs.getString("MSSV"),
                        rs.getString("MaLopHP"),
                        rs.getString("MaGV"),
                        rs.getDouble("DiemQT"),
                        rs.getDouble("DiemCK"),
                        rs.getDouble("DiemTongKet"),
                        rs.getString("TrangThai")
                    );
                    list.add(bd);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách bảng điểm của lớp HP: " + maLopHP);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * UC06.2: Lưu tạm thời điểm Quá trình và điểm Cuối kỳ cho một sinh viên cụ thể.
     * 🌟 ĐÃ SỬA: Chuyển thành Upsert — nếu bản ghi BangDiem chưa tồn tại (lần đầu
     * nhập điểm cho sinh viên/lớp này) thì tự động INSERT mới; nếu đã tồn tại
     * thì UPDATE như cũ. Trạng thái luôn tự động chuyển về N'Nháp'.
     */
    public boolean luuNhapDiemChiTiet(String maBangDiem, String mssv, String maLopHP,
                                       double diemQT, double diemCK, double diemTongKet) {
        String checkSql = "SELECT COUNT(*) FROM BangDiem WHERE MaBangDiem = ?";
        String updateSql = "UPDATE BangDiem SET DiemQT = ?, DiemCK = ?, DiemTongKet = ?, TrangThai = N'Nháp' WHERE MaBangDiem = ?";
        // Lấy MaGV trực tiếp từ LopHocPhan để không cần truyền thêm tham số ở Controller/View
        String insertSql = "INSERT INTO BangDiem (MaBangDiem, MSSV, MaLopHP, MaGV, DiemQT, DiemCK, DiemTongKet, TrangThai) " +
                            "SELECT ?, ?, ?, lhp.MaGV, ?, ?, ?, N'Nháp' FROM LopHocPhan lhp WHERE lhp.MaLopHP = ?";

        try (Connection conn = DataBaseConnection.getConnection()) {
            boolean exists;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, maBangDiem);
                try (ResultSet rs = checkPs.executeQuery()) {
                    exists = rs.next() && rs.getInt(1) > 0;
                }
            }

            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setDouble(1, diemQT);
                    ps.setDouble(2, diemCK);
                    ps.setDouble(3, diemTongKet);
                    ps.setString(4, maBangDiem);
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, maBangDiem);
                    ps.setString(2, mssv);
                    ps.setString(3, maLopHP);
                    ps.setDouble(4, diemQT);
                    ps.setDouble(5, diemCK);
                    ps.setDouble(6, diemTongKet);
                    ps.setString(7, maLopHP);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lưu nháp bản ghi bảng điểm: " + maBangDiem);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC06.4: Chốt điểm toàn bộ lớp học phần.
     * Khóa toàn bộ quyền chỉnh sửa của Giảng viên và đẩy trạng thái sang N'Chính thức'.
     */
    public boolean chotDiemLopHP(String maLopHP) {
        String sql = "UPDATE BangDiem SET TrangThai = N'Chính thức' WHERE MaLopHP = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLopHP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tiến hành chốt điểm lớp học phần: " + maLopHP);
            e.printStackTrace();
            return false;
        }
    }
}