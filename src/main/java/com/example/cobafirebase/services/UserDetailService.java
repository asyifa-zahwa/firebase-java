package com.example.cobafirebase.services;

import com.example.cobafirebase.dto.UserPrincipal;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutionException;

@Service
public class UserDetailService implements UserDetailsService {

    private final Firestore db;

    public UserDetailService(Firestore db) {
        this.db = db;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            DocumentSnapshot document = db.collection("users").document(email).get().get();

            if (!document.exists()) {
                throw new UsernameNotFoundException("User tidak ditemukan: " + email);
            }

            String password = document.getString("password"); // Ambil password dari Firestore
            return new UserPrincipal(email, password);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Gagal mengambil data user dari Firestore", e);
        }
    }
}
