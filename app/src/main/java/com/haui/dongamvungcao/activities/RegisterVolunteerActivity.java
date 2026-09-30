package com.haui.dongamvungcao.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper;
import com.haui.dongamvungcao.utils.DialogUtils;
import com.haui.dongamvungcao.utils.UserSession;
import java.util.ArrayList;

public class RegisterVolunteerActivity extends AppCompatActivity {

    private Spinner spnTimeSlot, spnCampaigns;
    private EditText edtVolunteerMessage;
    private Button btnSubmitVolunteer;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private UserSession session;
    private ArrayList<String> campaignList = new ArrayList<>();
    private ArrayAdapter<String> campaignAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_volunteer);

        Toolbar toolbar = findViewById(R.id.toolbarVolunteer);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        spnTimeSlot = findViewById(R.id.spnTimeSlot);
        spnCampaigns = findViewById(R.id.spnCampaigns);
        edtVolunteerMessage = findViewById(R.id.edtVolunteerMessage);
        btnSubmitVolunteer = findViewById(R.id.btnSubmitVolunteer);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        session = new UserSession(this);

        // 1. Cài đặt dữ liệu ComboBox thời gian rảnh
        String[] timeSlots = {"Tháng 06/2026 (Mùa hè xanh)", "Tháng 10/2026 (Khảo sát Đông ấm)", "Tháng 12/2026 (Đông ấm chính hội)"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeSlots);
        spnTimeSlot.setAdapter(timeAdapter);

        // 2. Thiết lập Adapter rỗng ban đầu cho ComboBox Chiến dịch
        campaignAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, campaignList);
        spnCampaigns.setAdapter(campaignAdapter);

        // 3. Sự kiện lọc động: Chọn thời gian nào -> Tự động quét DB tìm chiến dịch trạng thái đó
        spnTimeSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterCampaignsByTime(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 4. Xử lý ấn Xác nhận đăng ký tình nguyện viên
        btnSubmitVolunteer.setOnClickListener(v -> {
            if (!session.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để đăng ký!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (campaignList.isEmpty() || spnCampaigns.getSelectedItem() == null) {
                Toast.makeText(this, "Không có chiến dịch phù hợp vào thời gian này!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedTime = spnTimeSlot.getSelectedItem().toString();
            String selectedCampaign = spnCampaigns.getSelectedItem().toString();
            String message = edtVolunteerMessage.getText().toString().trim();
            String username = session.getUsername();

            // Thực hiện ghi nhận thông tin
            saveVolunteerRegistration(username, selectedTime, selectedCampaign, message);
        });
    }

    private void filterCampaignsByTime(int position) {
        campaignList.clear();
        Cursor cursor = null;
        try {
            // Lọc động trạng thái chiến dịch dựa theo mốc thời gian được chọn
            String statusFilter = "dang_dien_ra";
            if (position == 1) statusFilter = "sap_dien_ra";
            if (position == 2) statusFilter = "dang_dien_ra";

            cursor = database.query("Campaign", new String[]{"title"}, "status = ?", new String[]{statusFilter}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    campaignList.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        if (campaignList.isEmpty()) {
            campaignList.add("Không có chiến dịch nào");
        }
        campaignAdapter.notifyDataSetChanged();
    }

    private void saveVolunteerRegistration(String username, String time, String campaign, String msg) {
        try {
            // 1. Chèn thông báo xác nhận và lịch sử chi tiết cho chiến dịch cụ thể
            ContentValues notifyValues = new ContentValues();
            notifyValues.put("title", "Đăng ký tình nguyện thành công 📅");
            // THÊM DÒNG THÔNG BÁO CHO CHIẾN DỊCH NÀO THEO ĐÚNG YÊU CẦU CỦA BẠN:
            notifyValues.put("content", "Tài khoản [" + username + "] đã đăng ký tham gia thành công chiến dịch [" + campaign + "] vào lịch trình " + time + ".");
            notifyValues.put("time", "Vừa xong");
            database.insert("Notification", null, notifyValues);

            // 2. Kích hoạt SMS thông báo về máy ảo
            String userPhone = "0987654321";
            Cursor c = database.query("User", new String[]{"phone"}, "username = ?", new String[]{username}, null, null, null);
            if (c != null && c.moveToFirst()) {
                String p = c.getString(0);
                if (p != null && !p.isEmpty()) userPhone = p;
                c.close();
            }

            String smsBody = "Dong Am HaUI: Chuc mung ban da dang ky thanh cong di tinh nguyen chien dich [" + campaign + "] vao " + time + ". Quet ma vung cao!";
            com.haui.dongamvungcao.utils.MockNotificationUtils.simulateSmsMessage(this, userPhone, smsBody);

            // 3. Hiển thị thông báo thành công
            DialogUtils.showSuccessDialog(this, "Đăng ký lên đường chiến dịch " + campaign + " thành công!", this::finish);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi hệ thống đăng ký!", Toast.LENGTH_SHORT).show();
        }
    }
}