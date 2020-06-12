package com.lanislaru.manicure_loop;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.internal.InternalTokenProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailEditText,passwordEditText;
    private TextView forgotPasswordTextView;
    Button signInButton;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            if (!isNetworkAvailable()){
                firebaseAuth.signOut();
            }
            else{
                Intent intent=new Intent(MainActivity.this,UserMainActivity.class);
                startActivity(intent);
            }

        }

        emailEditText=findViewById(R.id.emailText);
        passwordEditText=findViewById(R.id.passwordText);
        forgotPasswordTextView=findViewById(R.id.forgotPasswordTextView);

        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);

        setButtonDrawableAndText(signInButton);
        setButtonDrawableAndText(signUpButton);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Manicure Loop");
    }

    private void setButtonDrawableAndText(Button button){
        button.setBackgroundResource(R.drawable.custom_button);
        button.setTextColor(Color.rgb(255,255,255));
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onSignInClicked(View view){
        String emailText = emailEditText.getText().toString();
        String passwordText = passwordEditText.getText().toString();

        try {
            firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent =new Intent(MainActivity.this,UserMainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,"Please fill E-Mail and Password properly.",Toast.LENGTH_LONG).show();
        }
    }
    public void onSignUpClicked(View view){
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
    public void forgotPasswordTextViewClicked(View view){
        Intent intent = new Intent(MainActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
