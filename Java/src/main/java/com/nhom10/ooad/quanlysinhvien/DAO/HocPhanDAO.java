package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.HocPhan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tầng DAO cho thực thể Học phần (HocPhan)
 * Phục vụ nghiệp vụ tra cứu danh mục học phần cố định của Giáo vụ (UC04.2)
 */
public class HocPhanDAO {

    /**
     * Logic UC04.2: Tìm kiếm học phần theo từ khóa (Mã HP hoặc Tên HP)
     * @param keyword Từ khóa tìm kiếm do người dùng nhập
     * @return Danh sách các học phần khớp với tiêu chí
     */
    public List<HocPhan> searchHocPhan(String keyword) {
        List<HocPhan> list = new ArrayList<>();
        // Truy vấn dựa trên các thuộc tính: maHP, tenHP, soTC, loaiHP, MaCTDT trong báo cáo
        String sql = "SELECT * FROM HocPhan WHERE maHP LIKE ? OR tenHP LIKE ?";
        
        try (Connection conn = DataBaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HocPhan hp = new HocPhan(
                        rs.getString("maHP"),
                        rs.getString("tenHP"),
                        rs.getInt("soTC"),
                        rs.getString("loaiHP"),
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
     * Phục vụ hiển thị mặc định trên giao diện tra cứu
     */
    public List<HocPhan> getAllHocPhan() {
        List<HocPhan> list = new ArrayList<>();
        String sql = "SELECT * FROM HocPhan";
        
        try (Connection conn = DataBaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new HocPhan(
                    rs.getString("maHP"),
                    rs.getString("tenHP"),
                    rs.getInt("soTC"),
                    rs.getString("loaiHP"),
                    rs.getString("MaCTDT")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}