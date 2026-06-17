package com.nhom10.ooad.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Tên máy chủ và cơ sở dữ liệu đã chuẩn hóa của Nhóm 10
            String serverName = "localhost"; // Hoặc tên máy riêng của mỗi người, VD: TRUNGHIEU
            String databaseName = "QUAN_LY_SINH_VIEN";
            
            // PHƯƠNG ÁN A: Dành cho máy sử dụng tài khoản SQL Server Authentication (sa/mật khẩu)
            String dbURL = "jdbc:sqlserver://" + serverName + ":1433;databaseName=" + databaseName + ";encrypt=true;trustServerCertificate=true;";
            String user = "sa"; 
            String password = "YOUR_PASSWORD_HERE"; // Thay mật khẩu SQL máy bạn vào đây
            conn = DriverManager.getConnection(dbURL, user, password);
            
            // PHƯƠNG ÁN B: Dành cho máy đăng nhập bằng Windows Authentication (Không cần user/pass)
            // Nếu dùng phương án này, bạn bỏ dấu comment (//) ở 3 dòng dưới và comment phương án A lại:
            // String dbURL = "jdbc:sqlserver://" + serverName + ":1433;databaseName=" + databaseName + ";integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
            // conn = DriverManager.getConnection(dbURL);
            
            System.out.println("Kết nối dữ liệu SQL Server thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối dữ liệu: " + e.getMessage());
        }
        return conn;
    }
}