package com.nhom10.ooad.quanlysinhvien.Controller;

import com.nhom10.ooad.quanlysinhvien.DAO.SinhVienDAO;
import java.sql.*;
import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;

public class QuanLyHoSoController {
    private SinhVienDAO svDAO = new SinhVienDAO();

    // Triển khai logic UC01.4: Xóa sinh viên và cập nhật sĩ số lớp [8]
    public boolean xoaSinhVien(String mssv, String maLop) {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo tính toàn vẹn [9]

            // 1. Xóa sinh viên
            if (svDAO.delete(mssv)) {
                // 2. Cập nhật giảm sĩ số lớp quản lý liên quan
                String updateSql = "UPDATE LopHoc SET SiSo = SiSo - 1 WHERE MaLopQuanLy = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, maLop);
                ps.executeUpdate();
                
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        }
        return false;
    }
}