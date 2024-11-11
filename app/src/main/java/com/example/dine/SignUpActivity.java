package com.example.dine;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.text.TextWatcher;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.util.Patterns;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, password, email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        email = findViewById(R.id.emailField);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupValidation(name, "Name Cannot Be Empty", s->!s.isEmpty());
        setupValidation(password, "Minimum 6 Characters", s-> s.length() >=6);
        setupValidation(email, "Invalid Email", s->Patterns.EMAIL_ADDRESS.matcher(s).matches());
    }

    private void setupValidation(EditText field, String errorMessage, ValidationRule rule){
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable leftDrawable = field.getCompoundDrawables()[0];
                if (rule.validate(s.toString())) {
                    field.setTextColor(Color.BLACK);
                    field.setCompoundDrawablesWithIntrinsicBounds(
                            leftDrawable,
                            null,
                            ContextCompat.getDrawable(SignUpActivity.this, R.drawable.check),
                            null);
                }else{
                    field.setTextColor(Color.RED);
                    field.setCompoundDrawablesWithIntrinsicBounds(
                            leftDrawable,
                            null,
                            ContextCompat.getDrawable(SignUpActivity.this, R.drawable.error),
                            null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private interface ValidationRule {
        boolean validate(String input);
    }

    public void signUp(View view) {
        if (validateAll()) {
            String email_sign = email.getText().toString().trim();
            String pass_sign = password.getText().toString().trim();

            mAuth.fetchSignInMethodsForEmail(email_sign)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                            if (isNewUser) {
                                registerUser(email_sign, pass_sign);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Email is already registered!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed to check email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String email_sign, String pass_sign) {
        mAuth.createUserWithEmailAndPassword(email_sign, pass_sign)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name.getText().toString().trim());
                            user.put("email", email_sign);

                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateAll(){
        return !name.getText().toString().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()
                && password.getText().toString().length() >= 6;
    }

    public void goBack(View view){
        finish();
    }
}