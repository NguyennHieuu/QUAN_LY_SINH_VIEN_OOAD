package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.GiangVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiangVienDAO {
    // Thêm mới giảng viên (Phục vụ nhiệm vụ quản lý hồ sơ của Giáo vụ)
    public boolean insert(GiangVien gv) {
        String sql = "INSERT INTO GiangVien (MaGV, HoTenGV, SDT, Email, DonViCongTac, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gv.getMaGV());
            ps.setString(2, gv.getHoTenGV());
            ps.setString(3, gv.getSdt());
            ps.setString(4, gv.getEmail());
            ps.setString(5, gv.getDonViCongTac());
            ps.setString(6, gv.getTenDangNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

    // Xóa giảng viên
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

    // Tra cứu giảng viên (UC04.4 - Phục vụ việc phân công giảng dạy)
    public List<GiangVien> search(String keyword) {
        List<GiangVien> list = new ArrayList<>();
        String sql = "SELECT * FROM GiangVien WHERE MaGV LIKE ? OR HoTenGV LIKE ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}