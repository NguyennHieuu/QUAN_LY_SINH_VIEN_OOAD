package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.QuanLyHoSoController;
import com.nhom10.ooad.quanlysinhvien.Model.GiangVien;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GiaoDienQuanLyGiangVien extends JPanel {
    private final QuanLyHoSoController controller = new QuanLyHoSoController();

    private JTable tblGiangVien;
    private DefaultTableModel model;
    private JTextField txtMaGV, txtHoTen, txtSDT, txtEmail, txtDonVi, txtTimKiem; // 🌟 ĐA SỬA: Thay txtHocVi bằng txtSDT và txtEmail để khớp DB
    private JButton btnThem, btnSua, btnXoa, btnTimKiem;
    private JLabel lblStatus;

    public GiaoDienQuanLyGiangVien() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        
        Font commonFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // --- 1. VÙNG FORM NHẬP LIỆU GIẢNG VIÊN (WEST) ---
        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 12)); // 🌟 ĐÃ SỬA: Đổi thành 5 dòng để chứa đủ thông tin chuẩn hóa
        pnlForm.setBorder(BorderFactory.createTitledBorder("Thông tin hồ sơ Giảng viên"));
        
        txtMaGV = new JTextField(12);
        txtHoTen = new JTextField(12);
        txtSDT = new JTextField(12);
        txtEmail = new JTextField(12);
        txtDonVi = new JTextField(12);

        JLabel[] labels = {new JLabel("Mã GV:"), new JLabel("Họ tên:"), new JLabel("Số điện thoại:"), new JLabel("Email:"), new JLabel("Đơn vị công tác:")};
        JComponent[] inputs = {txtMaGV, txtHoTen, txtSDT, txtEmail, txtDonVi};
        
        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(commonFont);
            inputs[i].setFont(commonFont);
            pnlForm.add(labels[i]);
            pnlForm.add(inputs[i]);
        }
        
        JPanel pnlWestWrapper = new JPanel(new BorderLayout());
        pnlWestWrapper.add(pnlForm, BorderLayout.NORTH);
        add(pnlWestWrapper, BorderLayout.WEST);

        // --- 2. THANH TRA CỨU NHANH (NORTH) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tra cứu Giảng viên"));
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(commonFont);
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(boldFont);
        
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        add(searchPanel, BorderLayout.NORTH);

        // --- 3. BẢNG HIỂN THỊ KẾT QUẢ (CENTER) ---
        String[] columns = {"Mã GV", "Họ và Tên", "Số Điện Thoại", "Email", "Đơn Vị Công Tác"}; // 🌟 ĐÃ SỬA: Đồng bộ cột hiển thị lưới
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGiangVien = new JTable(model);
        tblGiangVien.setFont(commonFont);
        tblGiangVien.getTableHeader().setFont(boldFont);
        tblGiangVien.setRowHeight(25);
        add(new JScrollPane(tblGiangVien), BorderLayout.CENTER);

        // --- 4. HÀNH ĐỘNG VÀ TRẠNG THÁI (SOUTH) ---
        JPanel actionArea = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnThem = new JButton("Thêm GV");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa GV");

        Dimension btnSize = new Dimension(110, 35);
        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa}) {
            btn.setFont(boldFont);
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        lblStatus = new JLabel("Sẵn sàng quản lý hồ sơ cán bộ giảng viên.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        actionArea.add(lblStatus, BorderLayout.WEST);
        actionArea.add(buttonPanel, BorderLayout.EAST);
        add(actionArea, BorderLayout.SOUTH);

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LIÊN KẾT ĐỒNG BỘ (ACTION LISTENERS)
        // ====================================================================
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnThem.addActionListener(e -> xuLyThem());
        btnSua.addActionListener(e -> xuLySua());
        btnXoa.addActionListener(e -> xuLyXoa());

        // 🌟 SỰ KIỆN CLICK DÒNG BẢNG: Đổ ngược thông tin từ lưới sang Form trái để chỉnh sửa
        tblGiangVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblGiangVien.getSelectedRow();
                if (selectedRow >= 0) {
                    txtMaGV.setText(model.getValueAt(selectedRow, 0).toString());
                    txtHoTen.setText(model.getValueAt(selectedRow, 1).toString());
                    txtSDT.setText(model.getValueAt(selectedRow, 2) != null ? model.getValueAt(selectedRow, 2).toString() : "");
                    txtEmail.setText(model.getValueAt(selectedRow, 3) != null ? model.getValueAt(selectedRow, 3).toString() : "");
                    txtDonVi.setText(model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : "");
                    
                    // Khóa trường khóa chính Mã GV tránh chỉnh sửa sai lệch logic hệ thống
                    txtMaGV.setEditable(false);
                }
            }
        });
    }

    /**
     * Nghiệp vụ 1: Tra cứu hồ sơ giảng viên từ SQL Server
     */
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        model.setRowCount(0); 
        clearForm();

        com.nhom10.ooad.quanlysinhvien.DAO.GiangVienDAO gvDAO = new com.nhom10.ooad.quanlysinhvien.DAO.GiangVienDAO();
        java.util.List<GiangVien> danhSach = gvDAO.search(keyword);

        if (danhSach == null || danhSach.isEmpty()) {
            setStatus("Không tìm thấy giảng viên nào phù hợp với từ khóa: " + keyword, true);
        } else {
            for (GiangVien gv : danhSach) {
                model.addRow(new Object[]{
                    gv.getMaGV(),
                    gv.getHoTenGV(),
                    gv.getSdt(),
                    gv.getEmail(),
                    gv.getDonViCongTac()
                });
            }
            setStatus("Đã tìm thấy " + danhSach.size() + " hồ sơ cán bộ giảng viên.", false);
        }
    }

    /**
     * Nghiệp vụ 2: Bấm nút "Thêm" -> Lưu vĩnh viễn vào DB và đồng bộ cấp tài khoản
     */
    private void xuLyThem() {
        String maGv = txtMaGV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String donVi = txtDonVi.getText().trim();

        if (maGv.isEmpty() || hoTen.isEmpty()) {
            setStatus("Lỗi: Không được để trống Mã GV và Họ tên giảng viên!", true);
            return;
        }

        GiangVien gv = new GiangVien(maGv, hoTen, sdt, email, donVi, maGv);

        boolean success = controller.themGiangVien(gv);
        if (success) {
            model.addRow(new Object[]{maGv, hoTen, sdt, email, donVi});
            setStatus("Thành công: Đã lưu giảng viên " + maGv + " và tự động cấp tài khoản đăng nhập!", false);
            clearForm();
        } else {
            setStatus("Lỗi nghiêm trọng: Lưu vào Database thất bại! (Có thể dính trùng Mã GV)", true);
        }
    }

    /**
     * Nghiệp vụ 3: Bấm nút "Sửa" -> Chỉnh sửa thông tin giảng viên vĩnh viễn dưới DB
     */
    private void xuLySua() {
        int selectedRow = tblGiangVien.getSelectedRow();
        if (selectedRow == -1) {
            setStatus("Vui lòng chọn một cán bộ giảng viên trên bảng để tiến hành sửa!", true);
            return;
        }

        String maGv = model.getValueAt(selectedRow, 0).toString();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String donVi = txtDonVi.getText().trim();

        if (hoTen.isEmpty()) {
            setStatus("Lỗi: Không được phép bỏ trống trường Họ tên giảng viên!", true);
            return;
        }

        GiangVien gv = new GiangVien(maGv, hoTen, sdt, email, donVi, maGv);

        boolean success = controller.suaGiangVien(gv);
        if (success) {
            model.setValueAt(hoTen, selectedRow, 1);
            model.setValueAt(sdt, selectedRow, 2);
            model.setValueAt(email, selectedRow, 3);
            model.setValueAt(donVi, selectedRow, 4);

            setStatus("Đã cập nhật thay đổi hồ sơ giảng viên " + maGv + " xuống Database thành công.", false);
            clearForm();
        } else {
            setStatus("Lỗi hệ thống: Cập nhật thông tin cán bộ thất bại!", true);
        }
    }

    /**
     * Nghiệp vụ 4: Xóa giảng viên (Chạy Transaction chuỗi gỡ bỏ lớp trước khi xóa tài khoản)
     */
    private void xuLyXoa() {
        int selectedRow = tblGiangVien.getSelectedRow();
        if (selectedRow == -1) {
            setStatus("Vui lòng chọn 1 giảng viên trên bảng để tiến hành xóa!", true);
            return;
        }

        String maGv = model.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Hành động sẽ xóa vĩnh viễn hồ sơ và tài khoản của cán bộ " + maGv + ".\nBạn có chắc chắn muốn tiếp tục?", 
                "Xác nhận xóa cán bộ giảng viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.xoaGiangVien(maGv);
            if (success) {
                setStatus("Đã xóa cán bộ và dọn dẹp phân công lớp thành công!", false);
                model.removeRow(selectedRow);
                clearForm();
            } else {
                setStatus("Lỗi hệ thống: Xóa dữ liệu thất bại do dính ràng buộc nghiêm ngặt!", true);
            }
        }
    }

    private void clearForm() {
        txtMaGV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDonVi.setText("");
        txtMaGV.setEditable(true);
    }

    public void setStatus(String message, boolean laLoi) {
        lblStatus.setText(" Trạng thái: " + message);
        lblStatus.setForeground(laLoi ? Color.RED : new Color(46, 125, 50));
    }
}