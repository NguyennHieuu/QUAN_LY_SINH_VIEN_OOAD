package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.nhom10.ooad.quanlysinhvien.Controller.HocPhiController;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;

/**
 * QuanLyThanhToanPanel - Tab 2: TRA CUU HOA DON (UC07.2) + CAP NHAT THANH TOAN (UC07.1).
 * Tren: thanh tim kiem/loc. Giua: bang hoa don. Duoi: form nop tien cho hoa don dang chon.
 */
public class QuanLyThanhToanPanel extends JPanel {

    private final HocPhiController controller = new HocPhiController();

    private final JTextField txtTuKhoa = new JTextField(14);
    private final JTextField txtHocKy  = new JTextField("HK20251", 8);
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

    private String maHoaDonChon = null;

    public QuanLyThanhToanPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(taoThanhTimKiem(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(taoFormThanhToan(), BorderLayout.SOUTH);

        // Khi chon 1 dong -> ghi nho ma hoa don + hien so con no
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                maHoaDonChon = (String) model.getValueAt(row, 0);
                String hoTen = (String) model.getValueAt(row, 2);
                String conNo = (String) model.getValueAt(row, 8);
                lblDangChon.setText("Hóa đơn " + maHoaDonChon + " | SV " + hoTen
                                  + " | Còn nợ: " + conNo);
            }
        });

        timHoaDon(); // nap san khi mo tab
    }

    private JPanel taoThanhTimKiem() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.add(new JLabel("Từ khóa (MSSV/Họ tên):"));
        p.add(txtTuKhoa);
        p.add(new JLabel("Học kỳ:"));
        p.add(txtHocKy);
        p.add(new JLabel("Trạng thái:"));
        p.add(cboTrangThai);
        JButton btnTim = new JButton("Tìm");
        p.add(btnTim);
        btnTim.addActionListener(e -> timHoaDon());
        return p;
    }

    private JPanel taoFormThanhToan() {
        JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Nộp tiền cho hóa đơn đang chọn"));

        p.add(lblDangChon);

        JPanel hang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hang.add(new JLabel("Số tiền nộp:"));
        hang.add(txtSoTien);
        hang.add(new JLabel("Hình thức:"));
        hang.add(cboHinhThuc);
        JButton btnNop = new JButton("Xác nhận nộp tiền");
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
        model.setRowCount(0);
        for (HoaDonView v : ds) {
            model.addRow(new Object[]{
                v.getMaHoaDon(), v.getMssv(), v.getHoTen(), v.getTenLop(), v.getMaHocKy(),
                v.getTongTinChi(), KeToanPanel.tien(v.getTongTienNop()),
                KeToanPanel.tien(v.getDaNop()), KeToanPanel.tien(v.getConNo()), v.getTrangThai()
            });
        }
        maHoaDonChon = null;
        lblDangChon.setText("Chưa chọn hóa đơn  (tìm thấy " + ds.size() + " hóa đơn)");
    }

    private void xuLyNopTien() {
        if (maHoaDonChon == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn 1 hóa đơn trong bảng trước.",
                    "Chưa chọn hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long soTien = KeToanPanel.doiTien(txtSoTien.getText());
        if (soTien <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền nộp không hợp lệ.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String hinhThuc = (String) cboHinhThuc.getSelectedItem();
        try {
            controller.thanhToan(maHoaDonChon, soTien, hinhThuc);
            JOptionPane.showMessageDialog(this,
                    "Đã ghi nhận nộp " + KeToanPanel.tien(soTien) + " cho hóa đơn " + maHoaDonChon,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            txtSoTien.setText("");
            timHoaDon(); // lam moi bang de cap nhat "Da nop / Con no / Trang thai"
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể thanh toán", JOptionPane.WARNING_MESSAGE);
        }
    }
}
