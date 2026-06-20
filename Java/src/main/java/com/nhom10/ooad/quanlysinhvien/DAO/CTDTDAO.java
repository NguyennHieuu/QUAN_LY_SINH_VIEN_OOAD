package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.CTDT;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CTDTDAO {

    public List<CTDT> getAllCTDT() {
        List<CTDT> list = new ArrayList<>();
        String sql = "SELECT * FROM CTDT";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new CTDT(
                    rs.getString("MaCTDT"),
                    rs.getString("TenNganh"),
                    rs.getString("NienKhoa"),
                    rs.getInt("TongSoTC")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}