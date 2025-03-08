package com.example.cobafirebase.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private final Firestore db;
    private final EmailService emailService;

    public OtpService(Firestore db, EmailService emailService) {
        this.db = db;
        this.emailService = emailService;
    }

    //  Kirim OTP dan simpan ke Firestore
    public String sendOtp(String email) {
        String otp = emailService.generateOtp();

        Map<String, Object> otpData = new HashMap<>();
        otpData.put("otp", otp);
        otpData.put("email", email);
        otpData.put("expiry", System.currentTimeMillis() + (5 * 60 * 1000)); // Berlaku 5 menit

        try {
            ApiFuture<WriteResult> future = db.collection("otps").document(email).set(otpData);
            logger.info("OTP {} berhasil dikirim ke {} pada {}", otp, email, future.get().getUpdateTime());
            
            // Kirim email
            emailService.sendOtpEmail(email, otp);
            return "OTP telah dikirim ke " + email;
        } catch (Exception e) {
            logger.error("Gagal menyimpan OTP ke Firestore untuk email {}: {}", email, e.getMessage());
            return "Gagal mengirim OTP";
        }
    }

    //  Verifikasi OTP
    public boolean verifyOtp(String email, String userOtp) {
        try {
            DocumentSnapshot document = db.collection("otps").document(email).get().get();

            if (!document.exists()) {
                logger.warn("OTP untuk email {} tidak ditemukan", email);
                return false;
            }

            String storedOtp = document.getString("otp");
            Long expiryTime = document.getLong("expiry");

            if (storedOtp == null || expiryTime == null) {
                logger.error("Data OTP tidak valid untuk email {}", email);
                return false;
            }

            //  Validasi OTP
            if (storedOtp.equals(userOtp) && System.currentTimeMillis() <= expiryTime) {
                db.collection("otps").document(email).delete(); // Hapus OTP setelah digunakan
                logger.info("OTP {} untuk email {} berhasil diverifikasi", storedOtp, email);
                return true;
            } else {
                logger.warn("OTP tidak valid atau sudah kadaluarsa untuk email {}", email);
                return false;
            }

        } catch (Exception e) {
            logger.error("Gagal memverifikasi OTP untuk email {}: {}", email, e.getMessage());
            return false;
        }
    }
}
