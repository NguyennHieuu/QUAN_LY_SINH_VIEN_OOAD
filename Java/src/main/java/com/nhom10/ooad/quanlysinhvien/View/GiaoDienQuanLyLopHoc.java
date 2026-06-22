package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.QuanLyHoSoController;
import com.nhom10.ooad.quanlysinhvien.Model.LopHoc;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GiaoDienQuanLyLopHoc extends JPanel {
    private final QuanLyHoSoController controller = new QuanLyHoSoController();

    private JTable tblLopHoc;
    private DefaultTableModel model;
    private JTextField txtMaLop, txtTenLop, txtMaGV, txtSiSo, txtTimKiem; 
    private JButton btnThem, btnSua, btnXoa, btnTimKiem; 
    private JLabel lblStatus;

    public GiaoDienQuanLyLopHoc() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        
        Font commonFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // --- 1. THANH TÌM KIẾM ĐƯỢC TÍCH HỢP MỚI (NORTH) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Công cụ tra cứu lớp học"));
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(commonFont);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(boldFont);
        
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        add(searchPanel, BorderLayout.NORTH);

        // --- 2. FORM NHẬP LIỆU CẤU HÌNH LỚP HÀNH CHÍNH (WEST) ---
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 5, 15));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Cấu hình Lớp hành chính"));
        
        txtMaLop = new JTextField(12);
        txtTenLop = new JTextField(12);
        txtMaGV = new JTextField(12);
        txtSiSo = new JTextField("0", 12);
        txtSiSo.setEditable(false); 

        JLabel[] labels = {new JLabel("Mã lớp:"), new JLabel("Tên lớp:"), new JLabel("Mã GV quản lý:"), new JLabel("Sĩ số lớp:")};
        JComponent[] inputs = {txtMaLop, txtTenLop, txtMaGV, txtSiSo};
        
        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(commonFont);
            inputs[i].setFont(commonFont);
            pnlForm.add(labels[i]);
            pnlForm.add(inputs[i]);
        }
        
        JPanel pnlWestWrapper = new JPanel(new BorderLayout());
        pnlWestWrapper.add(pnlForm, BorderLayout.NORTH);
        add(pnlWestWrapper, BorderLayout.WEST);

        // --- 3. LƯỚI DANH SÁCH LỚP HỌC (CENTER) ---
        String[] columns = {"Mã Lớp", "Tên Lớp", "Sĩ Số", "Mã GV Quản Lý"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLopHoc = new JTable(model);
        tblLopHoc.setFont(commonFont);
        tblLopHoc.getTableHeader().setFont(boldFont);
        tblLopHoc.setRowHeight(25);
        add(new JScrollPane(tblLopHoc), BorderLayout.CENTER);

        // --- 4. ĐIỀU KHIỂN & CẢNH BÁO (SOUTH) ---
        JPanel actionArea = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnThem = new JButton("Thêm lớp");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa lớp");

        Dimension btnSize = new Dimension(120, 35);
        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa}) {
            btn.setFont(boldFont);
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        lblStatus = new JLabel("Hệ thống quản lý lớp học hành chính.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        actionArea.add(lblStatus, BorderLayout.WEST);
        actionArea.add(buttonPanel, BorderLayout.EAST);
        add(actionArea, BorderLayout.SOUTH);

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LIÊN KẾT ĐỒNG BỘ (ACTION LISTENERS)
        // ====================================================================
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnThem.addActionListener(e -> xuLyThemLop());
        btnSua.addActionListener(e -> xuLySuaLop());
        btnXoa.addActionListener(e -> xuLyXoaLop());

        // 🌟 SỰ KIỆN CLICK DÒNG BẢNG
        tblLopHoc.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblLopHoc.getSelectedRow();
                if (selectedRow >= 0) {
                    txtMaLop.setText(model.getValueAt(selectedRow, 0).toString());
                    txtTenLop.setText(model.getValueAt(selectedRow, 1).toString());
                    txtSiSo.setText(model.getValueAt(selectedRow, 2).toString());
                    txtMaGV.setText(model.getValueAt(selectedRow, 3) != null ? model.getValueAt(selectedRow, 3).toString() : "");
                    txtMaLop.setEditable(false);
                }
            }
        });

        // 🌟 TỰ ĐỘNG TẢI TOÀN BỘ LỚP KHI BẬT APP ĐỂ TRÁNH TRỐNG BẢNG BAN ĐẦU
        SwingUtilities.invokeLater(() -> xuLyTimKiem());
    }

    /**
     * Nghiệp vụ 1: Tìm kiếm / Tra cứu lớp học từ SQL Server
     */
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        model.setRowCount(0); 

        java.util.List<LopHoc> danhSach = controller.timKiemLopHoc(keyword);

        if (danhSach == null || danhSach.isEmpty()) {
            hienThiThongBao("Không tìm thấy lớp học nào phù hợp với từ khóa: " + keyword, true);
        } else {
            for (LopHoc lh : danhSach) {
                model.addRow(new Object[]{
                    lh.getMaLopQuanLy(),
                    lh.getTenLop(),
                    lh.getSiSo(),
                    lh.getMaGV()
                });
            }
            hienThiThongBao("Đã tìm thấy " + danhSach.size() + " lớp học hành chính trong hệ thống.", false);
        }
        
        // Form chỉ được dọn dẹp sạch sẽ sau khi bảng đã nạp xong xuôi dữ liệu vật lý
        txtMaLop.setText("");
        txtTenLop.setText("");
        txtMaGV.setText("");
        txtSiSo.setText("0");
        txtMaLop.setEditable(true);
    }

    /**
     * Nghiệp vụ 2: Thêm lớp học mới vĩnh viễn vào Database
     */
    private void xuLyThemLop() {
        String maLop = txtMaLop.getText().trim();
        String tenLop = txtTenLop.getText().trim();
        String maGv = txtMaGV.getText().trim();

        if (maLop.isEmpty() || tenLop.isEmpty()) {
            hienThiThongBao("Lỗi: Không được để trống Mã lớp và Tên lớp học!", true);
            return;
        }

        LopHoc lh = new LopHoc();
        lh.setMaLopQuanLy(maLop);
        lh.setTenLop(tenLop);
        lh.setSiSo(0); 
        lh.setMaGV(maGv.isEmpty() ? null : maGv);

        boolean success = controller.themLopHoc(lh);
        if (success) {
            hienThiThongBao("Thành công: Đã thêm lớp học " + maLop + " vào Database!", false);
            xuLyTimKiem(); // Gọi tìm kiếm lại để đồng bộ tải mới dữ liệu từ ổ cứng lên
        } else {
            hienThiThongBao("Lỗi: Thêm lớp thất bại! Mã lớp có thể đã tồn tại hoặc sai mã giáo viên.", true);
        }
    }

    /**
     * Nghiệp vụ 3: Sửa thông tin lớp học dưới DB
     */
    private void xuLySuaLop() {
        int selectedRow = tblLopHoc.getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn 1 lớp học hành chính trên bảng để sửa!", true);
            return;
        }

        String maLop = model.getValueAt(selectedRow, 0).toString();
        String tenLop = txtTenLop.getText().trim();
        String maGv = txtMaGV.getText().trim();
        int siSo = Integer.parseInt(txtSiSo.getText().trim());

        if (tenLop.isEmpty()) {
            hienThiThongBao("Lỗi: Không được để trống Tên lớp học!", true);
            return;
        }

        LopHoc lh = new LopHoc();
        lh.setMaLopQuanLy(maLop);
        lh.setTenLop(tenLop);
        lh.setSiSo(siSo);
        lh.setMaGV(maGv.isEmpty() ? null : maGv);

        boolean success = controller.suaLopHoc(lh);
        if (success) {
            hienThiThongBao("Thành công: Đã cập nhật thông tin thay đổi của lớp " + maLop + "!", false);
            xuLyTimKiem(); 
        } else {
            hienThiThongBao("Lỗi hệ thống: Cập nhật thông tin lớp học thất bại!", true);
        }
    }

    /**
     * Nghiệp vụ 4: Xóa lớp hành chính (Chặn tuyệt đối nếu còn sinh viên)
     */
    private void xuLyXoaLop() {
        int selectedRow = tblLopHoc.getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một lớp học hành chính trên bảng để xóa!", true);
            return;
        }

        String maLop = model.getValueAt(selectedRow, 0).toString();
        int siSo = Integer.parseInt(model.getValueAt(selectedRow, 2).toString());

        if (siSo > 0) {
            hienThiThongBao("CẢNH BÁO ĐỎ: Chặn hành động xóa lớp do sĩ số hiện tại > 0!", true);
            JOptionPane.showMessageDialog(this, 
                    "Lỗi nghiệp vụ: Không được phép xóa lớp hành chính đang tồn tại sinh viên (" + siSo + " SV).\nVui lòng chuyển lớp hoặc xóa hết hồ sơ sinh viên thuộc lớp trước!", 
                    "Hệ thống bảo mật chặn thao tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa lớp hành chính trống " + maLop + " không?", "Xác nhận xóa lớp", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String res = controller.xoaLopHoc(maLop);
            if ("OK".equals(res)) {
                hienThiThongBao("Đã xóa lớp học hành chính thành công khỏi hệ thống vĩnh viễn.", false);
                xuLyTimKiem();
            } else {
                hienThiThongBao(res, true);
            }
        }
    }

    /**
     * 🌟 ĐÃ KHÔI PHỤC: Hàm hiển thị thông báo trạng thái dưới chân UI
     */
    public void hienThiThongBao(String msg, boolean laLoi) {
        lblStatus.setText(" Trạng thái: " + msg);
        lblStatus.setForeground(laLoi ? Color.RED : new Color(46, 125, 50)); 
    }

    private void clearForm() {
        txtMaLop.setText("");
        txtTenLop.setText("");
        txtMaGV.setText("");
        txtSiSo.setText("0");
        txtMaLop.setEditable(true);
    }
}