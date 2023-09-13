package com.project.growthshop;

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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context context;
    private static List<ItemData> itemList;
    private static OnItemClickListener listener;

    public ItemAdapter(Context context, List<ItemData> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcyle_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemData itemData = itemList.get(position);
        Picasso.get().load(itemData.getUrl()).into(holder.imageView);
        holder.namabar.setText(itemData.getNama());
        holder.statusbar.setText(itemData.getStatus());

        double harga = Double.parseDouble(itemData.getHarga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedHarga = formatRupiah.format(harga);
        holder.hargabar.setText(formattedHarga);
        holder.stokbar.setText(itemData.getJumlah() + " Items");

        holder.addBarang.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                ItemData item = itemList.get(adapterPosition);
                listener.onItemClick(item);
            }
        });

        holder.btndetal.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                ItemData item = itemList.get(adapterPosition);
                listener.onDetailButtonClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ItemData item);
        void onDetailButtonClicked(ItemData item);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView namabar;
        private TextView statusbar;
        private TextView hargabar;
        private TextView stokbar;
        private Button addBarang;
        private Button btndetal;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            namabar = itemView.findViewById(R.id.namabar);
            statusbar = itemView.findViewById(R.id.statusbar);
            hargabar = itemView.findViewById(R.id.hargabar);
            stokbar = itemView.findViewById(R.id.stokbar);
            addBarang = itemView.findViewById(R.id.addBarang);
            btndetal = itemView.findViewById(R.id.btndetal);

            addBarang.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                    ItemData item = itemList.get(adapterPosition);
                    listener.onItemClick(item);
                }
            });

            btndetal.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                    ItemData item = itemList.get(adapterPosition);
                    listener.onDetailButtonClicked(item);
                }
            });
        }
    }
}
