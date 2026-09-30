package com.haui.dongamvungcao.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.dongamvungcao.R;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final int[] bannerImages;

    // Hàm khởi tạo nhận vào mảng các ảnh drawable
    public BannerAdapter(int[] bannerImages) {
        this.bannerImages = bannerImages;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // Đổ dữ liệu ảnh từ mảng vào ImageView
        holder.imgBannerItem.setImageResource(bannerImages[position]);
    }

    @Override
    public int getItemCount() {
        return bannerImages != null ? bannerImages.length : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBannerItem;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBannerItem = itemView.findViewById(R.id.imgBannerItem);
        }
    }
}