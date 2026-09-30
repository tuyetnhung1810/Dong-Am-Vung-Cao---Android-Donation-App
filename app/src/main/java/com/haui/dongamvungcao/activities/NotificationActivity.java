package com.haui.dongamvungcao.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper;

public class NotificationActivity extends AppCompatActivity {

    private ListView lvNotifications;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lvNotifications = findViewById(R.id.lvNotifications);
        Toolbar toolbar = findViewById(R.id.toolbarNotification);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Tự động nạp dữ liệu thông báo hệ thống và trạng thái chiến dịch
        initDefaultNotifications();

        // Đổ dữ liệu ra màn hình
        loadNotifications();
    }

    private void loadNotifications() {
        try {
            if (database == null || !database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }

            Cursor cursor = database.rawQuery("SELECT id AS _id, title, content, time FROM Notification ORDER BY id DESC", null);

            String[] from = new String[]{"title", "content", "time"};
            int[] to = new int[]{android.R.id.text1, android.R.id.text2};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    from,
                    to,
                    0
            ) {
                @Override
                public void bindView(View view, android.content.Context context, Cursor cursor) {
                    TextView text1 = view.findViewById(android.R.id.text1);
                    TextView text2 = view.findViewById(android.R.id.text2);

                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));

                    // Cấu hình hiển thị tiêu đề rõ ràng
                    text1.setText(title);
                    text1.setTypeface(null, android.graphics.Typeface.BOLD);
                    text1.setTextColor(0xFF333333);

                    // Hiển thị nội dung chi tiết kèm thời gian rõ ràng
                    text2.setText(content + " [" + time + "]");
                    text2.setTextColor(0xFF666666);
                }
            };
            lvNotifications.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ĐÃ THÊM: Tạo danh sách thông báo mẫu về các chiến dịch theo yêu cầu của bạn
    // THAY THẾ TOÀN BỘ HÀM NÀY TRONG NotificationActivity.java
    private void initDefaultNotifications() {
        Cursor cursor = null;
        try {
            if (database == null || !database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }

            cursor = database.rawQuery("SELECT COUNT(*) FROM Notification", null);
            if (cursor != null && cursor.moveToFirst() && cursor.getInt(0) == 0) {

                // 1. Thông báo chiến dịch sắp kết thúc
                android.content.ContentValues cv1 = new android.content.ContentValues();
                cv1.put("title", "🔥 CHIẾN DỊCH SẮP KẾT THÚC");
                cv1.put("content", "Chiến dịch Quyên góp áo ấm Bản Mù chỉ còn 2 ngày nữa là đóng cổng tiếp nhận!");
                cv1.put("time", "Hôm nay");
                database.insert("Notification", null, cv1);

                // 2. Thông báo chiến dịch sắp diễn ra
                android.content.ContentValues cv2 = new android.content.ContentValues();
                cv2.put("title", "📅 CHIẾN DỊCH SẮP DIỄN RA");
                cv2.put("content", "Chiến dịch Đông Ấm Hà Giang 2026 đang hoàn thiện hồ sơ khảo sát, dự kiến phát động vào tuần sau.");
                cv2.put("time", "Hôm qua");
                database.insert("Notification", null, cv2);

                // 3. Thông báo chiến dịch đạt chỉ tiêu
                android.content.ContentValues cv3 = new android.content.ContentValues();
                cv3.put("title", "🎉 ĐẠT CHỈ TIÊU ĐÓNG GÓP");
                cv3.put("content", "Đông Ấm HaUI chúc mừng chiến dịch Sách vở cho em tới trường đạt mốc 100% mục tiêu quỹ.");
                cv3.put("time", "12/06/2026");
                database.insert("Notification", null, cv3);

                // 4. Thông báo chào mừng
                android.content.ContentValues cv4 = new android.content.ContentValues();
                cv4.put("title", "Chào mừng bạn mới");
                cv4.put("content", "Chào mừng bạn đến với ứng dụng tình nguyện Đông Ấm Vùng Cao HaUI.");
                cv4.put("time", "10/06/2026");
                database.insert("Notification", null, cv4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}