package com.example.greeshma_prasad_project2.product;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.models.Cart;
import com.example.greeshma_prasad_project2.models.Product;
import com.example.greeshma_prasad_project2.productDetails.ProductDetailsActivity;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context mContext;
    private List<Product> productList;
    private DatabaseReference cartRef;
    private String userId;
    private String category;

    public ProductAdapter(Context mContext, List<Product> productList,String category) {
        this.mContext = mContext;
        this.productList = productList;
        this.category=category;
        this.userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_category_item,parent,false);
            return new ProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Product product=productList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(product.getImages().get(0))
                .override(300, 300)
                .into(holder.imProductImage);
        holder.txtProductName.setText(product.getName());
        holder.txtProductQuantity.setText(product.getQuantity());
        holder.txtProductPrice.setText(String.format("$  %s", product.getPrice()));
        holder.btAddToCart.setOnClickListener(view -> {
            holder.btAddToCart.setVisibility(GONE);
            holder.llAddToCart.setVisibility(VISIBLE);
            holder.tvCartQuantity.setText("1");
            insertItemToCart(product,1);
        });

        cartRef.child(product.getId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()){
                Cart cart=task.getResult().getValue(Cart.class);
                if(cart!=null && cart.getProductCount()>0){
                    holder.btAddToCart.setVisibility(GONE);
                    holder.llAddToCart.setVisibility(VISIBLE);
                    holder.tvCartQuantity.setText(String.valueOf(cart.getProductCount()));
                }
            }
        });
        holder.tvAddToCartPlus.setOnClickListener(view -> {
            int quantity=Integer.parseInt(holder.tvCartQuantity.getText().toString())+1;
            holder.tvCartQuantity.setText(String.valueOf(quantity));
            insertItemToCart(product,quantity);
        });
        holder.tvAddToCartMinus.setOnClickListener(view -> {
            int quantity=Integer.parseInt(holder.tvCartQuantity.getText().toString())-1;
            if(quantity>0){
                holder.tvCartQuantity.setText(String.valueOf(quantity));
                insertItemToCart(product,quantity);

            }else{
                holder.btAddToCart.setVisibility(VISIBLE);
                holder.llAddToCart.setVisibility(GONE);
                removeItemFromCart(product);
            }

        });
        holder.cvProduct.setOnClickListener(view -> {
            Intent intent=new Intent(mContext, ProductDetailsActivity.class);
            intent.putExtra("productId",product.getId());
            intent.putExtra("ProductName",product.getName());
            intent.putExtra("category",category);
            mContext.startActivity(intent);
        });
    }

    private void removeItemFromCart(Product product){
        cartRef.child(product.getId()).removeValue();
    }
    private void insertItemToCart(Product product,int quantity){
        cartRef.child(product.getId()).setValue(new Cart(product.getId(),product.getName(),product.getPrice(),product.getQuantity(),product.getImages().get(0),product.getDescription(),quantity));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView txtProductName,txtProductQuantity,txtProductPrice;
            private ImageView imProductImage;
            private AppCompatButton btAddToCart;
            private LinearLayout llAddToCart;
            private TextView tvAddToCartMinus,tvAddToCartPlus,tvCartQuantity;
            private CardView cvProduct;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName=itemView.findViewById(R.id.tv_product_name);
            txtProductPrice=itemView.findViewById(R.id.tv_product_price);
            txtProductQuantity=itemView.findViewById(R.id.tv_product_quantity);
            imProductImage=itemView.findViewById(R.id.im_product_image);
            btAddToCart = itemView.findViewById(R.id.button_add_to_cart);
            llAddToCart=itemView.findViewById(R.id.ll_add_item_to_cart);
            tvAddToCartMinus=itemView.findViewById(R.id.tv_add_to_cart_minus);
            tvAddToCartPlus=itemView.findViewById(R.id.tv_add_to_cart_plus);
            tvCartQuantity=itemView.findViewById(R.id.tv_add_to_cart_quantity);
            cvProduct=itemView.findViewById(R.id.card_item);
        }
    }
}
