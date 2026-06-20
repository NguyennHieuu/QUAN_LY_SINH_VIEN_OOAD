package com.nhom10.ooad.quanlysinhvien.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Giao diện Quản lý Đào tạo - Phân hệ của Hoài Anh (Giáo vụ).
 * Thiết kế dưới dạng JPanel để nhúng vào vùng làm việc chính của MainFrame [4].
 */
public class DaoTaoPanel extends JPanel {
    // Các thành phần nhập liệu phục vụ UC04.1 [1]
    private JTextField txtMaHP, txtMaHK, txtSiSoMax, txtLichHoc, txtTrongSoQT, txtTrongSoCK;
    private JTable tblHocPhan;
    private JButton btnMoLop, btnTraCuu;

    public DaoTaoPanel() {
        initComponent();
    }

    private void initComponent() {
        // Sử dụng BorderLayout để chia 3 vùng tiêu chuẩn [3, 4]
        setLayout(new BorderLayout(10, 10));
        Font commonFont = new Font("Tahoma", Font.PLAIN, 14); // Tiêu chuẩn font Tahoma 14 [3]

        // --- VÙNG 1: THIẾT LẬP NHẬP LIỆU (NORTH) ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(null, "Thiết lập Mở lớp học phần", 
                0, 0, commonFont));

        // Hàng 1
        JLabel lblMaHP = new JLabel("Mã học phần:"); lblMaHP.setFont(commonFont);
        txtMaHP = new JTextField(); txtMaHP.setFont(commonFont);
        JLabel lblMaHK = new JLabel("Mã học kỳ:"); lblMaHK.setFont(commonFont);
        txtMaHK = new JTextField(); txtMaHK.setFont(commonFont);

        // Hàng 2
        JLabel lblSiSo = new JLabel("Sĩ số tối đa:"); lblSiSo.setFont(commonFont);
        txtSiSoMax = new JTextField(); txtSiSoMax.setFont(commonFont);
        JLabel lblLich = new JLabel("Lịch học:"); lblLich.setFont(commonFont);
        txtLichHoc = new JTextField(); txtLichHoc.setFont(commonFont);

        // Hàng 3 (Phục vụ logic trọng số điểm QT + CK = 100% [1])
        JLabel lblQT = new JLabel("Trọng số QT (%):"); lblQT.setFont(commonFont);
        txtTrongSoQT = new JTextField(); txtTrongSoQT.setFont(commonFont);
        JLabel lblCK = new JLabel("Trọng số CK (%):"); lblCK.setFont(commonFont);
        txtTrongSoCK = new JTextField(); txtTrongSoCK.setFont(commonFont);

        inputPanel.add(lblMaHP); inputPanel.add(txtMaHP);
        inputPanel.add(lblMaHK); inputPanel.add(txtMaHK);
        inputPanel.add(lblSiSo); inputPanel.add(txtSiSoMax);
        inputPanel.add(lblLich); inputPanel.add(txtLichHoc);
        inputPanel.add(lblQT); inputPanel.add(txtTrongSoQT);
        inputPanel.add(lblCK); inputPanel.add(txtTrongSoCK);

        add(inputPanel, BorderLayout.NORTH);

        // --- VÙNG 2: HIỂN THỊ DANH MỤC HỌC PHẦN (CENTER) ---
        // Phục vụ UC04.2: Tra cứu CTĐT và học phần [2, 6]
        String[] columns = {"Mã HP", "Tên Học Phần", "Số Tín Chỉ", "Loại HP", "Mã CTĐT"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblHocPhan = new JTable(model);
        tblHocPhan.setFont(commonFont);
        tblHocPhan.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tblHocPhan);
        add(scrollPane, BorderLayout.CENTER);

        // --- VÙNG 3: ĐIỀU KHIỂN HÀNH ĐỘNG (SOUTH) ---
        // Nút bấm đặt ở góc dưới cùng bên phải, kích thước bằng nhau [3, 4]
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnTraCuu = new JButton("Tra cứu CTĐT");
        btnMoLop = new JButton("Mở lớp");

        Dimension btnSize = new Dimension(140, 35);
        btnTraCuu.setPreferredSize(btnSize); btnTraCuu.setFont(commonFont);
        btnMoLop.setPreferredSize(btnSize); btnMoLop.setFont(commonFont);

        actionPanel.add(btnTraCuu);
        actionPanel.add(btnMoLop);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // Các Getters để Controller kết nối logic nghiệp vụ [4, 7]
    public String getMaHP() { return txtMaHP.getText(); }
    public String getMaHK() { return txtMaHK.getText(); }
    public String getSiSoMax() { return txtSiSoMax.getText(); }
    public String getLichHoc() { return txtLichHoc.getText(); }
    public String getTrongSoQT() { return txtTrongSoQT.getText(); }
    public String getTrongSoCK() { return txtTrongSoCK.getText(); }
    public JButton getBtnMoLop() { return btnMoLop; }
    public JButton getBtnTraCuu() { return btnTraCuu; }
    public JTable getTblHocPhan() { return tblHocPhan; }
}