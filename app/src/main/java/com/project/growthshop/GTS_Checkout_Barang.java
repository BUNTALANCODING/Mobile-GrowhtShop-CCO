package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class GTS_Checkout_Barang extends AppCompatActivity {
    private double hargaPerItem = 0.0;
    private int jumlahItem = 0;
    private double totalHarga = 0.0;

    private EditText JumlahItem;
    private TextView TotalHarga, NamaProduct, HargaBarang, noPesanan, statusPes;

    private TextView NamaUser, idbarang, imageView, bank;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private ImageView back;
    private Button checkoutButton;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_barang);
        inisialisasiFirebase();
        inisialisasiTampilan();
        aturClickListener();
        aturNamaPengguna();
        setPerubahanHarga();
        setNomerPesanan();
    }

    private void inisialisasiFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // User is not logged in, handle accordingly
        }
    }

    private void inisialisasiTampilan() {
        NamaProduct = findViewById(R.id.nama_barang);
        HargaBarang = findViewById(R.id.harga_barang);
        JumlahItem = findViewById(R.id.jumlah_barang);
        TotalHarga = findViewById(R.id.totalharga_barang);
        NamaUser = findViewById(R.id.NamaUser);
        back = findViewById(R.id.back);
        checkoutButton = findViewById(R.id.checkoutbtn);
        idbarang = findViewById(R.id.idbarang);
        noPesanan = findViewById(R.id.noPesanan);
        statusPes = findViewById(R.id.statusPes);
        imageView = findViewById(R.id.imageView);
        bank = findViewById(R.id.bank);
    }


    private void aturClickListener() {
        back.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Checkout_Barang.this, GTS_Halaman_Cart.class);
            startActivity(intent);
        });

        checkoutButton.setOnClickListener(view -> {
            String jumlahItemText = JumlahItem.getText().toString();

            if (TextUtils.isEmpty(jumlahItemText)) {
                JumlahItem.setError("Please enter the quantity");
                return;
            }

            int jumlahItem = Integer.parseInt(jumlahItemText);

            // Make sure the item quantity is not less than 1
            if (jumlahItem < 1) {
                JumlahItem.setError("Minimum purchase of 1 item");
                return;
            }

            final alert11 alert11 = new alert11(GTS_Checkout_Barang.this);
            alert11.startLoding();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                alert11.dismis();
                String namaBarang = NamaProduct.getText().toString();
                String noPemesananku = noPesanan.getText().toString();
                String statusPesku = statusPes.getText().toString();
                String namaCosku = NamaUser.getText().toString();
                String linkGambar = imageView.getText().toString();
                String itemId = idbarang.getText().toString();



                // Save the sale to the database
                savePenjualanToDatabase(namaBarang, hargaPerItem, jumlahItem, String.valueOf(totalHarga), noPemesananku, statusPesku, namaCosku, linkGambar);



                // Remove the item from the cart
                hapusItemDariKeranjang(itemId); // Replace 'itemId' with the ID of the item you want to remove
            }, 1600);
        });
    }


    private void aturNamaPengguna() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("pengguna").child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
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
            // User is not logged in, handle accordingly
        }
    }

    private void setNomerPesanan() {
        String randomCode = generateRandomCode();
        String codeWithPrefix = "GTS - " + randomCode + " - AF";
        noPesanan.setText(codeWithPrefix);
    }



    private void setPerubahanHarga() {
        // Get data from the Intent
        Intent intent = getIntent();
        String linkGambar = intent.getStringExtra("gambar_barang");
        String namaBarang = intent.getStringExtra("nama_barang");
        String hargaBarang = intent.getStringExtra("harga_barang");
        imageView.setText(linkGambar);
        NamaProduct.setText(namaBarang);

        double harga = Double.parseDouble(hargaBarang);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaRupiah = formatter.format(harga);
        HargaBarang.setText(hargaRupiah);
        hargaPerItem = harga;
        TotalHarga.setText("Total Harga: " + hargaRupiah);

        JumlahItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateTotalHarga();
            }
        });
    }

    private void updateTotalHarga() {
        double totalHarga = 0.0;

        String jumlahItemText = JumlahItem.getText().toString();

        if (!TextUtils.isEmpty(jumlahItemText)) {
            jumlahItem = Integer.parseInt(jumlahItemText);

            // Make sure the item quantity is not less than 1
            if (jumlahItem < 1) {
                JumlahItem.setError("Minimum purchase of 1 item");
                return;
            }
        } else {
            jumlahItem = 0;
        }

        totalHarga = hargaPerItem * jumlahItem;

        // Apply discount if the total price exceeds 100,000
        if (totalHarga > 100000) {
            double diskon = totalHarga * 0.1; // For example, a 10% discount from the total price
            totalHarga -= diskon;
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String totalHargaRupiah = formatter.format(totalHarga);
        TotalHarga.setText("Total Harga: " + totalHargaRupiah);

        this.totalHarga = totalHarga; // Store the totalHarga value as a class variable
    }



    private void savePenjualanToDatabase(String namaBarang, double hargaPerItem, int jumlahItem, String totalHargaRupiah, String noPemesanan, String statusPesku, String namaCosku, String linkGambar) {
        Intent intent = getIntent();
        String idCart = intent.getStringExtra("id_item");
        String noPemesananku = noPesanan.getText().toString();
        DatabaseReference cartRef = mDatabase.child("Data-Produk").child(idCart);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentStockString = dataSnapshot.child("jumlah").getValue(String.class);
                    if (currentStockString != null) {
                        int currentStock = Integer.parseInt(currentStockString);
                        if (currentStock >= jumlahItem) {
                            final alert12 alert12 = new alert12(GTS_Checkout_Barang.this);
                            alert12.startLoding();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alert12.dismis();
                                    // Stok cukup, lanjutkan dengan checkout
                                    Intent intent = new Intent(GTS_Checkout_Barang.this, GTS_Pesanan_Code.class);
                                    intent.putExtra("namaku", namaCosku);
                                    intent.putExtra("codeku", noPemesananku);
                                    startActivity(intent);
                                    DatabaseReference penjualanRef = mDatabase.child("penjualan").child(userId).push();

                                    String penjualanId = penjualanRef.getKey(); // Mendapatkan ID unik untuk entri penjualan
                                    penjualanRef.child("idBarang").setValue(penjualanId);
                                    penjualanRef.child("namaBarang").setValue(namaBarang);
                                    penjualanRef.child("hargaPerItem").setValue(hargaPerItem);
                                    penjualanRef.child("jumlahItem").setValue(jumlahItem);
                                    penjualanRef.child("totalHarga").setValue(totalHargaRupiah);
                                    penjualanRef.child("kodePemesanan").setValue(noPemesanan);
                                    penjualanRef.child("statusPemesanan").setValue(statusPesku);
                                    penjualanRef.child("namaCostumer").setValue(namaCosku);
                                    penjualanRef.child("gambarBarang").setValue(linkGambar)
                                            .addOnSuccessListener(aVoid -> {
                                                // Simpan berhasil
                                                updateStockQuantity(idCart, jumlahItem);
                                            })
                                            .addOnFailureListener(e -> {
                                                // Simpan gagal
                                                Toast.makeText(GTS_Checkout_Barang.this, "Checkout failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }, 1800);


                        } else {
                            // Stok tidak cukup, tampilkan pesan kesalahan
                            tampilkanAlert13();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GTS_Checkout_Barang", "Database Error: " + databaseError.getMessage());
            }
        });
    }



    private void updateStockQuantity(String idCart, int quantity) {
        DatabaseReference cartRef = mDatabase.child("Data-Produk").child(idCart);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentStockString = dataSnapshot.child("jumlah").getValue(String.class);
                    if (currentStockString != null) {
                        int currentStock = Integer.parseInt(currentStockString);
                        if (currentStock >= quantity) {
                            int updatedStock = currentStock - quantity;
                            cartRef.child("jumlah").setValue(String.valueOf(updatedStock))
                                    .addOnSuccessListener(aVoid -> {
                                        // Stock update successful
                                        Log.d("GTS_Checkout_Barang", "Stock updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Stock update failed
                                        Log.d("GTS_Checkout_Barang", "Failed to update stock");
                                    });
                        } else {
                            // Insufficient stock
                            tampilkanAlert13();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("GTS_Checkout_Barang", "Database Error: " + databaseError.getMessage());
            }
        });
    }







    private void hapusItemDariKeranjang(String itemId) {
        DatabaseReference userCartRef = mDatabase.child("keranjang").child(userId).child(itemId);
        userCartRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private String generateRandomCode() {
        int codeLength = 4; // Number of characters in the random code
        StringBuilder sb = new StringBuilder();

        // Characters that can be used in the random code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    private void tampilkanAlert12() {

    }

    private void tampilkanAlert13() {
        final alert13 alert13 = new alert13(GTS_Checkout_Barang.this);
        alert13.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(alert13::dismis, 1600);
    }



}
