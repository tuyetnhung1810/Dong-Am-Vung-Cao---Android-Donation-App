package com.haui.dongamvungcao.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.haui.dongamvungcao.MainActivity;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper;
import com.haui.dongamvungcao.utils.UserSession;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView btnGotoRegister;
    private DatabaseHelper dbHelper;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Ánh xạ các View từ giao diện XML
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGotoRegister = findViewById(R.id.btnGotoRegister);

        // 2. Khởi tạo Database và Session quản lý đăng nhập
        dbHelper = new DatabaseHelper(this);
        session = new UserSession(this);
    // 1. Khai báo biến ở đầu class hoặc trong onCreate
        Button btnLoginCancel = findViewById(R.id.btnLoginCancel);

    // 2. Bắt sự kiện click đóng màn hình để quay lại trang trước đó
        btnLoginCancel.setOnClickListener(v -> {
            finish();
        });
        // 3. Kiểm tra trạng thái: Nếu đã đăng nhập trước đó thì vào thẳng màn hình chính (MainActivity)
        if (session.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // 4. Xử lý sự kiện khi người dùng nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            // Kiểm tra xem người dùng đã điền đủ thông tin chưa
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = null;
            Cursor cursor = null;

            try {
                // Truy vấn SQLite đọc dữ liệu kiểm tra tài khoản và mật khẩu
                db = dbHelper.getReadableDatabase();
                cursor = db.rawQuery("SELECT * FROM User WHERE username = ? AND password = ?", new String[]{user, pass});

                if (cursor != null && cursor.moveToFirst()) {
                    // ĐĂNG NHẬP THÀNH CÔNG: Đọc dữ liệu an toàn theo TÊN CỘT định nghĩa trong DB
                    String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                    String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));

                    // Lưu dữ liệu vào Session để sử dụng toàn ứng dụng
                    session.createLoginSession(user, fullName, email, address);

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển hướng sang MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // ĐĂNG NHẬP THẤT BẠI: Sai thông tin tài khoản hoặc mật khẩu
                    Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Bắt các lỗi truy vấn hệ thống (nếu có)
                Toast.makeText(this, "Lỗi đăng nhập: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                // Đảm bảo luôn đóng Cursor để giải phóng bộ nhớ, tránh rò rỉ bộ nhớ (Memory Leak)
                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        // 5. Chuyển sang màn hình Đăng ký tài khoản (RegisterActivity)
        btnGotoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}