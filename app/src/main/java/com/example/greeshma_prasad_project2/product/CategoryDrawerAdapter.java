package com.example.greeshma_prasad_project2.product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.models.Category;

import java.util.List;

public class CategoryDrawerAdapter extends RecyclerView.Adapter<CategoryDrawerAdapter.MyViewHolder> {
    private Context mContext;
    private int selectedPosition=-1;
    private categoryListener listener;
    private String selectedCategory;

    public interface categoryListener{
        void onSelected(String categoryName);
    }

    public CategoryDrawerAdapter(Context mContext, List<Category> categoryList,String selectedCategory,int selectedPosition,categoryListener listener) {
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.selectedCategory=selectedCategory;
        this.listener=listener;
        this.selectedPosition=selectedPosition;

        }



    private List<Category> categoryList;



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_category_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Category category=categoryList.get(position);
        holder.pName.setText(category.getName());
        Log.i("TAG", "onBindViewHolder: "+category.getImage());
        Glide.with(holder.itemView.getContext())
                .load(category.getImage())
                .override(300, 300)
                .into(holder.pIamge);

        if (position== selectedPosition) {
            holder.cardView.setBackgroundResource(R.drawable.circle_button_ng);

        }
        else {
            holder.cardView.setBackgroundResource(R.drawable.rounded_edittext);
        }
        holder.cardView.setOnClickListener(view -> {

            int previosSelection=selectedPosition;
            selectedCategory=category.getName();
            selectedPosition=holder.getAdapterPosition();
            notifyItemChanged(previosSelection);
            notifyItemChanged(selectedPosition);
            listener.onSelected(selectedCategory);

        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView pName;
        private ImageView pIamge;

        private CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pIamge=itemView.findViewById(R.id.im_category_iamge);
            pName=itemView.findViewById(R.id.tv_category_name);
            cardView=itemView.findViewById(R.id.card_item);
        }
    }
}
