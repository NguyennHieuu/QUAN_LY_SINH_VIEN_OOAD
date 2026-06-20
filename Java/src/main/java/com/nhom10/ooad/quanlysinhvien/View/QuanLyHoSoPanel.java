package com.nhom10.ooad.quanlysinhvien.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Giao diện Quản lý hồ sơ (Sinh viên, Giảng viên & Lớp học)
 */
public class QuanLyHoSoPanel extends JPanel {
    private JTable tblHienThi;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnTimKiem;
    private JLabel lblStatus; // Nhãn hiển thị trạng thái cục bộ cho Panel

    public QuanLyHoSoPanel() {
        initComponent();
    }

    private void initComponent() {
        // Áp dụng bố cục BorderLayout chia vùng tiêu chuẩn [3-5]
        setLayout(new BorderLayout(10, 10));
        Font commonFont = new Font("Tahoma", Font.PLAIN, 14); // Tiêu chuẩn font Tahoma size 14 [5]

        // --- VÙNG 1: ĐIỀU HƯỚNG & TÌM KIẾM (NORTH) --- [3, 4]
        // Phục vụ UC01.2: Tìm kiếm sinh viên [6]
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Công cụ tra cứu"));
        
        JLabel lblSearch = new JLabel("Tìm kiếm (Mã/Họ tên):");
        lblSearch.setFont(commonFont);
        txtTimKiem = new JTextField(25);
        txtTimKiem.setFont(commonFont);
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(commonFont);
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        add(searchPanel, BorderLayout.NORTH);

        // --- VÙNG 2: VÙNG LÀM VIỆC CHÍNH - HIỂN THỊ DANH SÁCH (CENTER) --- [3, 4]
        // Cột hiển thị theo UC01.5: Hiển thị danh sách sinh viên [7]
        String[] columns = {"MSSV/Mã GV", "Họ Tên", "Ngày Sinh", "Giới Tính", "Lớp/Đơn Vị"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblHienThi = new JTable(model);
        tblHienThi.setFont(commonFont);
        tblHienThi.setRowHeight(25); // Tăng độ cao dòng để dễ quan sát
        
        JScrollPane scrollPane = new JScrollPane(tblHienThi);
        add(scrollPane, BorderLayout.CENTER);

        // --- VÙNG 3: ĐIỀU KHIỂN HÀNH ĐỘNG & THÔNG BÁO (SOUTH) --- [3-5]
        JPanel actionArea = new JPanel(new BorderLayout());
        
        // Vùng nút bấm: Nằm ở góc dưới cùng bên phải, kích thước bằng nhau [3, 5]
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");

        Dimension btnSize = new Dimension(100, 35);
        JButton[] buttons = {btnThem, btnSua, btnXoa};
        for (JButton btn : buttons) {
            btn.setFont(commonFont);
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        // Vùng thông báo trạng thái cục bộ (Dưới cùng bên trái) [5]
        lblStatus = new JLabel("Sẵn sàng");
        lblStatus.setFont(commonFont);
        
        actionArea.add(lblStatus, BorderLayout.WEST);
        actionArea.add(buttonPanel, BorderLayout.EAST);
        add(actionArea, BorderLayout.SOUTH);
    }

    // --- CÁC HÀM TIỆN ÍCH CHO CONTROLLER ---

    /**
     * Cập nhật thông báo trạng thái với màu sắc chuẩn [5]
     * @param message Nội dung thông báo
     * @param isError true nếu là lỗi (Đỏ), false nếu thành công (Xanh)
     */
    public void setStatus(String message, boolean isError) {
        lblStatus.setText(" Trạng thái: " + message);
        if (isError) {
            lblStatus.setForeground(Color.RED); // Lỗi màu đỏ [5]
        } else {
            lblStatus.setForeground(new Color(0, 150, 0)); // Thành công màu xanh [5]
        }
    }

    // Getters để Controller kết nối sự kiện [3]
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public String getKeyword() { return txtTimKiem.getText().trim(); }
    public JTable getTblHienThi() { return tblHienThi; }
}