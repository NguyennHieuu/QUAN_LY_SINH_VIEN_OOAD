/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.TaiKhoan;
import java.sql.*;

public class TaiKhoanDAO {
    public TaiKhoan verifyLogin(String user, String pass) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThaiHoatDong = 1";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TaiKhoan(rs.getString("TenDangNhap"), rs.getString("MatKhau"), 
                                   rs.getString("VaiTro"), rs.getBoolean("TrangThaiHoatDong"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
