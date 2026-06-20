package com.nhom10.ooad.quanlysinhvien.View;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình điều khiển chính (Main Shell) của hệ thống.
 * Do Hoài Anh (Giáo vụ) thiết kế và quản lý [1, 7].
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel pnlMainContent; // Vùng làm việc chính (Giữa) [2, 3]
    private JButton btnHoSo, btnDaoTao, btnDangXuat;
    private JLabel lblStatus;

    public MainFrame() {
        // Thiết lập cơ bản cho JFrame [2]
        setTitle("HỆ THỐNG QUẢN LÝ SINH VIÊN - NHÓM 10");
        setSize(1200, 800); // Kích thước mặc định [4]
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        
        initComponents();
        addEvents();
    }

    private void initComponents() {
        // Sử dụng BorderLayout để chia 3 vùng tiêu chuẩn [2-4]
        setLayout(new BorderLayout());
        Font commonFont = new Font("Tahoma", Font.PLAIN, 14); // Font chuẩn [4]

        // --- VÙNG 1: ĐIỀU HƯỚNG (WEST/LEFT) ---
        JPanel pnlNav = new JPanel();
        pnlNav.setPreferredSize(new Dimension(250, 0));
        pnlNav.setLayout(new GridLayout(10, 1, 5, 5));
        pnlNav.setBorder(BorderFactory.createTitledBorder(null, "MENU CHỨC NĂNG", 0, 0, commonFont));

        btnHoSo = new JButton("Quản lý Hồ sơ");
        btnDaoTao = new JButton("Quản lý Đào tạo");
        btnDangXuat = new JButton("Đăng xuất");

        // Thiết lập font cho các nút menu
        btnHoSo.setFont(commonFont);
        btnDaoTao.setFont(commonFont);
        btnDangXuat.setFont(commonFont);

        pnlNav.add(btnHoSo);
        pnlNav.add(btnDaoTao);
        pnlNav.add(new JSeparator()); // Ngăn cách
        pnlNav.add(btnDangXuat);
        
        add(pnlNav, BorderLayout.WEST);

        // --- VÙNG 2: VÙNG LÀM VIỆC CHÍNH (CENTER) ---
        cardLayout = new CardLayout();
        pnlMainContent = new JPanel(cardLayout);
        
        // Nhúng các Panel chức năng do Hoài Anh phụ trách [2, 7]
        pnlMainContent.add(new QuanLyHoSoPanel(), "HoSoScreen");
        pnlMainContent.add(new DaoTaoPanel(), "DaoTaoScreen");
        
        // Màn hình chào mừng mặc định
        JPanel pnlWelcome = new JPanel(new GridBagLayout());
        JLabel lblWelcome = new JLabel("Chào mừng bạn đến với Hệ thống Quản lý Sinh viên");
        lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 18));
        pnlWelcome.add(lblWelcome);
        pnlMainContent.add(pnlWelcome, "WelcomeScreen");

        add(pnlMainContent, BorderLayout.CENTER);
        cardLayout.show(pnlMainContent, "WelcomeScreen");

        // --- VÙNG 3: THÔNG BÁO TRẠNG THÁI (SOUTH) ---
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatus.setBorder(BorderFactory.createLoweredBevelBorder());
        lblStatus = new JLabel("Trạng thái: Sẵn sàng | Phiên làm việc: Giáo vụ");
        lblStatus.setFont(commonFont);
        pnlStatus.add(lblStatus);
        
        add(pnlStatus, BorderLayout.SOUTH);
    }

    private void addEvents() {
        // Xử lý chuyển đổi màn hình bằng CardLayout [2]
        btnHoSo.addActionListener(e -> {
            cardLayout.show(pnlMainContent, "HoSoScreen");
            updateStatus("Đang truy cập: Quản lý Hồ sơ Sinh viên & Lớp học");
        });

        btnDaoTao.addActionListener(e -> {
            cardLayout.show(pnlMainContent, "DaoTaoScreen");
            updateStatus("Đang truy cập: Quản lý Chương trình đào tạo & Mở lớp");
        });

        // Xử lý Đăng xuất (UC03.3) [8]
        btnDangXuat.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Đóng khung Main [8]
                // Sau này sẽ gọi lại màn hình Login ở đây
            }
        });
    }

    /**
     * Cập nhật dòng trạng thái ở vùng dưới cùng màn hình [4].
     */
    public void updateStatus(String message) {
        lblStatus.setText("Trạng thái: " + message);
    }
}