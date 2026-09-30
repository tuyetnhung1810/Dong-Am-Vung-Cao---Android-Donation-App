package com.haui.dongamvungcao.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "UserPref";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Các hằng số Key để tránh việc gõ nhầm chữ giữa các hàm
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";

    public UserSession(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Tạo phiên đăng nhập khi thành công (Đã chuyển sang dùng apply() tối ưu bộ nhớ)
    public void createLoginSession(String username, String name, String email, String address) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

    // Kiểm tra trạng thái đã đăng nhập chưa
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy họ và tên
    public String getName() {
        return pref.getString(KEY_NAME, "Người dùng HaUI");
    }

    // Lấy Email
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    // Lấy Địa chỉ
    public String getAddress() {
        return pref.getString(KEY_ADDRESS, "Chưa cập nhật");
    }

    // Lấy Tên tài khoản (Username) - Đã làm sạch code
    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    // Đăng xuất và xóa sạch cấu hình phiên làm việc
    public void logout() {
        editor.clear();
        editor.apply();
    }
}