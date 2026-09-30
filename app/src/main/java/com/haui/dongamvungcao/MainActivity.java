package com.haui.dongamvungcao;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.haui.dongamvungcao.fragments.CampaignMenuFragment;
import com.haui.dongamvungcao.fragments.HomeFragment;
import com.haui.dongamvungcao.fragments.HistoryFragment;
import com.haui.dongamvungcao.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Mặc định nạp Trang chủ khi vừa khởi chạy app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_donate) { // Khớp chuẩn với ID nút Quyên góp XML của bạn
                selectedFragment = new HistoryFragment();
            } else if (id == R.id.nav_campaigns) { // Khớp chuẩn với ID nút Chiến dịch XML của bạn
                selectedFragment = new CampaignMenuFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        // SỬA TẠI ĐÂY: Đổi fragment_container thành fragmentContainer cho khớp chuẩn XML của bạn
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }
            return true;
        });

// Đừng quên sửa cả đoạn ép app nạp mặc định trang Home lúc vừa mở lên ở dưới (nếu có):
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment()) // Đổi thành fragmentContainer ở đây nữa
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    public void switchToHistoryTab() {
        bottomNavigationView.setSelectedItemId(R.id.nav_donate);
    }
}