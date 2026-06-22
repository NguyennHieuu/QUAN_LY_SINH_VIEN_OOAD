package com.nhom10.ooad.quanlysinhvien.Utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CsvExporter - ghi du lieu ra file CSV (mo duoc bang Excel).
 *
 * Diem ky thuat: ghi them BOM (\uFEFF) o dau file + encoding UTF-8
 * => Excel hien dung tieng Viet co dau, khong bi loi font.
 *
 * (Neu sau nay muon xuat .xlsx that thi dung thu vien Apache POI.)
 */
public class CSVExporter {

    /**
     * @param duongDan duong dan file dich, vd "D:/baocao.csv"
     * @param tieuDe   mang ten cot
     * @param duLieu   danh sach dong, moi dong la mang gia tri (String)
     */
    public static void xuatCSV(String duongDan, String[] tieuDe, List<String[]> duLieu)
            throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(duongDan), StandardCharsets.UTF_8))) {

            bw.write('\uFEFF'); // BOM cho Excel

            bw.write(noiDong(tieuDe));
            bw.newLine();

            for (String[] dong : duLieu) {
                bw.write(noiDong(dong));
                bw.newLine();
            }
        }
    }

    /** Noi 1 dong: boc moi o trong dau ngoac kep, escape dau ngoac kep ben trong. */
    private static String noiDong(String[] o) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < o.length; i++) {
            if (i > 0) sb.append(',');
            String gt = (o[i] == null) ? "" : o[i];
            gt = gt.replace("\"", "\"\"");
            sb.append('"').append(gt).append('"');
        }
        return sb.toString();
    }
}