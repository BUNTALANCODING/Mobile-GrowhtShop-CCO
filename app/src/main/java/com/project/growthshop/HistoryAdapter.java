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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private Context context;
    private List<HistoryData> historiItemList;
    private HistoryAdapter.HistoryItemListener historyItemListener;

    public HistoryAdapter(Context context, List<HistoryData> historiItemList, HistoryAdapter.HistoryItemListener historyItemListener) {
        this.context = context;
        this.historiItemList = historiItemList;
        this.historyItemListener= historyItemListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.histori_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HistoryData historiData = historiItemList.get(position);
        Picasso.get().load(historiData.getGambarBarang()).into(holder.imageView);
        holder.namabar.setText(historiData.getNamaBarang());
        holder.hargabar.setText(formatRupiah(historiData.getTotalHarga()));
        holder.sttspes.setText(historiData.getStatusPemesanan());
        holder.kodePemesanan.setText(historiData.getKodePemesanan());

        holder.btnkonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryData item = historiItemList.get(position);
                historyItemListener.onKonfirmasiClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historiItemList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView namabar;
        private TextView hargabar, sttspes, kodePemesanan;

        private Button btnkonfirmasi;




        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            namabar = itemView.findViewById(R.id.namabar);
            hargabar = itemView.findViewById(R.id.hargabar);
            sttspes = itemView.findViewById(R.id.sttspes);
            kodePemesanan = itemView.findViewById(R.id.kodePemesanan);
            btnkonfirmasi = itemView.findViewById(R.id.button);
        }
    }

    private String formatRupiah(String harga) {
        double amount = Double.parseDouble(String.valueOf(harga));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

    public interface HistoryItemListener {
        void onKonfirmasiClicked(HistoryData item);
    }


}
