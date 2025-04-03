package com.example.greeshma_prasad_project2.category;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.cart.CartActivity;
import com.example.greeshma_prasad_project2.models.Cart;
import com.example.greeshma_prasad_project2.models.Category;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {
    List<Category> categoryList;
    CategoryAdapter adapter;
    RecyclerView mRecyclerview;
    private DatabaseReference mReference;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private DatabaseReference cartReference;
    private String userId;

    private TextView tvCartCount;
    private FrameLayout flCartContainer;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       bottomNavigationView=findViewById(R.id.bottom_nav_view);

//       if(savedInstanceState==null){
//           loadInitialFragment();
//       }

//       setupBottomNavigation();
       setCartCounToBottomBar();
       updateCartCount();

    }


    private void setCartCounToBottomBar(){
        BadgeDrawable cartCount=bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        cartCount.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        cartCount.setBadgeTextColor(getResources().getColor(R.color.white));
        cartCount.setVisible(false);
    }

//    private void loadInitialFragment(){
//        getSupportFragmentManager().beginTransaction().
//                replace(R.id.fragment_container,new HomeFragment()).commit();
//
//    }
//    @SuppressLint("NonConstantResourceId")
//    private void setupBottomNavigation(){
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            Fragment selectdFragment=null;
//            if(item.getItemId()==R.id.nav_category){
//                selectdFragment=new HomeFragment();
//            }else if(item.getItemId()==R.id.nav_cart){
//                selectdFragment=new CartFragment();
//
//            }else if(item.getItemId()==R.id.nav_profile){
//                selectdFragment=new ProfileFragment();
//
//            }
//            else{
//                selectdFragment=new HomeFragment();
//            }
//            if(selectdFragment!=null){
//                getSupportFragmentManager().beginTransaction().
//                        replace(R.id.fragment_container,selectdFragment).commit();
//            }
//        return true;
//        });
//    }
    private void backPress(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showAlertDialog("Exit App","Are you sure want to exit app?");
            }
        });
    }

    private void moveToCartScreen(){
        flCartContainer.setOnClickListener(view -> {
            Intent intent=new Intent(this, CartActivity.class);
            startActivity(intent);
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
                                    tvCartCount.setVisibility(VISIBLE);
                                    tvCartCount.setText(String.valueOf(productCount.size()));
                                }else{
                                    tvCartCount.setVisibility(GONE);
                                }
                                updateCartBadge(productCount.size());
                            }

                        }else{
                            tvCartCount.setVisibility(GONE);
                        }
                    }catch (Exception e){
                        Log.i("TAG", "updateCartCount: "+e.toString());
                    }


                }

                private void updateCartBadge(int count){
                    BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
                    if(count>0){
                        badge.setNumber(count);
                        badge.setVisible(true);
                    }else{
                        badge.setVisible(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            Log.i("TAG", "updateCartCount: "+e.toString());
        }

    }


    private void getCategoryData(){
        mReference=database.getReference("categories");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                Log.i("TAG", "onDataChange: "+snapshot.getValue());
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                   Category category=dataSnapshot.getValue(Category.class);
                   if(category!=null){
                       categoryList.add(category);
                       setRecyclerView();
                       hideProgressBar();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideProgressBar();
                Toast.makeText(HomeActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setRecyclerView(){
        adapter= new CategoryAdapter(HomeActivity.this,categoryList);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mRecyclerview.setLayoutManager(new GridLayoutManager(this,4));
        }else{
            mRecyclerview.setLayoutManager(new GridLayoutManager(this,2));
        }

        mRecyclerview.setAdapter(adapter);
    }
    private void showProgressBar(){
        progressBar.setVisibility(VISIBLE);

    }
    private void hideProgressBar(){
        progressBar.setVisibility(GONE);

    }



    public void showAlertDialog(String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> finish());
        AlertDialog alertDialog= alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}