package com.project.growthshop;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GTS_Halaman_Cart extends AppCompatActivity implements CartAdapter.CartItemListener {
    private RecyclerView recyclerView;
    private List<ItemData> cartItemList;
    private DatabaseReference databaseReference;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    private String itemId;

    private EditText searchEditText;
    private Button searchButton;
    private ImageView home, acc;
    private TextView NamaUser;
    private CartAdapter cartAdapter;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_cart);
        setupKeyboardClosing();
        inisialisasiFirebase();
        inisialisasiTampilan();
        aturRecyclerView();
        ambilDataItemDariFirebase();
        aturClickListener();
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
        searchEditText = findViewById(R.id.cariin);
        searchButton = findViewById(R.id.btncari);
        home = findViewById(R.id.home);
        acc = findViewById(R.id.acc);
        NamaUser = findViewById(R.id.NamaUser);
        recyclerView = findViewById(R.id.recycart);
    }

    private void aturClickListener() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEditText.getText().toString().trim();
                cariItem(keyword);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GTS_Halaman_Cart.this, GTS_Halaman_Utama.class);
                startActivity(intent);
            }
        });

        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GTS_Halaman_Cart.this, GTS_Halaman_ACC.class);
                startActivity(intent);
            }
        });
    }


    private void aturRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList, this);
        recyclerView.setAdapter(cartAdapter);
    }

    private void ambilDataItemDariFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("keranjang").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemData itemData = snapshot.getValue(ItemData.class);
                    cartItemList.add(itemData);
                }

                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GTS_Halaman_Cart", "Database Error: " + databaseError.getMessage());
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
        List<ItemData> filteredList = new ArrayList<>();

        for (ItemData item : cartItemList) {
            String namaBarang = item.getNama().toLowerCase();
            if (namaBarang.contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }
        cartAdapter = new CartAdapter(this, filteredList, this);
        recyclerView.setAdapter(cartAdapter);
    }

    private void hapusItemDariKeranjang(String itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus")
                .setMessage("Anda yakin ingin menghapus barang ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("keranjang").child(userId).child(itemId);
                        itemRef.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Item berhasil dihapus dari keranjang
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("GTS_Halaman_Cart", "Gagal menghapus item dari keranjang: " + e.getMessage());
                                    }
                                });
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    @Override
    public void onItemDeleted(String itemId) {
        hapusItemDariKeranjang(itemId);
    }

    @Override
    public void onBuyButtonClicked(ItemData item) {

        final alert11 alert11 = new alert11(GTS_Halaman_Cart.this);
        alert11.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert11.dismis();
                Intent intent = new Intent(GTS_Halaman_Cart.this, GTS_Checkout_Barang.class);
                intent.putExtra("gambar_barang", item.getUrl());
                intent.putExtra("nama_barang", item.getNama());
                intent.putExtra("harga_barang", item.getHarga());
                intent.putExtra("id_barang", item.getId());
                intent.putExtra("id_item", item.getId_item());
                startActivity(intent);
            }
        }, 1800);

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

}
