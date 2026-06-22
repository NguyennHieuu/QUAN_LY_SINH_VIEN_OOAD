package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.HocPhan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TẦNG DAO DÙNG CHUNG CHO THỰC THỂ HỌC PHẦN (HOC PHAN)
 * Phục vụ đồng thời:
 * - Phân hệ Giáo vụ: Tra cứu, hiển thị danh mục học phần tĩnh (Hoài Anh phụ trách)
 * - Phân hệ Sinh viên: Kiểm tra ràng buộc môn tiên quyết, hiển thị tín chỉ (Trung Hiếu phụ trách)
 */
public class HocPhanDAO {

    // ====================================================================
    // VÙNG CODE PHỤC VỤ PHÂN HỆ GIÁO VỤ (HOÀI ANH)
    // ====================================================================

    /**
     * Logic UC04.2: Tìm kiếm học phần theo từ khóa (Mã HP hoặc Tên HP)
     * @param keyword Từ khóa tìm kiếm do người dùng nhập từ giao diện giáo vụ
     * @return Danh sách các học phần khớp với tiêu chí
     */
    public List<HocPhan> searchHocPhan(String keyword) {
        List<HocPhan> list = new ArrayList<>();
        String sql = "SELECT * FROM HocPhan WHERE MaHP LIKE ? OR TenHP LIKE ?";
        
        try (Connection conn = DataBaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HocPhan hp = new HocPhan(
                        rs.getString("MaHP"),
                        rs.getString("TenHP"),
                        rs.getInt("SoTC"),
                        rs.getString("LoaiHP"),
                        rs.getString("MaCTDT")
                    );
                    list.add(hp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy toàn bộ danh mục học phần tĩnh từ hệ thống
     * Phục vụ hiển thị mặc định trên giao diện tra cứu của Giáo vụ
     */
    public List<HocPhan> getAllHocPhan() {
        List<HocPhan> list = new ArrayList<>();
        String sql = "SELECT * FROM HocPhan";
        
        try (Connection conn = DataBaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new HocPhan(
                    rs.getString("MaHP"),
                    rs.getString("TenHP"),
                    rs.getInt("SoTC"),
                    rs.getString("LoaiHP"),
                    rs.getString("MaCTDT")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====================================================================
    // VÙNG CODE PHỤC VỤ PHÂN HỆ SINH VIÊN (TRUNG HIẾU)
    // ====================================================================

    /**
     * Hàm lấy thông tin chi tiết của 1 Học phần (Mã, Tên môn, Số TC, Loại HP...)
     * Phục vụ hiển thị Tên học phần và lấy số Tín chỉ chuẩn trên giao diện đăng ký học
     */
    public HocPhan getHocPhanById(String maHP) {
        String sql = "SELECT * FROM HocPhan WHERE MaHP = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HocPhan hp = new HocPhan(
                        rs.getString("MaHP"),
                        rs.getString("TenHP"),
                        rs.getInt("SoTC"),
                        rs.getString("LoaiHP"),
                        rs.getString("MaCTDT")
                    );
                    return hp;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm học phần theo mã: " + maHP);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Quét bảng DieuKienHocPhan nhặt ra toàn bộ mã môn tiên quyết đổ vào List<String> của Model
     * Phục vụ bộ lọc kiểm tra bảo mật logic chặn đăng ký môn của Sinh viên
     */
    public List<String> getDanhSachMonTienQuyet(String maHP) {
        List<String> dsMonTienQuyet = new ArrayList<>();
        String sql = "SELECT MaHPDieuKien FROM DieuKienHocPhan WHERE MaHP = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHP);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsMonTienQuyet.add(rs.getString("MaHPDieuKien"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMonTienQuyet;
    }
}