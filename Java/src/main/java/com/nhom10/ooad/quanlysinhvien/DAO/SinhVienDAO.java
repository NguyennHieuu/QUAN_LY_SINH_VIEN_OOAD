package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.SinhVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 🌟 ĐÃ SỬA QUAN TRỌNG: DataBaseConnection dùng 1 Connection SINGLETON
 * cho toàn bộ ứng dụng. Vì vậy:
 * - KHÔNG bao giờ tự ý gọi conn.close() trên connection này nữa (trừ khi
 *   ứng dụng tắt hẳn) — đóng giữa app sẽ làm hỏng các DAO khác đang dùng chung.
 * - Sau khi dùng setAutoCommit(false) cho transaction, PHẢI trả lại
 *   setAutoCommit(true) ngay khi xong, để các lệnh UPDATE phía sau
 *   (ví dụ recalculateSiSo bên LopHocDAO) không bị "kẹt" trong transaction
 *   cũ chưa commit và bị rollback ngầm.
 */
public class SinhVienDAO {
    
    private final LopHocDAO lopHocDAO = new LopHocDAO();

    // Logic UC01.1: Thêm mới sinh viên vào DB kết hợp kiểm tra an toàn
    public boolean insert(SinhVien sv) {
        Connection conn = null;
        PreparedStatement psTK = null;
        PreparedStatement psSV = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // 🌟 BẮT ĐẦU TRANSACTION ĐỒNG BỘ CẤP TÀI KHOẢN

            // 1. Tạo tự động tài khoản hệ thống đi kèm (Mật khẩu mặc định: 123456)
            String sqlTaiKhoan = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThaiHoatDong) VALUES (?, ?, N'Sinh viên', 1)";
            psTK = conn.prepareStatement(sqlTaiKhoan);
            psTK.setString(1, sv.getMssv());
            psTK.setString(2, "123456"); 
            psTK.executeUpdate();

            // 2. Chèn hồ sơ cốt lõi vào bảng SinhVien
            String sqlSinhVien = "INSERT INTO SinhVien (MSSV, HoTen, NgaySinh, GioiTinh, MaLopQuanLy, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";
            psSV = conn.prepareStatement(sqlSinhVien);
            psSV.setString(1, sv.getMssv());
            psSV.setString(2, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                psSV.setDate(3, new java.sql.Date(sv.getNgaySinh().getTime()));
            } else {
                psSV.setNull(3, java.sql.Types.DATE);
            }
            psSV.setString(4, sv.getGioiTinh());
            psSV.setString(5, sv.getMaLopQuanLy());
            psSV.setString(6, sv.getMssv()); // Mặc định lấy MSSV làm tên đăng nhập liên kết khóa ngoại
            psSV.executeUpdate();

            conn.commit(); // CHỐT TRANSACTION LƯU Ổ CỨNG VĨNH VIỄN
            conn.setAutoCommit(true); // 🌟 QUAN TRỌNG: trả lại autoCommit để các lệnh UPDATE sau (sĩ số) không bị kẹt transaction

            // 3. 🌟 TỐI ƯU: Tính toán lại sĩ số thực tế của lớp mới nhận học sinh này
            lopHocDAO.recalculateSiSo(sv.getMaLopQuanLy());
            
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (psTK != null) psTK.close(); } catch (SQLException e) {}
            try { if (psSV != null) psSV.close(); } catch (SQLException e) {}
            // 🌟 ĐÃ XÓA: không còn conn.close() ở đây vì connection là singleton dùng chung toàn app.
            // Chỉ đảm bảo autoCommit được trả lại true.
            try { if (conn != null && !conn.getAutoCommit()) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    /**
     * Logic UC01.3: Cập nhật thông tin sinh viên từ form
     * 🌟 ĐÃ SỬA: Hỗ trợ tự động tính toán lại sĩ số của cả lớp cũ và lớp mới phòng trường hợp chuyển lớp.
     */
    public boolean update(SinhVien sv) {
        // Lấy mã lớp hiện tại của sinh viên trước khi cập nhật để kiểm tra chuyển lớp
        String maLopCu = "";
        String sqlGetOldClass = "SELECT MaLopQuanLy FROM SinhVien WHERE MSSV = ?";
        Connection connGet = DataBaseConnection.getConnection();
        try (PreparedStatement psGet = connGet.prepareStatement(sqlGetOldClass)) {
            connGet.setAutoCommit(true);
            psGet.setString(1, sv.getMssv());
            try (ResultSet rs = psGet.executeQuery()) {
                if (rs.next()) {
                    maLopCu = rs.getString("MaLopQuanLy");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sqlUpdate = "UPDATE SinhVien SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, MaLopQuanLy = ? WHERE MSSV = ?";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            conn.setAutoCommit(true); // 🌟 đảm bảo UPDATE này luôn commit ngay

            ps.setString(1, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                ps.setDate(2, new java.sql.Date(sv.getNgaySinh().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getMaLopQuanLy());
            ps.setString(5, sv.getMssv());
            
            boolean isUpdated = ps.executeUpdate() > 0;
            
            if (isUpdated) {
                // 🌟 Tính toán lại lớp mới
                lopHocDAO.recalculateSiSo(sv.getMaLopQuanLy());
                // Nếu dính trường hợp đổi sang lớp khác, tính toán lại cho cả lớp cũ vừa rời đi
                if (maLopCu != null && !maLopCu.equalsIgnoreCase(sv.getMaLopQuanLy())) {
                    lopHocDAO.recalculateSiSo(maLopCu);
                }
            }
            return isUpdated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🌟 HÀM OVERLOADING PHỤC VỤ TRANSACTION XÓA CHUỖI TỪ CONTROLLER.
     * Giữ nguyên: nhận Connection từ ngoài truyền vào, không tự setAutoCommit
     * vì nơi gọi (QuanLyHoSoController) đang tự quản lý transaction riêng.
     */
    public boolean delete(Connection conn, String mssv) throws SQLException {
        String sql = "DELETE FROM SinhVien WHERE MSSV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Logic UC01.4: Xoá hồ sơ sinh viên độc lập
     * 🌟 ĐÃ SỬA: Tính toán lại sĩ số lớp sau khi sinh viên bị xóa; không còn
     * tự đóng connection singleton (try-with-resources cũ trên Connection đã bỏ).
     */
    public boolean delete(String mssv) {
        String maLopCu = "";
        String sqlGetClass = "SELECT MaLopQuanLy FROM SinhVien WHERE MSSV = ?";

        Connection conn = DataBaseConnection.getConnection();
        try {
            conn.setAutoCommit(true); // 🌟 đảm bảo DELETE + UPDATE sĩ số sau đó luôn commit ngay

            try (PreparedStatement psGet = conn.prepareStatement(sqlGetClass)) {
                psGet.setString(1, mssv);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) maLopCu = rs.getString("MaLopQuanLy");
                }
            }
            
            String sqlDelete = "DELETE FROM SinhVien WHERE MSSV = ?";
            try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                psDel.setString(1, mssv);
                boolean isDeleted = psDel.executeUpdate() > 0;
                if (isDeleted && !maLopCu.isEmpty()) {
                    lopHocDAO.recalculateSiSo(maLopCu); // Cập nhật lại sĩ số lớp sau khi mất thành viên
                }
                return isDeleted;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic UC01.2: Tìm kiếm sinh viên theo MSSV hoặc Họ tên
    public List<SinhVien> search(String keyword) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT * FROM SinhVien WHERE MSSV LIKE ? OR HoTen LIKE ?";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SinhVien(
                        rs.getString("MSSV"),
                        rs.getString("HoTen"),
                        rs.getDate("NgaySinh"),
                        rs.getString("GioiTinh"),
                        rs.getString("MaLopQuanLy"),
                        rs.getString("TenDangNhap")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}