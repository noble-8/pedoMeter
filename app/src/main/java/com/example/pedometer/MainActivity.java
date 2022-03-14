package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    //static final variable
    private final double smoothFactor = 10;

    TextView op;
    Button reset;

    private  SensorManager sensorManager;
    private  Sensor accelerometer;
    private double lowPassAcceleration;

    private ArrayList<Double> filter = new ArrayList<Double>();


    private long lastUpdate = System.currentTimeMillis();
    private long lastCountUpdate = System.currentTimeMillis();
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        op = findViewById(R.id.textView2);
        reset = findViewById(R.id.resetButton);

        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                count=0;
                op.setText(count+"");
            }
        });
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        double currentAccelration = Math.sqrt(x*x + y*y + z*z);

//        lowPass(currentAccelration);
        lowPassAcceleration=lowPassLoop(currentAccelration);
        Log.d("batman",System.currentTimeMillis()-lastCountUpdate+"");

        if(lowPassAcceleration>10.05 && System.currentTimeMillis()-lastCountUpdate>600){
            count++;
            lowPassAcceleration = 0;
            lastCountUpdate = System.currentTimeMillis();
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
        long now = System.currentTimeMillis();
        long elapsedTime = now-lastUpdate;
        Log.d("elapsedTime", elapsedTime+"");
        Log.d("superman",lowPassAcceleration+"");
        Log.d("curr acceleration",currAcceleration+"");
        Log.d("mid step calculatoin",elapsedTime*((currAcceleration-lowPassAcceleration)/(smoothFactor))/1000+"");

        lowPassAcceleration = lowPassAcceleration + elapsedTime*((currAcceleration-lowPassAcceleration)/(smoothFactor))/1000;
        lastUpdate = now;
    }

    private double lowPassLoop(double currAcceleration){
        filter.add(currAcceleration);
        int size = filter.size();
        if(size>smoothFactor){
            double out = filter.remove(0);
            Log.d("removed this",out+"");
        }
        double sum = 0;
        for(double i:filter){
            sum = sum +i;
        }
        return sum/size;
    }

}