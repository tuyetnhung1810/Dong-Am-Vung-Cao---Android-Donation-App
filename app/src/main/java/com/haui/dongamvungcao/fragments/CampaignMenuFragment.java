package com.haui.dongamvungcao.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.adapters.CampaignAdapter;
import com.haui.dongamvungcao.database.CampaignDAO;
import com.haui.dongamvungcao.models.Campaign;
import java.util.ArrayList;
import java.util.List;

public class CampaignMenuFragment extends Fragment {

    private RecyclerView rvMenuCampaigns;
    private EditText edtSearchCampaign;

    private TextView txtTabActive, txtTabUpcoming;
    private View indicatorActive, indicatorUpcoming;
    private LinearLayout tabActiveLayout, tabUpcomingLayout;

    private CampaignAdapter adapter;
    private CampaignDAO campaignDAO;

    private List<Campaign> originalList = new ArrayList<>();
    private List<Campaign> filteredList = new ArrayList<>();
    private String currentSelectedTab = "dang_dien_ra";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campaign_menu, container, false);

        edtSearchCampaign = view.findViewById(R.id.edtSearchCampaign);
        rvMenuCampaigns = view.findViewById(R.id.rvMenuCampaigns);

        tabActiveLayout = view.findViewById(R.id.tabActiveLayout);
        tabUpcomingLayout = view.findViewById(R.id.tabUpcomingLayout);
        txtTabActive = view.findViewById(R.id.txtTabActive);
        txtTabUpcoming = view.findViewById(R.id.txtTabUpcoming);
        indicatorActive = view.findViewById(R.id.indicatorActive);
        indicatorUpcoming = view.findViewById(R.id.indicatorUpcoming);

        campaignDAO = new CampaignDAO(getContext());

        rvMenuCampaigns.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CampaignAdapter(filteredList);
        rvMenuCampaigns.setAdapter(adapter);

        tabActiveLayout.setOnClickListener(v -> {
            currentSelectedTab = "dang_dien_ra";
            txtTabActive.setTextColor(getResources().getColor(R.color.primary_color));
            indicatorActive.setBackgroundColor(getResources().getColor(R.color.primary_color));
            txtTabUpcoming.setTextColor(getResources().getColor(R.color.text_muted));
            indicatorUpcoming.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            filterData();
        });

        tabUpcomingLayout.setOnClickListener(v -> {
            currentSelectedTab = "sap_dien_ra";
            txtTabUpcoming.setTextColor(getResources().getColor(R.color.primary_color));
            indicatorUpcoming.setBackgroundColor(getResources().getColor(R.color.primary_color));
            txtTabActive.setTextColor(getResources().getColor(R.color.text_muted));
            indicatorActive.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            filterData();
        });

        edtSearchCampaign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterData() {
        String searchText = edtSearchCampaign.getText().toString().trim().toLowerCase();
        filteredList.clear();

        for (Campaign item : originalList) {
            boolean matchesTab = item.getStatus() != null && item.getStatus().equals(currentSelectedTab);
            boolean matchesSearch = item.getTitle().toLowerCase().contains(searchText);

            if (matchesTab && matchesSearch) {
                filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (campaignDAO != null) {
            campaignDAO.open();

            // Lấy danh sách hiện tại đang có trong Database ra trước
            List<Campaign> dbList = campaignDAO.getAllCampaigns();

            int activeCount = 0;
            int upcomingCount = 0;

            // ĐÃ SỬA: Đếm trực tiếp bằng vòng lặp Java từ dbList, không cần dùng hàm SQL rawQuery nữa
            if (dbList != null) {
                for (Campaign c : dbList) {
                    if (c.getStatus() != null) {
                        if (c.getStatus().equals("dang_dien_ra")) {
                            activeCount++;
                        } else if (c.getStatus().equals("sap_dien_ra")) {
                            upcomingCount++;
                        }
                    }
                }
            }

            boolean needsReRead = false;

            // 1. Kiểm tra xem tab ĐANG DIỄN RA đã đủ 5 cái chưa, nếu chưa thì chèn bù thêm 4 cái
            if (activeCount < 5) {
                campaignDAO.insertCampaign(
                        "Quỹ sữa dinh dưỡng trẻ vùng cao",
                        "Cung cấp sữa hộp và bữa ăn dinh dưỡng bổ sung cho 300 mầm non tại xã Tà Xùa, Sơn La.",
                        "", 300, 185, "dang_dien_ra"
                );
                campaignDAO.insertCampaign(
                        "Bình nước lọc sạch học đường",
                        "Lắp đặt hệ thống lọc nước sạch RO cho các điểm trường nội trú vùng cao huyện Đồng Văn, Hà Giang.",
                        "", 50, 22, "dang_dien_ra"
                );
                campaignDAO.insertCampaign(
                        "Tủ thuốc y tế cho bản em",
                        "Quyên góp trang bị bông băng, thuốc bổ và vật tư y tế cơ bản cho trạm y tế vùng sâu Yên Bái.",
                        "", 150, 95, "dang_dien_ra"
                );
                campaignDAO.insertCampaign(
                        "Thắp sáng đường bản Cao",
                        "Lắp đặt đèn năng lượng mặt trời dọc các lối đi nguy hiểm quanh sườn núi huyện Bát Xát, Lào Cai.",
                        "", 100, 45, "dang_dien_ra"
                );
                needsReRead = true;
            }

            // 2. Kiểm tra xem tab SẮP DIỄN RA đã đủ 5 cái chưa, nếu chưa thì chèn bù thêm 3 cái
            if (upcomingCount < 5) {
                campaignDAO.insertCampaign(
                        "Xây trường mới Bản Phố",
                        "Dự án quyên góp gạch đá, xi măng sửa sang 3 phòng học bị dột nát tại Bắc Hà, Lào Cai.",
                        "", 800, 210, "sap_dien_ra"
                );
                campaignDAO.insertCampaign(
                        "Giày vững bước tới tương lai",
                        "Hỗ trợ 400 đôi giày vải, ủng cao su chống sên vắt cho học sinh lội bùn đến lớp mùa mưa.",
                        "", 400, 0, "sap_dien_ra"
                );
                campaignDAO.insertCampaign(
                        "Áo phao cứu trợ mùa lũ lũ quét",
                        "Chuẩn bị sẵn nhu yếu phẩm và 250 áo phao cứu trợ khẩn cấp cho vùng cao rốn lũ Sơn La.",
                        "", 250, 0, "sap_dien_ra"
                );
                needsReRead = true;
            }

            // Nếu có dữ liệu chèn mới, đọc lại Database một lần nữa để cập nhật danh sách đầy đủ
            if (needsReRead) {
                dbList = campaignDAO.getAllCampaigns();
            }

            originalList.clear();
            if (dbList != null) {
                originalList.addAll(dbList);
            }

            filterData();
            campaignDAO.close();
        }
    }
}