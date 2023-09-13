package com.project.growthshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GTS_Halaman_Konfirmasi extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private TextView editTextKodePembayaran, editTextHargaTotal;
    private Spinner spinnerMetodePembayaran;
    private Button buttonPilihFoto, buttonSimpan;
    private String metodePembayaranTerpilih;
    private Bitmap bitmapInput;
    private Uri imageUri;
    private FirebaseAuth firebaseAuth;

    private String userID;

    private static final int REQUEST_IMAGE_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_konfirmasi);

        initializeViews();
        initializeFirebase();

        Intent intent = getIntent();
        String kodePembelian = intent.getStringExtra("kode_pembelian");
        String hargaTotal = intent.getStringExtra("harga_total");

        editTextKodePembayaran.setText(kodePembelian);
        editTextHargaTotal.setText(hargaTotal);

        setupMetodePembayaranSpinner();
        setButtonListeners();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView);
        editTextKodePembayaran = findViewById(R.id.editTextKodePembayaran);
        editTextHargaTotal = findViewById(R.id.editTextHargaTotal);
        spinnerMetodePembayaran = findViewById(R.id.spinnerMetodePembayaran);
        buttonPilihFoto = findViewById(R.id.buttonPilihFoto);
        buttonSimpan = findViewById(R.id.buttonSimpan);
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


    private void setupMetodePembayaranSpinner() {
        List<String> metodePembayaranList = getMetodePembayaranList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, metodePembayaranList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodePembayaran.setAdapter(adapter);
        spinnerMetodePembayaran.setOnItemSelectedListener(this);
    }

    private List<String> getMetodePembayaranList() {
        List<String> metodePembayaranList = new ArrayList<>();
        metodePembayaranList.add("BCA");
        metodePembayaranList.add("Mandiri");
        return metodePembayaranList;
    }

    private void setButtonListeners() {
        buttonPilihFoto.setOnClickListener(v -> pilihFotoDariGaleri());
        buttonSimpan.setOnClickListener(view -> {
            Intent intent = new Intent(GTS_Halaman_Konfirmasi.this, GTS_Riwayat_Pembelian.class);
            updateStatus();
            simpanDataKeFirebase();
            startActivity(intent);
        });
    }

    private void pilihFotoDariGaleri() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void simpanDataKeFirebase() {
        String kodePembayaran = editTextKodePembayaran.getText().toString().trim();
        String hargaTotal = editTextHargaTotal.getText().toString().trim();

        if (kodePembayaran.isEmpty() || hargaTotal.isEmpty() || imageUri == null) {
            // Menampilkan pesan error atau melakukan tindakan lain
            return;
        }

        String fileName = "foto_input.jpg";
        String folderName = "folder_gambar";
        StorageReference fileRef = storageReference.child(folderName).child(fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapInput.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String fotoUrl = uri.toString();

                // Memperbarui status pemesanan pada tabel penjualan
                DatabaseReference penjualanRef = databaseReference.child("penjualan");
                Query query = penjualanRef.orderByChild("userID").equalTo(userID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String penjualanKey = snapshot.getKey();
                            DatabaseReference penjualanDataRef = penjualanRef.child(penjualanKey);
                            penjualanDataRef.child("statusPemesanan").setValue("Sudah Dikonfirmasi");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Menampilkan pesan error atau melakukan tindakan lain
                    }
                });

                // Menyimpan data informasi ke Firebase Realtime Database
                DataModel dataModel = new DataModel(kodePembayaran, hargaTotal, metodePembayaranTerpilih, fotoUrl);
                DatabaseReference newDataRef = databaseReference.child("Konfirmasi-Pembelian").push();
                newDataRef.setValue(dataModel);

                resetForm();
                // Menampilkan pesan sukses atau melakukan tindakan lain
            });
        }).addOnFailureListener(e -> {
            // Menampilkan pesan gagal atau melakukan tindakan lain
        });
    }




    private void updateStatus() {
        String kodePembayaran = editTextKodePembayaran.getText().toString().trim();

        DatabaseReference penjualanRef = databaseReference.child("penjualan");
        penjualanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot barangSnapshot : userSnapshot.getChildren()) {
                        String kodePemesanan = barangSnapshot.child("kodePemesanan").getValue(String.class);
                        if (kodePemesanan != null && kodePemesanan.equals(kodePembayaran)) {
                            barangSnapshot.getRef().child("statusPemesanan").setValue("Sudah Bayar")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Status updated successfully
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to update status
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to query database
            }
        });
    }


    private void resetForm() {
        imageView.setImageResource(R.drawable.gts_logo);
        editTextKodePembayaran.setText("");
        editTextHargaTotal.setText("");
        spinnerMetodePembayaran.setSelection(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                bitmapInput = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        metodePembayaranTerpilih = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Tidak ada tindakan yang diambil saat tidak ada yang dipilih pada Spinner
    }

    private static class DataModel {
        private String kodePembayaran;
        private String hargaTotal;
        private String metodePembayaran;
        private String fotoUrl;

        public DataModel(String kodePembayaran, String hargaTotal, String metodePembayaran, String fotoUrl) {
            this.kodePembayaran = kodePembayaran;
            this.hargaTotal = hargaTotal;
            this.metodePembayaran = metodePembayaran;
            this.fotoUrl = fotoUrl;
        }

        public String getKodePembayaran() {
            return kodePembayaran;
        }

        public void setKodePembayaran(String kodePembayaran) {
            this.kodePembayaran = kodePembayaran;
        }

        public String getHargaTotal() {
            return hargaTotal;
        }

        public void setHargaTotal(String hargaTotal) {
            this.hargaTotal = hargaTotal;
        }

        public String getMetodePembayaran() {
            return metodePembayaran;
        }

        public void setMetodePembayaran(String metodePembayaran) {
            this.metodePembayaran = metodePembayaran;
        }

        public String getFotoUrl() {
            return fotoUrl;
        }

        public void setFotoUrl(String fotoUrl) {
            this.fotoUrl = fotoUrl;
        }
    }
}
