package com.project.growthshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GTS_Halaman_ACC extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ImageView cart, home, acc;
    private TextView NamaUser;
    private Button btnriwayat, logoutku;

    private String userId;
    @Override
    protected void onStart() {
        overridePendingTransition(0,0);
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_acc);

        inisialisasiFirebase();
        inisialisasiTampilan();
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
        home = findViewById(R.id.home);
        cart = findViewById(R.id.cart);
        NamaUser = findViewById(R.id.NamaUser);
        btnriwayat = findViewById(R.id.btnriwayat);
        logoutku = findViewById(R.id.logoutku);
    }

    private void aturClickListener() {
        home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Halaman_ACC.this,GTS_Halaman_Utama.class);
                startActivity(intent);
            }
        });

        cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Halaman_ACC.this, GTS_Halaman_Cart.class);
                startActivity(intent);
            }
        });

        btnriwayat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Halaman_ACC.this,GTS_Riwayat_Pembelian.class);
                startActivity(intent);
            }
        });

        logoutku.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
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

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Logout");
        builder.setMessage("Apakah Anda yakin ingin logout?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutFirebase();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void logoutFirebase() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, GTS_Login_User.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}