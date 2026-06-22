package com.nhom10.ooad.quanlysinhvien;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.View.GiaoDienDangNhap;
import javax.swing.UIManager;
import java.sql.Connection;

public class QuanLySinhVien {
    public static void main(String[] args) {
        
        // 🌟 BƯỚC 1: KÍCH HOẠT VÀ KIỂM TRA ĐƯỜNG TRUYỀN DATABASE NGAY KHI KHỞI ĐỘNG
        System.out.println("-> Hệ thống: Đang kiểm tra cấu hình kết nối SQL Server...");
        Connection testConn = DataBaseConnection.getConnection();
        
        if (testConn == null) {
            System.err.println("❌ LỖI KHỞI ĐỘNG: Không thể thiết lập kết nối ban đầu tới CSDL!");
            System.err.println("-> Vui lòng kiểm tra lại cấu hình tài khoản/mật khẩu 'sa' trong DataBaseConnection.");
        }

        // Đánh luồng đồ họa vào Event Dispatch Thread (EDT) để đảm bảo các Frame không bị xung đột, giật lag
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 🌟 BƯỚC 2: CẤU HÌNH LOOK & FEEL (Đồng bộ giao diện nút bấm phẳng theo hệ điều hành)
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("-> Không thể áp dụng giao diện phẳng hệ thống, sử dụng giao diện mặc định.");
                }

                // 🌟 BƯỚC 3: MỞ CỔNG ĐĂNG NHẬP ĐẦU VÀO CỦA HỆ THỐNG
                // Từ đây, XacThucController sẽ nhận quyền điều phối để bật GiaoDienChinh phù hợp (Sinh viên, Giáo vụ, Giảng viên, Kế toán)
                GiaoDienDangNhap khungDangNhap = new GiaoDienDangNhap();
                khungDangNhap.setVisible(true);
                
                System.out.println("====== [HỆ THỐNG] Khởi chạy cổng Đăng nhập Nhóm 10 THÀNH CÔNG! ======");
            }
        });

        // 🌟 BƯỚC 4: ĐĂNG KÝ HOOK NGẮT KẾT NỐI DB TỰ ĐỘNG KHI THOÁT PHẦN MỀM
        // Khi người dùng bấm nút "Thoát" (System.exit(0)) hoặc nút [X] trên tiêu đề, luồng này tự động giải phóng RAM dưới SQL
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("-> Hệ thống: Nhận tín hiệu đóng toàn bộ chương trình.");
            DataBaseConnection.closeConnection();
        }));
    }
}