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

public class SignUpActivity extends AppCompatActivity {

    private EditText name, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        email = findViewById(R.id.emailField);

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

    public void signUp(View view){
        if (validateAll()){
            Toast.makeText(this, "SignUp Successful", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "Please fill all the fields correctly", Toast.LENGTH_SHORT).show();
        }
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