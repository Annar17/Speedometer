package com.example.speedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class Speed_Violations extends AppCompatActivity {
    ListView listView;
    SharedPreferences preferences;
    SQLiteDatabase db;
    ListAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed__violations);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;
        db = openOrCreateDatabase("SpeedLogDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS speedLog(long REAL, lat REAL, speed REAL, lim REAL, date DATE, time TIME)");

        listView = (ListView) findViewById(R.id.listView);

        //Total Violations button
        Button totalV_p = (Button) findViewById(R.id.totalV_p);
        totalV_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "select * from speedLog where speed >= " + preferences.getFloat("Speed", 0);
                runQuery(query);
            }
        });

        //Last 7 Days Violations button
        Button L7D_p = (Button) findViewById(R.id.L7D_p);
        L7D_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "select * from speedLog where date between date('now','-7 days') and date('now') and speed >=" + preferences.getFloat("Speed",0);
                runQuery(query);
            }
        });

        //Button that goes to MainActivity
        Button back_bt2 = (Button) findViewById(R.id.back_bt2);
        back_bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Speed_Violations.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    //Execute query and print data
    public void runQuery(String query) {
        listView = (ListView) findViewById(R.id.listView);
        ArrayList<String> arrayList = new ArrayList<>();
        db = openOrCreateDatabase("SpeedLogDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS speedLog(long REAL, lat REAL, speed REAL, lim REAL, date DATE, time TIME)");

        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.getCount() > 0) {
            StringBuilder builder = new StringBuilder();
            while (cursor.moveToNext()) {
                builder.append("Location Stamp: ").append(cursor.getString(0)).append(", ").
                        append(cursor.getString(1)).
                        append("\nSpeed: ").append(cursor.getString(2)).
                        append("\nSpeed Limit: ").append(cursor.getString(3)).
                        append("\nDate-Time: ").append(cursor.getString(4)).append(", ").append(cursor.getString(4)).
                        append("\n\n");
                arrayList.add(builder.toString());
            }
        } else {
            arrayList.add("No records available");
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }
}
