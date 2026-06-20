package com.nhom10.ooad.quanlysinhvien.View;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * KeToanPanel - KHUNG GIAO DIEN cua phan KE TOAN (1 JPanel duy nhat).
 *
 * ====== HOP DONG TICH HOP VOI MAINFRAME (ban Giao vu - Nguoi 1) ======
 * Ban Giao vu chi can nhung panel nay vao vung noi dung cua MainFrame:
 *
 *     contentPanel.add(new view.KeToanPanel(), BorderLayout.CENTER);
 *     contentPanel.revalidate();
 *     contentPanel.repaint();
 *
 * Nho the toan bo 3 chuc nang (Tinh hoc phi / Thanh toan / Bao cao) xuat hien
 * ma KHONG dung den code ben trong MainFrame -> tranh xung dot.
 */
public class KeToanPanel extends JPanel {

    public KeToanPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tính học phí",     new TinhHocPhiPanel());
        tabs.addTab("Quản lý thanh toán", new QuanLyThanhToanPanel());
        tabs.addTab("Báo cáo tài chính", new BaoCaoTaiChinhPanel());

        add(tabs, BorderLayout.CENTER);
    }

    /** Dinh dang tien VND, vd 6800000 -> "6,800,000 d". Dung chung cho cac panel. */
    public static String tien(long v) {
        return String.format("%,d", v) + " đ";
    }

    /** Doc so tien tu o nhap (bo dau cham/phay/khoang trang). Rong/khong hop le -> -1. */
    public static long doiTien(String s) {
        if (s == null) return -1;
        String chiSo = s.replaceAll("[^0-9]", "");
        if (chiSo.isEmpty()) return -1;
        try { return Long.parseLong(chiSo); } catch (NumberFormatException e) { return -1; }
    }
}
