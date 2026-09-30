package com.haui.dongamvungcao.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.utils.UserSession;

public class CertificateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        Toolbar toolbar = findViewById(R.id.toolbarCert);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvCertDonorName = findViewById(R.id.tvCertDonorName);
        TextView tvCertContent = findViewById(R.id.tvCertContent);
        TextView tvCertDate = findViewById(R.id.tvCertDate);
        ImageView imgCertQr = findViewById(R.id.imgCertQr);

        UserSession session = new UserSession(this);
        if (session.isLoggedIn()) {
            String username = session.getUsername();
            tvCertDonorName.setText(username);

            tvCertContent.setText("Đã xuất sắc hoàn thành hành trình đóng góp và đồng hành cùng Đội Tình Nguyện HaUI mang hơi ấm, quần áo và sách vở tới cho trẻ em nghèo vùng cao.");

            String currentDate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
            tvCertDate.setText("Số: " + System.currentTimeMillis() / 100000 + "/HAUI - Ngày cấp: " + currentDate);

            // Sinh mã QR xác thực online dẫn đến trang chủ minh bạch thiện nguyện của trường
            String qrUrl = "https://img.vietqr.io/image/ICB-102873459999-compact2.jpg?amount=0&addInfo=" + android.net.Uri.encode("XAC THUC CHUNG NHAN " + username.toUpperCase());
            Glide.with(this).load(qrUrl).into(imgCertQr);
        }
    }
}