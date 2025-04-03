package com.example.greeshma_prasad_project2.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.category.CategoryAdapter;
import com.example.greeshma_prasad_project2.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private ArrayList<Cart> cartList;
    private Context mContext;
    private DatabaseReference cartRef;
    private String userId;

    public CartAdapter(Context mContext, ArrayList<Cart> cartList) {
        this.cartList = cartList;
        this.mContext = mContext;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(cart.getProductImage())
                .override(300, 300)
                .into(holder.imProductImage);
        holder.tvProductName.setText(cart.getProductName());
        holder.tvProductQuantity.setText(cart.getProductQuantity());
        double totalPrice = cart.getProductPrice() * cart.getProductCount();
        holder.tvProductPrice.setText(String.format("$  %s", totalPrice));
        holder.tvCartQuantity.setText(String.valueOf(cart.getProductCount()));
        holder.tvCartPlusButton.setOnClickListener(view -> {
            int updatedCount = cart.getProductCount() + 1;
            holder.tvCartQuantity.setText(String.valueOf(updatedCount));
            updateCart(cart, updatedCount);
        });
        holder.tvCartMinusButton.setOnClickListener(view -> {
            int updatedCount = cart.getProductCount() - 1;
            holder.tvCartQuantity.setText(String.valueOf(updatedCount));
            updateCart(cart, updatedCount);

        });

    }

    private void updateCart(Cart cart, int updatedQuantity) {
        if (updatedQuantity > 0) {
            cart.setProductCount(updatedQuantity);
            cartRef.child(cart.getProductId()).setValue(cart);

        } else {
            removeItemFromCart(cart);
        }

    }

    private void removeItemFromCart(Cart cart) {
        cartRef.child(cart.getProductId()).removeValue();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imProductImage;
        private TextView tvProductName, tvProductQuantity, tvProductPrice;
        private LinearLayout llCartLayout;
        private TextView tvCartPlusButton, tvCartMinusButton, tvCartQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imProductImage = itemView.findViewById(R.id.im_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            llCartLayout = itemView.findViewById(R.id.ll_cart_button);
            tvCartPlusButton = itemView.findViewById(R.id.btnIncreaseCart);
            tvCartMinusButton = itemView.findViewById(R.id.btnDecreaseCart);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);

        }
    }
}
