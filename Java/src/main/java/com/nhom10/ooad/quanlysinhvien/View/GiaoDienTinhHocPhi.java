package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font; // 🌟 THÊM IMPORT
import javax.swing.*;

import com.nhom10.ooad.quanlysinhvien.Controller.HocPhiController;

/**
 * GiaoDienTinhHocPhi - Tab 1: TINH HOC PHI (UC07.3).
 * 🌟 ĐÃ CẬP NHẬT: Chuyển toàn bộ các phương thức static tiện ích (tien, doiTien) sang GiaoDienChinhKeToan.
 */
public class GiaoDienTinhHocPhi extends JPanel {

    private final HocPhiController controller = new HocPhiController();

    // Khởi tạo biến Font hệ thống đồng bộ
    private final Font segoeFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font segoeBoldFont = new Font("Segoe UI", Font.BOLD, 14);

    private final JTextField txtHocKy  = new JTextField("20252", 10);
    private final JTextField txtDonGia = new JTextField("500000", 12);
    private final JTextArea  txtLog    = new JTextArea(12, 40);

    public GiaoDienTinhHocPhi() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Khu nhập liệu ---
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel lblHk = new JLabel("Mã học kỳ:");
        JLabel lblDg = new JLabel("Đơn giá / tín chỉ (VND):");
        JButton btnTinh = new JButton("Tính & tạo hóa đơn");

        // ÉP FONT: Cài đặt Segoe UI 14 cho vùng nhập liệu phía trên
        lblHk.setFont(segoeFont);
        txtHocKy.setFont(segoeFont);
        lblDg.setFont(segoeFont);
        txtDonGia.setFont(segoeFont);
        btnTinh.setFont(segoeBoldFont);

        form.add(lblHk);
        form.add(txtHocKy);
        form.add(lblDg);
        form.add(txtDonGia);
        form.add(btnTinh);
        add(form, BorderLayout.NORTH);

        // --- Khu log kết quả ---
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 13)); // Dùng font chữ code Consolas để log thẳng hàng, ngay ngắn
        txtLog.setEditable(false);
        txtLog.setText("Nhập học kỳ và đơn giá, sau đó bấm \"Tính & tạo hóa đơn\".\n"
                     + "Hệ thống sẽ tự đếm số tín chỉ mỗi sinh viên đã đăng ký để tạo hóa đơn.\n");
        add(new JScrollPane(txtLog), BorderLayout.CENTER);

        btnTinh.addActionListener(e -> xuLyTinh());
    }

    private void xuLyTinh() {
        String hocKy = txtHocKy.getText().trim();
        // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.doiTien
        long donGia = GiaoDienChinhKeToan.doiTien(txtDonGia.getText());
        
        if (donGia <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền đơn giá nhập vào không hợp lệ. Hãy kiểm tra lại!",
                    "Lỗi định dạng số", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int soMoi = controller.tinhHocPhi(hocKy, donGia);
            txtLog.append("--------------------------------------------------\n");
            txtLog.append("⏳ Hệ thống: Đang quét danh sách tín chỉ đăng ký học kỳ " + hocKy + "...\n");
            // 🌟 ĐÃ SỬA: Chuyển sang gọi GiaoDienChinhKeToan.tien
            txtLog.append("📌 Đơn giá áp dụng định mức: " + GiaoDienChinhKeToan.tien(donGia) + " / tín chỉ.\n");
            
            if (soMoi > 0) {
                txtLog.append("🎉 THÀNH CÔNG: Đã tính toán và khởi tạo thêm [" + soMoi + "] hóa đơn mới!\n");
            } else {
                txtLog.append("⚠️ THÔNG BÁO: Không có hóa đơn mới nào được sinh ra.\n");
                txtLog.append("  (Lý do: Các sinh viên đã có sẵn hóa đơn trong kỳ này, hoặc chưa có ai đăng ký tín chỉ thành công).\n");
            }
            txtLog.append("👉 Hướng dẫn: Thầy/Cô hãy chuyển sang tab \"Quản lý thanh toán\" ấn Tìm để nộp tiền.\n");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        }
    }
}