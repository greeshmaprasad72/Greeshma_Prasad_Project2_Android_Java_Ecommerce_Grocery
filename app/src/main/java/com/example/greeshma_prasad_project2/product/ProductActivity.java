package com.example.greeshma_prasad_project2.product;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.cart.CartActivity;
import com.example.greeshma_prasad_project2.models.Cart;
import com.example.greeshma_prasad_project2.models.Category;
import com.example.greeshma_prasad_project2.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductActivity extends AppCompatActivity implements CategoryDrawerAdapter.categoryListener {
    private RecyclerView recyclerView,rvDrawer;
    private TextView txtCategory;

    private String categoryName="";
    private DatabaseReference mReference;
    private FirebaseDatabase database;

    private ProductAdapter adapter;

    List<Product> productList;
    private ProgressBar progressBar;

    private ImageView imBackArrow;
    private DatabaseReference cartReference;
    private String userId;

    private TextView tvCartCount;
    private FrameLayout flCartContainer;
    List<Category> categoryList;
    private CategoryDrawerAdapter drawerAdapter;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(getIntent().getExtras()!=null){
            categoryName=getIntent().getStringExtra("category_name");
        }
        productList=new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();


        recyclerView=findViewById(R.id.rv_product);
        txtCategory=findViewById(R.id.textview_label);
        progressBar=findViewById(R.id.progress_bar);
        imBackArrow=findViewById(R.id.im_arrow_back);
        tvCartCount=findViewById(R.id.tv_cart_badge);
        flCartContainer=findViewById(R.id.fl_cart_container);
        rvDrawer=findViewById(R.id.drawer_category);
        txtCategory.setText(categoryName);
        drawerLayout=findViewById(R.id.drawer_layout);
        showProgressBar();

        backButtonClick();
        updateCartCount();
        moveToCartScreen();
        getCategoryData();
        setRecyclerview();
        fetchProducts();
        openDrawer();
    }

    private void openDrawer(){
        imBackArrow.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProducts();

    }

    private void moveToCartScreen(){
        flCartContainer.setOnClickListener(view -> {
            Intent intent=new Intent(this, CartActivity.class);
            startActivity(intent);
        });

    }
    private  void  setCategoryRecyclerView(){
        int selectedPosition=-1;

        for(int i=0;i<categoryList.size();i++) {
            if (categoryList.get(i).getName().equals(categoryName)) {
                selectedPosition = i;
                break;

            }
        }
        drawerAdapter=new CategoryDrawerAdapter(this,categoryList,categoryName,selectedPosition,this);
        rvDrawer.setLayoutManager(new LinearLayoutManager(this));
        rvDrawer.setAdapter(drawerAdapter);


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
                                    tvCartCount.setVisibility(VISIBLE);
                                    tvCartCount.setText(String.valueOf(productCount.size()));
                                }else{
                                    tvCartCount.setVisibility(GONE);
                                }
                            }

                        }else{
                            tvCartCount.setVisibility(GONE);
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

    private void getCategoryData(){
        try{
            categoryList=new ArrayList<>();
            mReference=database.getReference("categories");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        categoryList.clear();
                        Log.i("TAG", "onDataChange: "+snapshot.getValue());
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Category category=dataSnapshot.getValue(Category.class);
                            if(category!=null){
                                categoryList.add(category);
                            }
                            hideProgressBar();
                            setCategoryRecyclerView();
                        }
                    }catch (Exception e){
                        Log.i("TAG", "onDataChange: "+e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hideProgressBar();
                    Toast.makeText(ProductActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.i("TAG", "getCategoryData: "+e);
        }

    }

    private void backButtonClick(){
//        imBackArrow.setOnClickListener(view -> {
//            finish();
//        });
    }
    private void setRecyclerview(){
        adapter=new ProductAdapter(this,productList,categoryName);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        recyclerView.setAdapter(adapter);
    }

    private void fetchProducts(){
        mReference=database.getReference("categories").child(categoryName).child("products");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                     Product product=dataSnapshot.getValue(Product.class);
                    if(product!=null){
                        productList.add(product);
                    }
                }
                hideProgressBar();
                setRecyclerview();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressBar();
                Toast.makeText(ProductActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showProgressBar(){
        progressBar.setVisibility(VISIBLE);

    }
    private void hideProgressBar(){
        progressBar.setVisibility(GONE);

    }

    @Override
    public void onSelected(String categoryName) {
        this.categoryName=categoryName;
        txtCategory.setText(categoryName);
        fetchProducts();
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}