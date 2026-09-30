package com.haui.dongamvungcao.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; // ĐÃ THÊM
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.database.DatabaseHelper; // ĐÃ THÊM
import com.haui.dongamvungcao.database.DonationDAO;
import com.haui.dongamvungcao.utils.DialogUtils;
import com.haui.dongamvungcao.utils.UserSession;

public class CreateDonationActivity extends AppCompatActivity {

    private EditText edtDonorName, edtMoneyAmount, edtItemName, edtItemQty, edtMessage;
    private RadioGroup radioGroupType;
    private RadioButton radMoney, radItem;
    private LinearLayout layoutItemFields, layoutQrPayment;
    private ConstraintLayout layoutFormInput;
    private Button btnSubmitDonation, btnConfirmPayment;
    private TextView txtQrAmount, txtQrContent;
    private ImageView imgQrCode;
    private Toolbar toolbarDonation;
    private DonationDAO donationDAO;

    private UserSession session;

    // ĐÃ THÊM: Khai báo đối tượng kết nối database để ghi thông báo trực tiếp
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    private int selectedCampaignId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_donation);

        selectedCampaignId = getIntent().getIntExtra("campaign_id", -1);
        if (selectedCampaignId == -1) {
            selectedCampaignId = 1;
        }

        // Khởi tạo các công cụ quản lý
        donationDAO = new DonationDAO(this);
        session = new UserSession(this);

        // ĐÃ THÊM: Khởi tạo kết nối database bảo hiểm
        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Ánh xạ thành phần giao diện
        toolbarDonation = findViewById(R.id.toolbarDonation);
        layoutFormInput = findViewById(R.id.layoutFormInput);
        layoutQrPayment = findViewById(R.id.layoutQrPayment);
        txtQrAmount = findViewById(R.id.txtQrAmount);
        txtQrContent = findViewById(R.id.txtQrContent);
        imgQrCode = findViewById(R.id.imgQrCode);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        edtDonorName = findViewById(R.id.edtDonorName);
        radioGroupType = findViewById(R.id.radioGroupType);
        radMoney = findViewById(R.id.radMoney);
        radItem = findViewById(R.id.radItem);
        edtMoneyAmount = findViewById(R.id.edtMoneyAmount);
        layoutItemFields = findViewById(R.id.layoutItemFields);

        // ĐÃ SỬA: Ánh xạ chính xác 2 ô nhập liệu vật phẩm để không bị rỗng dữ liệu
        edtItemName = findViewById(R.id.edtItemName);
        edtItemQty = findViewById(R.id.edtItemQty);

        edtMessage = findViewById(R.id.edtMessage);
        btnSubmitDonation = findViewById(R.id.btnSubmitDonation);

        if (session.isLoggedIn()) {
            edtDonorName.setText(session.getUsername());
        }

        setSupportActionBar(toolbarDonation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbarDonation.setNavigationOnClickListener(v -> finish());

        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) edtMessage.getLayoutParams();
            if (checkedId == R.id.radMoney) {
                edtMoneyAmount.setVisibility(View.VISIBLE);
                layoutItemFields.setVisibility(View.GONE);
                params.topToBottom = R.id.edtMoneyAmount;
            } else if (checkedId == R.id.radItem) {
                edtMoneyAmount.setVisibility(View.GONE);
                layoutItemFields.setVisibility(View.VISIBLE);
                params.topToBottom = R.id.layoutItemFields;
            }
            edtMessage.setLayoutParams(params);
        });

        btnSubmitDonation.setOnClickListener(v -> {
            String name = edtDonorName.getText().toString().trim();
            String message = edtMessage.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ tên người quyên góp!", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());

            if (radMoney.isChecked()) {
                String moneyStr = edtMoneyAmount.getText().toString().trim();
                if (moneyStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập số tiền quyên góp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                layoutFormInput.setVisibility(View.GONE);
                layoutQrPayment.setVisibility(View.VISIBLE);

                txtQrAmount.setText("Số tiền: " + moneyStr + " VNĐ");
                String transferContent = name.toUpperCase() + " UNG HO DONG AM HAUI";
                txtQrContent.setText("Nội dung: " + transferContent);

                String bankId = "ICB";
                String accountNo = "102873459999";
                String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact2.jpg?amount=" + moneyStr + "&addInfo=" + android.net.Uri.encode(transferContent);

                Glide.with(this)
                        .load(qrUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgQrCode);

                btnConfirmPayment.setOnClickListener(view -> {
                    int amount = Integer.parseInt(moneyStr);
                    saveToDatabaseAndFinish(name, amount, currentDate, message + " (Quyên góp: " + moneyStr + " VNĐ qua QR)");
                });

            } else {
                String itemName = edtItemName.getText().toString().trim();
                String itemQtyStr = edtItemQty.getText().toString().trim();
                if (itemName.isEmpty() || itemQtyStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng điền đầy đủ vật phẩm và số lượng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = Integer.parseInt(itemQtyStr);
                saveToDatabaseAndFinish(name, quantity, currentDate, message + " (Quyên góp: " + quantity + " " + itemName + ")");
            }
        });
    }

    private void saveToDatabaseAndFinish(String inputName, int qty, String date, String details) {
        String finalDonorName = session.isLoggedIn() ? session.getUsername() : inputName;
        String finalDetails = "[Người thực hiện: " + inputName + "] " + details;

        donationDAO.open();
        boolean isSuccess = donationDAO.insertDonation(selectedCampaignId, finalDonorName, qty, date, finalDetails);
        donationDAO.close();

        if (isSuccess) {
            // 1. Ghi vào bảng Notification trong app (Giữ nguyên)
            try {
                if (database == null || !database.isOpen()) {
                    database = dbHelper.getWritableDatabase();
                }
                ContentValues notifyValues = new ContentValues();
                notifyValues.put("title", "Quyên góp thành công 🎉");

// ĐÃ SỬA   : Đọc tên chiến dịch từ SQLite ra hoặc lấy thông tin chi tiết truyền trực tiếp vào nội dung thông báo lịch sử
                notifyValues.put("content", "Bạn đã hoàn thành việc đóng góp vật phẩm/tiền quỹ.\nChi tiết: " + details + "\nĐã đóng góp thành công cho chiến dịch ID: " + selectedCampaignId);

                notifyValues.put("time", "Vừa xong");
                database.insert("Notification", null, notifyValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. CẬP NHẬT: LẤY SỐ ĐIỆN THOẠI ĐĂNG KÝ THỰC TẾ TRONG SESSION ĐỂ PHÁT SMS
            String userEmail = "dongamvungcao@haui.edu.vn";
            String userPhone = "0987654321"; // Số điện thoại dự phòng

            if (session.isLoggedIn()) {
                String currentUsername = session.getUsername();
                Cursor userCursor = null;
                try {
                    userCursor = database.query("User", new String[]{"phone", "email"}, "username = ?", new String[]{currentUsername}, null, null, null);
                    if (userCursor != null && userCursor.moveToFirst()) {
                        int phoneIdx = userCursor.getColumnIndex("phone");
                        int emailIdx = userCursor.getColumnIndex("email");

                        if (phoneIdx != -1) {
                            String p = userCursor.getString(phoneIdx);
                            if (p != null && !p.isEmpty()) userPhone = p;
                        }
                        if (emailIdx != -1) {
                            String e = userCursor.getString(emailIdx);
                            if (e != null && !e.isEmpty()) userEmail = e;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (userCursor != null) userCursor.close();
                }
            }

            // ĐÃ SỬA: Nội dung tin nhắn xác nhận chuẩn hóa
            String smsContent = "Dong Am HaUI xac nhan: Hao tam [" + finalDonorName + "] da quyen gop " + details + " vao ngay " + date + ". Cam on ban!";

            // ĐÃ THAY ĐỔI: Kích hoạt gọi lệnh ADB Shell nội bộ ép máy ảo tự phát sóng và nhận SMS từ luồng hệ thống
            com.haui.dongamvungcao.utils.MockNotificationUtils.simulateSmsMessage(this, userPhone, smsContent);

            // 3. Hiển thị Dialog thành công (Giữ nguyên)
            DialogUtils.showSuccessDialog(this,
                    "Cảm ơn tấm lòng vàng của hảo tâm " + inputName + " đã đồng hành cùng Đông Ấm HaUI!",
                    this::finish);
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi lưu dữ liệu vào hệ thống!", Toast.LENGTH_SHORT).show();
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