package com.example.greeshma_prasad_project2.productDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.product.ProductAdapter;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder> {
    private List<String> images;

    public ViewPagerAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item,parent,false);
        return new ViewPagerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(images.get(position))
                .override(300, 300)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.imageView_product);
        }
    }
}
