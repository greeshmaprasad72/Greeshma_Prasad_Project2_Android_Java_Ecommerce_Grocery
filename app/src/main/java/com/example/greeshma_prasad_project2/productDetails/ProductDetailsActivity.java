package com.example.greeshma_prasad_project2.productDetails;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.cart.CartActivity;
import com.example.greeshma_prasad_project2.models.Cart;
import com.example.greeshma_prasad_project2.models.Product;
import com.example.greeshma_prasad_project2.product.ProductAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView imBackArrow,imProduct,imDotOne,imDotTwo;
    private ViewPager2 viewPagerProductImage;
    private TextView tvProductName,tvPrice,tvQuantity,tvCategory,tvDescription,tvCartBadge,tvAddToCartPlus,tvAddToCartMinus,tvAddToCartLabel;
    private FrameLayout  flCart;
    private LinearLayout llAddToCart;
    private Button btnAddToCart;
    private DatabaseReference mReference;
    private FirebaseDatabase database;
    private DatabaseReference cartReference;

    private String category="";
    private String userId="";

    private ViewPagerAdapter adapter;
    private List<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseDatabase.getInstance();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();


        imBackArrow=findViewById(R.id.imageview_back_arrow);
        viewPagerProductImage=findViewById(R.id.viewpager_product);
        tvProductName=findViewById(R.id.textview_product_name);
        tvPrice=findViewById(R.id.textview_price);
        tvQuantity=findViewById(R.id.textview_quantity);
        tvCategory=findViewById(R.id.textview_category);
        tvDescription=findViewById(R.id.textview_product_description);
        btnAddToCart=findViewById(R.id.button_add_to_cart);
        imProduct=findViewById(R.id.imageView_product);
        flCart=findViewById(R.id.fl_cart_container);
        tvCartBadge=findViewById(R.id.tv_cart_badge);
        llAddToCart=findViewById(R.id.ll_add_item_to_cart);
        tvAddToCartPlus=findViewById(R.id.tv_add_to_cart_plus);
        viewPagerProductImage=findViewById(R.id.viewpager_product);
        imDotOne=findViewById(R.id.im_dot_one);
        imDotTwo=findViewById(R.id.im_dot_two);
        imageList=new ArrayList<>();

        imBackArrow.setOnClickListener(view -> {
                finish();
        });
        String productId=getIntent().getStringExtra("productId");
        String ProductName=getIntent().getStringExtra("ProductName");

        category=getIntent().getStringExtra("category");
        if(productId!=null){
            getProductDetails(ProductName);
        }
        updateCartCount();
        moveToCartScreen();
    }

    private void setViewPager(){
        adapter=new ViewPagerAdapter(imageList);
        adapter.notifyDataSetChanged();
        viewPagerProductImage.setAdapter(adapter);
        viewPagerProductImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                changeDotColor();
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                changeDotColor();
            }
        });
    }

    private void changeDotColor(){
        switch (viewPagerProductImage.getCurrentItem()){
            case  0:
                imDotOne.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                imDotTwo.setBackgroundColor(this.getResources().getColor(R.color.light_gray));
                break;
            case  1:
                imDotOne.setBackgroundColor(this.getResources().getColor(R.color.light_gray));
                imDotTwo.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                break;
            default:
                imDotOne.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                imDotTwo.setBackgroundColor(this.getResources().getColor(R.color.light_gray));
                break;
        }
    }

    private void moveToCartScreen(){
        flCart.setOnClickListener(view -> {
            Intent intent=new Intent(this, CartActivity.class);
            startActivity(intent);
        });

    }

    private void getProductDetails(String ProductName){
        mReference= database.getReference("categories").child(category).child("products");

        mReference .child(ProductName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                if(snapshot.exists()){
                    Product product=snapshot.getValue(Product.class);

                    if(product!=null && product.getImages()!=null){
                        imageList =product.getImages();
                    }
                    setViewPager();
                    setProductDetails(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateCartCount(){
        try{
            cartReference=database.getReference("cart").child(userId);
            cartReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        Set<String> productCount=new ArraySet<>();
                        if(snapshot.exists() && snapshot.getChildrenCount()>0){
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                Cart cart=dataSnapshot.getValue(Cart.class);
                                if (cart!=null && cart.getProductId()!=null){
                                    productCount .add(cart.getProductId());
                                }
                                if(!productCount.isEmpty()){
                                    tvCartBadge.setVisibility(VISIBLE);
                                    tvCartBadge.setText(String.valueOf(productCount.size()));
                                }else{
                                    tvCartBadge.setVisibility(GONE);
                                }
                            }

                        }else{
                            tvCartBadge.setVisibility(GONE);
                        }
                    }catch (Exception e){
                        Log.i("TAG", "onDataChange: "+ e);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            cartReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        Set<String> productCount=new ArraySet<>();
                        if(snapshot.exists() && snapshot.getChildrenCount()>0){
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                Cart cart=dataSnapshot.getValue(Cart.class);
                                if (cart!=null && cart.getProductId()!=null){
                                    productCount .add(cart.getProductId());
                                }
                                if(!productCount.isEmpty()){
                                    tvCartBadge.setVisibility(VISIBLE);
                                    tvCartBadge.setText(String.valueOf(productCount.size()));
                                }else{
                                    tvCartBadge.setVisibility(GONE);
                                }
                            }

                        }else{
                            tvCartBadge.setVisibility(GONE);
                        }
                    }catch (Exception e){
                        Log.i("TAG", "onDataChange: "+ e);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            Log.i("TAG", "updateCartCount: "+ e);
        }

    }

    private void setProductDetails(Product product){
        Glide.with(this)
                .load(product.getImages().get(0))
                .override(300, 300)
                .into(imProduct);
        tvProductName.setText(product.getName());
        tvDescription.setText(product.getDescription());
        tvCategory.setText(category);
        tvPrice.setText(String.format("$  %s", product.getPrice()));
        tvQuantity.setText(product.getQuantity());
        btnAddToCart.setOnClickListener(view -> {
                addProductToCart(product);
        });

    }

    private void  addProductToCart(Product product){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartReference= FirebaseDatabase.getInstance().getReference("cart").child(userId);
        cartReference.child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Cart cart=snapshot.getValue(Cart.class);
                    if(cart!=null){
                        int updatedCount=cart.getProductCount()+1;
                        cartReference.child(product.getId()).child("productCount").setValue(updatedCount).addOnSuccessListener(unused -> {
                            Intent intent=new Intent(ProductDetailsActivity.this, CartActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                }else{
                    cartReference. setValue(new Cart(product.getId(),product.getName(),product.getPrice(),product.getQuantity(),product.getImages().get(0),product.getDescription(),1))
                            .addOnSuccessListener(unused -> {
                                Intent intent=new Intent(ProductDetailsActivity.this, CartActivity.class);
                                startActivity(intent);
                                finish();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}