package com.haui.dongamvungcao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.haui.dongamvungcao.models.DonationDetail;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {
    private DatabaseHelper dbHelper;

    public DonationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Giữ lại hàm này để tránh lỗi đỏ code ở các file cũ, nhưng không cần gọi thủ công nữa
    public void open() {}
    public void close() {}

    /**
     * 🛠️ ĐÃ TỐI ƯU: Tự mở và tự đóng kết nối an toàn, chống xung đột luồng ghi với màn Profile
     */
    public boolean insertDonation(int campaignId, String donorName, int quantity, String date, String message) {
        // Tự động mở kết nối ghi trực tiếp tại hàm
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("campaignId", campaignId);
        values.put("donorName", donorName);
        values.put("quantity", quantity);
        values.put("date", date);
        values.put("message", message);

        long result = -1;
        try {
            result = database.insertOrThrow("Donation", null, values);
            Log.d("LUU_QUYEN_GOP_OK", "Đã chèn thành công bản ghi quyên góp cho: " + donorName + ", số tiền: " + quantity);
        } catch (Exception e) {
            Log.e("LOI_LUU_QUYEN_GOP", "Lỗi SQLite khi chèn dữ liệu: " + e.getMessage(), e);
        } finally {
            // Ép buộc đóng kết nối database ngay lập tức sau khi chèn xong để nhường quyền cho màn Profile đọc
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
        return result != -1;
    }

    // Lấy lịch sử đóng góp của một chiến dịch cụ thể
    public List<DonationDetail> getDonationsByCampaign(int targetCampaignId) {
        List<DonationDetail> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = database.query("Donation", null, "campaignId = ?",
                    new String[]{String.valueOf(targetCampaignId)}, null, null, "id DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex("id");
                int cIdIdx = cursor.getColumnIndex("campaignId");
                int nameIdx = cursor.getColumnIndex("donorName");
                int qtyIdx = cursor.getColumnIndex("quantity");
                int dateIdx = cursor.getColumnIndex("date");
                int msgIdx = cursor.getColumnIndex("message");

                do {
                    int id = idIdx != -1 ? cursor.getInt(idIdx) : 0;
                    int cId = cIdIdx != -1 ? cursor.getInt(cIdIdx) : 0;
                    String name = nameIdx != -1 ? cursor.getString(nameIdx) : "";
                    int qty = qtyIdx != -1 ? cursor.getInt(qtyIdx) : 0;
                    String date = dateIdx != -1 ? cursor.getString(dateIdx) : "";
                    String msg = msgIdx != -1 ? cursor.getString(msgIdx) : "";

                    list.add(new DonationDetail(id, cId, name, qty, date, msg));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (database != null && database.isOpen()) database.close();
        }
        return list;
    }

    // Lấy tất cả lịch sử đóng góp
    public List<DonationDetail> getAllDonations() {
        List<DonationDetail> list = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = database.query("Donation", null, null, null, null, null, "id DESC");

            if (cursor != null && cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex("id");
                int cIdIdx = cursor.getColumnIndex("campaignId");
                int nameIdx = cursor.getColumnIndex("donorName");
                int quantityIdx = cursor.getColumnIndex("quantity");
                int dateIdx = cursor.getColumnIndex("date");
                int msgIdx = cursor.getColumnIndex("message");

                do {
                    int id = idIdx != -1 ? cursor.getInt(idIdx) : 0;
                    int cId = cIdIdx != -1 ? cursor.getInt(cIdIdx) : 0;
                    String name = nameIdx != -1 ? cursor.getString(nameIdx) : "";
                    int qty = quantityIdx != -1 ? cursor.getInt(quantityIdx) : 0;
                    String date = dateIdx != -1 ? cursor.getString(dateIdx) : "";
                    String msg = msgIdx != -1 ? cursor.getString(msgIdx) : "";

                    list.add(new DonationDetail(id, cId, name, qty, date, msg));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (database != null && database.isOpen()) database.close();
        }
        return list;
    }
}