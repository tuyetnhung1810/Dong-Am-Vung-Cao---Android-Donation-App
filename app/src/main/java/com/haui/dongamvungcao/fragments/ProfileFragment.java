package com.haui.dongamvungcao.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper;
import com.haui.dongamvungcao.utils.UserSession;
import java.text.DecimalFormat;

public class ProfileFragment extends Fragment {

    // ĐÃ KHÔI PHỤC: Khai báo đầy đủ các biến TextView thông tin cá nhân
    private TextView tvProfName, tvProfPhone, tvProfEmail, tvProfAddress;
    private ImageView imgMenuMore;
    private UserSession session;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    private TextView tvProfDonationCount, tvProfDonationTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // ĐÃ BỔ SUNG: Ánh xạ đầy đủ các View bao gồm cả Địa chỉ cư trú
        tvProfName = view.findViewById(R.id.tvProfName);
        tvProfPhone = view.findViewById(R.id.tvProfPhone);
        tvProfEmail = view.findViewById(R.id.tvProfEmail);
        tvProfAddress = view.findViewById(R.id.tvProfAddress); // Thêm ánh xạ cho Địa chỉ cư trú

        imgMenuMore = view.findViewById(R.id.imgMenuMore);
        tvProfDonationCount = view.findViewById(R.id.tvProfDonationCount);
        tvProfDonationTotal = view.findViewById(R.id.tvProfDonationTotal);

        session = new UserSession(getContext());
        dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();

        imgMenuMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), imgMenuMore);
            popupMenu.getMenu().add(0, 1, 0, "Cập nhật thông tin");
            popupMenu.getMenu().add(0, 2, 1, "Đăng xuất");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    showUpdateProfileDialog();
                    return true;
                } else if (item.getItemId() == 2) {
                    session.logout();
                    Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), com.haui.dongamvungcao.activities.LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        // Xử lý click mở trang Giấy chứng nhận Thành tựu cá nhân
        LinearLayout layoutAchievements = view.findViewById(R.id.layoutAchievements);
        if (layoutAchievements != null) {
            layoutAchievements.setOnClickListener(v -> {
                if (session.isLoggedIn()) {
                    String countText = tvProfDonationCount.getText().toString();
                    if (countText.equals("0 lần")) {
                        Toast.makeText(getContext(), "Bạn cần thực hiện quyên góp ít nhất 1 lần để mở khóa thành tựu này!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), com.haui.dongamvungcao.activities.CertificateActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập để xem thành tựu!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (session != null && session.isLoggedIn()) {
            loadCurrentUserData();
        }
    }

    private void loadCurrentUserData() {
        String currentUsername = session.getUsername();

        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }

        Cursor cursor = database.query("User", null, "username = ?", new String[]{currentUsername}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIdx = cursor.getColumnIndex("fullName");
                int phoneIdx = cursor.getColumnIndex("phone");
                int emailIdx = cursor.getColumnIndex("email");
                // ĐÃ BỔ SUNG: Tìm kiếm index cột address trong database
                int addressIdx = cursor.getColumnIndex("address");

                String name = nameIdx != -1 ? cursor.getString(nameIdx) : "";
                String phone = phoneIdx != -1 ? cursor.getString(phoneIdx) : "";
                String email = emailIdx != -1 ? cursor.getString(emailIdx) : "";
                // ĐÃ BỔ SUNG: Lấy dữ liệu chuỗi địa chỉ ra
                String address = addressIdx != -1 ? cursor.getString(addressIdx) : "";

                if (tvProfName != null) tvProfName.setText(currentUsername);
                if (tvProfPhone != null) tvProfPhone.setText(phone.isEmpty() ? "Chưa cập nhật" : phone);
                if (tvProfEmail != null) tvProfEmail.setText(email.isEmpty() ? "Chưa cập nhật" : email);

                // ĐÃ BỔ SUNG: Đổ dữ liệu địa chỉ lên màn hình hiển thị chính của Fragment
                if (tvProfAddress != null) tvProfAddress.setText(address.isEmpty() ? "Chưa cập nhật" : address);

                loadDonationStats(currentUsername, name);
            }
            cursor.close();
        }
    }

    private void loadDonationStats(String username, String fullName) {
        Cursor statsCursor = null;
        try {
            String query = "SELECT COUNT(*) as total_count, SUM(CAST(quantity AS INTEGER)) as total_amount " +
                    "FROM Donation WHERE LOWER(trim(donorName)) = LOWER(trim(?)) " +
                    "OR (LOWER(trim(donorName)) = LOWER(trim(?)) AND ? != '')";

            statsCursor = database.rawQuery(query, new String[]{username, fullName, fullName != null ? fullName : ""});

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

    private void showUpdateProfileDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_profile, null);

        final EditText edtName = dialogView.findViewById(R.id.dialogEdtName);
        final EditText edtPhone = dialogView.findViewById(R.id.dialogEdtPhone);
        final EditText edtEmail = dialogView.findViewById(R.id.dialogEdtEmail);
        final EditText edtAddress = dialogView.findViewById(R.id.dialogEdtAddress);
        Button btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        Button btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        String currentUsername = session.getUsername();
        Cursor cursor = database.query("User", null, "username = ?", new String[]{currentUsername}, null, null, null);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                Toast.makeText(getContext(), "Họ tên và Email không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("fullName", name);
            values.put("phone", phone);
            values.put("email", email);
            values.put("address", address);

            int rows = database.update("User", values, "username = ?", new String[]{currentUsername});
            if (rows > 0) {
                try {
                    ContentValues notifyValues = new ContentValues();
                    notifyValues.put("title", "Cập nhật hồ sơ 📝");
                    notifyValues.put("content", "Hồ sơ cá nhân của bạn đã được chỉnh sửa và đồng bộ hóa thành công.");
                    notifyValues.put("time", "Vừa xong");
                    database.insert("Notification", null, notifyValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                loadCurrentUserData();
                alertDialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Lỗi cập nhật dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}