package com.nhom10.ooad.quanlysinhvien.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    
    // Lưu trữ một kết nối duy nhất trong suốt vòng đời ứng dụng
    private static Connection connection = null;

    // 🌟 ĐÃ SỬA: Đổi databaseName từ "CỬA HÀNG TIỆN LỢI" thành "QUAN_LY_SINH_VIEN"
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QUAN_LY_SINH_VIEN;encrypt=false;characterEncoding=UTF-8";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "sa"; // Mỗi thành viên tự sửa lại pass sa cho khớp với máy mình

    /**
     * Hàm lấy kết nối tới SQL Server (Trả về đối tượng Connection)
     */
    public static Connection getConnection() {
        try {
            // Nếu chưa có kết nối hoặc kết nối cũ đã bị đóng, tiến hành khởi tạo mới
            if (connection == null || connection.isClosed()) {
                // 1. Nạp driver điều khiển
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                
                // 2. Thiết lập kết nối chính thức
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("====== [SQL Server] Kết nối cơ sở dữ liệu THÀNH CÔNG! ======");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ LỖI: Không tìm thấy thư viện JDBC Driver của SQL Server!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ LỖI: Kết nối thất bại! Hãy kiểm tra URL, tài khoản 'sa' hoặc trạng thái SQL Server Services.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Hàm đóng kết nối an toàn khi tắt ứng dụng hoặc giải phóng tài nguyên
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("====== [SQL Server] Đã ngắt kết nối an toàn. ======");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}