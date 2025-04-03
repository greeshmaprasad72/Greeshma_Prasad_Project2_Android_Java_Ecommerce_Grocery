package com.example.greeshma_prasad_project2.category;

import android.content.Context;
import android.content.Intent;
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
import com.example.greeshma_prasad_project2.product.ProductActivity;
import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context mContext;

    public CategoryAdapter(Context mContext, List<Category> categoryList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
    }

    private List<Category> categoryList;



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
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
            holder.cardView.setOnClickListener(view -> {
            Intent intent=new Intent(mContext, ProductActivity.class);
            intent.putExtra("category_name",category.getName());
            mContext.startActivity(intent);

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
