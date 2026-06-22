package com.nhom10.ooad.quanlysinhvien.Model;

import java.util.ArrayList;
import java.util.List;

public class HocPhan {
    private String maHP;
    private String tenHP;
    private int soTC;
    private String loaiHP;
    private String maCTDT;
    
    // Đưa danh sách mã học phần điều kiện/tiên quyết làm thuộc tính trực tiếp
    private List<String> dsMaHPTienQuyet; 

    // Constructor rỗng (Khởi tạo sẵn danh sách rỗng để tránh lỗi NullPointerException)
    public HocPhan() {
        this.dsMaHPTienQuyet = new ArrayList<>();
    }

    // Constructor đầy đủ tham số (Không bao gồm danh sách - sẽ add vào sau khi truy vấn)
    public HocPhan(String maHP, String tenHP, int soTC, String loaiHP, String maCTDT) {
        this.maHP = maHP;
        this.tenHP = tenHP;
        this.soTC = soTC;
        this.loaiHP = loaiHP;
        this.maCTDT = maCTDT;
        this.dsMaHPTienQuyet = new ArrayList<>();
    }

    // Constructor đầy đủ bao gồm cả danh sách môn tiên quyết
    public HocPhan(String maHP, String tenHP, int soTC, String loaiHP, String maCTDT, List<String> dsMaHPTienQuyet) {
        this.maHP = maHP;
        this.tenHP = tenHP;
        this.soTC = soTC;
        this.loaiHP = loaiHP;
        this.maCTDT = maCTDT;
        this.dsMaHPTienQuyet = dsMaHPTienQuyet;
    }

    // Getter và Setter cho các thuộc tính cơ bản
    public String getMaHP() { return maHP; }
    public void setMaHP(String maHP) { this.maHP = maHP; }

    public String getTenHP() { return tenHP; }
    public void setTenHP(String tenHP) { this.tenHP = tenHP; }

    public int getSoTC() { return soTC; }
    public void setSoTC(int soTC) { this.soTC = soTC; }

    public String getLoaiHP() { return loaiHP; }
    public void setLoaiHP(String loaiHP) { this.loaiHP = loaiHP; }

    public String getMaCTDT() { return maCTDT; }
    public void setMaCTDT(String maCTDT) { this.maCTDT = maCTDT; }

    // Getter và Setter cho danh sách môn tiên quyết
    public List<String> getDsMaHPTienQuyet() { return dsMaHPTienQuyet; }
    public void setDsMaHPTienQuyet(List<String> dsMaHPTienQuyet) { this.dsMaHPTienQuyet = dsMaHPTienQuyet; }
    
    /**
     * Hàm tiện ích giúp thêm nhanh một mã môn tiên quyết vào danh sách
     */
    public void addHocPhanTienQuyet(String maHPTienQuyet) {
        if (this.dsMaHPTienQuyet == null) {
            this.dsMaHPTienQuyet = new ArrayList<>();
        }
        this.dsMaHPTienQuyet.add(maHPTienQuyet);
    }
}