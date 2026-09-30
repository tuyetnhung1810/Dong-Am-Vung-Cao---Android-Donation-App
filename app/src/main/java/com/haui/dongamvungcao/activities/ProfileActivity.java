package com.haui.dongamvungcao.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper;
import com.haui.dongamvungcao.utils.UserSession;
import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {

    // ĐÃ KHÔI PHỤC: Thêm tvProfAddress để không bị lỗi đỏ và hiển thị được địa chỉ
    private TextView tvProfName, tvProfPhone, tvProfEmail, tvProfAddress;
    private ImageView imgMenuMore;
    private DatabaseHelper dbHelper;
    private UserSession session;
    private TextView tvProfSubUsername;
    private TextView tvProfDonationCount, tvProfDonationTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        // 1. Ánh xạ các thành phần giao diện
        tvProfName = findViewById(R.id.tvProfName);
        tvProfPhone = findViewById(R.id.tvProfPhone);
        tvProfEmail = findViewById(R.id.tvProfEmail);
        // ĐÃ KHÔI PHỤC: Ánh xạ lại ô Địa chỉ cư trú từ giao diện XML
        tvProfAddress = findViewById(R.id.tvProfAddress);

        imgMenuMore = findViewById(R.id.imgMenuMore);
        tvProfSubUsername = findViewById(R.id.tvProfSubUsername);
        tvProfDonationCount = findViewById(R.id.tvProfDonationCount);
        tvProfDonationTotal = findViewById(R.id.tvProfDonationTotal);

        // 2. Khởi tạo cấu hình hệ thống
        dbHelper = new DatabaseHelper(this);
        session = new UserSession(this);

        // 3. ĐÃ NÂNG CẤP: Bấm nút ba chấm sẽ hiện Menu gồm cả Cập nhật thông tin và Đăng xuất cho đồng bộ với Fragment
        imgMenuMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, imgMenuMore);
            popupMenu.getMenu().add(0, 1, 0, "Cập nhật thông tin");
            popupMenu.getMenu().add(0, 2, 1, "Đăng xuất");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    showUpdateProfileDialog(); // Mở Dialog sửa đổi thông tin cá nhân
                    return true;
                } else if (item.getItemId() == 2) {
                    session.logout();
                    Toast.makeText(this, "Đã đăng xuất tài khoản!", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            String currentUsername = session.getUsername();
            loadUserProfileData(currentUsername); // Tải lại toàn bộ dữ liệu realtime
        } else {
            navigateToLogin();
        }
    }


    private void loadUserProfileData(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM User WHERE username = ?", new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                // ĐÃ KHÔI PHỤC: Đọc trường address từ bảng User trong Database ra
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));

                if (tvProfName != null) tvProfName.setText(fullName);
                if (tvProfSubUsername != null) tvProfSubUsername.setText("Xin chào, " + username + "!");

                if (tvProfPhone != null) tvProfPhone.setText(phone == null || phone.trim().isEmpty() ? "Chưa cập nhật" : phone);
                if (tvProfEmail != null) tvProfEmail.setText(email == null || email.trim().isEmpty() ? "Chưa cập nhật" : email);

                // ĐÃ KHÔI PHỤC: Đổ dữ liệu địa chỉ cư trú ra ngoài màn hình chính của Activity
                if (tvProfAddress != null) tvProfAddress.setText(address == null || address.trim().isEmpty() ? "Chưa cập nhật" : address);

                Cursor statsCursor = null;
                try {
                    String query = "SELECT COUNT(*) as total_count, SUM(CAST(quantity AS INTEGER)) as total_amount " +
                            "FROM Donation WHERE LOWER(trim(donorName)) = LOWER(trim(?)) " +
                            "OR (LOWER(trim(donorName)) = LOWER(trim(?)) AND ? != '')";

                    statsCursor = db.rawQuery(query, new String[]{username, fullName, fullName != null ? fullName : ""});

                    if (statsCursor != null && statsCursor.moveToFirst()) {
                        int count = statsCursor.getInt(statsCursor.getColumnIndexOrThrow("total_count"));
                        long totalAmount = statsCursor.getLong(statsCursor.getColumnIndexOrThrow("total_amount"));

                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String formattedMoney = formatter.format(totalAmount) + "đ";

                        if (tvProfDonationCount != null) {
                            tvProfDonationCount.setText(count + " lần");
                        }
                        if (tvProfDonationTotal != null) {
                            tvProfDonationTotal.setText(totalAmount > 0 ? formattedMoney : "0đ");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (statsCursor != null) statsCursor.close();
                }
                // =========================================================================

            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu người dùng này!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi hiển thị dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }


    private void showUpdateProfileDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_profile, null);

        final EditText edtName = dialogView.findViewById(R.id.dialogEdtName);
        final EditText edtPhone = dialogView.findViewById(R.id.dialogEdtPhone);
        final EditText edtEmail = dialogView.findViewById(R.id.dialogEdtEmail);
        final EditText edtAddress = dialogView.findViewById(R.id.dialogEdtAddress);
        Button btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        Button btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        String currentUsername = session.getUsername();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("User", null, "username = ?", new String[]{currentUsername}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIdx = cursor.getColumnIndex("fullName");
            int phoneIdx = cursor.getColumnIndex("phone");
            int emailIdx = cursor.getColumnIndex("email");
            int addressIdx = cursor.getColumnIndex("address");

            edtName.setText(nameIdx != -1 ? cursor.getString(nameIdx) : "");
            edtPhone.setText(phoneIdx != -1 ? cursor.getString(phoneIdx) : "");
            edtEmail.setText(emailIdx != -1 ? cursor.getString(emailIdx) : "");
            edtAddress.setText(addressIdx != -1 ? cursor.getString(addressIdx) : "");
            cursor.close();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Họ tên và Email không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("fullName", name);
            values.put("phone", phone);
            values.put("email", email);
            values.put("address", address);

            int rows = db.update("User", values, "username = ?", new String[]{currentUsername});
            if (rows > 0) {
                try {
                    ContentValues notifyValues = new ContentValues();
                    notifyValues.put("title", "Cập nhật hồ sơ 📝");
                    notifyValues.put("content", "Hồ sơ cá nhân của bạn đã được chỉnh sửa và đồng bộ hóa thành công.");
                    notifyValues.put("time", "Vừa xong");
                    db.insert("Notification", null, notifyValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                loadUserProfileData(currentUsername); // Làm mới dữ liệu tức thì
                alertDialog.dismiss();
            } else {
                Toast.makeText(this, "Lỗi cập nhật dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    /**
     * Hàm tính tổng số lần và tổng số tiền quyên góp từ bảng Donation
     */
    private void loadDonationStats(SQLiteDatabase db, String username) {
        Cursor statsCursor = null;
        try {
            String query = "SELECT COUNT(*) as total_count, SUM(quantity) as total_amount FROM Donation WHERE donorName = ?";
            statsCursor = db.rawQuery(query, new String[]{username});

            if (statsCursor != null && statsCursor.moveToFirst()) {
                int count = statsCursor.getInt(statsCursor.getColumnIndexOrThrow("total_count"));
                long totalAmount = statsCursor.getLong(statsCursor.getColumnIndexOrThrow("total_amount"));

                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedMoney = formatter.format(totalAmount) + "đ";

                if (tvProfDonationCount != null) {
                    tvProfDonationCount.setText(count + " lần");
                }
                if (tvProfDonationTotal != null) {
                    tvProfDonationTotal.setText(totalAmount > 0 ? formattedMoney : "0đ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statsCursor != null) statsCursor.close();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}