package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.GiangVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiangVienDAO {
    
    // Logic: Thêm mới giảng viên vào DB đồng thời tự tạo tài khoản hệ thống (Transaction)
    public boolean insert(GiangVien gv) {
        Connection conn = null;
        PreparedStatement psTK = null;
        PreparedStatement psGV = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION ĐỒNG BỘ CẤP TÀI KHOẢN

            // 1. Tạo tự động tài khoản Giảng viên (Mật khẩu mặc định: 123456)
            String sqlTaiKhoan = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThaiHoatDong) VALUES (?, ?, N'Giảng viên', 1)";
            psTK = conn.prepareStatement(sqlTaiKhoan);
            psTK.setString(1, gv.getMaGV()); // Lấy Mã GV làm tên đăng nhập luôn cho đồng bộ
            psTK.setString(2, "123456"); 
            psTK.executeUpdate();

            // 2. Chèn hồ sơ cốt lõi vào bảng GiangVien
            String sqlGiangVien = "INSERT INTO GiangVien (MaGV, HoTenGV, SDT, Email, DonViCongTac, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";
            psGV = conn.prepareStatement(sqlGiangVien);
            psGV.setString(1, gv.getMaGV());
            psGV.setString(2, gv.getHoTenGV());
            psGV.setString(3, gv.getSdt());
            psGV.setString(4, gv.getEmail());
            psGV.setString(5, gv.getDonViCongTac());
            psGV.setString(6, gv.getMaGV()); // Khóa ngoại map sang bảng TaiKhoan vừa tạo
            psGV.executeUpdate();

            conn.commit(); // CHỐT TRANSACTION LƯU VĨNH VIỄN
            return true;
        } catch (SQLException e) {
            try { 
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) { 
                ex.printStackTrace(); 
            }
            e.printStackTrace();
            return false;
        } finally { // 🌟 ĐÃ SỬA: Sửa lỗi chính tả từ "finaly" thành "finally" giúp hết báo lỗi đỏ
            try { if (psTK != null) psTK.close(); } catch (SQLException e) {}
            try { if (psGV != null) psGV.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // Cập nhật thông tin giảng viên
    public boolean update(GiangVien gv) {
        String sql = "UPDATE GiangVien SET HoTenGV=?, SDT=?, Email=?, DonViCongTac=? WHERE MaGV=?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gv.getHoTenGV());
            ps.setString(2, gv.getSdt());
            ps.setString(3, gv.getEmail());
            ps.setString(4, gv.getDonViCongTac());
            ps.setString(5, gv.getMaGV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🌟 HÀM OVERLOADING PHỤC VỤ TRANSACTION XÓA TỪ CONTROLLER.
     * Nhận kết nối conn dùng chung để xóa sạch dữ liệu phân công giảng dạy trước khi xóa hồ sơ cốt lõi.
     */
    public boolean delete(Connection conn, String maGV) throws SQLException {
        String sql = "DELETE FROM GiangVien WHERE MaGV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGV);
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa giảng viên độc lập (Giữ tương thích ngược cho dự án)
    public boolean delete(String maGV) {
        String sql = "DELETE FROM GiangVien WHERE MaGV = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maGV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tra cứu giảng viên theo Mã hoặc Tên
    public List<GiangVien> search(String keyword) {
        List<GiangVien> list = new ArrayList<>();
        String sql = "SELECT * FROM GiangVien WHERE MaGV LIKE ? OR HoTenGV LIKE ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new GiangVien(
                        rs.getString("MaGV"),
                        rs.getString("HoTenGV"),
                        rs.getString("SDT"),
                        rs.getString("Email"),
                        rs.getString("DonViCongTac"),
                        rs.getString("TenDangNhap")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}