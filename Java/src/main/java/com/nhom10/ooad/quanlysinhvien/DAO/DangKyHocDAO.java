package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.DangKyHoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DangKyHocDAO {

    // Thêm bản ghi đăng ký mới khi sinh viên nhấn nút chọn môn thành công
    public boolean insert(DangKyHoc dkh) {
        String sql = "INSERT INTO DangKyHoc (MaDangKy, MSSV, MaLopHP, ThoiGianDKi, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dkh.getMaDangKy());
            ps.setString(2, dkh.getMssv());
            ps.setString(3, dkh.getMaLopHP());
            ps.setTimestamp(4, new java.sql.Timestamp(dkh.getThoiGianDangKy().getTime()));
            ps.setString(5, dkh.getTrangThai());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UC05.4: Xóa bản ghi khi sinh viên bấm Hủy môn học
    public boolean delete(String mssv, String maLopHP) {
        String sql = "DELETE FROM DangKyHoc WHERE MSSV = ? AND MaLopHP = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, mssv);
            ps.setString(2, maLopHP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách mã lớp học phần sinh viên ĐÃ đăng ký trong kỳ để check TRÙNG LỊCH học
    public List<String> getListByMSSV(String mssv, String maHocKy) {
        List<String> listMaLopHP = new ArrayList<>();
        String sql = "SELECT dkh.MaLopHP FROM DangKyHoc dkh " +
                     "JOIN LopHocPhan lhp ON dkh.MaLopHP = lhp.MaLopHP " +
                     "WHERE dkh.MSSV = ? AND lhp.MaHocKy = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listMaLopHP.add(rs.getString("MaLopHP"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listMaLopHP;
    }

    // ====================================================================
    // 🌟 PHÂN HỆ XỬ LÝ DÀNH CHO GIẢNG VIÊN (ĐÌNH VIỆT) - TỐI GIẢN LOGIC LẤY SV
    // ====================================================================

    /**
     * UC06.3: Truy xuất danh sách sinh viên thuộc lớp học phần.
     * 🌟 ĐÃ SỬA: LEFT JOIN với bảng BangDiem để lấy đúng điểm đã lưu (Điểm QT,
     * Điểm CK, Điểm tổng kết) và trạng thái Nháp/Chính thức THEO TỪNG SINH VIÊN
     * (trước đây hardcode "" nên điểm bị "biến mất" sau khi load lại trang).
     * Sinh viên chưa từng nhập điểm (chưa có dòng BangDiem) sẽ mặc định "Nháp"
     * và các ô điểm hiển thị trống.
     */
    public List<Object[]> getDanhSachSinhVienByLopHP(String maLopHP) {
        List<Object[]> listSinhVien = new ArrayList<>();
        
        String sql = "SELECT sv.MSSV, sv.HoTen, sv.NgaySinh, sv.MaLopQuanLy, " +
                     "       bd.DiemQT, bd.DiemCK, bd.DiemTongKet, " +
                     "       ISNULL(bd.TrangThai, N'Nháp') AS TrangThaiDiem " +
                     "FROM SinhVien sv " +
                     "INNER JOIN DangKyHoc dkh ON sv.MSSV = dkh.MSSV " +
                     "LEFT JOIN BangDiem bd ON bd.MSSV = dkh.MSSV AND bd.MaLopHP = dkh.MaLopHP " +
                     "WHERE dkh.MaLopHP = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLopHP);
            
            try (ResultSet rs = ps.executeQuery()) {
                int stt = 1;
                while (rs.next()) {
                    // Nếu chưa có bản ghi BangDiem (chưa từng lưu nháp) -> hiển thị ô trống
                    Object diemQT = rs.getObject("DiemQT");
                    Object diemCK = rs.getObject("DiemCK");
                    Object diemTK = rs.getObject("DiemTongKet");

                    Object[] rowData = new Object[] {
                        stt++,                                                  // Cột 0: STT
                        rs.getString("MSSV"),                                   // Cột 1: Mã SV
                        rs.getString("HoTen"),                                  // Cột 2: Họ và tên
                        rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toString() : "", // Cột 3: Ngày sinh
                        rs.getString("MaLopQuanLy"),                            // Cột 4: Lớp quản lý
                        diemQT != null ? diemQT : "",                           // Cột 5: Điểm QT
                        diemCK != null ? diemCK : "",                           // Cột 6: Điểm CK
                        diemTK != null ? diemTK : "",                           // Cột 7: Điểm tổng kết
                        rs.getString("TrangThaiDiem")                           // Cột 8: Trạng thái (Phục vụ khóa bảng)
                    };
                    listSinhVien.add(rowData);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách sinh viên của lớp học phần: " + maLopHP);
            e.printStackTrace();
        }
        return listSinhVien;
    }

    /**
     * 🌟 LƯU Ý: Hàm này hiện không còn được Controller/View phía giảng viên gọi tới
     * (sau khi sửa, trạng thái khóa bảng được lấy từ BangDiem.TrangThai theo từng
     * sinh viên thay vì LopHocPhan.TrangThai theo cả lớp). Có thể giữ lại để dùng
     * cho mục đích khác nếu cần, hoặc xóa nếu không dùng đến.
     */
    public boolean chotDiemChinhThucLop(String maLopHP) {
        String sql = "UPDATE LopHocPhan SET TrangThai = N'Chính thức' WHERE MaLopHP = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLopHP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực thi khóa chốt bảng điểm lớp học phần: " + maLopHP);
            e.printStackTrace();
            return false;
        }
    }
}