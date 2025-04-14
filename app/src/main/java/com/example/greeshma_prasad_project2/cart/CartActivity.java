package com.example.greeshma_prasad_project2.cart;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerview;
    private ImageView imBackArrow;
    private TextView tvSubTotal, tvDeliveryFee, tvTax, tvTotal;
    private String userId;
    private DatabaseReference reference;
    private CartAdapter adapter;

    private ArrayList<Cart> cartList;
    private double subTotal = 0.0;
    private double deliveryFee=0.0;
    private double tax=0.0;
    private double total=0.0;
    private Button btCartContinue,btEmptyCart;
    private CardView clSummary;
    private LinearLayout llEmptyCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cartList = new ArrayList<>();
        recyclerview = findViewById(R.id.rv_cart);
        imBackArrow = findViewById(R.id.imageview_back_arrow);
        tvSubTotal = findViewById(R.id.tv_sub_total);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        btCartContinue=findViewById(R.id.bt_cart_continue);
        clSummary=findViewById(R.id.cv_total);
        llEmptyCart=findViewById(R.id.ll_empty_cart);
        btEmptyCart=findViewById(R.id.bt_start_shopping);
        btEmptyCart.setOnClickListener(view -> {
                finish();
        });
        btCartContinue.setOnClickListener(view -> {
            Intent intent=new Intent(this, AddressDetailsActivity.class);
            startActivity(intent);
            finish();
        });

        onBackPress();
        getCartData();

    }

    private void getCartData() {
        try{
            reference = FirebaseDatabase.getInstance().getReference("cart").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        cartList.clear();
                        subTotal = 0.0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Cart cart = dataSnapshot.getValue(Cart.class);
                            if (cart != null) {
                                cartList.add(cart);
                                calculateSubTotal(cart);
                            }
                        }
                        calculateTotal();
                        setRecyclerview();
                        displayView();

                    }catch (Exception e){
                        subTotal = 0.0;
                        deliveryFee=0.0;
                        tax=0.0;
                        total=0.0;
                        Log.i("TAG", "getCartData: "+e);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Failed to load category.", Toast.LENGTH_SHORT).show();
                    displayView();
                }
            });
        }catch (Exception e){
            subTotal = 0.0;
          deliveryFee=0.0;
             tax=0.0;
            total=0.0;
            Log.i("TAG", "getCartData: "+e);
        }

    }

    private void displayView(){
        if(cartList.isEmpty()){
            clSummary.setVisibility(GONE);
            recyclerview.setVisibility(GONE);
            btCartContinue.setVisibility(GONE);
            llEmptyCart.setVisibility(VISIBLE);


        }else {
            clSummary.setVisibility(VISIBLE);
            recyclerview.setVisibility(VISIBLE);
            btCartContinue.setVisibility(VISIBLE);
            llEmptyCart.setVisibility(GONE);
        }
    }
    private void calculateSubTotal(Cart cart) {
        double price = cart.getProductPrice();
        int quantity = cart.getProductCount();
        subTotal += price * quantity;
    }

    private void calculateTotal() {

        if(subTotal>25.0 || subTotal==0.0){
            deliveryFee=0.0;
        } else{
            deliveryFee=4.99;
        }
        tax=Math.round((subTotal* 0.13)*100.0)/100.0;
        total=subTotal+deliveryFee+tax;
        tvSubTotal.setText(String.format(Locale.US, "$ %.2f", subTotal));
        tvDeliveryFee.setText(String.format(Locale.US, "$ %.2f", deliveryFee));
        tvTax.setText(String.format(Locale.US, "$ %.2f", tax));
        tvTotal.setText(String.format(Locale.US, "$ %.2f", total));

    }

    private void setRecyclerview() {
        adapter = new CartAdapter(this,cartList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);

    }


    private void onBackPress() {
        imBackArrow.setOnClickListener(view -> {
            finish();
        });
    }
}