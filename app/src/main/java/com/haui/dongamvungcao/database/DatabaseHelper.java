package com.haui.dongamvungcao.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DongAmHaui.db";
    // Đã nâng version lên 3 để ép buộc hệ thống xóa sạch rác và kích hoạt lại toàn bộ bảng
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Chiến dịch
        String createCampaignTable = "CREATE TABLE Campaign (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "imageUrl TEXT, " +
                "targetQty INTEGER, " +
                "currentQty INTEGER," +
                "status TEXT)";
        db.execSQL(createCampaignTable);

        // 2. Tạo bảng Quyên góp
        String createDonationTable = "CREATE TABLE Donation (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "campaignId INTEGER, " +
                "donorName TEXT, " +
                "quantity INTEGER, " +
                "date TEXT, " +
                "message TEXT)";
        db.execSQL(createDonationTable);

        // 3. Tạo bảng Người dùng
        String createUserTable = "CREATE TABLE User (" +
                "username TEXT PRIMARY KEY, " +
                "fullName TEXT, " +
                "password TEXT, " +
                "phone TEXT, " +
                "email TEXT, " +
                "address TEXT)";
        db.execSQL(createUserTable);

        // 4. Tạo bảng Thông báo
        String createNotificationTable = "CREATE TABLE Notification (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "time TEXT)";
        db.execSQL(createNotificationTable);
        try {
            db.execSQL("INSERT INTO Notification (title, content, time) VALUES ('🔥 CHIẾN DỊCH SẮP KẾT THÚC', 'Chiến dịch Quyên góp áo ấm Bản Mù chỉ còn 2 ngày nữa là đóng cổng tiếp nhận!', 'Hôm nay')");
            db.execSQL("INSERT INTO Notification (title, content, time) VALUES ('📅 CHIẾN DỊCH SẮP DIỄN RA', 'Chiến dịch Đông Ấm Hà Giang 2026 đang hoàn thiện hồ sơ khảo sát, dự kiến phát động vào tuần sau.', 'Hôm qua')");
            db.execSQL("INSERT INTO Notification (title, content, time) VALUES ('🎉 ĐẠT CHỈ TIÊU ĐÓNG GÓP', 'Đông Ấm HaUI chúc mừng chiến dịch Sách vở cho em tới trường đạt mốc 100% mục tiêu quỹ.', '12/06/2026')");
            db.execSQL("INSERT INTO Notification (title, content, time) VALUES ('Chào mừng bạn mới', 'Chào mừng bạn đến với ứng dụng tình nguyện Đông Ấm Vùng Cao HaUI.', '10/06/2026')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ĐÃ SỬA: Bổ sung xóa bảng Notification để dọn sạch rác cấu trúc cũ hoàn toàn
        db.execSQL("DROP TABLE IF EXISTS Donation");
        db.execSQL("DROP TABLE IF EXISTS Campaign");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Notification"); // <-- BẮT BUỘC PHẢI CÓ DÒNG NÀY

        // Khởi tạo lại cấu trúc mới tinh sạch sẽ
        onCreate(db);
    }
}