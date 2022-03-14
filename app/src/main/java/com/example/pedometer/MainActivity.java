package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    //tuned parameters set after experimentation
    private final double smoothFactor = 20;
    private final double THRESHOLD = 0.2;
    private final double GRAVITY = 9.8;
    private final int TIME_BETWEEN_STEPS_MILLISECONDS = 400;

    TextView op;
    Button reset;

    //sensor initialization
    private  SensorManager sensorManager;
    private  Sensor accelerometer;

    private ArrayList<Double> filter = new ArrayList<Double>();


    private long lastUpdate = System.currentTimeMillis();
    private long lastCountUpdate = System.currentTimeMillis();
    int numberOfSteps = 0;

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
                numberOfSteps =0;
                op.setText(numberOfSteps +"");
            }
        });
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //get the sensor data
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        //get the absolute magnitude of the force on the device
        double currentAccelration = Math.sqrt(x*x + y*y + z*z);

        //low pass filter to remove the high frequency noise
        double lowPassAcceleration = lowPassLoop(currentAccelration);
        /*
        * CHECK IF THE ACCELERATION/FORCE ON THE DEVICE IS GREATER THAN THE THRESHOLD SET
        * ITS NOT POSSIBLE TO TAKE A STEP IN LESS THAN THE THRESHOLD SET IGNORING OTHER JERKS DURING THIS PERIOD, THE TIME THRESHOLD CAN BE LOWERED FOR A RUNNING MODE.
        * */
        if(lowPassAcceleration > GRAVITY + THRESHOLD && System.currentTimeMillis()-lastCountUpdate>TIME_BETWEEN_STEPS_MILLISECONDS){
            numberOfSteps++;
            lowPassAcceleration = 0;
            lastCountUpdate = System.currentTimeMillis();
        }
        //SET THE VALUE IN THE TEXT BOX
        op.setText(numberOfSteps +"");

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


    private double lowPassLoop(double currAcceleration){
        filter.add(currAcceleration);
        int size = filter.size();
        if(size>=smoothFactor){
            filter.remove(0);
        }
        double sum = 0;
        for(double i:filter){
            sum = sum +i;
        }
        return sum/size;
    }

}