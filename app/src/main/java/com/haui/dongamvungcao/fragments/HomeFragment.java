package com.haui.dongamvungcao.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.activities.CreateDonationActivity;
import com.haui.dongamvungcao.adapters.CampaignAdapter;
import com.haui.dongamvungcao.database.CampaignDAO;
import com.haui.dongamvungcao.models.Campaign;
import com.haui.dongamvungcao.utils.UserSession;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCampaigns;
    private CampaignAdapter adapter;
    private CampaignDAO campaignDAO;
    private FloatingActionButton fabAddDonation;
    private Button btnHomeLogin;
    private UserSession session;
    private List<Campaign> campaignList = new ArrayList<>();

    private ViewPager2 bannerViewPager;
    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;
    private SearchView searchViewHome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCampaigns = view.findViewById(R.id.rvCampaigns);
        fabAddDonation = view.findViewById(R.id.fabAddDonation);
        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        searchViewHome = view.findViewById(R.id.searchViewHome);

        session = new UserSession(getContext());
        campaignDAO = new CampaignDAO(getContext());

        rvCampaigns.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CampaignAdapter(campaignList);
        rvCampaigns.setAdapter(adapter);

        ImageView imgNotificationBell = view.findViewById(R.id.imgNotificationBell);
        if (session.isLoggedIn()) {
            btnHomeLogin.setVisibility(View.GONE);
            imgNotificationBell.setVisibility(View.VISIBLE);
        } else {
            btnHomeLogin.setVisibility(View.VISIBLE);
            imgNotificationBell.setVisibility(View.GONE);
        }

        imgNotificationBell.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.haui.dongamvungcao.activities.NotificationActivity.class);
            startActivity(intent);
        });

        setupBannerSlider();

        if (searchViewHome != null) {
            searchViewHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterCampaigns(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterCampaigns(newText);
                    return true;
                }
            });
        }

        if (session.isLoggedIn()) {
            btnHomeLogin.setText("Đăng xuất");
        } else {
            btnHomeLogin.setText("Đăng nhập");
        }

        btnHomeLogin.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                session.logout();
                Toast.makeText(getContext(), "Đã đăng xuất tài khoản thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), com.haui.dongamvungcao.activities.LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), com.haui.dongamvungcao.activities.LoginActivity.class);
                startActivity(intent);
            }
        });

        fabAddDonation.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.haui.dongamvungcao.activities.RegisterVolunteerActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void filterCampaigns(String keyword) {
        if (campaignDAO != null) {
            campaignDAO.open();
            List<Campaign> allCampaigns = campaignDAO.getAllCampaigns();
            campaignList.clear();

            if (keyword == null || keyword.trim().isEmpty()) {
                if (allCampaigns != null) {
                    campaignList.addAll(allCampaigns);
                }
            } else {
                java.util.Locale vnLocale = new java.util.Locale("vi", "VN");
                String cleanKeyword = keyword.toLowerCase(vnLocale).trim();

                if (allCampaigns != null) {
                    for (Campaign campaign : allCampaigns) {
                        if (campaign.getTitle() != null) {
                            String titleLowerCase = campaign.getTitle().toLowerCase(vnLocale);
                            if (titleLowerCase.contains(cleanKeyword)) {
                                campaignList.add(campaign);
                            }
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
            campaignDAO.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchViewHome == null || searchViewHome.getQuery().toString().isEmpty()) {
            if (campaignDAO != null) {
                campaignDAO.open();
                List<Campaign> dbCampaigns = campaignDAO.getAllCampaigns();

                // ĐÃ SỬA: Thay đổi tham số thứ 3 từ "" thành tên các file ảnh trong drawable của cậu
                if (dbCampaigns == null || dbCampaigns.isEmpty()) {
                    campaignDAO.insertCampaign(
                            "Áo ấm tới Bản Mù",
                            "Gom 500 áo khoác dày cho học sinh tiểu học tại điểm trường Bản Mù, Trạm Tấu, Yên Bái.",
                            "img_ao_am", // Tên ảnh trong res/drawable
                            500, 120,
                            "dang_dien_ra"
                    );
                    campaignDAO.insertCampaign(
                            "Chăn ấm vùng cao Mèo Vạc",
                            "Tặng 200 chăn bông ấm áp cho các hộ gia đình nghèo khó, hoàn cảnh đặc biệt tại Hà Giang.",
                            "img_chan_am", // Tên ảnh trong res/drawable
                            200, 65,
                            "sap_dien_ra"
                    );
                    campaignDAO.insertCampaign(
                            "Sách vở cho em tới trường",
                            "Quyên góp 1000 bộ sách giáo khoa cũ, vở viết và đồ dùng học tập cho học sinh vùng cao.",
                            "img_sach_vo", // Tên ảnh trong res/drawable
                            1000, 350,
                            "sap_dien_ra"
                    );
                    dbCampaigns = campaignDAO.getAllCampaigns();
                }

                campaignList.clear();
                if (dbCampaigns != null) {
                    campaignList.addAll(dbCampaigns);
                }
                adapter.notifyDataSetChanged();
                campaignDAO.close();
            }
        }

        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.postDelayed(bannerRunnable, 3000);
        }
    }

    private void setupBannerSlider() {
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner_1);
        bannerImages.add(R.drawable.banner_2);
        bannerImages.add(R.drawable.banner_3);

        bannerViewPager.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return new RecyclerView.ViewHolder(imageView) {};
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((ImageView) holder.itemView).setImageResource(bannerImages.get(position));
            }
            @Override
            public int getItemCount() { return bannerImages.size(); }
        });

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int totalItems = bannerViewPager.getAdapter() != null ? bannerViewPager.getAdapter().getItemCount() : 0;
                if (totalItems > 0) {
                    bannerViewPager.setCurrentItem((currentItem + 1) % totalItems, true);
                    bannerHandler.postDelayed(this, 3000);
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }
}