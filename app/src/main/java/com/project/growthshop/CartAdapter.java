package com.project.growthshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<ItemData> cartItemList;

    private CartItemListener cartItemListener;


    public CartAdapter(Context context, List<ItemData> cartItemList, CartItemListener cartItemListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.cartItemListener = cartItemListener;
    }



    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemData itemData = cartItemList.get(position);
        Picasso.get().load(itemData.getUrl()).into(holder.imageView);
        holder.namabar.setText(itemData.getNama());
        holder.hargabar.setText(formatRupiah(itemData.getHarga()));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = cartItemList.get(position).getId();
                cartItemListener.onItemDeleted(itemId);
            }
        });

        holder.beliin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemData item = cartItemList.get(position);
                cartItemListener.onBuyButtonClicked(item);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView namabar;
        private TextView hargabar;
        private Button deleteButton, beliin;




        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            namabar = itemView.findViewById(R.id.namabar);
            hargabar = itemView.findViewById(R.id.hargabar);
            deleteButton = itemView.findViewById(R.id.hapusin);
            beliin = itemView.findViewById(R.id.beliin);
        }
    }




    private String formatRupiah(String harga) {
        double amount = Double.parseDouble(harga);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

    private void setAlert14(){

    }

    public interface CartItemListener {
        void onItemDeleted(String itemId);
        void onBuyButtonClicked(ItemData item);
    }

}
