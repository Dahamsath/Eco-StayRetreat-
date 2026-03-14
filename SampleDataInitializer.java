package com.example.ecostayretreat;

import android.content.Context;
import android.widget.Toast;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.UserDao;
import com.example.ecostayretreat.database.UserEntity;
import java.security.MessageDigest;

/**
 * Utility class to initialize sample data for the EcoStay Retreat app
 */
public class SampleDataInitializer {
    
    private AppDatabase database;
    private UserDao userDao;
    
    public SampleDataInitializer(Context context) {
        database = AppDatabase.getDatabase(context);
        userDao = database.userDao();
    }
    
    /**
     * Creates sample users with different eco-consciousness levels and preferences
     */
    public void createSampleUsers() {
        new Thread(() -> {
            try {
                // Check if users already exist
                if (userDao.getAllUsers().isEmpty()) {
                    // Create diverse sample users for testing
                    
                    // Eco-conscious user
                    UserEntity ecoUser = new UserEntity("eco@example.com", "Alex Green", hashPassword("password123"));
                    ecoUser.setPreferences("eco-friendly, sustainable, mountain views");
                    ecoUser.setTravelDates("2024-11-15 to 2024-11-22");
                    ecoUser.setPreferredRoomType("Eco-Pod");
                    ecoUser.setSustainabilityLevel(5);
                    ecoUser.setEcoActivityPreferences("hiking, bird watching, workshops");
                    ecoUser.setNotificationPreferences("eco-events, discounts, workshops");
                    
                    // Nature lover
                    UserEntity natureUser = new UserEntity("nature@example.com", "Sam Rivers", hashPassword("password123"));
                    natureUser.setPreferences("nature, hiking, outdoor activities");
                    natureUser.setTravelDates("2024-12-01 to 2024-12-07");
                    natureUser.setPreferredRoomType("Mountain-View");
                    natureUser.setSustainabilityLevel(4);
                    natureUser.setEcoActivityPreferences("hiking, foraging, stargazing");
                    natureUser.setNotificationPreferences("outdoor activities, seasonal events");
                    
                    // Budget-conscious traveler
                    UserEntity budgetUser = new UserEntity("budget@example.com", "Jordan Smith", hashPassword("password123"));
                    budgetUser.setPreferences("budget-friendly, comfortable, educational");
                    budgetUser.setTravelDates("2024-11-30 to 2024-12-03");
                    budgetUser.setPreferredRoomType("Standard");
                    budgetUser.setSustainabilityLevel(3);
                    budgetUser.setEcoActivityPreferences("workshops, tours");
                    budgetUser.setNotificationPreferences("discounts, special offers");
                    
                    // Luxury eco-traveler
                    UserEntity luxuryUser = new UserEntity("luxury@example.com", "Morgan Taylor", hashPassword("password123"));
                    luxuryUser.setPreferences("luxury, comfort, eco-conscious, spa");
                    luxuryUser.setTravelDates("2024-12-20 to 2024-12-27");
                    luxuryUser.setPreferredRoomType("Luxury");
                    luxuryUser.setSustainabilityLevel(4);
                    luxuryUser.setEcoActivityPreferences("tours, workshops, conservation");
                    luxuryUser.setNotificationPreferences("exclusive events, premium offerings");
                    
                    // Conservation enthusiast
                    UserEntity conservationUser = new UserEntity("conservation@example.com", "Riley Chen", hashPassword("password123"));
                    conservationUser.setPreferences("conservation, citizen science, education");
                    conservationUser.setTravelDates("2024-11-25 to 2024-12-02");
                    conservationUser.setPreferredRoomType("Eco-Pod");
                    conservationUser.setSustainabilityLevel(5);
                    conservationUser.setEcoActivityPreferences("conservation, wildlife monitoring, restoration");
                    conservationUser.setNotificationPreferences("conservation projects, educational programs");
                    
                    // Insert users
                    userDao.insertUser(ecoUser);
                    userDao.insertUser(natureUser);
                    userDao.insertUser(budgetUser);
                    userDao.insertUser(luxuryUser);
                    userDao.insertUser(conservationUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Simple password hashing method (same as AuthActivity)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password; // Fallback to plain password if hashing fails
        }
    }
    
    /**
     * Initialize all sample data (call this on app first run)
     */
    public static void initializeAllSampleData(Context context) {
        SampleDataInitializer initializer = new SampleDataInitializer(context);
        initializer.createSampleUsers();
    }
}