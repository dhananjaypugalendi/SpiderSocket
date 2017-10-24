package com.dhananjay.spidersocket;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float yValue;

    private Socket socket;

    private TextView accelData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        initAccelerometer();

        initSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        socket.disconnect();
    }

    private void initViews() {
        accelData = (TextView) findViewById(R.id.accel);
    }

    private void initAccelerometer() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initSocket() {
        try {
            socket = IO.socket("http://0.0.0.0:8080");
            socket.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "initSocket: ", e);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        yValue = event.values[1]; // [x, y, z] are available values
        accelData.setText(String.valueOf(yValue));
        //Log.d(TAG, "onSensorChanged: "+ yValue);
        socket.emit("yvalue", yValue);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
