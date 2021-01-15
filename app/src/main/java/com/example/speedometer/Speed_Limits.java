package com.example.speedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Speed_Limits extends AppCompatActivity {
    private float speed;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed__limits);

        EditText editSpeed = (EditText) findViewById(R.id.editSpeed);

        //Button that goes to MainActivity
        Button back_bt = (Button) findViewById(R.id.back_bt);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Speed_Limits.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        float sl = preferences.getFloat("Speed", 0);
        editSpeed.setText("" + sl, TextView.BufferType.EDITABLE);

        Button edit_bt = (Button) findViewById(R.id.edit_bt);
        edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                updateText();
            }
        });
    }

    //Saves speed limit to shared preferences
    public void saveData() {
        EditText editSpeed = (EditText) findViewById(R.id.editSpeed);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("Speed", Float.parseFloat(editSpeed.getText().toString()));
        editor.apply();
        Toast.makeText(this, "Data saved!", Toast.LENGTH_LONG).show();
    }

    //Update EditText with current speed limit
    public void updateText() {
        EditText editSpeed = (EditText) findViewById(R.id.editSpeed);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speed = preferences.getFloat("Speed", 0);
        editSpeed.setText("" + speed, TextView.BufferType.EDITABLE);
    }
}
