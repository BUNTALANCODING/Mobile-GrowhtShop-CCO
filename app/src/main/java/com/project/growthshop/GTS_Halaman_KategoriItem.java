package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class GTS_Halaman_KategoriItem extends AppCompatActivity implements ItemAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private List<ItemData> daftarItem;
    private DatabaseReference referensiDatabase;
    private DatabaseReference referensiFirebase;
    private FirebaseAuth auth;
    private String idPengguna;

    private EditText searchEditText;
    private Button searchButton;
    private ItemAdapter itemAdapter;
    private ImageView back;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_kategori_item);

        inisialisasiFirebase();
        inisialisasiView();
        aturRecyclerView();
        aturClickListener();
        ambilDataItemDariFirebase();
        aturPenutupanKeyboardPadaSentuhanLuar();
    }

    private void inisialisasiFirebase() {
        auth = FirebaseAuth.getInstance();
        referensiFirebase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser penggunaSekarang = auth.getCurrentUser();
        if (penggunaSekarang != null) {
            idPengguna = penggunaSekarang.getUid();
        } else {
            // Pengguna tidak login, tambahkan penanganan sesuai kebutuhan Anda
        }
    }

    private void inisialisasiView() {
        searchEditText = findViewById(R.id.cariin);
        searchButton = findViewById(R.id.btncari);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recylock);
    }

    private void aturClickListener() {
        back.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_KategoriItem.this, GTS_Halaman_Utama.class);
            startActivity(intent);
        });
        searchButton.setOnClickListener(view -> {
            String queryPencarian = searchEditText.getText().toString().trim();
            cariItem(queryPencarian);
        });
    }



    private void aturRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        daftarItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, daftarItem);
        itemAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(itemAdapter);
    }

    private void ambilDataItemDariFirebase() {
        referensiDatabase = FirebaseDatabase.getInstance().getReference().child("Data-Produk");
        referensiDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                daftarItem.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemData itemData = snapshot.getValue(ItemData.class);
                    if (itemData != null && itemData.getKategori() != null && itemData.getKategori().equalsIgnoreCase("Item")) {
                        daftarItem.add(itemData);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GTS_Halaman_KategoriLock", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void cariItem(String keyword) {
        List<ItemData> hasilPencarian = new ArrayList<>();
        for (ItemData item : daftarItem) {
            String namaItem = item.getNama().toLowerCase();
            if (namaItem.contains(keyword.toLowerCase())) {
                hasilPencarian.add(item);
            }
        }
        ItemAdapter adapterPencarian = new ItemAdapter(GTS_Halaman_KategoriItem.this, hasilPencarian);
        recyclerView.setAdapter(adapterPencarian);
    }

    @Override
    public void onItemClick(ItemData item) {
        FirebaseUser penggunaSekarang = auth.getCurrentUser();
        if (penggunaSekarang != null) {
            tampilkanAlert10();
            DatabaseReference referensiKeranjang = referensiFirebase.child("keranjang").child(idPengguna).push();
            String idItemKeranjang = referensiKeranjang.getKey(); // Mendapatkan ID unik untuk item keranjang baru
            item.setId(idItemKeranjang); // Mengatur ID item menjadi ID keranjang baru
            referensiKeranjang.setValue(item);
        } else {
            // Pengguna tidak login, tambahkan penanganan sesuai kebutuhan Anda
        }
    }

    @Override
    public void onDetailButtonClicked(ItemData item) {
        Intent intent = new Intent(GTS_Halaman_KategoriItem.this, GTS_Halaman_Detail.class);
        intent.putExtra("id_barang", item.getId());
        startActivity(intent);
    }

    private void tampilkanAlert10() {
        final alert10 alert10 = new alert10(GTS_Halaman_KategoriItem.this);
        alert10.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            alert10.dismis();
        }, 1600);
    }

    private void aturPenutupanKeyboardPadaSentuhanLuar() {
        View layoutUtama = findViewById(android.R.id.content);
        layoutUtama.setOnTouchListener((v, event) -> {
            View fokusView = getCurrentFocus();
            if (fokusView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fokusView.getWindowToken(), 0);
                fokusView.clearFocus();
            }
            return false;
        });
    }
}
