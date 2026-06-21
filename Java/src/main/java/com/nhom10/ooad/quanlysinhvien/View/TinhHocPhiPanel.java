package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;

import com.nhom10.ooad.quanlysinhvien.Controller.HocPhiController;

/**
 * TinhHocPhiPanel - Tab 1: TINH HOC PHI (UC07.3).
 * Nhap hoc ky + don gia/tin chi -> bam nut -> he thong tu dem TC va tao hoa don.
 */
public class TinhHocPhiPanel extends JPanel {

    private final HocPhiController controller = new HocPhiController();

    private final JTextField txtHocKy  = new JTextField("20252", 10);
    private final JTextField txtDonGia = new JTextField("500000", 12);
    private final JTextArea  txtLog    = new JTextArea(12, 40);

    public TinhHocPhiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Khu nhap lieu ---
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.add(new JLabel("Mã học kỳ:"));
        form.add(txtHocKy);
        form.add(new JLabel("Đơn giá / tín chỉ (VND):"));
        form.add(txtDonGia);
        JButton btnTinh = new JButton("Tính & tạo hóa đơn");
        form.add(btnTinh);
        add(form, BorderLayout.NORTH);

        // --- Khu log ket qua ---
        txtLog.setEditable(false);
        txtLog.setText("Nhập học kỳ và đơn giá, sau đó bấm \"Tính & tạo hóa đơn\".\n"
                     + "Hệ thống sẽ tự đếm số tín chỉ mỗi sinh viên đã đăng ký để tạo hóa đơn.\n");
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        btnTinh.addActionListener(e -> xuLyTinh());
    }

    private void xuLyTinh() {
        String hocKy = txtHocKy.getText().trim();
        long donGia = KeToanPanel.doiTien(txtDonGia.getText());
        if (donGia <= 0) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int soMoi = controller.tinhHocPhi(hocKy, donGia);
            txtLog.append("--------------------------------------------------\n");
            txtLog.append("Học kỳ " + hocKy + " | đơn giá " + KeToanPanel.tien(donGia) + "/TC\n");
            if (soMoi > 0) {
                txtLog.append("=> Đã tạo " + soMoi + " hóa đơn mới.\n");
            } else {
                txtLog.append("=> Không có hóa đơn mới (có thể đã tạo trước đó, hoặc "
                            + "không có sinh viên đăng ký trong kỳ này).\n");
            }
            txtLog.append("Mở tab \"Quản lý thanh toán\" và bấm Tìm để xem danh sách hóa đơn.\n");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }
}
