package com.example.pulseoxim;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference dR_pulse;
    public DatabaseReference dR_spo;
    public DatabaseReference dR_temp;
    private TextView pulseValue;
    private TextView spoValue;
    private TextView tempValue;
    String status_pulse, status_spo, status_temp;
    //LineGraphSeries<DataPoint> series;
    public float pulse, spo2,temp, step;
    public double valuedata;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_clos);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pulseValue = (TextView) findViewById(R.id.mBeatRate);
        spoValue = (TextView) findViewById(R.id.mSPOVal);
        tempValue = (TextView) findViewById(R.id.mTemp);
        dR_pulse = FirebaseDatabase.getInstance().getReference();
        dR_pulse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status_pulse = dataSnapshot.child("MAX30100/Puls").getValue().toString();
                pulseValue.setText(status_pulse);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dR_spo = FirebaseDatabase.getInstance().getReference();
        dR_spo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                status_spo = dataSnapshot.child("MAX30100/Saturatia de Oxigen").getValue().toString();
                spoValue.setText(status_spo);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
        dR_temp = FirebaseDatabase.getInstance().getReference();
        dR_temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                status_temp = dataSnapshot.child("MAX30100/Temperatura").getValue().toString();
                tempValue.setText(status_temp);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

        FirebaseDatabase max30100 = FirebaseDatabase.getInstance();
        DatabaseReference max = max30100.getReference("MAX30100");
        ValueEventListener plot = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            pulse = Integer.parseInt(String.valueOf(dataSnapshot.child("Puls").getValue()));
            step = Integer.parseInt(String.valueOf(dataSnapshot.child("Step").getValue()));
            Log.i("xValue", String.valueOf(step));
            Log.i("yValue", String.valueOf(pulse));
                valuedata = Double.parseDouble(String.valueOf(dataSnapshot.child("Puls").getValue()));
                GraphView graph = (GraphView) findViewById(R.id.mGraph);

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(step, pulse)

                });

                graph.getViewport().setXAxisBoundsManual(false);
                graph.getViewport().setYAxisBoundsManual(false);
                graph.getViewport().setMinY(0);
                graph.getViewport().setMaxY(200);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(200);
                graph.getViewport().setScrollable(true);
                series.appendData(new DataPoint(step, pulse), true, 200);
                graph.addSeries(series);
                series.setColor(Color.rgb(0,80,100));
                series.setTitle("Curve 1");
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(5);
                series.setThickness(2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }

        };

        max.addValueEventListener(plot);
    }

    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_statistics:
                startActivity(new Intent(getApplicationContext(), com.example.pulseoxim.Profile.class));
                break;
            case R.id.changeProfile:
                startActivity(new Intent(getApplicationContext(), com.example.pulseoxim.EditProfile.class));
                break;
            case R.id.deconectare:
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}