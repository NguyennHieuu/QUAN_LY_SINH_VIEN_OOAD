package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.DiemSoController;
import com.nhom10.ooad.quanlysinhvien.Controller.XacThucController;
import com.nhom10.ooad.quanlysinhvien.DAO.LopHocPhanDAO;
import com.nhom10.ooad.quanlysinhvien.Model.BangDiem;
import com.nhom10.ooad.quanlysinhvien.Model.LopHocPhan;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class GiaoDienQuanLyDiem extends JPanel {

    private final DiemSoController diemSoController = new DiemSoController();
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO(); // Tích hợp DAO để xử lý nạp lớp HP thật

    // Các thành phần điều khiển vùng 1
    private final JComboBox<String> cboLopHP = new JComboBox<>(); 
    private final JButton btnXemDanhSach = new JButton("Xem danh sách");

    // Các thành phần vùng 2 (Bảng dữ liệu)
    private final String[] cols = {
        "STT", "Mã SV", "Họ và tên", "Ngày sinh", "Lớp quản lý", "Điểm QT", "Điểm CK", "Điểm tổng kết", "Trạng thái"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(cols, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Nghiệp vụ bảo mật: Nếu lớp đã chốt "Chính thức", khóa toàn bộ bảng không cho sửa
            String trangThaiLop = (String) getValueAt(row, 8);
            if ("Chính thức".equals(trangThaiLop)) {
                return false;
            }
            // Quy tắc Edit: Chỉ mở duy nhất cột Điểm QT (Index 5) và Điểm CK (Index 6)
            return column == 5 || column == 6;
        }
    };
    private final JTable tableDiem = new JTable(tableModel);

    // Các thành phần điều khiển vùng 3
    private final JButton btnLuuNhap = new JButton("Lưu nháp");
    private final JButton btnChotDiem = new JButton("Chốt điểm");
    private final JLabel lblStatus = new JLabel("Sẵn sàng", SwingConstants.LEFT);

    // Biến cờ ngăn vòng lặp vô hạn khi TableModelListener tự cập nhật dữ liệu
    private boolean isUpdatingRealtime = false;

    public GiaoDienQuanLyDiem() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        Font fontChung = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontButton = new Font("Segoe UI", Font.BOLD, 14);

        // ====================================================================
        // VÙNG 1: ĐIỀU HƯỚNG & LỰA CHỌN (NORTH)
        // ====================================================================
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTop.setBorder(BorderFactory.createTitledBorder("Bộ lọc lớp học phần"));
        
        JLabel lblLop = new JLabel("Lớp học phần:");
        lblLop.setFont(fontChung);
        cboLopHP.setFont(fontChung);
        cboLopHP.setPreferredSize(new Dimension(180, 28));
        btnXemDanhSach.setFont(fontButton);

        panelTop.add(lblLop);
        panelTop.add(cboLopHP);
        panelTop.add(btnXemDanhSach);
        add(panelTop, BorderLayout.NORTH);

        // ====================================================================
        // VÙNG 2: LƯỚI NHẬP ĐIỂM CHÍNH (CENTER)
        // ====================================================================
        tableDiem.setFont(fontChung);
        tableDiem.setRowHeight(24);
        tableDiem.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableDiem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableDiem.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tableDiem), BorderLayout.CENTER);

        // ====================================================================
        // VÙNG 3: NÚT BẤM HÀNH ĐỘNG & THÔNG BÁO TRẠNG THÁI (SOUTH)
        // ====================================================================
        JPanel panelBottom = new JPanel(new BorderLayout(5, 5));

        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblStatus.setForeground(Color.BLUE);
        panelBottom.add(lblStatus, BorderLayout.WEST);

        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnLuuNhap.setFont(fontButton);
        btnChotDiem.setFont(fontButton);
        
        Dimension btnSize = new Dimension(120, 32);
        btnLuuNhap.setPreferredSize(btnSize);
        btnChotDiem.setPreferredSize(btnSize);

        panelActions.add(btnLuuNhap);
        panelActions.add(btnChotDiem);
        panelBottom.add(panelActions, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN HÀNH ĐỘNG (EVENT LISTENERS)
        // ====================================================================
        btnXemDanhSach.addActionListener(e -> taiDuLieuLop());
        btnLuuNhap.addActionListener(e -> xuLyLuuNhap());
        btnChotDiem.addActionListener(e -> xuLyChotDiem());

        // Tự động lắng nghe sự kiện thay đổi của JComboBox để tự làm mới bảng điểm
        cboLopHP.addActionListener(e -> {
            if (cboLopHP.getSelectedItem() != null) {
                taiDuLieuLop();
            }
        });

        // 🌟 THÊM MỚI: TỰ ĐỘNG CẬP NHẬT ĐIỂM TỔNG KẾT VÀ TRẠNG THÁI REALTIME KHI GIẢNG VIÊN SỬA Ô
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Chỉ xử lý khi thao tác sửa ô dữ liệu (UPDATE) và không ở trong luồng tự cập nhật của hệ thống
                if (e.getType() == TableModelEvent.UPDATE && !isUpdatingRealtime) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    // Nếu sửa đúng cột Điểm QT (Index 5) hoặc Điểm CK (Index 6)
                    if (column == 5 || column == 6) {
                        isUpdatingRealtime = true; // Bật cờ chặn vòng lặp vô hạn
                        
                        try {
                            String maLopHP = (String) cboLopHP.getSelectedItem();
                            LopHocPhan lhpInfo = lopHocPhanDAO.getByID(maLopHP);
                            double trongSoQT = (lhpInfo != null) ? lhpInfo.getTrongSoQT() : 0.3;
                            double trongSoCK = (lhpInfo != null) ? lhpInfo.getTrongSoCK() : 0.7;

                            Object qtObj = tableModel.getValueAt(row, 5);
                            Object ckObj = tableModel.getValueAt(row, 6);

                            // Đọc dữ liệu thô từ ô gõ, nếu trống hoặc lỗi thì coi là 0.0
                            double diemQT = (qtObj != null && !qtObj.toString().trim().isEmpty()) ? Double.parseDouble(qtObj.toString().trim()) : 0.0;
                            double diemCK = (ckObj != null && !ckObj.toString().trim().isEmpty()) ? Double.parseDouble(ckObj.toString().trim()) : 0.0;

                            // Công thức tính toán tổng kết môn
                            double diemTK = (diemQT * trongSoQT) + (diemCK * trongSoCK);
                            diemTK = Math.round(diemTK * 10.0) / 10.0; 

                            // Ghi ngược lại lưới hiển thị công khai ngay lập tức
                            tableModel.setValueAt(diemTK, row, 7); // Điểm tổng kết (Cột 7)
                            tableModel.setValueAt("Nháp", row, 8); // Trạng thái (Cột 8)

                        } catch (NumberFormatException ex) {
                            tableModel.setValueAt(0.0, row, 7);
                            tableModel.setValueAt("Lỗi số", row, 8);
                        } finally {
                            isUpdatingRealtime = false; // Giải phóng cờ sau khi cập nhật xong
                        }
                    }
                }
            }
        });

        // Thực hiện nạp danh sách các Lớp học phần thật ngay khi giao diện dựng lên
        loadDanhSachLopHocPhanGiangVien();
    }

    private void loadDanhSachLopHocPhanGiangVien() {
        cboLopHP.removeAllItems();
        if (XacThucController.getTaiKhoanHienTai() != null) {
            String maGV = XacThucController.getTaiKhoanHienTai().getTenDangNhap();
            List<String> dsMaLopHP = lopHocPhanDAO.getMaLopHPByGiangVien(maGV);
            
            for (String maLop : dsMaLopHP) {
                cboLopHP.addItem(maLop);
            }
            
            if (cboLopHP.getItemCount() == 0) {
                hienThiThongBao("Cảnh báo: Thầy/Cô chưa được phân công phụ trách lớp học phần nào trong kỳ này!", false);
                btnLuuNhap.setEnabled(false);
                btnChotDiem.setEnabled(false);
            }
        }
    }

    private void taiDuLieuLop() {
        String maLopHP = (String) cboLopHP.getSelectedItem();
        
        isUpdatingRealtime = true; // Bật cờ khóa tạm thời để việc clear và load dữ liệu không kích hoạt bộ tính điểm nhầm
        tableModel.setRowCount(0); 
        isUpdatingRealtime = false;
        
        if (maLopHP == null || maLopHP.isEmpty()) return;

        List<Object[]> dsSinhVien = diemSoController.layDanhSachSinhVienLopDay(maLopHP);

        if (dsSinhVien.isEmpty()) {
            hienThiThongBao("Lớp học phần " + maLopHP + " hiện tại chưa có sinh viên đăng ký học tín chỉ.", false);
            btnLuuNhap.setEnabled(false);
            btnChotDiem.setEnabled(false);
            return;
        }

        isUpdatingRealtime = true;
        for (Object[] row : dsSinhVien) {
            tableModel.addRow(row);
        }
        isUpdatingRealtime = false;

        String trangThaiGoc = (String) tableModel.getValueAt(0, 8);
        if ("Chính thức".equals(trangThaiGoc)) {
            btnLuuNhap.setEnabled(false);
            btnChotDiem.setEnabled(false);
            hienThiThongBao("Bảng điểm lớp này đã chốt CHÍNH THỨC. Hệ thống đã khóa toàn bộ quyền sửa đổi!", false);
        } else {
            btnLuuNhap.setEnabled(true);
            btnChotDiem.setEnabled(true);
            hienThiThongBao("Tải danh sách sinh viên lớp thành công. Sẵn sàng nhập hoặc sửa đổi điểm.", true);
        }
    }

    private void xuLyLuuNhap() {
        if (tableModel.getRowCount() == 0) return;

        String maLopHP = (String) cboLopHP.getSelectedItem();
        
        LopHocPhan lhpInfo = lopHocPhanDAO.getByID(maLopHP);
        double trongSoQT = 0.3; 
        double trongSoCK = 0.7;
        
        if (lhpInfo != null) {
            trongSoQT = lhpInfo.getTrongSoQT();
            trongSoCK = lhpInfo.getTrongSoCK();
        }

        List<BangDiem> danhSachDiemGuiDi = new ArrayList<>();

        try {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                BangDiem bd = new BangDiem();
                bd.setMaBangDiem(maLopHP + "-" + tableModel.getValueAt(i, 1)); 
                bd.setMssv((String) tableModel.getValueAt(i, 1));
                bd.setMaLopHP(maLopHP);
                
                Object qtObj = tableModel.getValueAt(i, 5);
                Object ckObj = tableModel.getValueAt(i, 6);
                
                double diemQT = (qtObj != null && !qtObj.toString().trim().isEmpty()) ? Double.parseDouble(qtObj.toString().trim()) : 0.0;
                double diemCK = (ckObj != null && !ckObj.toString().trim().isEmpty()) ? Double.parseDouble(ckObj.toString().trim()) : 0.0;

                if (diemQT < 0 || diemQT > 10 || diemCK < 0 || diemCK > 10) {
                    hienThiThongBao("Lỗi dữ liệu: Điểm số nhập vào bắt buộc phải thuộc thang điểm chuẩn từ [0 đến 10]!", false);
                    return;
                }

                bd.setDiemQT(diemQT);
                bd.setDiemCK(diemCK);
                danhSachDiemGuiDi.add(bd);
            }

            String res = diemSoController.luuNhapBangDiem(danhSachDiemGuiDi, trongSoQT, trongSoCK);
            if ("OK".equals(res)) {
                hienThiThongBao("Đã lưu nháp bảng điểm thành công vào hệ thống!", true);
                taiDuLieuLop(); 
            } else {
                hienThiThongBao(res, false);
            }
        } catch (NumberFormatException ex) {
            hienThiThongBao("Lỗi định dạng: Giá trị điểm nhập vào các ô bảng JTable phải là ký tự số thực hợp lệ!", false);
        }
    }

    private void xuLyChotDiem() {
        if (tableModel.getRowCount() == 0) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Lưu ý: Sau khi tiến hành chốt điểm, bạn sẽ KHÔNG THỂ chỉnh sửa thông tin điểm số được nữa.\nBạn có chắc chắn muốn gửi khóa bảng điểm chính thức này không?",
                "Xác nhận chốt khóa điểm số vĩnh viễn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            String maLopHP = (String) cboLopHP.getSelectedItem();
            String res = diemSoController.chotBangDiemChinhThuc(maLopHP);
            
            if ("OK".equals(res)) {
                hienThiThongBao("Hệ thống: Khóa và chốt bảng điểm thành công! Khóa toàn bộ quyền chỉnh sửa.", true);
                taiDuLieuLop(); 
            } else {
                hienThiThongBao(res, false); 
            }
        }
    }

    private void hienThiThongBao(String msg, boolean laThanhCong) {
        lblStatus.setText(msg);
        if (laThanhCong) {
            lblStatus.setForeground(new Color(46, 125, 50)); 
        } else {
            lblStatus.setForeground(Color.RED); 
        }
    }
}