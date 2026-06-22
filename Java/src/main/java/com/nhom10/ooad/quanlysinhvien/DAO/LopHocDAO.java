package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.LopHoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tầng DAO cho thực thể Lớp học hành chính (LopHoc)
 * Phục vụ nhóm chức năng quản lý lớp của Giáo vụ (UC02)
 */
public class LopHocDAO {

    /**
     * UC02.1: Thêm mới một lớp hành chính vào hệ thống
     */
    public boolean insert(LopHoc lop) {
        // 🌟 ĐÃ SỬA: Đổi tên cột cuối cùng thành MaGV cho khớp file SQL script
        String sql = "INSERT INTO LopHoc (MaLopQuanLy, TenLop, SiSo, MaGV) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lop.getMaLopQuanLy());
            ps.setString(2, lop.getTenLop());
            ps.setInt(3, lop.getSiSo());
            ps.setString(4, lop.getMaGV()); 
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC02.2: Cập nhật thông tin lớp học (Tên lớp, Giáo viên quản lý)
     * Giữ nguyên Khóa chính MaLopQuanLy và Sĩ số lớp
     */
    public boolean update(LopHoc lop) {
        // 🌟 ĐÃ SỬA: Đổi MaGVQL = ? thành MaGV = ?
        String sql = "UPDATE LopHoc SET TenLop = ?, MaGV = ? WHERE MaLopQuanLy = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, lop.getTenLop());
            ps.setString(2, lop.getMaGV()); 
            ps.setString(3, lop.getMaLopQuanLy());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC02.3: Xóa lớp hành chính khỏi hệ thống
     * ⚠️ RÀNG BUỘC NGHIỆP VỤ BẢO MẬT: Chỉ thực thi lệnh DELETE nếu sĩ số lớp = 0
     */
    public boolean delete(String maLopQuanLy) {
        String sqlCheck = "SELECT SiSo FROM LopHoc WHERE MaLopQuanLy = ?";
        String sqlDelete = "DELETE FROM LopHoc WHERE MaLopQuanLy = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            
            psCheck.setString(1, maLopQuanLy);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    int siSoHienTai = rs.getInt("SiSo");
                    if (siSoHienTai > 0) {
                        System.out.println("-> Chặn hành động: Lớp " + maLopQuanLy + " hiện có sĩ số > 0, không được xóa!");
                        return false; 
                    }
                }
            }
            
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setString(1, maLopQuanLy);
                return psDelete.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🌟 THÊM MỚI ĐỒNG BỘ: Tra cứu, lọc danh sách lớp học theo Mã lớp hoặc Tên lớp
     */
    public List<LopHoc> search(String keyword) {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM LopHoc WHERE MaLopQuanLy LIKE ? OR TenLop LIKE ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LopHoc lop = new LopHoc();
                    lop.setMaLopQuanLy(rs.getString("MaLopQuanLy"));
                    lop.setTenLop(rs.getString("TenLop"));
                    lop.setSiSo(rs.getInt("SiSo"));
                    lop.setMaGV(rs.getString("MaGV")); // 🌟 ĐÃ SỬA: Lấy từ cột "MaGV" chuẩn thay vì "MaGVQL"
                    list.add(lop);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực hiện tìm kiếm lớp học với từ khóa: " + keyword);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * HÀM PHỤC VỤ TRANSACTION: Tự động giảm sĩ số lớp khi xóa sinh viên.
     */
    public boolean giamSiSoLop(Connection conn, String maLop) throws SQLException {
        String sql = "UPDATE LopHoc SET SiSo = SiSo - 1 WHERE MaLopQuanLy = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * HÀM DÙNG CHUNG: Tăng hoặc giảm sĩ số lớp linh hoạt theo số lượng
     */
    public boolean updateSiSo(String maLop, int soLuong) {
        String sql = "UPDATE LopHoc SET SiSo = SiSo + ? WHERE MaLopQuanLy = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, soLuong);
            ps.setString(2, maLop);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hàm phụ trợ: Lấy danh sách toàn bộ các lớp học hành chính
     */
    public List<LopHoc> getAllLopHoc() {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM LopHoc";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                LopHoc lop = new LopHoc();
                lop.setMaLopQuanLy(rs.getString("MaLopQuanLy"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setSiSo(rs.getInt("SiSo"));
                lop.setMaGV(rs.getString("MaGV")); // 🌟 ĐÃ SỬA: Đổi sang cột "MaGV" chuẩn
                list.add(lop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}