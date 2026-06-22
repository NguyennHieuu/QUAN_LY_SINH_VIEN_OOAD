package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font; // 🌟 THÊM IMPORT THƯ VIỆN FONT
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.nhom10.ooad.quanlysinhvien.Controller.BaoCaoController;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.ThongKeDoanhThu;
import com.nhom10.ooad.quanlysinhvien.Utils.CSVExporter;

/**
 * GiaoDienBaoCaoTaiChinh - Tab 3: BAO CAO TAI CHINH (UC08.2) + XUAT FILE (UC08.3).
 * 🌟 ĐÃ CẬP NHẬT: Định dạng toàn bộ giao diện sang Segoe UI cỡ 14 và chuyển tiền tố sang GiaoDienChinhKeToan.
 */
public class GiaoDienBaoCaoTaiChinh extends JPanel {

    private final BaoCaoController controller = new BaoCaoController();

    // Khởi tạo các biến font chữ dùng chung
    private final Font segoeFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font segoeBoldFont = new Font("Segoe UI", Font.BOLD, 14);

    private final JComboBox<String> cboLoai = new JComboBox<>(new String[]{
        "Doanh thu theo học kỳ", "Doanh thu tất cả học kỳ", "Danh sách công nợ"
    });
    private final JTextField txtHocKy = new JTextField("20252", 8); // Đổi mặc định sang học kỳ mẫu 20252

    private final DefaultTableModel model = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblTongKet = new JLabel(" ");

    public GiaoDienBaoCaoTaiChinh() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- Khu vực thanh công cụ phía trên ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JLabel lblLoai = new JLabel("Loại báo cáo:");
        JLabel lblHk = new JLabel("Học kỳ:");
        JButton btnXem = new JButton("Xem");
        JButton btnXuat = new JButton("Xuất CSV");

        // 🌟 CẬP NHẬT FONT: Thanh công cụ chọn báo cáo
        lblLoai.setFont(segoeFont);
        cboLoai.setFont(segoeFont);
        lblHk.setFont(segoeFont);
        txtHocKy.setFont(segoeFont);
        btnXem.setFont(segoeBoldFont);  // Nút xem chữ Bold
        btnXuat.setFont(segoeBoldFont); // Nút xuất chữ Bold

        top.add(lblLoai);
        top.add(cboLoai);
        top.add(lblHk);
        top.add(txtHocKy);
        top.add(btnXem);
        top.add(btnXuat);
        add(top, BorderLayout.NORTH);

        // 🌟 CẬP NHẬT FONT: Bảng JTable hiển thị báo cáo
        table.setFont(segoeFont);
        table.setRowHeight(24); // Tăng chiều cao dòng cho chữ 14 dễ nhìn
        table.getTableHeader().setFont(segoeBoldFont); // Thẻ tiêu đề cột dùng chữ Bold

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 🌟 CẬP NHẬT FONT: Nhãn tổng kết ở đáy dưới cùng dùng chữ Bold
        lblTongKet.setFont(segoeBoldFont);
        add(lblTongKet, BorderLayout.SOUTH);

        btnXem.addActionListener(e -> xemBaoCao());
        btnXuat.addActionListener(e -> xuatCSV());
    }

    private void xemBaoCao() {
        String loai = (String) cboLoai.getSelectedItem();
        lblTongKet.setText(" ");
        try {
            if ("Doanh thu theo học kỳ".equals(loai)) {
                ThongKeDoanhThu tk = controller.baoCaoDoanhThu(txtHocKy.getText().trim());
                datCot(new String[]{"Học kỳ", "Số hóa đơn", "Tổng phải thu", "Đã thu", "Còn lại"});
                
                // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.tien()
                model.addRow(new Object[]{ tk.getMaHocKy(), tk.getSoHoaDon(),
                        GiaoDienChinhKeToan.tien(tk.getTongPhaiThu()), GiaoDienChinhKeToan.tien(tk.getDaThu()),
                        GiaoDienChinhKeToan.tien(tk.getConLai()) });
                
                lblTongKet.setText("Tổng phải thu: " + GiaoDienChinhKeToan.tien(tk.getTongPhaiThu())
                        + "   |   Đã thu: " + GiaoDienChinhKeToan.tien(tk.getDaThu())
                        + "   |   Còn lại: " + GiaoDienChinhKeToan.tien(tk.getConLai()));

            } else if ("Doanh thu tất cả học kỳ".equals(loai)) {
                List<ThongKeDoanhThu> ds = controller.baoCaoDoanhThuTatCaKy();
                datCot(new String[]{"Học kỳ", "Số hóa đơn", "Tổng phải thu", "Đã thu", "Còn lại"});
                long tongThu = 0, tongDaThu = 0;
                for (ThongKeDoanhThu tk : ds) {
                    // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.tien()
                    model.addRow(new Object[]{ tk.getMaHocKy(), tk.getSoHoaDon(),
                            GiaoDienChinhKeToan.tien(tk.getTongPhaiThu()), GiaoDienChinhKeToan.tien(tk.getDaThu()),
                            GiaoDienChinhKeToan.tien(tk.getConLai()) });
                    tongThu += tk.getTongPhaiThu();
                    tongDaThu += tk.getDaThu();
                }
                lblTongKet.setText("TỔNG CỘNG - Phải thu: " + GiaoDienChinhKeToan.tien(tongThu)
                        + "   |   Đã thu: " + GiaoDienChinhKeToan.tien(tongDaThu)
                        + "   |   Còn lại: " + GiaoDienChinhKeToan.tien(tongThu - tongDaThu));

            } else { // Danh sach cong no
                List<HoaDonView> ds = controller.baoCaoCongNo(txtHocKy.getText().trim());
                datCot(new String[]{"Mã hóa đơn", "MSSV", "Họ tên", "Lớp",
                        "Tổng phải nộp", "Đã nộp", "Còn nợ", "Trạng thái"});
                long tongNo = 0;
                for (HoaDonView v : ds) {
                    // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.tien()
                    model.addRow(new Object[]{ v.getMaHoaDon(), v.getMssv(), v.getHoTen(),
                            v.getTenLop(), GiaoDienChinhKeToan.tien(v.getTongTienNop()),
                            GiaoDienChinhKeToan.tien(v.getDaNop()), GiaoDienChinhKeToan.tien(v.getConNo()),
                            v.getTrangThai() });
                    tongNo += v.getConNo();
                }
                lblTongKet.setText("Số sinh viên còn nợ: " + ds.size()
                        + "   |   Tổng còn nợ: " + GiaoDienChinhKeToan.tien(tongNo));
                
                // Tự động căn chỉnh độ rộng riêng cho lưới Công nợ
                if (table.getColumnModel().getColumnCount() > 0) {
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    table.getColumnModel().getColumn(0).setPreferredWidth(100);  
                    table.getColumnModel().getColumn(1).setPreferredWidth(100);  
                    table.getColumnModel().getColumn(2).setPreferredWidth(185); // Tăng độ rộng cột Họ tên cho font 14 to
                    table.getColumnModel().getColumn(3).setPreferredWidth(100);  
                    table.getColumnModel().getColumn(4).setPreferredWidth(120); 
                    table.getColumnModel().getColumn(5).setPreferredWidth(110); 
                    table.getColumnModel().getColumn(6).setPreferredWidth(110); 
                    table.getColumnModel().getColumn(7).setPreferredWidth(105); 
                }
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Đặt lại tiêu đề cột + xóa dữ liệu cũ. */
    private void datCot(String[] tieuDe) {
        model.setRowCount(0);
        model.setColumnIdentifiers(tieuDe);
    }

    /** UC08.3 - Xuất bảng đang hiển thị ra file CSV (mở bằng Excel). */
    private void xuatCSV() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để xuất. Hãy bấm Xem trước.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Cài đặt đồng bộ font cho hộp thoại chọn file popup
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bao_cao_tai_chinh.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String duongDan = chooser.getSelectedFile().getAbsolutePath();
        if (!duongDan.toLowerCase().endsWith(".csv")) duongDan += ".csv";

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
            CSVExporter.xuatCSV(duongDan, tieuDe, duLieu);
            JOptionPane.showMessageDialog(this, "Đã xuất file:\n" + duongDan,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}