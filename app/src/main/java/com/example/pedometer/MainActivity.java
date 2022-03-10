package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    //static final variable
    private final int smoothFactor = 10;

    TextView op;

    private  SensorManager sensorManager;
    private  Sensor accelerometer;
    private double lowPassAcceleration;


    private Date lastUpdate = new Date();
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        op = findViewById(R.id.textView2);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        double currentAccelration = Math.sqrt(x*x + y*y + z*z);

        lowPass(currentAccelration);
        if(lowPassAcceleration>10){
            count++;
            lowPassAcceleration = 0;
        }
        op.setText(count+"");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void lowPass(double currAcceleration){
        Date now = new Date();
        int elapsedTime = new Date().compareTo(lastUpdate);
        lowPassAcceleration = lowPassAcceleration + elapsedTime*(currAcceleration-lowPassAcceleration)/smoothFactor;
        lastUpdate = now;
    }

}