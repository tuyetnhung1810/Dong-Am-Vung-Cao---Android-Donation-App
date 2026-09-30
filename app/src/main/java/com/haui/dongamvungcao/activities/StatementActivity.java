package com.haui.dongamvungcao.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson; // Đảm bảo cậu đã thêm thư viện GSON vào build.gradle
import com.google.gson.reflect.TypeToken;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.adapters.StatementAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementActivity extends AppCompatActivity {

    private ImageView imgStatementCampaign;
    private TextView tvTotalReceive, tvTotalSpend, tvCountReceive, tvCountSpend;
    private RecyclerView rvStatementItems;
    private StatementAdapter adapter;
    private List<Map<String, String>> statementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        // Ánh xạ View
        imgStatementCampaign = findViewById(R.id.imgStatementCampaign);
        tvTotalReceive = findViewById(R.id.tvTotalReceive);
        tvTotalSpend = findViewById(R.id.tvTotalSpend);
        tvCountReceive = findViewById(R.id.tvCountReceive);
        tvCountSpend = findViewById(R.id.tvCountSpend);
        rvStatementItems = findViewById(R.id.rvStatementItems);
        Toolbar toolbarStatement = findViewById(R.id.toolbarStatement);

        rvStatementItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatementAdapter(statementList);
        rvStatementItems.setAdapter(adapter);

        toolbarStatement.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Gọi hàm này trong onResume để nó luôn cập nhật dữ liệu mới nhất
    }

    private void loadData() {
        statementList.clear();
        int campaignId = getIntent().getIntExtra("campaign_id", -1);

        // 1. Lấy dữ liệu từ bộ nhớ tạm (Giả lập việc lưu dữ liệu từ trang quyên góp)
        SharedPreferences prefs = getSharedPreferences("DonationPrefs", MODE_PRIVATE);
        String json = prefs.getString("donations_" + campaignId, "");

        if (!json.isEmpty()) {
            List<Map<String, String>> savedData = new Gson().fromJson(json, new TypeToken<List<Map<String, String>>>(){}.getType());
            statementList.addAll(savedData);
        } else {
            // Nếu chưa có dữ liệu thật thì nạp dữ liệu mẫu
            generateFakeStatementData(campaignId);
        }

        // 2. Tính toán tổng thu/chi từ list hiện tại
        calculateTotals();
        adapter.notifyDataSetChanged();
    }

    private void calculateTotals() {
        int receive = 0;
        int spend = 0;
        int rCount = 0;
        int sCount = 0;

        for (Map<String, String> item : statementList) {
            String amount = item.get("amount").replace(",", "").replace("đ", "");
            if (amount.startsWith("+")) {
                receive += Integer.parseInt(amount.replace("+", ""));
                rCount++;
            } else {
                spend += Integer.parseInt(amount.replace("-", ""));
                sCount++;
            }
        }
        tvTotalReceive.setText("+" + receive + "đ");
        tvTotalSpend.setText("-" + spend + "đ");
        tvCountReceive.setText("Tổng " + rCount + " giao dịch");
        tvCountSpend.setText("Tổng " + sCount + " giao dịch");
    }

    private void generateFakeStatementData(int campaignId) {
        addLog("Nhà hảo tâm ẩn danh", "12/06/2026 14:11", "+1200000");
        addLog("Lê Văn C", "12/06/2026 16:35", "+2000000");
        addLog("Chi phí mua nhu yếu phẩm", "13/06/2026 09:00", "-2100000");
    }

    private void addLog(String name, String time, String amount) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("time", time);
        map.put("amount", amount);
        statementList.add(map);
    }
}