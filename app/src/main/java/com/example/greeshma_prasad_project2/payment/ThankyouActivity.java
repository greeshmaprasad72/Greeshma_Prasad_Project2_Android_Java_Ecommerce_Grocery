package com.example.greeshma_prasad_project2.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.category.CategoryActivity;
import com.example.greeshma_prasad_project2.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cdflynn.android.library.checkview.CheckView;

public class ThankyouActivity extends AppCompatActivity {
    private CheckView checkView;
    private String userId="";
    private FirebaseDatabase database;
    private Button btBackHome,btSendReceipt;
    private String email="";
    private List<Cart> cartList = new ArrayList<>();
    private double subTotal = 0.0, deliveryFee = 5.0, tax = 0.0, total = 0.0;

    private RecyclerView recyclerView;
    private ReceiptAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thankyou);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkView=findViewById(R.id.imageview_success);
        btBackHome=findViewById(R.id.button_back_home);
        btSendReceipt=findViewById(R.id.button_send_recipet);
        recyclerView=findViewById(R.id.recyclerview_cart_items);
        checkView.check();

        adapter = new ReceiptAdapter(cartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database=FirebaseDatabase.getInstance();
        getUserDetails();
        getCartData();
        btBackHome.setOnClickListener(view -> {
            removeAllItemFromCart();
        });
        btSendReceipt.setOnClickListener(view -> {
            getCartDataAndSendEmail();
        });

    }

    private void getUserDetails(){
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    email=snapshot.child("email").getValue(String.class);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCartData() {
        DatabaseReference cartRef = database.getReference("cart").child(userId);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                subTotal = 0.0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Cart cart = dataSnapshot.getValue(Cart.class);
                    if (cart != null) {
                        cartList.add(cart);
                        calculateSubTotal(cart);
                    }
                }


                tax = subTotal * 0.1;
                total = subTotal + deliveryFee + tax;

                adapter.notifyDataSetChanged();
                if (cartList.isEmpty()) {
                    Toast.makeText(ThankyouActivity.this, "No items in cart.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThankyouActivity.this, "Failed to load cart data.", Toast.LENGTH_SHORT).show();
                Log.e("ThankyouActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void calculateTotal() {

        if(subTotal>25.0 || subTotal==0.0){
            deliveryFee=0.0;
        } else{
            deliveryFee=4.99;
        }
        tax=Math.round((subTotal* 0.15)*100.0)/100.0;
        total=subTotal+deliveryFee+tax;


    }
    private void calculateSubTotal(Cart cart) {
        double price = cart.getProductPrice();
        int quantity = cart.getProductCount();
        subTotal += price * quantity;
    }

    private void getCartDataAndSendEmail() {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cart").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
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
                        if (!cartList.isEmpty()) {
                            sendCartDataEmail();
                        }

                    } catch (Exception e) {
                        subTotal = 0.0;
                        deliveryFee = 0.0;
                        tax = 0.0;
                        total = 0.0;
                        Log.e("TAG", "getCartData: " + e);
                        Toast.makeText(ThankyouActivity.this, "Error processing cart data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ThankyouActivity.this, "Failed to load cart data.", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            subTotal = 0.0;
            deliveryFee = 0.0;
            tax = 0.0;
            total = 0.0;
            Log.e("TAG", "getCartData: " + e);

        }
    }


    private void sendCartDataEmail() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Cart Details");

        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Here are your current cart items:\n\n");

        body.append("Items:\n");
        for (Cart cartItem : cartList) {
            double itemTotal = cartItem.getProductPrice() * cartItem.getProductCount();
            body.append("• ").append(cartItem.getProductName())
                    .append(" (").append(cartItem.getProductQuantity()).append(")")
                    .append(" $").append(String.format("%.2f", cartItem.getProductPrice()))
                    .append(" = $").append(String.format("%.2f", itemTotal))
                    .append("\n");
        }

        body.append("\nSubtotal: $").append(String.format("%.2f", subTotal));
        body.append("\nDelivery Fee: $").append(String.format("%.2f", deliveryFee));
        body.append("\nTax: $").append(String.format("%.2f", tax));
        body.append("\nTotal: $").append(String.format("%.2f", total));

        body.append("\n\nThank you for shopping with us!");

        // Set email body
        emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());

        try {

            startActivity(Intent.createChooser(emailIntent, "Send cart details..."));
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(ThankyouActivity.this, "No email clients installed on device", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeAllItemFromCart();
    }

    private void removeAllItemFromCart(){
        database.getReference("cart").child(userId).removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                homeNavigation();
            }
        });
    }

    private void homeNavigation(){
        Intent intent =new Intent(this, CategoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}