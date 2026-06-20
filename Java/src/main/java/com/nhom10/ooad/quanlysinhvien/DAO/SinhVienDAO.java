package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.SinhVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {
    
    // Logic UC01.1: Thêm mới sinh viên vào DB [3]
    public boolean insert(SinhVien sv) {
        String sql = "INSERT INTO SinhVien (MSSV, HoTen, NgaySinh, GioiTinh, MaLopQuanLy, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sv.getMssv());
            ps.setString(2, sv.getHoTen());
            ps.setDate(3, new java.sql.Date(sv.getNgaySinh().getTime()));
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getMaLopQuanLy());
            ps.setString(6, sv.getTenDangNhap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic UC01.3: Cập nhật thông tin sinh viên [1]
    // Sửa mọi thông tin ngoại trừ MSSV (Khóa chính)
    public boolean update(SinhVien sv) {
        String sql = "UPDATE SinhVien SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, MaLopQuanLy = ?, TenDangNhap = ? WHERE MSSV = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sv.getHoTen());
            ps.setDate(2, new java.sql.Date(sv.getNgaySinh().getTime()));
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getMaLopQuanLy());
            ps.setString(5, sv.getTenDangNhap());
            ps.setString(6, sv.getMssv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic UC01.4: Xoá hồ sơ sinh viên [4]
    public boolean delete(String mssv) {
        String sql = "DELETE FROM SinhVien WHERE MSSV = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic UC01.2: Tìm kiếm sinh viên theo MSSV hoặc Họ tên [5]
    public List<SinhVien> search(String keyword) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT * FROM SinhVien WHERE MSSV LIKE ? OR HoTen LIKE ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SinhVien(
                    rs.getString("MSSV"),
                    rs.getString("HoTen"),
                    rs.getDate("NgaySinh"),
                    rs.getString("GioiTinh"),
                    rs.getString("MaLopQuanLy"),
                    rs.getString("TenDangNhap")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}