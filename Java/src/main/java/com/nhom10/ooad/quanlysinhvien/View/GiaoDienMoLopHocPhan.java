package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.QuanLyHoSoController;
import com.nhom10.ooad.quanlysinhvien.Model.LopHocPhan;
import com.nhom10.ooad.quanlysinhvien.Model.GiangVien;
import com.nhom10.ooad.quanlysinhvien.Model.HocPhan; 
import com.nhom10.ooad.quanlysinhvien.Model.LopHPView; 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiaoDienMoLopHocPhan extends JPanel {
    private final QuanLyHoSoController controller = new QuanLyHoSoController();

    // Các trường nhập liệu của Form mở lớp (Vùng WEST)
    private JTextField txtMaLopHP, txtMaHP, txtMaHK, txtSiSoGIOIHAN, txtLichHoc, txtTrongSoQT, txtTrongSoCK, txtMaGV; 
    private JButton btnMoLop, btnHuyBo, btnCapNhatBang; 
    
    // Các trường tra cứu nhanh (Vùng CENTER - Phía trên)
    private JTextField txtTimKiemNhanh;
    private JComboBox<String> cboLoaiTraCuu;
    private JButton btnTimKiemNhanh;
    private JTable tblTraCuuNhanh;
    private DefaultTableModel modelTraCuu;

    // Bảng hiển thị danh sách lớp HP đã mở (Vùng CENTER - Phía dưới)
    private JTable tblLopHPChinh;
    private DefaultTableModel modelLopHPChinh;
    
    private JLabel lblStatus;

    public GiaoDienMoLopHocPhan() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        Font commonFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // ====================================================================
        // VÙNG 1: BIỂU MẪU THIẾT LẬP THÔNG SỐ MỞ LỚP (WEST)
        // ====================================================================
        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 5, 12)); 
        pnlForm.setBorder(BorderFactory.createTitledBorder("Cấu hình Mở Lớp học phần mới"));

        txtMaLopHP = new JTextField(12); 
        txtMaHP = new JTextField(12);
        txtMaHK = new JTextField("20252", 12);
        txtSiSoGIOIHAN = new JTextField("60", 12);
        txtLichHoc = new JTextField("Thứ 2 (08:00 - 11:30)", 12);
        txtTrongSoQT = new JTextField("30", 12);
        txtTrongSoCK = new JTextField("70", 12);
        txtMaGV = new JTextField(12);

        JLabel[] labels = {
            new JLabel("Mã Lớp HP (*):"), new JLabel("Mã Học Phần (*):"), new JLabel("Mã Học Kỳ:"), 
            new JLabel("Giới hạn sĩ số:"), new JLabel("Lịch học:"), 
            new JLabel("Trọng số Quá trình (%):"), new JLabel("Trọng số Cuối kỳ (%):"), 
            new JLabel("Mã Giảng viên (*):")
        };
        JComponent[] inputs = {txtMaLopHP, txtMaHP, txtMaHK, txtSiSoGIOIHAN, txtLichHoc, txtTrongSoQT, txtTrongSoCK, txtMaGV};

        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(commonFont);
            inputs[i].setFont(commonFont);
            pnlForm.add(labels[i]);
            pnlForm.add(inputs[i]);
        }

        JPanel pnlWestWrapper = new JPanel(new BorderLayout());
        pnlWestWrapper.add(pnlForm, BorderLayout.NORTH);
        pnlWestWrapper.setPreferredSize(new Dimension(340, 0));
        add(pnlWestWrapper, BorderLayout.WEST);

        // ====================================================================
        // VÙNG 2: KHU VỰC THAO TÁC CHÍNH (CENTER) - CHIA LÀM 2 TẦNG
        // ====================================================================
        JPanel pnlCenterLayout = new JPanel(new GridLayout(2, 1, 0, 10));

        // --- TẦNG TRÊN: BỘ TRA CỨU NHANH ĐỘNG ---
        JPanel pnlTraCuuTichHop = new JPanel(new BorderLayout(5, 5));
        pnlTraCuuTichHop.setBorder(BorderFactory.createTitledBorder("Hộp tra cứu nhanh thông tin nguồn"));

        JPanel pnlSearchTopBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cboLoaiTraCuu = new JComboBox<>(new String[]{"Tìm Học Phần / CTĐT", "Tìm Giảng Viên Phân Công"});
        cboLoaiTraCuu.setFont(commonFont);
        txtTimKiemNhanh = new JTextField(15);
        txtTimKiemNhanh.setFont(commonFont);
        btnTimKiemNhanh = new JButton("Tìm nhanh");
        btnTimKiemNhanh.setFont(boldFont);
        
        pnlSearchTopBar.add(new JLabel("Bộ lọc:"));
        pnlSearchTopBar.add(cboLoaiTraCuu);
        pnlSearchTopBar.add(txtTimKiemNhanh);
        pnlSearchTopBar.add(btnTimKiemNhanh);
        pnlTraCuuTichHop.add(pnlSearchTopBar, BorderLayout.NORTH);

        modelTraCuu = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTraCuuNhanh = new JTable(modelTraCuu);
        tblTraCuuNhanh.setFont(commonFont);
        tblTraCuuNhanh.getTableHeader().setFont(boldFont);
        tblTraCuuNhanh.setRowHeight(24);
        pnlTraCuuTichHop.add(new JScrollPane(tblTraCuuNhanh), BorderLayout.CENTER);
        
        // --- TẦNG DƯỚI: DANH SÁCH LỚP HỌC PHẦN CHÍNH THỨC ĐÃ MỞ ---
        JPanel pnlLopHocPhanDaMo = new JPanel(new BorderLayout(5, 5));
        pnlLopHocPhanDaMo.setBorder(BorderFactory.createTitledBorder("Danh sách Lớp học phần đã khởi tạo thành công"));

        String[] colsLopHP = {"Mã Lớp HP", "Mã HP", "Học Kỳ", "Giới Hạn SV", "Lịch Học", "Trọng Số QT/CK", "Mã GV"};
        modelLopHPChinh = new DefaultTableModel(colsLopHP, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLopHPChinh = new JTable(modelLopHPChinh);
        tblLopHPChinh.setFont(commonFont);
        tblLopHPChinh.getTableHeader().setFont(boldFont);
        tblLopHPChinh.setRowHeight(24);
        pnlLopHocPhanDaMo.add(new JScrollPane(tblLopHPChinh), BorderLayout.CENTER);

        pnlCenterLayout.add(pnlTraCuuTichHop);
        pnlCenterLayout.add(pnlLopHocPhanDaMo);
        add(pnlCenterLayout, BorderLayout.CENTER);

        // ====================================================================
        // VÙNG 3: BỘ NÚT CHỨC NĂNG HÀNH ĐỘNG ĐỒNG BỘ & TRẠNG THÁI (SOUTH)
        // ====================================================================
        JPanel actionArea = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnMoLop = new JButton("Mở lớp");
        btnHuyBo = new JButton("Hủy/Xóa lớp"); 
        btnCapNhatBang = new JButton("🔄 Cập nhật bảng"); 

        Dimension btnSize = new Dimension(140, 35);
        for (JButton btn : new JButton[]{btnMoLop, btnHuyBo, btnCapNhatBang}) {
            btn.setFont(boldFont);
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        lblStatus = new JLabel("Hệ thống tích hợp nghiệp vụ Đào tạo và Mở lớp sẵn sàng.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        actionArea.add(lblStatus, BorderLayout.WEST);
        actionArea.add(buttonPanel, BorderLayout.EAST);
        add(actionArea, BorderLayout.SOUTH);

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LIÊN KẾT LOGIC NÂNG CAO
        // ====================================================================
        cboLoaiTraCuu.addActionListener(e -> xuLyThayDoiBoLoc());
        btnTimKiemNhanh.addActionListener(e -> xuLyTimKiemNhanh());
        btnMoLop.addActionListener(e -> xuLyMoLopHocPhan());
        btnHuyBo.addActionListener(e -> xuLyXoaLopHocPhan());
        btnCapNhatBang.addActionListener(e -> taiDanhSachLopHPDaMo()); 
        
        tblTraCuuNhanh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblTraCuuNhanh.getSelectedRow();
                if (row >= 0) {
                    String ma = modelTraCuu.getValueAt(row, 0).toString();
                    if (cboLoaiTraCuu.getSelectedIndex() == 1) {
                        txtMaGV.setText(ma); 
                    } else {
                        txtMaHP.setText(ma); 
                    }
                }
            }
        });

        tblLopHPChinh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblLopHPChinh.getSelectedRow();
                if (row >= 0) {
                    txtMaLopHP.setText(modelLopHPChinh.getValueAt(row, 0).toString());
                    txtMaHP.setText(modelLopHPChinh.getValueAt(row, 1).toString());
                    txtMaHK.setText(modelLopHPChinh.getValueAt(row, 2).toString());
                    txtSiSoGIOIHAN.setText(modelLopHPChinh.getValueAt(row, 3).toString());
                    txtLichHoc.setText(modelLopHPChinh.getValueAt(row, 4).toString());
                    txtMaGV.setText(modelLopHPChinh.getValueAt(row, 6).toString());
                }
            }
        });

        xuLyThayDoiBoLoc();
        taiDanhSachLopHPDaMo();
    }

    private void xuLyThayDoiBoLoc() {
        int index = cboLoaiTraCuu.getSelectedIndex();
        if (index == 0) {
            modelTraCuu.setColumnIdentifiers(new String[]{"Mã Học Phần", "Tên Học Phần Chi Tiết", "Số Tín Chỉ", "Loại Học Phần"});
        } else {
            modelTraCuu.setColumnIdentifiers(new String[]{"Mã Giảng Viên", "Tên Giảng Viên Cán Bộ", "Số Điện Thoại", "Đơn Vị Công Tác"});
        }
        xuLyTimKiemNhanh(); 
    }

    /**
     * 🌟 ĐÃ SỬA DỨT ĐIỂM: Khớp hoàn chỉnh với phương thức searchHocPhan() và các trường thực tế trong SQL.
     */
    private void xuLyTimKiemNhanh() {
        modelTraCuu.setRowCount(0);
        String keyword = txtTimKiemNhanh.getText().trim();
        int index = cboLoaiTraCuu.getSelectedIndex();

        if (index == 0) { 
            // Gọi chính xác phương thức từ file HocPhanDAO.java của bạn
            com.nhom10.ooad.quanlysinhvien.DAO.HocPhanDAO hpDAO = new com.nhom10.ooad.quanlysinhvien.DAO.HocPhanDAO();
            List<HocPhan> listHp = hpDAO.searchHocPhan(keyword); 
            
            for (HocPhan hp : listHp) {
                // Khớp chuẩn xác theo các phương thức Getter tương ứng cấu trúc bảng SQL của bạn
                modelTraCuu.addRow(new Object[]{
                    hp.getMaHP(), 
                    hp.getTenHP(), 
                    hp.getSoTC() + " TC", 
                    hp.getLoaiHP()
                });
            }
            hienThiThongBao("Đã đồng bộ thành công danh sách Học phần hiện hành từ Database.", false);
        } else { 
            com.nhom10.ooad.quanlysinhvien.DAO.GiangVienDAO gvDAO = new com.nhom10.ooad.quanlysinhvien.DAO.GiangVienDAO();
            List<GiangVien> listGv = gvDAO.search(keyword);
            for (GiangVien gv : listGv) {
                modelTraCuu.addRow(new Object[]{gv.getMaGV(), gv.getHoTenGV(), gv.getSdt(), gv.getDonViCongTac()});
            }
            hienThiThongBao("Đã tìm thấy " + listGv.size() + " cán bộ giảng viên sẵn sàng phân công.", false);
        }
    }

    private void taiDanhSachLopHPDaMo() {
        modelLopHPChinh.setRowCount(0);
        List<LopHPView> listLop = controller.getAllLopHocPhan(); 
        
        if (listLop != null && !listLop.isEmpty()) {
            for (LopHPView lhp : listLop) {
                modelLopHPChinh.addRow(new Object[]{
                    lhp.getMaLopHP(),
                    lhp.getMaHP(),
                    lhp.getMaHocKy(),
                    lhp.getSiSoToiDa(),
                    "Thứ 2 (08:00 - 11:30)", 
                    "30/70", 
                    lhp.getMaGV()
                });
            }
            hienThiThongBao("Hệ thống: Đã cập nhật và đồng bộ danh sách lớp học phần hiện hành từ SQL Server.", false);
        } else {
            hienThiThongBao("Hiện tại không có lớp học phần nào được khởi tạo dưới cơ sở dữ liệu.", false);
        }
    }

    private void xuLyMoLopHocPhan() {
        txtMaLopHP.setBackground(Color.WHITE);
        txtMaHP.setBackground(Color.WHITE);
        txtMaGV.setBackground(Color.WHITE);
        txtTrongSoQT.setBackground(Color.WHITE);
        txtTrongSoCK.setBackground(Color.WHITE);
        txtLichHoc.setBackground(Color.WHITE);

        String maLopHP = txtMaLopHP.getText().trim();
        String maHP = txtMaHP.getText().trim();
        String maHK = txtMaHK.getText().trim();
        String siSoMax = txtSiSoGIOIHAN.getText().trim();
        String lichHoc = txtLichHoc.getText().trim();
        String maGV = txtMaGV.getText().trim();

        if (maLopHP.isEmpty() || maHP.isEmpty() || maGV.isEmpty() || lichHoc.isEmpty()) {
            hienThiThongBao("LỖI VALIDATE: Vui lòng nhập đầy đủ Mã lớp HP, Mã môn, Lịch học và Mã GV!", true);
            if (maLopHP.isEmpty()) txtMaLopHP.setBackground(new Color(255, 204, 204));
            if (maHP.isEmpty()) txtMaHP.setBackground(new Color(255, 204, 204));
            if (maGV.isEmpty()) txtMaGV.setBackground(new Color(255, 204, 204));
            if (lichHoc.isEmpty()) txtLichHoc.setBackground(new Color(255, 204, 204));
            return;
        }

        try {
            int tsQT = Integer.parseInt(txtTrongSoQT.getText().trim());
            int tsCK = Integer.parseInt(txtTrongSoCK.getText().trim());

            if ((tsQT + tsCK) != 100) {
                hienThiThongBao("LỖI NGHIỆP VỤ: Tổng trọng số điểm Quá trình + Cuối kỳ bắt buộc phải bằng 100%!", true);
                txtTrongSoQT.setBackground(new Color(255, 204, 204));
                txtTrongSoCK.setBackground(new Color(255, 204, 204));
                return;
            }

            boolean biTrungLichTrenView = false;
            for (int r = 0; r < modelLopHPChinh.getRowCount(); r++) {
                String maLopHPOnTable = modelLopHPChinh.getValueAt(r, 0).toString();
                String hkOnTable = modelLopHPChinh.getValueAt(r, 2).toString();
                String lichOnTable = modelLopHPChinh.getValueAt(r, 4).toString();
                String gvOnTable = modelLopHPChinh.getValueAt(r, 6).toString();

                if (maHK.equals(hkOnTable) && maGV.equalsIgnoreCase(gvOnTable) && lichHoc.equalsIgnoreCase(lichOnTable)) {
                    if (!maLopHP.equalsIgnoreCase(maLopHPOnTable)) {
                        biTrungLichTrenView = true;
                        break;
                    }
                }
            }

            if (biTrungLichTrenView) {
                hienThiThongBao("CHẶN THAO TÁC: Giảng viên " + maGV + " đã dính lịch dạy vào khung giờ này rồi!", true);
                txtMaGV.setBackground(new Color(255, 204, 204));
                txtLichHoc.setBackground(new Color(255, 204, 204));
                JOptionPane.showMessageDialog(this, 
                        "Xung đột lịch trình: Giảng viên đã được phân công đứng lớp học phần khác\nvào đúng khung giờ " + lichHoc + " trong học kỳ " + maHK + "!", 
                        "Cảnh báo xung đột lịch dạy", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double weightQT = (double) tsQT / 100.0;
            double weightCK = (double) tsCK / 100.0;

            LopHocPhan lhp = new LopHocPhan(maLopHP, maHP, maHK, maGV, 0, Integer.parseInt(siSoMax), weightQT, weightCK);
            boolean success = controller.themLopHocPhan(lhp); 

            if (success) {
                hienThiThongBao("Thành công: Đã lưu vĩnh viễn và mở lớp học phần " + maLopHP + " xuống Database!", false);
                taiDanhSachLopHPDaMo(); 
                clearForm();
            } else {
                hienThiThongBao("Lỗi hệ thống: Trùng Mã lớp HP hoặc vi phạm ràng buộc dữ liệu nguồn!", true);
            }

        } catch (NumberFormatException ex) {
            hienThiThongBao("LỖI ĐỊNH DẠNG: Trọng số điểm và giới hạn sĩ số phải nhập ký tự số nguyên!", true);
        }
    }

    private void xuLyXoaLopHocPhan() {
        int selectedRow = tblLopHPChinh.getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một lớp học phần ở bảng danh sách phía dưới để hủy bỏ!", true);
            return;
        }

        String maLopHP = modelLopHPChinh.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Hành động sẽ hủy và xóa vĩnh viễn lớp học phần " + maLopHP + " khỏi hệ thống.\nBạn có chắc chắn muốn tiếp tục?", 
                "Xác nhận hủy lớp học phần", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.xoaLopHocPhan(maLopHP); 

            if (success) {
                hienThiThongBao("Hệ thống: Đã xóa hoàn toàn lớp học phần " + maLopHP + " khỏi cơ sở dữ liệu.", false);
                taiDanhSachLopHPDaMo(); 
                clearForm();
            } else {
                hienThiThongBao("Lỗi: Không thể xóa lớp (Lớp học phần đã tồn tại sinh viên ghi danh đăng ký)!", true);
            }
        }
    }

    public void hienThiThongBao(String msg, boolean laLoi) {
        lblStatus.setText(" Trạng thái: " + msg);
        lblStatus.setForeground(laLoi ? Color.RED : new Color(46, 125, 50));
    }

    private void clearForm() {
        txtMaLopHP.setText("");
        txtMaHP.setText("");
        txtMaGV.setText("");
        txtTrongSoQT.setText("30");
        txtTrongSoCK.setText("70");
        txtMaLopHP.setEditable(true);
    }
}