package com.example.greeshma_prasad_project2.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.models.Cart;

import java.util.List;

public class ReceiptAdapter  extends RecyclerView.Adapter<ReceiptAdapter.MyViewHolder> {
    private List<Cart> cartList;

    public ReceiptAdapter(List<Cart> cartList) {
        this.cartList = cartList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cart cartItem = cartList.get(position);
        holder.textViewProductName.setText(cartItem.getProductName());
        holder.textViewQuantity.setText("Qty: " + cartItem.getProductCount());
        holder.textViewPrice.setText(String.format("$%.2f", cartItem.getProductPrice()));
        double total = cartItem.getProductPrice() * cartItem.getProductCount();
        holder.textViewTotal.setText(String.format("Total: $%.2f", total));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewProductName, textViewQuantity, textViewPrice, textViewTotal;
         public MyViewHolder(@NonNull View itemView) {
             super(itemView);
             textViewProductName = itemView.findViewById(R.id.textview_product_name);
             textViewQuantity = itemView.findViewById(R.id.textview_quantity);
             textViewPrice = itemView.findViewById(R.id.textview_price);
             textViewTotal = itemView.findViewById(R.id.textview_total);
         }
     }
}
