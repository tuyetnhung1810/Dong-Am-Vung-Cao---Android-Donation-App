package com.haui.dongamvungcao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.haui.dongamvungcao.models.Campaign;
import java.util.ArrayList;
import java.util.List;

public class CampaignDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public CampaignDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // HÀM INSERT: Khớp chuẩn cột với SQLite
    public boolean insertCampaign(String title, String description, String imageUrl, int targetQty, int currentQty, String status) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("imageUrl", imageUrl);
        values.put("targetQty", targetQty);
        values.put("currentQty", currentQty);
        values.put("status", status);

        try {
            long result = database.insertOrThrow("Campaign", null, values);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("LOI_CHUYEN_DOI_SQL", "Lỗi chèn chiến dịch: " + e.getMessage());
            return false;
        }
    }

    // HÀM SELECT: Đã đồng bộ đọc thêm cột "imageUrl" và "status" từ Cursor
    public List<Campaign> getAllCampaigns() {
        List<Campaign> list = new ArrayList<>();

        Cursor cursor = database.query("Campaign", null, null, null, null, null, "id DESC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndex("id");
                int titleIdx = cursor.getColumnIndex("title");
                int descIdx = cursor.getColumnIndex("description");

                // ĐÃ THÊM: Tìm vị trí cột imageUrl trong database
                int imgIdx = cursor.getColumnIndex("imageUrl");

                int targetIdx = cursor.getColumnIndex("targetQty");
                int currentIdx = cursor.getColumnIndex("currentQty");
                int statusIdx = cursor.getColumnIndex("status");

                do {
                    Campaign campaign = new Campaign();
                    campaign.setId(idIdx != -1 ? cursor.getInt(idIdx) : 0);
                    campaign.setTitle(titleIdx != -1 ? cursor.getString(titleIdx) : "");
                    campaign.setDescription(descIdx != -1 ? cursor.getString(descIdx) : "");

                    // ĐÃ THÊM: Đọc chuỗi tên ảnh từ SQLite gán vào model Campaign
                    // Lưu ý: Nếu trong lớp Campaign của cậu hàm set tên ảnh là setImage() thì cậu sửa setImageUrl thành setImage nhé!
                    campaign.setImageUrl(imgIdx != -1 ? cursor.getString(imgIdx) : "");

                    campaign.setTargetQty(targetIdx != -1 ? cursor.getInt(targetIdx) : 0);
                    campaign.setCurrentQty(currentIdx != -1 ? cursor.getInt(currentIdx) : 0);

                    // Đọc chuỗi status ("dang_dien_ra"/"sap_dien_ra") gán vào model Campaign
                    campaign.setStatus(statusIdx != -1 ? cursor.getString(statusIdx) : "sap_dien_ra");

                    list.add(campaign);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}