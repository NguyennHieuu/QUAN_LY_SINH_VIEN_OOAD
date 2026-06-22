package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font; 
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.nhom10.ooad.quanlysinhvien.Controller.HocPhiController;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;

/**
 * GiaoDienQuanLyThanhToan - Tab 2: TRA CUU HOA DON (UC07.2) + CAP NHAT THANH TOAN (UC07.1).
 * 🌟 ĐÃ CẬP NHẬT: Chuyển toàn bộ các phương thức static tiện ích (tien, doiTien) sang GiaoDienChinhKeToan.
 */
public class GiaoDienQuanLyThanhToan extends JPanel {

    private final HocPhiController controller = new HocPhiController();

    // KHỞI TẠO BIẾN FONT SỬ DỤNG CHUNG TOÀN CmatrixỦA TAB
    private final Font segoeFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font segoeBoldFont = new Font("Segoe UI", Font.BOLD, 14);

    private final JTextField txtTuKhoa = new JTextField(14);
    private final JTextField txtHocKy  = new JTextField("20252", 8); 
    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(new String[]{"Tất cả", "Chưa nộp", "Còn nợ", "Đã nộp"});

    private final String[] cols = {
        "Mã hóa đơn", "MSSV", "Họ tên", "Lớp", "Học kỳ",
        "Số TC", "Tổng phải nộp", "Đã nộp", "Còn nợ", "Trạng thái"
    };
    private final DefaultTableModel model = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final JLabel lblDangChon = new JLabel("Chưa chọn hóa đơn");
    private final JTextField txtSoTien = new JTextField(12);
    private final JComboBox<String> cboHinhThuc =
            new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});

    public GiaoDienQuanLyThanhToan() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Đặt font chữ cho Table (Bảng hiển thị hóa đơn) và Header của bảng
        table.setFont(segoeFont);
        table.setRowHeight(24); 
        table.getTableHeader().setFont(segoeBoldFont);

        add(taoThanhTimKiem(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(taoFormThanhToan(), BorderLayout.SOUTH);

        // Bọc điều kiện phòng chống lỗi IndexOutOfBounds khi làm sạch TableModel
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < model.getRowCount()) {
                    String maHoaDonChon = (String) model.getValueAt(row, 0);
                    String hoTen = (String) model.getValueAt(row, 2);
                    String conNo = (String) model.getValueAt(row, 8);
                    lblDangChon.setText("Hóa đơn: " + maHoaDonChon + " | SV: " + hoTen + " | Còn nợ: " + conNo);
                } else {
                    giaiPhongFormChon();
                }
            }
        });

        timHoaDon(); // nạp sẵn dữ liệu khi mở tab
    }

    private void giaiPhongFormChon() {
        lblDangChon.setText("Chưa chọn hóa đơn");
    }

    private JPanel taoThanhTimKiem() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        
        JLabel lblTk = new JLabel("Từ khóa (MSSV/Họ tên):");
        JLabel lblHk = new JLabel("Học kỳ:");
        JLabel lblTt = new JLabel("Trạng thái:");
        JButton btnTim = new JButton("Tìm");

        lblTk.setFont(segoeFont);
        txtTuKhoa.setFont(segoeFont);
        lblHk.setFont(segoeFont);
        txtHocKy.setFont(segoeFont);
        lblTt.setFont(segoeFont);
        cboTrangThai.setFont(segoeFont);
        btnTim.setFont(segoeBoldFont); 

        p.add(lblTk);
        p.add(txtTuKhoa);
        p.add(lblHk);
        p.add(txtHocKy);
        p.add(lblTt);
        p.add(cboTrangThai);
        p.add(btnTim);
        
        btnTim.addActionListener(e -> timHoaDon());
        return p;
    }

    private JPanel taoFormThanhToan() {
        JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        
        TitledBorder border = BorderFactory.createTitledBorder("Nộp tiền cho hóa đơn đang chọn");
        border.setTitleFont(segoeBoldFont);
        p.setBorder(border);

        lblDangChon.setFont(segoeFont);
        p.add(lblDangChon);

        JPanel hang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JLabel lblSt = new JLabel("Số tiền nộp:");
        JLabel lblHt = new JLabel("Hình thức:");
        JButton btnNop = new JButton("Xác nhận nộp tiền");

        lblSt.setFont(segoeFont);
        txtSoTien.setFont(segoeFont);
        lblHt.setFont(segoeFont);
        cboHinhThuc.setFont(segoeFont);
        btnNop.setFont(segoeBoldFont);

        hang.add(lblSt);
        hang.add(txtSoTien);
        hang.add(lblHt);
        hang.add(cboHinhThuc);
        hang.add(btnNop);
        
        btnNop.addActionListener(e -> xuLyNopTien());
        p.add(hang);
        return p;
    }

    private void timHoaDon() {
        String tuKhoa = txtTuKhoa.getText().trim();
        String hocKy = txtHocKy.getText().trim();
        String tt = (String) cboTrangThai.getSelectedItem();
        if ("Tất cả".equals(tt)) tt = "";

        List<HoaDonView> ds = controller.timHoaDon(tuKhoa, hocKy, tt);
        
        giaiPhongFormChon();
        model.setRowCount(0);
        
        for (HoaDonView v : ds) {
            // 🌟 ĐÃ SỬA: Chuyển toàn bộ lời gọi từ GiaoDienKeToan sang GiaoDienChinhKeToan
            model.addRow(new Object[]{
                v.getMaHoaDon(), v.getMssv(), v.getHoTen(), v.getTenLop(), v.getMaHocKy(),
                v.getTongTinChi(), GiaoDienChinhKeToan.tien(v.getTongTienNop()),
                GiaoDienChinhKeToan.tien(v.getDaNop()), GiaoDienChinhKeToan.tien(v.getConNo()), v.getTrangThai()
            });
        }
        
        lblDangChon.setText("Chưa chọn hóa đơn  (tìm thấy " + ds.size() + " hóa đơn)");

        // Tự động co giãn chiều rộng tối đa cho các cột để hiển thị chữ rõ nét hơn
        if (table.getColumnModel().getColumnCount() > 0) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.getColumnModel().getColumn(0).setPreferredWidth(100); 
            table.getColumnModel().getColumn(1).setPreferredWidth(100); 
            table.getColumnModel().getColumn(2).setPreferredWidth(185); 
            table.getColumnModel().getColumn(3).setPreferredWidth(100); 
            table.getColumnModel().getColumn(4).setPreferredWidth(85);  
            table.getColumnModel().getColumn(5).setPreferredWidth(75);  
            table.getColumnModel().getColumn(6).setPreferredWidth(120); 
            table.getColumnModel().getColumn(7).setPreferredWidth(110); 
            table.getColumnModel().getColumn(8).setPreferredWidth(110); 
            table.getColumnModel().getColumn(9).setPreferredWidth(105); 
        }
    }

    private void xuLyNopTien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn 1 hóa đơn trong bảng trước.",
                    "Chưa chọn hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maHoaDonChon = (String) model.getValueAt(selectedRow, 0);
        // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.doiTien
        long soTien = GiaoDienChinhKeToan.doiTien(txtSoTien.getText());
        if (soTien <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền nộp không hợp lệ. Vui lòng kiểm tra lại số ký tự nhập!",
                    "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String hinhThuc = (String) cboHinhThuc.getSelectedItem();
        try {
            controller.thanhToan(maHoaDonChon, soTien, hinhThuc);
            
            // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.tien
            JOptionPane.showMessageDialog(this,
                    "Đã ghi nhận nộp " + GiaoDienChinhKeToan.tien(soTien) + " cho hóa đơn " + maHoaDonChon,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            txtSoTien.setText("");
            timHoaDon();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể thanh toán", JOptionPane.WARNING_MESSAGE);
        }
    }
}