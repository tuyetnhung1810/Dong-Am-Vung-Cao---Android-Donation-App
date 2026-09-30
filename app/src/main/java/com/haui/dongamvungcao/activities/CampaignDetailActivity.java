package com.haui.dongamvungcao.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.haui.dongamvungcao.R;

public class CampaignDetailActivity extends AppCompatActivity {

    private ImageView detailImg;
    private TextView detailTitle, detailDescription, detailDateTime, detailLocation, detailMembers;

    // Giữ lại thẻ trạng thái trên Banner ảnh
    private TextView txtTagStatus;

    // 🔥 ĐÃ THÊM: Các ImageView đại diện cho khối hình ảnh hoạt động cuộn ngang mới
    private ImageView imgActivity1, imgActivity2, imgActivity3;

    private Button btnDonateNow, btnStatement;
    private Toolbar toolbarDetail;
    private int campaignId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);

        // 1. Ánh xạ các thành phần giao diện chính từ XML sang Java
        toolbarDetail = findViewById(R.id.toolbarDetail);
        detailImg = findViewById(R.id.detailImg);
        detailTitle = findViewById(R.id.detailTitle);
        detailDescription = findViewById(R.id.detailDescription);
        detailDateTime = findViewById(R.id.detailDateTime);
        detailLocation = findViewById(R.id.detailLocation);
        detailMembers = findViewById(R.id.detailMembers);

        // Ánh xạ thẻ trạng thái trên banner
        txtTagStatus = findViewById(R.id.txtTagStatus);

        // 🔥 ĐÃ THÊM: Ánh xạ 3 ImageView của khối hình ảnh hoạt động thực tế
        imgActivity1 = findViewById(R.id.imgActivity1);
        imgActivity2 = findViewById(R.id.imgActivity2);
        imgActivity3 = findViewById(R.id.imgActivity3);

        btnDonateNow = findViewById(R.id.btnDonateNow);
        btnStatement = findViewById(R.id.btnStatement);

        // 2. Cài đặt Toolbar làm Actionbar và xử lý nút quay lại (Back)
        setSupportActionBar(toolbarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // Ẩn tiêu đề gốc để giao diện thoáng sạch
        }
        toolbarDetail.setNavigationOnClickListener(v -> finish());

        // 3. Nhận dữ liệu động được truyền sang từ Intent của CampaignAdapter
        campaignId = getIntent().getIntExtra("campaign_id", -1);
        String title = getIntent().getStringExtra("campaign_title");
        String desc = getIntent().getStringExtra("campaign_desc");
        String status = getIntent().getStringExtra("campaign_status");
        String imageName = getIntent().getStringExtra("campaign_image");

        // 4. Đổ dữ liệu chữ và xử lý trạng thái chiến dịch
        if (campaignId != -1) {
            detailTitle.setText(title);
            detailDescription.setText(desc);
            detailDateTime.setText("15/12/2026 - 30/12/2026\nCòn 15 ngày");
            detailLocation.setText("Hà Giang\nXã Xín Mần, Huyện Xín Mần");
            detailMembers.setText("Ban tổ chức: Đội Tình Nguyện HaUI");

            // --- XỬ LÝ ĐỔI CHỮ THẺ TRẠNG THÁI TRÊN BANNER MÀU SẮC RIÊNG ---
            if (status != null && status.equals("dang_dien_ra")) {
                txtTagStatus.setText("Chiến dịch đang diễn ra");
                txtTagStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Màu xanh lá khi đang chạy
            } else {
                txtTagStatus.setText("Chiến dịch sắp diễn ra");
                txtTagStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Màu cam khi sắp diễn ra
            }

            // --- HIỂN THỊ HÌNH ẢNH BANNER ĐỘNG TỪ DRAWABLE ---
            int mainImageId = 0;
            if (imageName != null && !imageName.trim().isEmpty()) {
                mainImageId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                if (mainImageId != 0) {
                    detailImg.setImageResource(mainImageId);
                } else {
                    detailImg.setImageResource(R.drawable.banner_1);
                }
            } else {
                detailImg.setImageResource(R.drawable.banner_1);
            }

            // --- 🔥 ĐÃ THÊM: XỬ LÝ TÌM VÀ HIỂN THỊ CÁC ẢNH HOẠT ĐỘNG PHỤ ĐỘNG ---
            if (imageName != null && !imageName.trim().isEmpty()) {
                // Tìm ID ảnh dựa vào quy tắc đặt tên: tên_ảnh_gốc_1, tên_ảnh_gốc_2, tên_ảnh_gốc_3
                int imgId1 = getResources().getIdentifier(imageName + "_1", "drawable", getPackageName());
                int imgId2 = getResources().getIdentifier(imageName + "_2", "drawable", getPackageName());
                int imgId3 = getResources().getIdentifier(imageName + "_3", "drawable", getPackageName());

                // Nếu tìm thấy ảnh phụ ứng với bài viết thì đổi, nếu không thì dùng ảnh banner chính (hoặc banner_1) làm nền dự phòng chống trống
                imgActivity1.setImageResource(imgId1 != 0 ? imgId1 : (mainImageId != 0 ? mainImageId : R.drawable.banner_1));
                imgActivity2.setImageResource(imgId2 != 0 ? imgId2 : (mainImageId != 0 ? mainImageId : R.drawable.banner_1));
                imgActivity3.setImageResource(imgId3 != 0 ? imgId3 : (mainImageId != 0 ? mainImageId : R.drawable.banner_1));
            } else {
                // Nếu hoàn toàn không có tên ảnh, đưa tất cả về ảnh mặc định hệ thống
                imgActivity1.setImageResource(R.drawable.banner_1);
                imgActivity2.setImageResource(R.drawable.banner_1);
                imgActivity3.setImageResource(R.drawable.banner_1);
            }
        }

        // 5. Kiểm tra trạng thái để Ẩn / Hiện nút quyên góp phù hợp
        if (status != null && status.equals("sap_dien_ra")) {
            btnDonateNow.setVisibility(View.VISIBLE);
        } else {
            btnDonateNow.setVisibility(View.GONE);
        }

        // 6. Bắt sự kiện Click nút QUYÊN GÓP mở Form điền thông tin quyên góp
        btnDonateNow.setOnClickListener(v -> {
            Intent intent = new Intent(CampaignDetailActivity.this, CreateDonationActivity.class);
            intent.putExtra("campaign_id", campaignId);
            startActivity(intent);
        });

        // 7. Bắt sự kiện Click nút SAO KÊ để mở màn hình danh sách sao kê minh bạch công khai
        btnStatement.setOnClickListener(v -> {
            Intent intent = new Intent(CampaignDetailActivity.this, StatementActivity.class);
            intent.putExtra("campaign_id", campaignId);
            startActivity(intent);
        });
    }
}