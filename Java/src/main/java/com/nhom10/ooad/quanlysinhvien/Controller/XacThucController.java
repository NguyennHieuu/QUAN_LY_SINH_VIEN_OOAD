/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom10.ooad.quanlysinhvien.Controller;

/**
 *
 * @author ADMIN
 */
import com.nhom10.ooad.quanlysinhvien.DAO.TaiKhoanDAO;
import com.nhom10.ooad.quanlysinhvien.Model.TaiKhoan;
import com.nhom10.ooad.quanlysinhvien.View.LoginView;
import com.nhom10.ooad.quanlysinhvien.View.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Thẻ CRC ID: 13 - Điều phối xác thực và phân quyền
 */
public class XacThucController {
    private LoginView loginView;
    private TaiKhoanDAO taiKhoanDAO;

    public XacThucController(LoginView loginView) {
        this.loginView = loginView;
        this.taiKhoanDAO = new TaiKhoanDAO();
        
        // Gán sự kiện cho nút đăng nhập [1]
        this.loginView.getBtnLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String user = loginView.getUsername();
        String pass = loginView.getPassword();
        
        TaiKhoan tk = taiKhoanDAO.checkLogin(user, pass);
        
        if (tk != null) {
            // Đăng nhập thành công, mở MainFrame (Cái "vỏ" do Hoài Anh tạo) [3]
            MainFrame mainFrame = new MainFrame();
            // TODO: Thiết lập Menu dựa trên vai trò (tk.getVaiTro())
            mainFrame.setVisible(true);
            loginView.dispose();
        } else {
            // Thông báo lỗi theo màu tiêu chuẩn (Đỏ) [4]
            loginView.showErrorMessage("Tài khoản hoặc mật khẩu không đúng!");
        }
    }
}