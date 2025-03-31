package com.example.cobafirebase.services;


import com.example.cobafirebase.dto.UserRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);
    private final Firestore db;

    public FirebaseService(Firestore db) {
        this.db = db;
    }

    // Tambah Data ke Firestore
    public String registerUser(UserRequest userRequest) {
        //cek email sudah ada atau belum
        try {
            Query query = db.collection("users").whereEqualTo("email", userRequest.getEmail());
            QuerySnapshot querySnapshot = query.get().get();
            if (!querySnapshot.isEmpty()) {
                logger.warn("Email {} sudah terdaftar", userRequest.getEmail());
                return "Email sudah terdaftar";
            }
        } catch (Exception e) {
            logger.error("Gagal mengecek email", e);
            return "Gagal mengecek email";
        }
        //cek username sudah ada atau belum
        try {
            Query query = db.collection("users").whereEqualTo("userName", userRequest.getUserName());
            QuerySnapshot querySnapshot = query.get().get();
            if (!querySnapshot.isEmpty()) {
                logger.warn("Username {} sudah terdaftar", userRequest.getUserName());
                return "Username sudah terdaftar";
            }
        } catch (Exception e) {
            logger.error("Gagal mengecek username", e);
            return "Gagal mengecek username";
        }
        // Ambil ID terbesar dari nama dokumen Firestore
        long newUserId = getNextUserId();
        try {
            
            Map<String, Object> data = Map.of(
                "id", newUserId,
                "userName", userRequest.getUserName(),
                "email", userRequest.getEmail(),
                "password", userRequest.getPassword()
            );

            ApiFuture<WriteResult> future = db.collection("users").document(String.valueOf(newUserId)).set(data);
            return "Data berhasil disimpan dengan userId " + newUserId + " pada " + future.get().getUpdateTime();
        } catch (Exception e) {
            logger.error("Gagal menyimpan data ke Firestore", e);
            return "Gagal menyimpan data";
        }
    }

    // Ambil Data dari Firestore
    public Map<String, Object> getUser(String userId) {
        try {
            DocumentReference docRef = db.collection("users").document(userId);
            DocumentSnapshot document = docRef.get().get();
            
            if (document.exists()) {
                return document.getData();
            } else {
                logger.warn("Data dengan ID {} tidak ditemukan", userId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Gagal mengambil data dari Firestore", e);
            return null;
        }
    }

    // Hapus Data dari Firestore
    public String deleteUser(String userId) {
        try {
            ApiFuture<WriteResult> future = db.collection("users").document(userId).delete();
            return "Data berhasil dihapus pada " + future.get().getUpdateTime();
        } catch (Exception e) {
            logger.error("Gagal menghapus data dari Firestore", e);
            return "Gagal menghapus data";
        }
    }
    //mengambil userId terbesar dan selanjutnya
    private long getNextUserId() {
        try {
            CollectionReference usersCollection = db.collection("users");
            ApiFuture<QuerySnapshot> future = usersCollection.get();
            QuerySnapshot snapshot = future.get();

            long maxId = 0;
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                try {
                    long docId = Long.parseLong(document.getId());
                    if (docId > maxId) {
                        maxId = docId;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Dokumen dengan ID {} bukan angka, dilewati", document.getId());
                }
            }
            return maxId + 1;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Gagal mengambil userId terbesar", e);
            return 1; // Jika gagal, mulai dari 1
        }
    }
    public String authenticateUser(String usernameOrEmail, String password) {
        try {
            Query query = db.collection("users")
                .whereEqualTo("email", usernameOrEmail)
                .whereEqualTo("password", password);
    
            QuerySnapshot querySnapshot = query.get().get();
    
            if (!querySnapshot.isEmpty()) {
                return "Login berhasil";
            } else {
                return "Email atau password salah";
            }
        } catch (Exception e) {
            logger.error("Gagal melakukan autentikasi", e);
            return "Gagal melakukan autentikasi";
        }
    }
}
