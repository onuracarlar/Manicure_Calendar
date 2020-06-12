package com.lanislaru.manicure_loop;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class MyManicuresActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userEmail;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private LinearLayout pnl;
    private ScrollView scrollView;
    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_manicures);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        tf = getResources().getFont(R.font.coming_soon);
        init();
        setContentView(scrollView);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("My Manicures");
    }
    private void setButtonDrawableAndText(Button button){
        button.setBackgroundResource(R.drawable.custom_button);
        button.setTextColor(Color.rgb(255,255,255));
    }

    private void init(){
        scrollView=new ScrollView(this);
        scrollView.setBackgroundResource(R.drawable.pinkbackground);

        pnl = new LinearLayout(this);
        pnl.setOrientation(LinearLayout.VERTICAL);

        storageReference.child(userEmail).child("Manicures").listAll()
                .addOnSuccessListener(listResult -> {
                    int index=0;
                    for (final StorageReference item : listResult.getItems()) {
                        setLayout(index,item);
                        index++;
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MyManicuresActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show());
        scrollView.addView(pnl);
    }

    private void showHideSelectedPicture(StorageReference item, ImageView img) {
        item.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(img);
        });
        if(img.getVisibility() == View.VISIBLE){
            img.setVisibility(View.GONE);
        }
        else {
            img.setVisibility(View.VISIBLE);
        }

    }
    private void showHideDeleteButtons(Button button){
        if (button.getVisibility()==View.VISIBLE){
            button.setVisibility(View.GONE);
        }
        else {
            button.setVisibility(View.VISIBLE);
        }
    }
    private void deleteSelectedManicure(StorageReference item){
        item.delete().addOnSuccessListener(aVoid -> Toast.makeText(MyManicuresActivity.this,"File Deleted",Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(MyManicuresActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show());

    }

    private Button createAndConfigureDeleteButton(int index, StorageReference item) {
        LinearLayout.LayoutParams btnDeleteLp = new LinearLayout.LayoutParams(90, 90);
        btnDeleteLp.setMargins(30,30,30,30);
        btnDeleteLp.gravity=Gravity.CENTER;

        Button btnDelete=new Button(this);
        btnDelete.setLayoutParams(btnDeleteLp);
        btnDelete.setVisibility(View.GONE);
        btnDelete.setTextSize(50);
        btnDelete.setOnClickListener(v -> setAlertDialog(index,item));
        btnDelete.setBackgroundResource(R.drawable.delete_button_image);

        return btnDelete;
    }

    private ImageView createAndConfigureImage(StorageReference item) {
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgLp.setMargins(30,30,30,30);
        imgLp.gravity=Gravity.CENTER;

        ImageView img= new ImageView(this);
        img.setLayoutParams(imgLp);
        img.setVisibility(View.GONE);
        item.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(img);
        });
        return img;
    }

    private Button createAndConfigureManicureButton(int index, StorageReference item, Button deleteButton, ImageView image) {
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(30,30,30,30);
        btnLp.gravity=Gravity.CENTER;

        Button btnManicure =new Button(this);
        btnManicure.setLayoutParams(btnLp);
        btnManicure.setText((index+1)+". Manicure - "+item.getName().replaceAll("_"," "));
        btnManicure.setTextSize(20);

        btnManicure.setTypeface(tf);
        btnManicure.setTypeface(tf,Typeface.BOLD);

        btnManicure.setOnClickListener(v -> {
            showHideDeleteButtons(deleteButton);
            showHideSelectedPicture(item, image);
        });
        setButtonDrawableAndText(btnManicure);

        return btnManicure;
    }

    private void setLayout(int index,StorageReference item){
        ImageView image = createAndConfigureImage(item);
        Button deleteButton = createAndConfigureDeleteButton(index, item);
        Button manicureButton = createAndConfigureManicureButton(index, item, deleteButton, image);

        pnl.addView(manicureButton);
        pnl.addView(image);
        pnl.addView(deleteButton);
    }

    private void setAlertDialog(int index, StorageReference item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Deleting Item");
        builder.setMessage("Are you sure you want to delete your "+ (index + 1) +". Manicure?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Do nothing but close the dialog
                deleteSelectedManicure(item);
            Intent toUserMainActivity=new Intent(MyManicuresActivity.this,UserMainActivity.class);
            startActivity(toUserMainActivity);
            dialog.dismiss();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Do nothing
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
