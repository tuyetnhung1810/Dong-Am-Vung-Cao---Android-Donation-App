package com.haui.dongamvungcao.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.models.DonationDetail;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorViewHolder> {

    private List<DonationDetail> donationList;

    public DonorAdapter(List<DonationDetail> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor, parent, false);
        return new DonorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        DonationDetail detail = donationList.get(position);
        holder.txtDonorName.setText(detail.getDonorName());
        holder.txtDonateQty.setText("Đã ủng hộ: " + detail.getQuantity() + " món quà");
        holder.txtDonateDate.setText(detail.getDate());
        holder.txtDonorMessage.setText("\"" + detail.getMessage() + "\"");
    }

    @Override
    public int getItemCount() {
        return donationList != null ? donationList.size() : 0;
    }

    public static class DonorViewHolder extends RecyclerView.ViewHolder {
        TextView txtDonorName, txtDonateQty, txtDonateDate, txtDonorMessage;

        public DonorViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDonorName = itemView.findViewById(R.id.txtDonorName);
            txtDonateQty = itemView.findViewById(R.id.txtDonateQty);
            txtDonateDate = itemView.findViewById(R.id.txtDonateDate);
            txtDonorMessage = itemView.findViewById(R.id.txtDonorMessage);
        }
    }
}