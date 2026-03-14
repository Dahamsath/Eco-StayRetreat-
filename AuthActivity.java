package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecostayretreat.database.AppDatabase;
import com.example.ecostayretreat.database.UserEntity;
import com.example.ecostayretreat.database.UserDao;

public class AuthActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText;
    private Button loginButton, registerButton;
    private boolean isLoginMode = true;
    private AppDatabase database;
    private UserDao userDao;
    

    private static final String PREF_NAME = "EcoStaySession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }
        
        setContentView(R.layout.activity_auth);


        database = AppDatabase.getDatabase(this);
        userDao = database.userDao();

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        nameEditText = findViewById(R.id.editTextName);
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);


        updateModeUI();


        registerButton.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateModeUI();
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString().trim() : "";
            String name = nameEditText.getText().toString().trim();

            if (isLoginMode) {

                if (validateInput(email, password, "", null)) {
                    new Thread(() -> {
                        UserEntity user = userDao.getUserByEmail(email);
                        boolean isValidLogin = (user != null && verifyPassword(password, user.getPassword()));
                        runOnUiThread(() -> {
                            if (isValidLogin) {

                                saveUserSession(user.getUserId(), user.getEmail());
                                Toast.makeText(AuthActivity.this, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
                                navigateToMainActivity();
                            } else {
                                Toast.makeText(AuthActivity.this, R.string.msg_invalid_input, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                } else {

                }
            } else {

                if (validateInput(email, password, confirmPassword, name)) {
                    new Thread(() -> {
                        UserEntity existingUser = userDao.getUserByEmail(email);
                        if (existingUser == null) {
                            UserEntity newUser = new UserEntity(email, name, hashPassword(password));
                            userDao.insertUser(newUser);
                            runOnUiThread(() -> {
                                Toast.makeText(AuthActivity.this, R.string.msg_registration_success, Toast.LENGTH_LONG).show();

                                isLoginMode = true;
                                updateModeUI();

                                emailEditText.setText("");
                                passwordEditText.setText("");
                                if (confirmPasswordEditText != null) confirmPasswordEditText.setText("");
                                nameEditText.setText("");
                            });
                        } else {
                            runOnUiThread(() -> {
                                emailEditText.setError(getString(R.string.error_email_already_registered));
                            });
                        }
                    }).start();
                } else {

                }
            }
        });
    }

    private boolean validateInput(String email, String password, String confirmPassword, String name) {
        boolean isValid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }
        if (password.length() < 6) {
            passwordEditText.setError(getString(R.string.error_password_short));
            isValid = false;
        }
        if (!isLoginMode && (name == null || name.trim().isEmpty())) {
            nameEditText.setError(getString(R.string.error_name_required));
            isValid = false;
        }
        if (!isLoginMode) {
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.setError(getString(R.string.error_confirm_password_required));
                isValid = false;
            } else if (!password.equals(confirmPassword)) {
                confirmPasswordEditText.setError(getString(R.string.error_passwords_do_not_match));
                isValid = false;
            }
        }
        return isValid;
    }

    private void updateModeUI() {
        if (isLoginMode) {
            loginButton.setText(R.string.btn_login);
            nameEditText.setVisibility(View.GONE);
            confirmPasswordEditText.setVisibility(View.GONE);
            registerButton.setText(R.string.btn_switch_to_register);
        } else {
            loginButton.setText(R.string.btn_register);
            nameEditText.setVisibility(View.VISIBLE);
            confirmPasswordEditText.setVisibility(View.VISIBLE);
            registerButton.setText(R.string.btn_switch_to_login);
        }
    }
    

    private boolean isUserLoggedIn() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    private void saveUserSession(int userId, String email) {
        android.content.SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    public static void clearUserSession(android.content.Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    
    public static int getCurrentUserId(android.content.Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, -1);
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
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
            return password; 
        }
    }
    
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}
