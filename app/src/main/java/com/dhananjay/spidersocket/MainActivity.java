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

    private static final String TAG = "MainActivity"; // TAG to identify log messages

    private static final String PREF_KEY_IP_ADDRESS = "PREF_KEY_IP_ADDRESS"; // key to fetch previously stored IP address

    // sensors
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private double yValue;
    private String ipAddress;

    private Socket socket;

    private TextView accelData;
    private ImageButton editIp;

    // the entry point of the program
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // call the super class

        setContentView(R.layout.activity_main); // specifies which layout file to inflate

        initViews(); // initialize the views

        initAccelerometer(); // initialize the sensor

    }

    // method executed when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        // make the activity fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME); // start listening to sensor events

        initSocket(); // initialize the socket

    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this); // stop receiving sensor updates

    }

    // last method called before the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();


        // if socket is not null, disconnect it
        if(socket != null){
            socket.disconnect();
        }
    }

    // initialize views
    private void initViews() {
        accelData = (TextView) findViewById(R.id.accel_value);
        editIp = (ImageButton) findViewById(R.id.edit_ip);
        editIp.setOnClickListener(this); // handle button click
    }

    // initialize accel
    private void initAccelerometer() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // initalize socket
    private void initSocket() {

        ipAddress = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString(PREF_KEY_IP_ADDRESS, null); // check if a valid IP address is already saved

        // check if the IP address is valid, is so create and connect to the socket
        if( ipAddress != null ){
            try {
                socket = IO.socket("http://"+ ipAddress +":3000"); // ip address : port no
                socket.connect();
            } catch (URISyntaxException e) {
                Log.e(TAG, "initSocket: ", e); // log errors if any
            }
        }else{
            Toast.makeText( getApplicationContext(), "ENTER IP", Toast.LENGTH_SHORT).show(); // display message to user that IP is invalid
        }
    }

    // called every time sensor data is changed
    @Override
    public void onSensorChanged(SensorEvent event) {
        yValue = event.values[1]; // [x, y, z] are available values
        yValue = Math.round(yValue * 1000.0) / 1000.0; // round off up to thousandth of decimal
        accelData.setText(String.valueOf(yValue)); // update the accel data
        if(socket!=null) {
            socket.emit("yvalue", yValue); // send the values to server
        }
    }

    // called when accuracy of the sensor changes
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // handle image button click
    @Override
    public void onClick(View v) {
        // create an intent to navigate to EditIpActivity
        Intent editIpIntent = new Intent(this, EditIpActivity.class);
        startActivity(editIpIntent);
    }
}
