package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.DotDangKy;
import java.sql.*;

public class DotDangKyDAO {

    // Lấy thông tin cổng mở/đóng đợt đăng ký của một kỳ
    public DotDangKy getCurrentDotDangKy(String maHocKy) {
        String sql = "SELECT * FROM DotDangKy WHERE MaHocKy = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Chuyển đổi từ DATETIME (SQL) sang java.util.Date (Java)
                    return new DotDangKy(
                        rs.getString("MaDotDangKy"),
                        rs.getString("MaHocKy"),
                        rs.getTimestamp("ThoiGianMo"),
                        rs.getTimestamp("ThoiGianDong")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}