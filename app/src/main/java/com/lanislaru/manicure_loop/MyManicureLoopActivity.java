package com.lanislaru.manicure_loop;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyManicureLoopActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "notificationID";
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userEmail;

    long myFirstManicureDateValue;
    long nextManicureDateMillis;

    TextView myLastManicureDate;
    String myLastManicureDateUntrim;
    TextView nextManicureDate;
    String nextManicureDateFormat="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_manicure_loop);

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        userEmail=user.getEmail();

        myLastManicureDate=findViewById(R.id.myLastManicureTextView);
        nextManicureDate=findViewById(R.id.nextDateTextView);

        createNotificationChannel();
        calculateLoop();

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("My Manicure Loop");

    }
    private void setAlarmNotification(long time){
        Intent intent=new Intent(MyManicureLoopActivity.this,ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyManicureLoopActivity.this,0,intent,0);
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
               time,pendingIntent);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void calculateLoop(){
        storageReference.child(userEmail).child("Manicures").listAll()
                .addOnSuccessListener(listResult -> {
                    int index=0;
                    int listSize=listResult.getItems().size();
                    for (final StorageReference item : listResult.getItems()) {

                        if (index==0){
                            myFirstManicureDateValue=convertDateToMilliseconds(item.getName());
                        }

                        if(index==listSize-1){
                            myLastManicureDateUntrim=item.getName();
                            myLastManicureDate.setText(myLastManicureDateUntrim.replaceAll("_"," "));
                        }
                        index++;
                    }
                    if (listSize>2){
                        nextManicureDateMillis =convertDateToMilliseconds(myLastManicureDateUntrim)+(convertDateToMilliseconds(myLastManicureDateUntrim) - myFirstManicureDateValue) / (listSize-1);
                        nextManicureDateFormat=getDate(nextManicureDateMillis,"dd.MM.yyyy");

                        if(System.currentTimeMillis()-nextManicureDateMillis<0){
                            nextManicureDate.setText("Your next manicure should be approximately on: "+nextManicureDateFormat);
                            setAlarmNotification(nextManicureDateMillis); //ALARM
                        }
                        else{
                            nextManicureDate.setText("You may have missed your manicure, you should have it approximately on: "+nextManicureDateFormat);
                        }
                    }
                    else{
                       if (myLastManicureDateUntrim!=null&&listSize>1){
                           nextManicureDateMillis =convertDateToMilliseconds(myLastManicureDateUntrim)+(convertDateToMilliseconds(myLastManicureDateUntrim) - myFirstManicureDateValue) / (listSize-1);
                           nextManicureDateFormat=getDate(nextManicureDateMillis,"dd.MM.yyyy");
                       }
                       else if(myLastManicureDateUntrim==null){
                           myLastManicureDate.setText("You have no manicure on record. Please add a manicure first.");
                       }
                       nextManicureDate.setText("You need to have at least three manicures on record.");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MyManicureLoopActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show());
    }

    public long convertDateToMilliseconds(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try
        {
            Date mDate = sdf.parse(date.substring(0,10));
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return 0;
    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

