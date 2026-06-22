package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.DangKyHocController;
import com.nhom10.ooad.quanlysinhvien.Model.LopHPView; // 🌟 THÊM IMPORT: Sử dụng lớp DTO mới gộp dữ liệu
import com.nhom10.ooad.quanlysinhvien.Model.LopHocPhan;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class GiaoDienDangKyHoc extends JPanel {

    private final DangKyHocController controller = new DangKyHocController();
    private final String mssvHienTai; 
    private final String maHocKyHienTai = "20252"; 

    private JTextField txtTimKiem;
    private JComboBox<String> cbKhoaVien;
    private JButton btnTimKiem, btnDangKy, btnHuyDangKy;
    private JTable tblMonMo, tblMonDaDK;
    private DefaultTableModel modelMonMo, modelMonDaDK;
    private JLabel lblTongSoTC, lblMessage;

    public GiaoDienDangKyHoc(String mssv) {
        this.mssvHienTai = mssv;
        initComponent();
        loadDataMonMonAndResetSelection(); // Cập nhật dữ liệu ban đầu
    }

    private void initComponent() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setBackground(Color.WHITE);

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 16);

        // ====================================================================
        // 1. KHU VỰC TÌM KIẾM (NORTH)
        // ====================================================================
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Bộ lọc tìm kiếm", TitledBorder.LEFT, TitledBorder.TOP, boldFont));

        JLabel lblTimKiem = new JLabel("Từ khóa:");
        lblTimKiem.setFont(mainFont);
        txtTimKiem = new JTextField(18);
        txtTimKiem.setFont(mainFont);

        JLabel lblKhoa = new JLabel("Khoa/Viện:");
        lblKhoa.setFont(mainFont);
        cbKhoaVien = new JComboBox<>(new String[]{"-- Tất cả --", "Điện - Điện tử", "Công nghệ thông tin"});
        cbKhoaVien.setFont(mainFont);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(boldFont);
        btnTimKiem.setPreferredSize(new Dimension(110, 30));

        pnlSearch.add(lblTimKiem);
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(lblKhoa);
        pnlSearch.add(cbKhoaVien);
        pnlSearch.add(btnTimKiem);
        this.add(pnlSearch, BorderLayout.NORTH);

        // ====================================================================
        // 2. KHU VỰC BẢNG HIỂN THỊ DỮ LIỆU (CENTER)
        // ====================================================================
        JPanel pnlCenter = new JPanel(new GridLayout(2, 1, 0, 15));
        pnlCenter.setBackground(Color.WHITE);

        // 2.1 Bảng 1: Danh sách học phần mở
        JPanel pnlMonMo = new JPanel(new BorderLayout());
        pnlMonMo.setBackground(Color.WHITE);
        JLabel lblTitleMonMo = new JLabel("DANH SÁCH HỌC PHẦN ĐANG MỞ TRONG KỲ");
        lblTitleMonMo.setFont(titleFont);
        lblTitleMonMo.setBorder(new EmptyBorder(0, 0, 5, 0));
        pnlMonMo.add(lblTitleMonMo, BorderLayout.NORTH);

        String[] colsMonMo = {"Mã lớp HP", "Mã học phần", "Tên học phần", "Học kỳ", "Mã giảng viên", "Sĩ số hiện tại", "Sĩ số tối đa"};
        modelMonMo = new DefaultTableModel(colsMonMo, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; } 
        };
        tblMonMo = new JTable(modelMonMo);
        tblMonMo.setFont(mainFont);
        tblMonMo.setRowHeight(25);
        tblMonMo.getTableHeader().setFont(boldFont);
        pnlMonMo.add(new JScrollPane(tblMonMo), BorderLayout.CENTER);
        pnlCenter.add(pnlMonMo);

        // 2.2 Bảng 2: Danh sách môn đã đăng ký
        JPanel pnlMonDaDK = new JPanel(new BorderLayout());
        pnlMonDaDK.setBackground(Color.WHITE);
        JLabel lblTitleMonDaDK = new JLabel("DANH SÁCH MÔN HỌC BẠN ĐÃ ĐĂNG KÝ");
        lblTitleMonDaDK.setFont(titleFont);
        lblTitleMonDaDK.setBorder(new EmptyBorder(0, 0, 5, 0));
        pnlMonDaDK.add(lblTitleMonDaDK, BorderLayout.NORTH);

        String[] colsMonDaDK = {"Mã lớp HP", "Mã học phần", "Tên học phần"}; 
        modelMonDaDK = new DefaultTableModel(colsMonDaDK, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMonDaDK = new JTable(modelMonDaDK);
        tblMonDaDK.setFont(mainFont);
        tblMonDaDK.setRowHeight(25);
        tblMonDaDK.getTableHeader().setFont(boldFont);
        pnlMonDaDK.add(new JScrollPane(tblMonDaDK), BorderLayout.CENTER);
        pnlCenter.add(pnlMonDaDK);

        this.add(pnlCenter, BorderLayout.CENTER);

        // ====================================================================
        // 3. KHU VỰC NÚT BẤM VÀ THÔNG BÁO (SOUTH)
        // ====================================================================
        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setBackground(Color.WHITE);

        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlStatus.setBackground(Color.WHITE);
        lblTongSoTC = new JLabel("Tổng số tín chỉ hiện tại: 0");
        lblTongSoTC.setFont(boldFont);
        lblMessage = new JLabel("");
        lblMessage.setFont(boldFont);
        pnlStatus.add(lblTongSoTC);
        pnlStatus.add(new JLabel("  |  "));
        pnlStatus.add(lblMessage);
        pnlSouth.add(pnlStatus, BorderLayout.WEST);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        pnlButtons.setBackground(Color.WHITE);
        
        // 🌟 ĐÃ CẬP NHẬT FONT CHỮ & TRẠNG THÁI KHỞI TẠO NÚT ĐĂNG KÝ MÔN
        btnDangKy = new JButton("Đăng ký môn");
        btnDangKy.setFont(boldFont); // Đảm bảo Font Segoe UI BOLD
        btnDangKy.setPreferredSize(new Dimension(140, 35));
        btnDangKy.setBackground(new Color(0, 122, 255)); 
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setEnabled(false); // Khởi tạo ở trạng thái mờ (Disabled)

        // 🌟 ĐÃ CẬP NHẬT FONT CHỮ & TRẠNG THÁI KHỞI TẠO NÚT HỦY ĐĂNG KÝ
        btnHuyDangKy = new JButton("Hủy đăng ký");
        btnHuyDangKy.setFont(boldFont); // Đảm bảo Font Segoe UI BOLD
        btnHuyDangKy.setPreferredSize(new Dimension(140, 35));
        btnHuyDangKy.setBackground(new Color(255, 59, 48)); 
        btnHuyDangKy.setForeground(Color.WHITE);
        btnHuyDangKy.setEnabled(false); // Khởi tạo ở trạng thái mờ (Disabled)

        pnlButtons.add(btnDangKy);
        pnlButtons.add(btnHuyDangKy);
        pnlSouth.add(pnlButtons, BorderLayout.EAST);

        this.add(pnlSouth, BorderLayout.SOUTH);

        // ====================================================================
        // GẮN SỰ KIỆN LOGIC VÀ THEO DÕI SỰ KIỆN CLICK BẢNG (LISTENERS)
        // ====================================================================
        
        // 🌟 ĐÃ THÊM: Theo dõi sự kiện click trên bảng môn học đang mở để kích hoạt nút Đăng ký môn
        tblMonMo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tblMonMo.getSelectedRow() != -1;
                btnDangKy.setEnabled(hasSelection);
            }
        });

        // 🌟 ĐÃ THÊM: Theo dõi sự kiện click trên bảng môn đã đăng ký để kích hoạt nút Hủy đăng ký
        tblMonDaDK.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tblMonDaDK.getSelectedRow() != -1;
                btnHuyDangKy.setEnabled(hasSelection);
            }
        });
        
        btnTimKiem.addActionListener(e -> {
            loadDataMonMonAndResetSelection();
            showMsg("Đã cập nhật danh sách lọc tìm kiếm thành công!", true);
        });

        btnDangKy.addActionListener(e -> {
            int selectedRow = tblMonMo.getSelectedRow();
            if (selectedRow == -1) {
                showMsg("Vui lòng chọn một lớp học phần ở bảng trên để đăng ký!", false);
                return;
            }
            
            String maLopHP = tblMonMo.getValueAt(selectedRow, 0).toString();
            LopHocPhan targetLhp = new com.nhom10.ooad.quanlysinhvien.DAO.LopHocPhanDAO().getByID(maLopHP);
            
            if (targetLhp != null) {
                String checkMsg = controller.kiemTraDieuKienDangKy(mssvHienTai, targetLhp);
                if (checkMsg.equals("OK")) {
                    boolean success = controller.dangKyMon(mssvHienTai, targetLhp);
                    if (success) {
                        showMsg("Đăng ký học phần thành công!", true);
                        loadDataMonMonAndResetSelection(); // Tải lại dữ liệu và reset mờ nút
                    } else {
                        showMsg("Lỗi hệ thống: Giao dịch đăng ký thất bại!", false);
                    }
                } else {
                    showMsg(checkMsg, false);
                }
            }
        });

        btnHuyDangKy.addActionListener(e -> {
            int selectedRow = tblMonDaDK.getSelectedRow();
            if (selectedRow == -1) {
                showMsg("Vui lòng chọn một môn học ở bảng dưới để hủy đăng ký!", false);
                return;
            }
            
            String maLopHP = tblMonDaDK.getValueAt(selectedRow, 0).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn rút khỏi lớp học phần " + maLopHP + " không?", 
                    "Xác nhận hủy môn học", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.huyMon(mssvHienTai, maLopHP);
                if (success) {
                    showMsg("Đã hủy đăng ký học phần thành công!", true);
                    loadDataMonMonAndResetSelection(); // Tải lại dữ liệu và reset mờ nút
                } else {
                    showMsg("Lỗi: Không thể hủy đăng ký (Có thể đã hết thời hạn đóng cổng)!", false);
                }
            }
        });
    }

    // 🌟 ĐÃ CẬP NHẬT: Hàm gộp để vừa load lại data, vừa clear trạng thái chọn dòng cũ để làm mờ nút an toàn
    private void loadDataMonMonAndResetSelection() {
        loadDataMonMo();
        loadDataMonDaDK();
        tblMonMo.clearSelection();
        tblMonDaDK.clearSelection();
        btnDangKy.setEnabled(false);
        btnHuyDangKy.setEnabled(false);
    }

    private void loadDataMonMo() {
        modelMonMo.setRowCount(0);
    
        String tuKhoa = txtTimKiem.getText();
        String khoaVienSelected = cbKhoaVien.getSelectedItem().toString();
    
        List<LopHPView> list = controller.getDanhSachHocPhanLoc(maHocKyHienTai, tuKhoa, khoaVienSelected);
        for (LopHPView lhp : list) {
            modelMonMo.addRow(new Object[]{
                lhp.getMaLopHP(), lhp.getMaHP(), lhp.getTenHP(), lhp.getMaHocKy(), 
                lhp.getMaGV(), lhp.getSiSoHienTai(), lhp.getSiSoToiDa()
            });
        }

        if (tblMonMo.getColumnModel().getColumnCount() > 0) {
            tblMonMo.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tblMonMo.getColumnModel().getColumn(0).setPreferredWidth(120); 
            tblMonMo.getColumnModel().getColumn(1).setPreferredWidth(90);  
            tblMonMo.getColumnModel().getColumn(2).setPreferredWidth(320); 
            tblMonMo.getColumnModel().getColumn(3).setPreferredWidth(70);  
            tblMonMo.getColumnModel().getColumn(4).setPreferredWidth(100); 
            tblMonMo.getColumnModel().getColumn(5).setPreferredWidth(95);  
            tblMonMo.getColumnModel().getColumn(6).setPreferredWidth(95);  
        }
    }

    private void loadDataMonDaDK() {
        modelMonDaDK.setRowCount(0);
        
        List<LopHPView> listDaDK = controller.getDanhSachMonDaDK(mssvHienTai, maHocKyHienTai);
        int totalTC = 0;
        
        for (LopHPView lhp : listDaDK) {
            modelMonDaDK.addRow(new Object[]{lhp.getMaLopHP(), lhp.getMaHP(), lhp.getTenHP()});
            totalTC += lhp.getSoTC(); 
        }
        lblTongSoTC.setText("Tổng số tín chỉ hiện tại: " + totalTC);

        if (tblMonDaDK.getColumnModel().getColumnCount() > 0) {
            tblMonDaDK.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tblMonDaDK.getColumnModel().getColumn(0).setPreferredWidth(140); 
            tblMonDaDK.getColumnModel().getColumn(1).setPreferredWidth(110); 
            tblMonDaDK.getColumnModel().getColumn(2).setPreferredWidth(550); 
        }
    }

    private void showMsg(String text, boolean isSuccess) {
        lblMessage.setText(text);
        lblMessage.setForeground(isSuccess ? new Color(40, 167, 69) : Color.RED);
    }
}