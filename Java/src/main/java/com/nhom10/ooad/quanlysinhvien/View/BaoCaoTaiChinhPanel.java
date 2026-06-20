package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.nhom10.ooad.quanlysinhvien.Controller.BaoCaoController;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.ThongKeDoanhThu;
import com.nhom10.ooad.quanlysinhvien.Util.CsvExporter;

/**
 * BaoCaoTaiChinhPanel - Tab 3: BAO CAO TAI CHINH (UC08.2) + XUAT FILE (UC08.3).
 * Chon loai bao cao -> Xem -> (tuy chon) Xuat CSV mo bang Excel.
 */
public class BaoCaoTaiChinhPanel extends JPanel {

    private final BaoCaoController controller = new BaoCaoController();

    private final JComboBox<String> cboLoai = new JComboBox<>(new String[]{
        "Doanh thu theo học kỳ", "Doanh thu tất cả học kỳ", "Danh sách công nợ"
    });
    private final JTextField txtHocKy = new JTextField("HK20251", 8);

    private final DefaultTableModel model = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblTongKet = new JLabel(" ");

    public BaoCaoTaiChinhPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Loại báo cáo:"));
        top.add(cboLoai);
        top.add(new JLabel("Học kỳ:"));
        top.add(txtHocKy);
        JButton btnXem = new JButton("Xem");
        JButton btnXuat = new JButton("Xuất CSV");
        top.add(btnXem);
        top.add(btnXuat);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(lblTongKet, BorderLayout.SOUTH);

        btnXem.addActionListener(e -> xemBaoCao());
        btnXuat.addActionListener(e -> xuatCSV());
    }

    private void xemBaoCao() {
        String loai = (String) cboLoai.getSelectedItem();
        lblTongKet.setText(" ");
        try {
            if ("Doanh thu theo học kỳ".equals(loai)) {
                ThongKeDoanhThu tk = controller.baoCaoDoanhThu(txtHocKy.getText());
                datCot(new String[]{"Học kỳ", "Số hóa đơn", "Tổng phải thu", "Đã thu", "Còn lại"});
                model.addRow(new Object[]{ tk.getMaHocKy(), tk.getSoHoaDon(),
                        KeToanPanel.tien(tk.getTongPhaiThu()), KeToanPanel.tien(tk.getDaThu()),
                        KeToanPanel.tien(tk.getConLai()) });
                lblTongKet.setText("Tổng phải thu: " + KeToanPanel.tien(tk.getTongPhaiThu())
                        + "   |   Đã thu: " + KeToanPanel.tien(tk.getDaThu())
                        + "   |   Còn lại: " + KeToanPanel.tien(tk.getConLai()));

            } else if ("Doanh thu tất cả học kỳ".equals(loai)) {
                List<ThongKeDoanhThu> ds = controller.baoCaoDoanhThuTatCaKy();
                datCot(new String[]{"Học kỳ", "Số hóa đơn", "Tổng phải thu", "Đã thu", "Còn lại"});
                long tongThu = 0, tongDaThu = 0;
                for (ThongKeDoanhThu tk : ds) {
                    model.addRow(new Object[]{ tk.getMaHocKy(), tk.getSoHoaDon(),
                            KeToanPanel.tien(tk.getTongPhaiThu()), KeToanPanel.tien(tk.getDaThu()),
                            KeToanPanel.tien(tk.getConLai()) });
                    tongThu += tk.getTongPhaiThu();
                    tongDaThu += tk.getDaThu();
                }
                lblTongKet.setText("TỔNG CỘNG - Phải thu: " + KeToanPanel.tien(tongThu)
                        + "   |   Đã thu: " + KeToanPanel.tien(tongDaThu)
                        + "   |   Còn lại: " + KeToanPanel.tien(tongThu - tongDaThu));

            } else { // Danh sach cong no
                List<HoaDonView> ds = controller.baoCaoCongNo(txtHocKy.getText());
                datCot(new String[]{"Mã hóa đơn", "MSSV", "Họ tên", "Lớp",
                        "Tổng phải nộp", "Đã nộp", "Còn nợ", "Trạng thái"});
                long tongNo = 0;
                for (HoaDonView v : ds) {
                    model.addRow(new Object[]{ v.getMaHoaDon(), v.getMssv(), v.getHoTen(),
                            v.getTenLop(), KeToanPanel.tien(v.getTongTienNop()),
                            KeToanPanel.tien(v.getDaNop()), KeToanPanel.tien(v.getConNo()),
                            v.getTrangThai() });
                    tongNo += v.getConNo();
                }
                lblTongKet.setText("Số sinh viên còn nợ: " + ds.size()
                        + "   |   Tổng còn nợ: " + KeToanPanel.tien(tongNo));
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Dat lai tieu de cot + xoa du lieu cu. */
    private void datCot(String[] tieuDe) {
        model.setRowCount(0);
        model.setColumnIdentifiers(tieuDe);
    }

    /** UC08.3 - Xuat bang dang hien thi ra file CSV (mo bang Excel). */
    private void xuatCSV() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để xuất. Hãy bấm Xem trước.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bao_cao_tai_chinh.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String duongDan = chooser.getSelectedFile().getAbsolutePath();
        if (!duongDan.toLowerCase().endsWith(".csv")) duongDan += ".csv";

        // Gom tieu de + du lieu tu bang
        int soCot = model.getColumnCount();
        String[] tieuDe = new String[soCot];
        for (int c = 0; c < soCot; c++) tieuDe[c] = model.getColumnName(c);

        List<String[]> duLieu = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            String[] dong = new String[soCot];
            for (int c = 0; c < soCot; c++) {
                Object o = model.getValueAt(r, c);
                dong[c] = (o == null) ? "" : o.toString();
            }
            duLieu.add(dong);
        }
        try {
            CsvExporter.xuatCSV(duongDan, tieuDe, duLieu);
            JOptionPane.showMessageDialog(this, "Đã xuất file:\n" + duongDan,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
