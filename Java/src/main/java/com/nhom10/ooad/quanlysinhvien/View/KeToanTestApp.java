package com.nhom10.ooad.quanlysinhvien.View;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * KeToanTestApp - CHAY THU phan Ke toan MOT MINH, khong can MainFrame cua ca nhom.
 *
 * Cach chay: chuot phai file nay > Run As > Java Application.
 * (Nho da chay xong ScriptSystem.sql va DataBaseConnection ket noi dung CSDL.)
 */
public class KeToanTestApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Phân hệ Kế toán - Tài chính & Báo cáo (Bản chạy thử)");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new KeToanPanel());
            f.setSize(1000, 640);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
