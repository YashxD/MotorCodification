package io.github.yashxd.motorcodification;

import android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner rating, rpm, mounting, frame;
    Button submit;
    TextView serial;
    String ratingValue, rpmValue, mountingValue, frameValue, serialValue;
    Resources res;
    String[] rpmArray, mountingArray;
    int ratingIndex;
    long[] serialInt = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
    String[] ratingChar = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"};

    DatabaseReference mDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener serialListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i<15; i++) {
                    serialInt[i] = (long) dataSnapshot.child(""+i).child("serialValue").getValue();
                    Log.e("LOL", "serialInt["+i+"] = "+serialInt[i]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error while retreiving from database", Toast.LENGTH_LONG).show();
            }
        };
        mDatabase.addValueEventListener(serialListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rating = findViewById(R.id.spinner_rating);
        rpm = findViewById(R.id.spinner_rpm);
        mounting = findViewById(R.id.spinner_mounting);
        frame = findViewById(R.id.spinner_frame);
        submit = findViewById(R.id.button_submit);
        serial = findViewById(R.id.textview_serial);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        res = getResources();
        rpmArray = res.getStringArray(R.array.rpm_array);
        mountingArray = res.getStringArray(R.array.mounting_array);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingValue = String.valueOf(rating.getSelectedItem());
                rpmValue = String.valueOf(rpm.getSelectedItem());
                mountingValue = String.valueOf(mounting.getSelectedItem());
                frameValue = String.valueOf(frame.getSelectedItem());
                ratingIndex = rating.getSelectedItemPosition();

                if(rpmValue.equals(rpmArray[0]))
                    rpmValue = "A";
                else if(rpmValue.equals(rpmArray[1]))
                    rpmValue = "B";
                else if(rpmValue.equals(rpmArray[2]))
                    rpmValue = "C";
                else if(rpmValue.equals(rpmArray[3]))
                    rpmValue = "D";

                if(mountingValue.equals(mountingArray[0]))
                    mountingValue = "FT";
                if(mountingValue.equals(mountingArray[1]))
                    mountingValue = "FF";
                if(mountingValue.equals(mountingArray[2]))
                    mountingValue = "FL";

                serialValue = ratingValue + rpmValue + mountingValue + frameValue + ratingChar[ratingIndex] + String.format("%03d", serialInt[ratingIndex]);
                serial.setText(serialValue);

                serialInt[ratingIndex]++;
                //Set new Serial number
                mDatabase.child(""+ratingIndex).child("serialValue").setValue(serialInt[ratingIndex]);
            }
        });
    }
}
