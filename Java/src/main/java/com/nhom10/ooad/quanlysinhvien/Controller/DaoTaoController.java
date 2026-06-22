package com.nhom10.ooad.quanlysinhvien.Controller;

import com.nhom10.ooad.quanlysinhvien.Model.HocPhan;
import java.util.ArrayList;
import java.util.List;

/**
 * Thẻ CRC ID: 14 - Lớp điều khiển DaoTaoController
 */
public class DaoTaoController {

    // Logic UC04.2: Tra cứu học phần theo từ khóa (Mã hoặc Tên) [7]
    public List<HocPhan> traCuuHocPhan(String keyword) {
        // Logic sẽ gọi HocPhanDAO để truy vấn DB SQL Server
        // Đảm bảo lọc dữ liệu khớp với tiêu chí tìm kiếm
        return new ArrayList<>(); 
    }

    // Logic: Tra cứu điều kiện tiên quyết của học phần [6, 8]
    public List<String> getMonTienQuyet(String maHP) {
        // Logic truy xuất bảng điều kiện học phần trong DB
        return new ArrayList<>();
    }
}