package com.lanislaru.manicure_loop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button sendEmailButton;
    EditText recoverEmailEditText;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendEmailButton=findViewById(R.id.sendEmailButton);
        setButtonDrawableAndText(sendEmailButton);

        recoverEmailEditText=findViewById(R.id.recoverEmailEditText);

        firebaseAuth=FirebaseAuth.getInstance();

    }
    private void setButtonDrawableAndText(Button button){
        button.setBackgroundResource(R.drawable.custom_button);
        button.setTextColor(Color.rgb(255,255,255));
    }
    public void passwordResetButtonClicked(View view){
        String userEmail=recoverEmailEditText.getText().toString();
        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(ForgotPasswordActivity.this,"Please type your E-Mail.",Toast.LENGTH_LONG).show();
        }
        else{
            firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ForgotPasswordActivity.this, "A reset E-Mail has been sent to your E-Mail address. Please check your E-Mail.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        String message=task.getException().getMessage();
                        Toast.makeText(ForgotPasswordActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
