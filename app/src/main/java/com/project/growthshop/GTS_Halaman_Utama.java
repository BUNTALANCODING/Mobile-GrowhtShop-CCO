package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GTS_Halaman_Utama extends AppCompatActivity implements ItemAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<ItemData> daftarItem;
    private DatabaseReference referensiDatabase;
    private TextView NamaUser;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ViewPager2 viewPager;
    private List<Integer> daftarGambar = new ArrayList<>();
    private String userId;
    private ImageView cart, home, acc, lock, farmy, itemy;
    private EditText inputCari;
    private Button btnCari;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_utama);

        setupKeyboardClosing();
        inisialisasiTampilan();
        aturClickListener();
        inisialisasiFirebase();
        aturViewPager();
        aturRecyclerView();
        ambilDataItemDariFirebase();
        aturNamaPengguna();
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
        cart = findViewById(R.id.cart);
        lock = findViewById(R.id.locky);
        farmy = findViewById(R.id.farmy);
        itemy = findViewById(R.id.itemy);
        acc = findViewById(R.id.acc);
        inputCari = findViewById(R.id.cariin);
        btnCari = findViewById(R.id.btncari);
        viewPager = findViewById(R.id.ViewPager);
        recyclerView = findViewById(R.id.inirecyle);
        NamaUser = findViewById(R.id.NamaUser);
    }

    private void aturClickListener() {
        cart.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Utama.this, GTS_Halaman_Cart.class);
            startActivity(intent);
        });

        lock.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Utama.this, GTS_Halaman_KategoriLock.class);
            startActivity(intent);
        });

        itemy.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Utama.this, GTS_Halaman_KategoriItem.class);
            startActivity(intent);
        });

        farmy.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Utama.this, GTS_Halaman_KategoriFarm.class);
            startActivity(intent);
        });

        acc.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Utama.this, GTS_Halaman_ACC.class);
            startActivity(intent);
        });

        btnCari.setOnClickListener(v -> {
            String keyword = inputCari.getText().toString().trim();
            cariItem(keyword);
        });


    }


    private void aturViewPager() {
        daftarGambar.add(R.drawable.gts_banner2);
        daftarGambar.add(R.drawable.gts_banner1);
        daftarGambar.add(R.drawable.gts_banner3);
        CarouselPagerAdapter pagerAdapter = new CarouselPagerAdapter(this, daftarGambar);
        viewPager.setAdapter(pagerAdapter);
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
                    ItemData dataItem = snapshot.getValue(ItemData.class);
                    daftarItem.add(dataItem);
                }

                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GTS_Halaman_Utama", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void aturNamaPengguna() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("pengguna").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            NamaUser.setText(name);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("GTS_Halaman_Cart", "Database Error: " + databaseError.getMessage());
                }
            });
        } else {
            // User tidak login, tambahkan penanganan sesuai kebutuhan Anda
        }
    }

    private void cariItem(String keyword) {
        List<ItemData> hasilPencarian = new ArrayList<>();
        for (ItemData item : daftarItem) {
            String namaItem = item.getNama().toLowerCase();
            if (namaItem.contains(keyword.toLowerCase())) {
                hasilPencarian.add(item);
            }
        }
        ItemAdapter adapterPencarian = new ItemAdapter(GTS_Halaman_Utama.this, hasilPencarian);
        recyclerView.setAdapter(adapterPencarian);
    }




    private void tambahkanKeKeranjang(ItemData dataItem) {
        DatabaseReference referensiKeranjang = mDatabase.child("keranjang").child(userId).push();
        String idKeranjang = referensiKeranjang.getKey();

        // Simpan data item ke dalam tabel keranjang
        referensiKeranjang.child("id").setValue(idKeranjang);
        referensiKeranjang.child("id_item").setValue(dataItem.getId());
        referensiKeranjang.child("harga").setValue(dataItem.getHarga());
        referensiKeranjang.child("nama").setValue(dataItem.getNama());
        referensiKeranjang.child("url").setValue(dataItem.getUrl());

        tampilkanAlert10();
    }
    @Override
    public void onDetailButtonClicked(ItemData itemId) {
        Intent intent = new Intent(GTS_Halaman_Utama.this, GTS_Halaman_Detail.class);
        intent.putExtra("id_barang", itemId.getId());
        startActivity(intent);
    }

    @Override
    public void onItemClick(ItemData dataItem) {
        FirebaseUser penggunaSekarang = mAuth.getCurrentUser();
        if (penggunaSekarang != null) {
            tambahkanKeKeranjang(dataItem);
        } else {
            // Pengguna tidak login, tambahkan penanganan sesuai kebutuhan Anda
        }
    }

    private void setupKeyboardClosing() {
        View rootLayout = findViewById(android.R.id.content);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View focusedView = getCurrentFocus();
                if (focusedView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                    focusedView.clearFocus();
                }
                return false;
            }
        });
    }

    private void tampilkanAlert10() {
        final alert10 alert10 = new alert10(GTS_Halaman_Utama.this);
        alert10.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(alert10::dismis, 1600);
    }
}
