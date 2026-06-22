package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.QuanLyHoSoController;
import com.nhom10.ooad.quanlysinhvien.DAO.TaiKhoanDAO;
import com.nhom10.ooad.quanlysinhvien.Model.TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiaoDienQuanLyTaiKhoan extends JPanel {
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final QuanLyHoSoController controller = new QuanLyHoSoController(); 

    private JTable tblTaiKhoan;
    private DefaultTableModel model;
    private JComboBox<String> cboCheDoHienThi;
    private JTextField txtTimKiem; 
    private JButton btnQuetDuLieu, btnTimKiem, btnResetPassword, btnToggleStatus; // 🌟 ĐÃ XÓA: btnCapTaiKhoan
    private JLabel lblStatus;

    public GiaoDienQuanLyTaiKhoan() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        
        Font commonFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // --- 1. VÙNG ĐIỀU HƯỚNG & LỌC TRA CỨU ĐA NĂNG (NORTH) ---
        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlTitle.setBorder(BorderFactory.createTitledBorder("Bộ lọc phân quyền hệ thống"));
        
        JLabel lblGoc = new JLabel("Chế độ xem:");
        lblGoc.setFont(commonFont);
        
        // 🌟 ĐÃ SỬA: Chỉ giữ lại chế độ xem tài khoản hiện hành thực tế
        cboCheDoHienThi = new JComboBox<>(new String[]{
            "Danh sách toàn bộ tài khoản hiện hành trong hệ thống"
        });
        cboCheDoHienThi.setFont(commonFont);
        
        btnQuetDuLieu = new JButton("Quét hệ thống");
        btnQuetDuLieu.setFont(boldFont);
        btnQuetDuLieu.setPreferredSize(new Dimension(140, 32));

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(commonFont);
        txtTimKiem = new JTextField(15);
        txtTimKiem.setFont(commonFont);
        
        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setFont(boldFont);
        btnTimKiem.setPreferredSize(new Dimension(80, 32));
        
        pnlTitle.add(lblGoc);
        pnlTitle.add(cboCheDoHienThi);
        pnlTitle.add(btnQuetDuLieu);
        pnlTitle.add(new JSeparator(SwingConstants.VERTICAL)); 
        pnlTitle.add(lblTimKiem);
        pnlTitle.add(txtTimKiem);
        pnlTitle.add(btnTimKiem);
        
        add(pnlTitle, BorderLayout.NORTH);

        // --- 2. VÙNG LÀM VIỆC CHÍNH (CENTER) ---
        String[] columns = {"Mã Định Danh / Tên Đăng Nhập", "Vai Trò Người Dùng", "Lớp / Đơn vị (Nếu có)", "Trạng Thái Tài Khoản"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTaiKhoan = new JTable(model);
        tblTaiKhoan.setFont(commonFont);
        tblTaiKhoan.getTableHeader().setFont(boldFont);
        tblTaiKhoan.setRowHeight(26);
        add(new JScrollPane(tblTaiKhoan), BorderLayout.CENTER);

        // --- 3. VÙNG THÀNH PHẦN ĐIỀU KHIỂN ĐỒNG BỘ NÚT BẤM (SOUTH) ---
        JPanel actionArea = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnResetPassword = new JButton("🔄 Reset mật khẩu");
        btnToggleStatus = new JButton("🔒 Khóa / Mở khóa");

        Dimension btnSize = new Dimension(160, 35);
        for (JButton btn : new JButton[]{btnResetPassword, btnToggleStatus}) {
            btn.setFont(boldFont);
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        lblStatus = new JLabel("Hệ thống bảo mật tài khoản sẵn sàng.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        actionArea.add(lblStatus, BorderLayout.WEST);
        actionArea.add(buttonPanel, BorderLayout.EAST);
        add(actionArea, BorderLayout.SOUTH);

        // --- ĐĂNG KÝ SỰ KIỆN XỬ LÝ ACTION THÀNH CÔNG ---
        btnQuetDuLieu.addActionListener(e -> xuLyQuetHeThong());
        btnTimKiem.addActionListener(e -> xuLyTimKiem()); 
        btnResetPassword.addActionListener(e -> xuLyResetMatKhau());
        btnToggleStatus.addActionListener(e -> xuLyToggleStatus());

        // Thực hiện tự động tải dữ liệu thật lên bảng ngay khi mở giao diện
        xuLyQuetHeThong();
    }

    /**
     * Nghiệp vụ 1: Làm mới danh sách tài khoản thật từ Database
     */
    private void xuLyQuetHeThong() {
        model.setRowCount(0);
        txtTimKiem.setText(""); 
        napToanBoTaiKhoanThat("");
    }

    /**
     * Nghiệp vụ 2: Xử lý tìm kiếm tài khoản từ thanh JTextField qua SQL Server
     */
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        model.setRowCount(0);
        napToanBoTaiKhoanThat(keyword);
    }

    /**
     * Hàm phụ trợ nạp danh sách tài khoản thật từ bảng TaiKhoan qua Controller
     */
    private void napToanBoTaiKhoanThat(String keyword) {
        List<TaiKhoan> listTk = controller.timKiemTaiKhoan(keyword);
        if (listTk.isEmpty()) {
            hienThiStatus("Không tìm thấy dữ liệu tài khoản nào khớp với từ khóa: " + keyword, true);
        } else {
            for (TaiKhoan tk : listTk) {
                model.addRow(new Object[]{
                    tk.getTenDangNhap(),
                    tk.getVaiTro(),
                    "Hệ thống", 
                    tk.getTrangThaiHoatDong() == 1 ? "Đang hoạt động" : "Bị Khóa"
                });
            }
            hienThiStatus("Hiển thị danh sách kết quả tra cứu tài khoản thật từ hệ thống cơ sở dữ liệu.", false);
        }
    }

    /**
     * Nghiệp vụ 3: Đổi mật khẩu về mặc định "123456"
     */
    private void xuLyResetMatKhau() {
        int row = tblTaiKhoan.getSelectedRow();
        if (row == -1) {
            hienThiStatus("Lỗi: Vui lòng chọn một hàng tài khoản hiện hành để thực hiện Reset!", true);
            return;
        }

        String username = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn Reset mật khẩu của tài khoản '" + username + "' về '123456' không?", "Xác nhận Reset mật khẩu", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = taiKhoanDAO.resetMatKhau(username);
            if (success) {
                hienThiStatus("Thành công: Đã khôi phục mật khẩu tài khoản '" + username + "' về mặc định vĩnh viễn.", false);
            } else {
                hienThiStatus("Lỗi: Không thể thực thi yêu cầu Reset trên Database!", true);
            }
        }
    }

    /**
     * Nghiệp vụ 4: Khóa/Mở khóa tài khoản đồng bộ cập nhật trường TrangThaiHoatDong
     */
    private void xuLyToggleStatus() {
        int row = tblTaiKhoan.getSelectedRow();
        if (row == -1) {
            hienThiStatus("Lỗi: Vui lòng chọn một dòng tài khoản trên bảng để Khóa/Mở khóa!", true);
            return;
        }

        String currentStatus = model.getValueAt(row, 3).toString();
        String username = model.getValueAt(row, 0).toString();
        int trangThaiMoi = "Đang hoạt động".equals(currentStatus) ? 0 : 1;
        String strTrangThaiMoi = (trangThaiMoi == 1) ? "Đang hoạt động" : "Bị Khóa";

        boolean success = taiKhoanDAO.capNhatTrangThai(username, trangThaiMoi);
        if (success) {
            model.setValueAt(strTrangThaiMoi, row, 3);
            hienThiStatus("Thành công: Đã chuyển đổi quyền hoạt động tài khoản '" + username + "' sang dạng: " + strTrangThaiMoi, false);
        } else {
            hienThiStatus("Lỗi: Cập nhật trạng thái bảo mật thất bại!", true);
        }
    }

    private void hienThiStatus(String msg, boolean laLoi) {
        lblStatus.setText(" Trạng thái: " + msg);
        lblStatus.setForeground(laLoi ? Color.RED : new Color(46, 125, 50));
    }
}