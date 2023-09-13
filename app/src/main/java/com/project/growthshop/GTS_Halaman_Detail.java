package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class GTS_Halaman_Detail extends AppCompatActivity {
    private ImageView imageView, back;
    private TextView namaBarangTextView;
    private TextView statusBarangTextView;
    private TextView hargaBarangTextView;
    private TextView stokBarangTextView;
    private ImageView addButton;
    private Button beliLangsungButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private String itemId;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_detail);

        inisialisasiFirebase();
        inisialisasiTampilan();
        idItemDetailBarang();
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
        imageView = findViewById(R.id.imageView);
        namaBarangTextView = findViewById(R.id.namabar);
        statusBarangTextView = findViewById(R.id.statusbar);
        hargaBarangTextView = findViewById(R.id.hargabar);
        stokBarangTextView = findViewById(R.id.stokbar);
        addButton = findViewById(R.id.addBarang);
        beliLangsungButton = findViewById(R.id.btnbelilgsng);
        back = findViewById(R.id.back);
    }

    private void aturClickListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahkanKeKeranjang();
            }
        });

        beliLangsungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanAlert11();
                beliBarangLangsung();
            }
        });

        back.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Detail.this, GTS_Halaman_Utama.class);
            startActivity(intent);
        });
    }

    private void idItemDetailBarang(){
        Intent intent = getIntent();
        itemId = intent.getStringExtra("id_barang");
        tampilkanDetailBarang(itemId);
    }

    private void tampilkanDetailBarang(String itemId) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("Data-Produk").child(itemId);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ItemData itemData = dataSnapshot.getValue(ItemData.class);
                    if (itemData != null) {
                        String namaBarang = itemData.getNama();
                        String hargaBarang = itemData.getHarga();
                        String imageUrl = itemData.getUrl();
                        String statusBarang = itemData.getStatus();
                        String stokBarang = itemData.getJumlah();

                        namaBarangTextView.setText(namaBarang);
                        hargaBarangTextView.setText(formatRupiah(hargaBarang));
                        Picasso.get().load(imageUrl).into(imageView);
                        statusBarangTextView.setText(statusBarang);
                        stokBarangTextView.setText(stokBarang);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan database
            }
        });
    }

    private void tambahkanKeKeranjang() {
        DatabaseReference cartRef = mDatabase.child("keranjang").child(userId).push();
        String cartItemId = cartRef.getKey();

        DatabaseReference itemRef = mDatabase.child("Data-Produk").child(itemId);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ItemData itemData = dataSnapshot.getValue(ItemData.class);
                    if (itemData != null) {
                        String hargaBarang = itemData.getHarga();
                        String namaBarang = itemData.getNama();
                        String urlBarang = itemData.getUrl();

                        cartRef.child("id").setValue(cartItemId);
                        cartRef.child("harga").setValue(hargaBarang);
                        cartRef.child("nama").setValue(namaBarang);
                        cartRef.child("url").setValue(urlBarang);

                        tampilkanAlert10();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan database
            }
        });
    }

    private void beliBarangLangsung() {
        DatabaseReference itemRef = mDatabase.child("Data-Produk").child(itemId);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final alert11 alert11 = new alert11(GTS_Halaman_Detail.this);
                alert11.startLoding();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alert11.dismis();
                        if (dataSnapshot.exists()) {
                            ItemData itemData = dataSnapshot.getValue(ItemData.class);
                            if (itemData != null) {
                                String namaBarang = itemData.getNama();
                                String hargaBarang = itemData.getHarga();
                                String idBarang = itemData.getId();
                                String gambarBarang = itemData.getUrl();
                                Intent intent = new Intent(GTS_Halaman_Detail.this, GTS_Checkout_Barang.class);
                                intent.putExtra("nama_barang", namaBarang);
                                intent.putExtra("harga_barang", hargaBarang);
                                intent.putExtra("id_item", idBarang);
                                intent.putExtra("gambar_barang", gambarBarang);
                                startActivity(intent);
                            }
                        }
                    }
                }, 1800);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Tangani kesalahan database
            }
        });
    }

    private void tampilkanAlert10() {
        final alert10 alert10 = new alert10(GTS_Halaman_Detail.this);
        alert10.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(alert10::dismis, 1600);
    }

    private void tampilkanAlert11() {
        final alert11 alert11 = new alert11(GTS_Halaman_Detail.this);
        alert11.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(alert11::dismis, 1600);
    }

    private String formatRupiah(String price) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(Double.parseDouble(price));
    }
}
