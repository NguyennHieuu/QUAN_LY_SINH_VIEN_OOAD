package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.QuanLyHoSoController;
import com.nhom10.ooad.quanlysinhvien.Model.SinhVien;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GiaoDienQuanLySinhVien extends JPanel {
    private final QuanLyHoSoController controller = new QuanLyHoSoController();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private JTable tblSinhVien;
    private DefaultTableModel model;
    private JTextField txtMSSV, txtHoTen, txtNgaySinh, txtTimKiem, txtLopQL;
    private JComboBox<String> cboGioiTinh;
    private JButton btnThem, btnSua, btnXoa, btnTimKiem;
    private JLabel lblStatus;

    public GiaoDienQuanLySinhVien() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        
        Font commonFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // --- 1. VÙNG FORM NHẬP LIỆU (WEST) ---
        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 12));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Thông tin hồ sơ Sinh viên"));
        
        txtMSSV = new JTextField(12);
        txtHoTen = new JTextField(12);
        txtNgaySinh = new JTextField("2005-01-01", 12);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        txtLopQL = new JTextField(12);

        JLabel[] labels = {new JLabel("MSSV:"), new JLabel("Họ tên:"), new JLabel("Ngày sinh:"), new JLabel("Giới tính:"), new JLabel("Lớp quản lý:")};
        JComponent[] inputs = {txtMSSV, txtHoTen, txtNgaySinh, cboGioiTinh, txtLopQL};
        
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
        searchPanel.setBorder(BorderFactory.createTitledBorder("Công cụ tra cứu"));
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(commonFont);
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(boldFont);
        
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        add(searchPanel, BorderLayout.NORTH);

        // --- 3. BẢNG HIỂN THỊ KẾT QUẢ (CENTER) ---
        String[] columns = {"MSSV", "Họ Tên", "Ngày Sinh", "Giới Tính", "Lớp Quản Lý"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } 
        };
        tblSinhVien = new JTable(model);
        tblSinhVien.setFont(commonFont);
        tblSinhVien.getTableHeader().setFont(boldFont);
        tblSinhVien.setRowHeight(25);
        add(new JScrollPane(tblSinhVien), BorderLayout.CENTER);

        // --- 4. THANH HÀNH ĐỘNG HỆ THỐNG (SOUTH) ---
        JPanel actionArea = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");

        Dimension btnSize = new Dimension(110, 35);
        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa}) {
            btn.setFont(boldFont);
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        lblStatus = new JLabel("Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        actionArea.add(lblStatus, BorderLayout.WEST);
        actionArea.add(buttonPanel, BorderLayout.EAST);
        add(actionArea, BorderLayout.SOUTH);

        // --- ĐĂNG KÝ SỰ KIỆN LIÊN KẾT ---
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnThem.addActionListener(e -> xuLyThem());
        btnSua.addActionListener(e -> xuLySua());
        btnXoa.addActionListener(e -> xuLyXoa());

        // SỰ KIỆN CLICK DÒNG BẢNG
        tblSinhVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblSinhVien.getSelectedRow();
                if (selectedRow >= 0) {
                    txtMSSV.setText(model.getValueAt(selectedRow, 0).toString());
                    txtHoTen.setText(model.getValueAt(selectedRow, 1).toString());
                    txtNgaySinh.setText(model.getValueAt(selectedRow, 2).toString());
                    cboGioiTinh.setSelectedItem(model.getValueAt(selectedRow, 3).toString());
                    txtLopQL.setText(model.getValueAt(selectedRow, 4).toString());
                    txtMSSV.setEditable(false);
                }
            }
        });
    }

    /**
     * Nghiệp vụ 1: Tra cứu dốc dữ liệu từ SQL Server
     */
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        model.setRowCount(0); 
        clearForm();

        com.nhom10.ooad.quanlysinhvien.DAO.SinhVienDAO svDAO = new com.nhom10.ooad.quanlysinhvien.DAO.SinhVienDAO();
        java.util.List<SinhVien> danhSach = svDAO.search(keyword);

        if (danhSach == null || danhSach.isEmpty()) {
            setStatus("Không tìm thấy kết quả nào phù hợp với từ khóa: " + keyword, true);
        } else {
            for (SinhVien sv : danhSach) {
                String strNgaySinh = "";
                if (sv.getNgaySinh() != null) {
                    strNgaySinh = df.format(sv.getNgaySinh());
                }
                model.addRow(new Object[]{
                    sv.getMssv(),
                    sv.getHoTen(),
                    strNgaySinh,
                    sv.getGioiTinh(),
                    sv.getMaLopQuanLy()
                });
            }
            setStatus("Đã tìm thấy " + danhSach.size() + " hồ sơ sinh viên trùng khớp.", false);
        }
    }

    /**
     * Nghiệp vụ 2: Bấm nút "Thêm" -> Đẩy dữ liệu vào Database thật và hiển thị lên lưới phải
     */
    private void xuLyThem() {
        String mssv = txtMSSV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String strNgaySinh = txtNgaySinh.getText().trim();
        String gioiTinh = cboGioiTinh.getSelectedItem().toString();
        String maLop = txtLopQL.getText().trim();

        if (mssv.isEmpty() || hoTen.isEmpty() || maLop.isEmpty()) {
            setStatus("Lỗi: Không được để trống MSSV, Họ tên và Mã lớp quản lý!", true);
            return;
        }
        
        try {
            SinhVien sv = new SinhVien();
            sv.setMssv(mssv);
            sv.setHoTen(hoTen);
            sv.setNgaySinh(df.parse(strNgaySinh));
            sv.setGioiTinh(gioiTinh);
            sv.setMaLopQuanLy(maLop);

            // Gọi Controller đẩy xuống SQL Server
            boolean success = controller.themSinhVien(sv);
            if (success) {
                model.addRow(new Object[]{mssv, hoTen, strNgaySinh, gioiTinh, maLop});
                setStatus("Đã thêm thành công sinh viên " + mssv + " vào cơ sở dữ liệu!", false);
                clearForm();
            } else {
                setStatus("Lỗi: Không thể lưu vào DB! Kiểm tra xem lớp '" + maLop + "' đã tồn tại chưa.", true);
            }
        } catch (java.text.ParseException ex) {
            setStatus("Lỗi định dạng ngày sinh! Vui lòng nhập chuẩn dạng yyyy-MM-dd.", true);
        }
    }

    /**
     * Nghiệp vụ 3: Bấm nút "Sửa" -> Cập nhật dữ liệu thật dưới Database
     */
    private void xuLySua() {
        int selectedRow = tblSinhVien.getSelectedRow();
        if (selectedRow == -1) {
            setStatus("Vui lòng chọn 1 sinh viên trên bảng để tiến hành sửa thông tin!", true);
            return;
        }

        String mssv = model.getValueAt(selectedRow, 0).toString();
        String hoTen = txtHoTen.getText().trim();
        String strNgaySinh = txtNgaySinh.getText().trim();
        String gioiTinh = cboGioiTinh.getSelectedItem().toString();
        String maLop = txtLopQL.getText().trim();

        if (hoTen.isEmpty() || maLop.isEmpty()) {
            setStatus("Lỗi: Họ tên và Mã lớp quản lý không được bỏ trống!", true);
            return;
        }

        try {
            SinhVien sv = new SinhVien();
            sv.setMssv(mssv); // MSSV giữ vai trò Khóa chính cố định
            sv.setHoTen(hoTen);
            sv.setNgaySinh(df.parse(strNgaySinh));
            sv.setGioiTinh(gioiTinh);
            sv.setMaLopQuanLy(maLop);

            // Gọi Controller xử lý truy vấn UPDATE
            boolean success = controller.suaSinhVien(sv);
            if (success) {
                model.setValueAt(hoTen, selectedRow, 1);
                model.setValueAt(strNgaySinh, selectedRow, 2);
                model.setValueAt(gioiTinh, selectedRow, 3);
                model.setValueAt(maLop, selectedRow, 4);

                setStatus("Đã cập nhật thay đổi hồ sơ sinh viên " + mssv + " xuống Database thành công.", false);
                clearForm();
            } else {
                setStatus("Lỗi hệ thống: Cập nhật thông tin thất bại!", true);
            }
        } catch (java.text.ParseException ex) {
            setStatus("Lỗi định dạng ngày sinh! Vui lòng nhập chuẩn dạng yyyy-MM-dd.", true);
        }
    }

    /**
     * Nghiệp vụ 4: Xóa sinh viên (Đồng bộ chạy Transaction chuỗi)
     */
    private void xuLyXoa() {
        int selectedRow = tblSinhVien.getSelectedRow();
        if (selectedRow == -1) {
            setStatus("Vui lòng chọn 1 dòng sinh viên trên bảng để xóa!", true);
            return;
        }

        String mssv = model.getValueAt(selectedRow, 0).toString();
        String maLop = model.getValueAt(selectedRow, 4).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Hành động này sẽ xóa vĩnh viễn hồ sơ sinh viên " + mssv + ".\nBạn có chắc chắn muốn tiếp tục?", 
                "Xác nhận xóa hồ sơ sinh viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.xoaSinhVien(mssv, maLop);
            if (success) {
                setStatus("Đã xóa sinh viên và cập nhật giảm sĩ số lớp thành công!", false);
                model.removeRow(selectedRow);
                clearForm();
            } else {
                setStatus("Lỗi hệ thống hoặc kết nối Database thất bại!", true);
            }
        }
    }

    private void clearForm() {
        txtMSSV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("2005-01-01");
        cboGioiTinh.setSelectedIndex(0);
        txtLopQL.setText("");
        txtMSSV.setEditable(true); 
    }

    public void setStatus(String message, boolean isError) {
        lblStatus.setText(" Trạng thái: " + message);
        lblStatus.setForeground(isError ? Color.RED : new Color(46, 125, 50));
    }
}