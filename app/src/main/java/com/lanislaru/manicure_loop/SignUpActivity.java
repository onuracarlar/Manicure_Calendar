package com.lanislaru.manicure_loop;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailEditText,passwordEditText,password2EditText;
    String emailSignUpText,passwordSignUpText,password2SignUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth=FirebaseAuth.getInstance();

        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        password2EditText=findViewById(R.id.password2EditText);

        Button signUpButton = findViewById(R.id.signUpButton);

        setButtonDrawableAndText(signUpButton);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Sign Up");

    }
    private void setButtonDrawableAndText(Button button){
        button.setBackgroundResource(R.drawable.custom_button);
        button.setTextColor(Color.rgb(255,255,255));
    }

    public void signUpClicked(View view){
        emailSignUpText=emailEditText.getText().toString();
        password2SignUpText=password2EditText.getText().toString();
        passwordSignUpText=passwordEditText.getText().toString();

        try {
            if (passwordSignUpText.equals(password2SignUpText)){
                firebaseAuth.createUserWithEmailAndPassword(emailSignUpText,passwordSignUpText).addOnSuccessListener(authResult -> {
                    Toast.makeText(SignUpActivity.this,"User Created",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);

                }).addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show());


            }
            else{
                Toast.makeText(SignUpActivity.this,"Passwords must be the same",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(SignUpActivity.this,"Please fill E-Mail and Password properly.",Toast.LENGTH_LONG).show();
        }

    }
}
