package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GTS_Riwayat_Pembelian extends AppCompatActivity implements HistoryAdapter.HistoryItemListener {

    private RecyclerView recyclerView;
    private List<HistoryData> historyItemList;
    private DatabaseReference databaseReference;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    private EditText searchEditText;
    private Button searchButton;

    private HistoryAdapter historyAdapter;
    private ImageView back;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat_pembelian);

        inisialisasiFirebase();
        inisialisasiTampilan();
        aturRecyclerView();
        ambilDataItemDariFirebase();
        aturClickListener();
    }

    private void inisialisasiFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Pengguna tidak login, tambahkan penanganan sesuai kebutuhan Anda
        }

    }

    private void inisialisasiTampilan() {
        searchEditText = findViewById(R.id.cariin);
        searchButton = findViewById(R.id.btncari);
        recyclerView = findViewById(R.id.recyhis);
        back = findViewById(R.id.back);
    }

    private void aturClickListener() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEditText.getText().toString().trim();
                cariItem(keyword);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GTS_Riwayat_Pembelian.this, GTS_Halaman_ACC.class);
                startActivity(intent);
            }
        });
    }

    private void aturRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyItemList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyItemList , this);
        recyclerView.setAdapter(historyAdapter);
    }

    private void ambilDataItemDariFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("penjualan").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HistoryData historyData = snapshot.getValue(HistoryData.class);
                    historyItemList.add(historyData);
                }

                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GTS_Riwayat_Pembelian", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void cariItem(String keyword) {
        List<HistoryData> filteredList = new ArrayList<>();

        for (HistoryData item : historyItemList) {
            String namaBarang = item.getKodePemesanan().toLowerCase();
            if (namaBarang.contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }
        historyAdapter = new HistoryAdapter(this, filteredList, this);
        recyclerView.setAdapter(historyAdapter);
    }

    @Override
    public void onKonfirmasiClicked(HistoryData item) {
        final alert11 alert11 = new alert11(GTS_Riwayat_Pembelian.this);
        alert11.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert11.dismis();
                Intent intent = new Intent(GTS_Riwayat_Pembelian.this, GTS_Halaman_Konfirmasi.class);
                intent.putExtra("kode_pembelian", item.getKodePemesanan());
                intent.putExtra("harga_total", item.getTotalHarga());

                startActivity(intent);
            }
        }, 1800);
    }
}