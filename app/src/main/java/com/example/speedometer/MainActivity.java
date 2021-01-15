package com.example.speedometer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.RelativeDateTimeFormatter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {
    SharedPreferences preferences;
    TTS MyTts;
    SQLiteDatabase db;
    private static final int REC_RESULT = 653;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyTts = new TTS(this);
        //Create or open database SpeedLogDB
        db = openOrCreateDatabase("SpeedLogDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS speedLog(long REAL, lat REAL, speed REAL, lim REAL, date DATE, time TIME)");

        //Getting permission to use location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
            return;
        }
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

        //Button go to speed limit activity
        Button speedL_bt = (Button) findViewById(R.id.speedL_bt);
        speedL_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Speed_Limits.class));
            }
        });

        //Button go to speed violations activity
        Button speedV_bt = (Button) findViewById(R.id.speedV_bt);
        speedV_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Speed_Violations.class));
            }
        });
    }

    //Change speed when both location is changed and switch is on
    @SuppressLint("NewApi")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        ConstraintLayout backgroundID = (ConstraintLayout) findViewById(R.id.backgroundID);
        TextView speed_text = findViewById(R.id.speed_txt);
        Switch Start_sw = (Switch) findViewById(R.id.Start_sw);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        float sP = preferences.getFloat("Speed", 0);
        if (location == null) {
                speed_text.setText("0,00 km/h");
        } else {
            if (Start_sw.isChecked())  {
                float nCurrentSpeed = location.getSpeed();
                double x = location.getLatitude();
                double y = location.getLongitude();
                speed_text.setText(String.format("%.2f", (nCurrentSpeed * 3.6)) + " km/h");
                if((nCurrentSpeed * 3.6) >= sP){ //if speed greater than speed limit then make background red and texttospeech mesage
                    backgroundID.setBackground(ContextCompat.getDrawable(this, R.color.red));
                    MyTts.speak("Έχετε ξεπεράσει το όριο ταχύτητας, παρακαλώ πολύ ελλατώστε!");
                    db.execSQL("INSERT INTO speedLog VALUES(" + x + "," + y + "," + nCurrentSpeed*3.6 + "," + sP + ",date('now', 'localtime'),time('now', 'localtime'))"); //Insert into database speed violations
                } else {
                    backgroundID.setBackground(ContextCompat.getDrawable(this, R.drawable.brick));
                }
            }
        }
    }

    //Button for speech recognition
    public void recognize(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
        startActivityForResult(intent,REC_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Switch Start_sw = (Switch) findViewById(R.id.Start_sw);
        if (requestCode == REC_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.contains("on")) { //switch on
                Start_sw.setChecked(true);
            } else if (matches.contains("off")) { //switch off
                Start_sw.setChecked(false);
            }
            if (matches.contains("close")) { //application closes
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}