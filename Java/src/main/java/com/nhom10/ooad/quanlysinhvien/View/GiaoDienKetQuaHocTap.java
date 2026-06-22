package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.DiemSoController;
import com.nhom10.ooad.quanlysinhvien.Model.DiemSoView; // 🌟 THÊM IMPORT: Dùng cấu hình DTO mới gộp dữ liệu
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class GiaoDienKetQuaHocTap extends JPanel {

    private final DiemSoController controller = new DiemSoController();
    private final String mssvHienTai;
    
    private JComboBox<String> cbHocKy;
    private JButton btnCapNhat; 
    private JTable tblDiem;
    private DefaultTableModel modelDiem;
    private JLabel lblGPAValue;

    public GiaoDienKetQuaHocTap(String mssv) {
        this.mssvHienTai = mssv;
        initComponent();
        loadBangDiem();
    }

    private void initComponent() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setBackground(Color.WHITE);

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);

        // ====================================================================
        // 1. KHU VỰC ĐIỀU HƯỚNG CHỌN KÝ & CẬP NHẬT (NORTH)
        // ====================================================================
        JPanel pnlNorth = new JPanel(new BorderLayout());
        pnlNorth.setBackground(Color.WHITE);

        // Đảm bảo chữ tiêu đề luôn rõ ràng trên nền trắng
        JLabel lblTitle = new JLabel("PHIẾU ĐIỂM VÀ KẾT QUẢ HỌC TẬP CÁ NHÂN");
        lblTitle.setFont(titleFont);
        pnlNorth.add(lblTitle, BorderLayout.WEST);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlFilter.setBackground(Color.WHITE);
        
        JLabel lblChonKy = new JLabel("Chọn học kỳ:");
        lblChonKy.setFont(mainFont);
        
        cbHocKy = new JComboBox<>(new String[]{"20252", "20251"}); 
        cbHocKy.setFont(mainFont);
        cbHocKy.setPreferredSize(new Dimension(100, 30));
        
        // 🌟 SỬA TẠI ĐÂY: Ép hiển thị màu gốc và border để không bị mờ Look and Feel đè
        btnCapNhat = new JButton("🔄 Cập nhật");
        btnCapNhat.setFont(boldFont);
        btnCapNhat.setPreferredSize(new Dimension(120, 30));
        btnCapNhat.setBackground(new Color(40, 167, 69)); // Giữ nguyên màu xanh lục
        btnCapNhat.setForeground(Color.WHITE);            // Chữ trắng nổi bật
        btnCapNhat.setOpaque(true);                        // 🌟 Ép hiển thị đúng màu nền gốc
        btnCapNhat.setBorderPainted(false);                // 🌟 Bỏ viền mờ mặc định của Swing
        btnCapNhat.setEnabled(true);                       // 🌟 Chắc chắn nút luôn sáng rõ để click
        
        pnlFilter.add(lblChonKy);
        pnlFilter.add(cbHocKy);
        pnlFilter.add(btnCapNhat); 
        pnlNorth.add(pnlFilter, BorderLayout.EAST);
        
        this.add(pnlNorth, BorderLayout.NORTH);

        // ====================================================================
        // 2. BẢNG HIỂN THỊ CHI TIẾT ĐIỂM SỐ (CENTER)
        // ====================================================================
        String[] columns = {"Mã học phần", "Mã lớp HP", "Tên học phần", "Số tín chỉ", "Trọng số QT-CK", "Điểm QT", "Điểm CK", "Tổng kết môn", "Trạng thái"};
        modelDiem = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblDiem = new JTable(modelDiem);
        tblDiem.setFont(mainFont);
        tblDiem.setRowHeight(28);
        tblDiem.getTableHeader().setFont(boldFont);
        this.add(new JScrollPane(tblDiem), BorderLayout.CENTER);

        // ====================================================================
        // 3. KHU VỰC TỔNG KẾT GPA CUỐI TRANG (SOUTH)
        // ====================================================================
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        pnlSouth.setBackground(new Color(245, 245, 247)); 
        pnlSouth.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblGPALabel = new JLabel("ĐIỂM TRUNG BÌNH TÍCH LŨY HỌC KỲ (GPA):");
        lblGPALabel.setFont(boldFont);
        
        lblGPAValue = new JLabel("0.00");
        lblGPAValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGPAValue.setForeground(new Color(0, 122, 255)); 

        pnlSouth.add(lblGPALabel);
        pnlSouth.add(lblGPAValue);
        this.add(pnlSouth, BorderLayout.SOUTH);

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LOGIC (LISTENERS)
        // ====================================================================
        cbHocKy.addActionListener(e -> loadBangDiem());
        
        btnCapNhat.addActionListener(e -> {
            loadBangDiem();
            JOptionPane.showMessageDialog(this, 
                "Đã đồng bộ kết quả điểm số mới nhất từ cơ sở dữ liệu!", 
                "Hệ thống", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Đồng bộ logic điều phối từ DiemSoController sử dụng đối tượng DiemSoView
     */
    private void loadBangDiem() {
        modelDiem.setRowCount(0);
        String selectedKy = cbHocKy.getSelectedItem().toString();
        
        // 🌟 TỐI ƯU: Nhận về danh sách DTO DiemSoView đã được kết hợp JOIN từ CSDL thô
        List<DiemSoView> listDiem = controller.xemDiemCaNhan(mssvHienTai, selectedKy);
        
        if (listDiem == null) {
            JOptionPane.showMessageDialog(this, 
                "Hệ thống đã khóa quyền xem điểm!\nVui lòng hoàn thành nghĩa vụ học phí kỳ cũ tại phòng kế toán trước khi tra cứu.", 
                "CẢNH BÁO TÀI CHÍNH", JOptionPane.ERROR_MESSAGE);
            lblGPAValue.setText("BỊ KHÓA");
            return;
        }

        // Đổ trực tiếp dữ liệu từ DTO lên giao diện không cần thông qua các hàm bổ trợ DAO
        for (DiemSoView ds : listDiem) {
            String trangThaiHienThi = "";
            if (ds.getDiemTongKet() != null) {
                trangThaiHienThi = (ds.getDiemTongKet() >= 4.0) ? "Đạt" : "Trượt (F)";
            }
            
            modelDiem.addRow(new Object[]{
                ds.getMaHocPhan(),
                ds.getMaLopHP(), 
                ds.getTenHocPhan(), 
                ds.getSoTinChi(), 
                ds.getTrongSo(),
                ds.getDiemQT(),
                ds.getDiemCK(),
                ds.getDiemTongKet(),
                trangThaiHienThi
            });
        }
        
        // Cấu hình tỷ lệ co giãn chiều rộng thông minh cho các cột hiển thị
        if (tblDiem.getColumnModel().getColumnCount() > 0) {
            tblDiem.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tblDiem.getColumnModel().getColumn(0).setPreferredWidth(95);  // Mã học phần
            tblDiem.getColumnModel().getColumn(1).setPreferredWidth(120); // Mã lớp HP
            tblDiem.getColumnModel().getColumn(2).setPreferredWidth(320); // Tên học phần (Kéo rộng tối đa)
            tblDiem.getColumnModel().getColumn(3).setPreferredWidth(80);  // Số tín chỉ
            tblDiem.getColumnModel().getColumn(4).setPreferredWidth(110); // Trọng số QT-CK
            tblDiem.getColumnModel().getColumn(5).setPreferredWidth(75);  // Điểm QT
            tblDiem.getColumnModel().getColumn(6).setPreferredWidth(75);  // Điểm CK
            tblDiem.getColumnModel().getColumn(7).setPreferredWidth(95);  // Tổng kết môn
            tblDiem.getColumnModel().getColumn(8).setPreferredWidth(85);  // Trạng thái
        }
        
        double gpa = controller.tinhGPA(mssvHienTai, selectedKy);
        lblGPAValue.setText(String.format("%.2f", gpa));
    }
}