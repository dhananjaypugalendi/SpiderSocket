package com.dhananjay.spidersocket;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String PREF_KEY_IP_ADDRESS = "PREF_KEY_IP_ADDRESS";

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private double yValue;
    private String ipAddress;

    private Socket socket;

    private TextView accelData;
    private ImageButton editIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ipAddress = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString(PREF_KEY_IP_ADDRESS, null);

        initViews();

        initAccelerometer();

        initSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);

        if(socket != null){
            socket.disconnect();
        }
    }

    private void initViews() {
        accelData = (TextView) findViewById(R.id.accel_value);
        editIp = (ImageButton) findViewById(R.id.edit_ip);
        editIp.setOnClickListener(this);
    }

    private void initAccelerometer() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initSocket() {
        Log.d(TAG, "initSocket: "+ipAddress);
        if( ipAddress != null ){
            try {
                socket = IO.socket("http://0.0.0.0:8080");
                socket.connect();
            } catch (URISyntaxException e) {
                Log.e(TAG, "initSocket: ", e);
            }
        }else{
            Toast.makeText( getApplicationContext(), "ENTER IP", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        yValue = event.values[1]; // [x, y, z] are available values
        yValue = Math.round(yValue * 1000.0) / 1000.0;
        accelData.setText(String.valueOf(yValue));
        //Log.d(TAG, "onSensorChanged: "+ yValue);
        if(socket!=null) {
            socket.emit("yvalue", yValue);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {
        Intent editIpIntent = new Intent(this, EditIpActivity.class);
        startActivity(editIpIntent);
        finish();
    }
}
