package com.haui.dongamvungcao.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.dongamvungcao.R;
import java.util.List;
import java.util.Map;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ViewHolder> {

    private List<Map<String, String>> listData;

    public StatementAdapter(List<Map<String, String>> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> data = listData.get(position);
        holder.tvName.setText(data.get("name"));
        holder.tvTime.setText(data.get("time"));
        holder.tvAmount.setText(data.get("amount"));

        // Nếu là tiền chi ra thì đổi chữ sang màu đỏ
        if (data.get("amount").contains("-")) {
            holder.tvAmount.setTextColor(0xFFE53935); // Màu đỏ
        } else {
            holder.tvAmount.setTextColor(0xFF4CAF50); // Màu xanh lá
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemDonorName);
            tvTime = itemView.findViewById(R.id.itemDonateTime);
            tvAmount = itemView.findViewById(R.id.itemDonateAmount);
        }
    }
}