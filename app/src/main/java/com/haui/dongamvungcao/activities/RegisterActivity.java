package com.haui.dongamvungcao.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText regUsername, regFullName, regPassword, regPhone, regEmail, regAddress;
    private Button btnSubmitRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Ánh xạ các View từ giao diện XML Đăng ký
        regUsername = findViewById(R.id.regUsername);
        regFullName = findViewById(R.id.regFullName);
        regPassword = findViewById(R.id.regPassword);
        regPhone = findViewById(R.id.regPhone);
        regEmail = findViewById(R.id.regEmail);
        regAddress = findViewById(R.id.regAddress);
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);

        dbHelper = new DatabaseHelper(this);
// 1. Khai báo biến ánh xạ nút Hủy đăng ký
        Button btnRegisterCancel = findViewById(R.id.btnRegisterCancel);

// 2. Bắt sự kiện click để kết thúc Activity, tự động quay về trang trước
        btnRegisterCancel.setOnClickListener(v -> {
            finish();
        });
        // 2. Xử lý sự kiện khi nhấn nút Đăng ký tài khoản
        btnSubmitRegister.setOnClickListener(v -> {
            String user = regUsername.getText().toString().trim();
            String name = regFullName.getText().toString().trim();
            String pass = regPassword.getText().toString().trim();
            String phone = regPhone.getText().toString().trim();
            String email = regEmail.getText().toString().trim();
            String addr = regAddress.getText().toString().trim();

            // Kiểm tra các trường bắt buộc không được để trống
            if (user.isEmpty() || name.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đủ các thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = null;
            Cursor cursor = null;

            try {
                db = dbHelper.getWritableDatabase();

                // LƯU Ý QUAN TRỌNG: Kiểm tra chủ động xem Username đã tồn tại trong DB chưa
                cursor = db.rawQuery("SELECT * FROM User WHERE username = ?", new String[]{user});
                if (cursor != null && cursor.getCount() > 0) {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.", Toast.LENGTH_SHORT).show();
                    return; // Dừng xử lý, không chèn trùng
                }

                // Chuẩn bị dữ liệu để chèn vào bảng
                ContentValues values = new ContentValues();
                values.put("username", user);
                values.put("password", pass);

                // MẸO: Hãy chắc chắn rằng chữ "fullName", "phone", "email", "address"
                // viết hoa viết thường khớp 100% với các cột trong file DatabaseHelper.java của cậu nhé!
                values.put("fullName", name);
                values.put("phone", phone);
                values.put("email", email);
                values.put("address", addr);

                // Tiến hành chèn dữ liệu vào bảng User
                long result = db.insert("User", null, values);

                if (result != -1) {
                    Toast.makeText(this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Kết thúc màn hình đăng ký, quay về màn hình Login
                } else {
                    Toast.makeText(this, "Lỗi: Không thể lưu dữ liệu vào cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Hiển thị trực tiếp lỗi hệ thống lên màn hình để dễ bắt bệnh khi debug
                Toast.makeText(this, "Lỗi hệ thống: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                // Giải phóng Cursor để tránh tràn RAM máy ảo
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }
}