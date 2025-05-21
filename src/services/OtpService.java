package services;

import utils.EmailUtil;
import dao.CustomerDAO;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OtpService {
    private static final int OTP_LENGTH = 6;
    private static final int EXPIRY_MINUTES = 5;
    private static final Map<String, Object[]> otpStorage = new HashMap<>();

    public static void generateAndSendOtp( String email) throws Exception {
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(rnd.nextInt(10));
        }
        String otp = sb.toString();

        // to get the expiry time
        Timestamp expiry = Timestamp.from(Instant.now().plusSeconds(EXPIRY_MINUTES * 60));

        // Store OTP in memory (something as cache or local storage)
        otpStorage.put(email, new Object[]{otp, expiry});

        // Send OTP via email
        EmailUtil.sendOtpEmail(email, otp);  
    }

    public static boolean verifyOtp(String email, String otp) throws Exception {
        Object[] otpData = otpStorage.get(email);
        if (otpData == null) {
            throw new Exception("No OTP found for user: " + email);
        }

        String storedOtp = (String) otpData[0];
        Timestamp expiry = (Timestamp) otpData[1];

        if (expiry.before(Timestamp.from(Instant.now()))) {
            otpStorage.remove(email);
            throw new Exception("OTP has expired");
        }

        if (!storedOtp.equals(otp)) {
            throw new Exception("Invalid OTP");
        }

        otpStorage.remove(email);
        CustomerDAO customerDAO = new CustomerDAO();
        if (!customerDAO.verifyUser(email)) {
            throw new Exception("Failed to verify user with OTP");
        }
        return true;
    }

    public static void resendOtp( String email) throws Exception {
        generateAndSendOtp(email);
    }
}
