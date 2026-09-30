package com.haui.dongamvungcao.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.adapters.DonorAdapter;
import com.haui.dongamvungcao.database.DatabaseHelper;
import com.haui.dongamvungcao.database.DonationDAO;
import com.haui.dongamvungcao.models.DonationDetail;
import com.haui.dongamvungcao.utils.UserSession;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private DonorAdapter adapter;
    private DonationDAO donationDAO;
    private List<DonationDetail> historyList = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private UserSession session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        donationDAO = new DonationDAO(getContext());

        dbHelper = new DatabaseHelper(getContext());
        session = new UserSession(getContext());

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DonorAdapter(historyList);
        rvHistory.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<DonationDetail> combinedList = new ArrayList<>();

        // 1. Đọc dữ liệu quyên góp cũ
        if (donationDAO != null) {
            donationDAO.open();
            List<DonationDetail> listDonation = donationDAO.getAllDonations();
            if (listDonation != null) {
                combinedList.addAll(listDonation);
            }
            donationDAO.close();
        }

        // 2. Đọc dữ liệu đăng ký tình nguyện (Đã gọi qua hàm tách riêng để hết cảnh báo)
        if (session != null && session.isLoggedIn()) {
            combinedList.addAll(loadVolunteerHistoryData());
        }

        // 3. Làm tươi giao diện
        historyList.clear();
        historyList.addAll(combinedList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * ĐÃ TÁCH HÀM (Extract Method): Giúp dọn sạch cảnh báo lồng ghép của Android Studio
     */
    private List<DonationDetail> loadVolunteerHistoryData() {
        List<DonationDetail> volunteerList = new ArrayList<>();
        String currentUsername = session.getUsername();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT title, content, time FROM Notification WHERE title LIKE ? AND content LIKE ? ORDER BY id DESC";
            cursor = db.rawQuery(query, new String[]{"%Đăng ký tình nguyện%", "%" + currentUsername + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));

                    // Khởi tạo phần tử bằng Constructor
                    DonationDetail volunteerItem = new DonationDetail(0, 1, title, 1, time, content);
                    volunteerList.add(volunteerItem);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return volunteerList;
    }
}