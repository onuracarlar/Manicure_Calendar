package com.lanislaru.manicure_loop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserMainActivity extends AppCompatActivity {

    String currentPhotoPath; //create image file
    String fileName;
    String userEmail;

    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Button addManicureButton;
    Button myManicuresButton;
    Button myManicureLoopButton;
    Button logOutButton;
    Button infoButton;

    private static final int REQUEST_CAMERA_CODE=100;
    private static final int REQUEST_CAMERA_PERMISSION_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        userEmail= user.getEmail();

        addManicureButton=findViewById(R.id.addManicureButton);
        myManicuresButton=findViewById(R.id.myManicuresButton);
        myManicureLoopButton=findViewById(R.id.myManicureLoopButton);
        logOutButton=findViewById(R.id.logOut);

        setButtonDrawableAndText(addManicureButton);
        setButtonDrawableAndText(myManicuresButton);
        setButtonDrawableAndText(myManicureLoopButton);
        setButtonDrawableAndText(logOutButton);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Main Menu");

    }

    public void logOut(View view){
       setAlertDialog();
    }

    private void setAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Loggin Out");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            firebaseAuth.signOut();
            Intent intent=new Intent(UserMainActivity.this,MainActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        builder.setNegativeButton("No", (dialog, which) -> {

            // Do nothing
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setButtonDrawableAndText(Button button){
        button.setBackgroundResource(R.drawable.custom_button);
        button.setTextColor(Color.rgb(255,255,255));
    }

    public void uploadImageToFirebase(final Uri uri){
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        if(uri!=null){
           storageReference.child(userEmail).child("Manicures").listAll().addOnSuccessListener(listResult -> {

              fileName=new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(new Date());

               storageReference.child(userEmail).child("Manicures").child(fileName).putFile(uri)
                       .addOnSuccessListener(taskSnapshot -> Toast.makeText(UserMainActivity.this,"Image Saved.",Toast.LENGTH_LONG).show())
                       .addOnFailureListener(e -> Toast.makeText(UserMainActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show());
            }).addOnFailureListener(e -> Toast.makeText(UserMainActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show());
        }
    }

    public void addManicureButtonClicked(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            askCameraPermission();
        }
        else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CAMERA_PERMISSION_CODE&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
              dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA_CODE) {
            if (resultCode == RESULT_OK) {
                    File f =new File(currentPhotoPath);
                    uploadImageToFirebase(Uri.fromFile(f));
            }
        }
    }

    private void askCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(UserMainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION_CODE);

        }
        else{
            dispatchTakePictureIntent();
        }
    }

    private File createImageFile() throws IOException{
        //Create an image file name
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="JPEG_"+timeStamp+"_";
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath=image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.lanislaru.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
            }
        }
    }

    public void myManicuresButtonClicked(View view){
        Intent intent =new Intent(UserMainActivity.this,MyManicuresActivity.class);
        startActivity(intent);
    }

    public void myManicureLoopButtonClicked(View view){
        Intent intent=new Intent(UserMainActivity.this,MyManicureLoopActivity.class);
        startActivity(intent);
    }
}
