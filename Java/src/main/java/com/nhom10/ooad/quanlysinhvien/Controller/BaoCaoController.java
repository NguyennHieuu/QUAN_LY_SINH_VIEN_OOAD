package com.nhom10.ooad.quanlysinhvien.Controller;

import java.util.List;

import com.nhom10.ooad.quanlysinhvien.DAO.BaoCaoDAO;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.ThongKeDoanhThu;

/**
 * BaoCaoController - dieu phoi nghiep vu BAO CAO TAI CHINH.
 *
 * Phu trach:
 *   UC08.2 Lap bao cao tai chinh -> baoCaoDoanhThu(...) / baoCaoDoanhThuTatCaKy()
 *   + Danh sach cong no          -> baoCaoCongNo(...)
 *   (UC08.3 Xuat file thuc hien o tang View bang lop util.CsvExporter)
 */
public class BaoCaoController {

    private final BaoCaoDAO baoCaoDAO = new BaoCaoDAO();

    /** Bao cao doanh thu 1 hoc ky. */
    public ThongKeDoanhThu baoCaoDoanhThu(String maHocKy) {
        if (maHocKy == null || maHocKy.trim().isEmpty())
            throw new IllegalArgumentException("Vui lòng nhập mã học kỳ.");
        return baoCaoDAO.doanhThuHocKy(maHocKy.trim());
    }

    /** Bao cao doanh thu tat ca cac hoc ky. */
    public List<ThongKeDoanhThu> baoCaoDoanhThuTatCaKy() {
        return baoCaoDAO.doanhThuTatCaKy();
    }

    /** Bao cao danh sach cong no cua 1 hoc ky. */
    public List<HoaDonView> baoCaoCongNo(String maHocKy) {
        if (maHocKy == null || maHocKy.trim().isEmpty())
            throw new IllegalArgumentException("Vui lòng nhập mã học kỳ.");
        return baoCaoDAO.danhSachNo(maHocKy.trim());
    }
}