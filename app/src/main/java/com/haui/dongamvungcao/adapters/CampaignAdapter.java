package com.haui.dongamvungcao.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.dongamvungcao.R;
import com.haui.dongamvungcao.models.Campaign;

import java.util.List;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder> {

    private List<Campaign> campaignList;

    public CampaignAdapter(List<Campaign> campaignList) {
        this.campaignList = campaignList;
    }

    @NonNull
    @Override
    public CampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign, parent, false);
        return new CampaignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignViewHolder holder, int position) {
        Campaign campaign = campaignList.get(position);
        holder.txtTitle.setText(campaign.getTitle());
        holder.txtDescription.setText(campaign.getDescription());

        // XỬ LÝ SỰ KIỆN TÌM VÀ HIỂN THỊ ẢNH ĐỘNG TỪ SQLITE
        String imageName = campaign.getImageUrl();

        if (imageName != null && !imageName.trim().isEmpty()) {
            int imageId = holder.itemView.getContext().getResources().getIdentifier(
                    imageName,
                    "drawable",
                    holder.itemView.getContext().getPackageName()
            );

            if (imageId != 0) {
                holder.imgCampaign.setImageResource(imageId);
            } else {
                holder.imgCampaign.setImageResource(R.drawable.banner_1);
            }
        } else {
            holder.imgCampaign.setImageResource(R.drawable.banner_1);
        }

        // XỬ LÝ SỰ KIỆN CLICK VÀO ITEM CHIẾN DỊCH
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), com.haui.dongamvungcao.activities.CampaignDetailActivity.class);
            intent.putExtra("campaign_id", campaign.getId());
            intent.putExtra("campaign_title", campaign.getTitle());
            intent.putExtra("campaign_desc", campaign.getDescription());
            intent.putExtra("campaign_status", campaign.getStatus());
            intent.putExtra("campaign_image", campaign.getImageUrl());

            // 🔥 ĐÃ THÊM: Truyền thêm số liệu mục tiêu và thực tế sang màn hình chi tiết
            intent.putExtra("campaign_target", campaign.getTargetQty());
            intent.putExtra("campaign_current", campaign.getCurrentQty());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return campaignList != null ? campaignList.size() : 0;
    }

    public static class CampaignViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription;
        ImageView imgCampaign;

        public CampaignViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgCampaign = itemView.findViewById(R.id.imgCampaign);

            // Giữ nguyên ánh xạ cũ của cậu không thay đổi gì hết
            txtDescription = itemView.findViewById(R.id.txtStatus);
        }
    }
}