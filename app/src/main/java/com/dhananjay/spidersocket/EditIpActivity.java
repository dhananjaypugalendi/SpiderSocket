package com.dhananjay.spidersocket;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditIpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity"; // TAG to identify log messages

    private static final String PREF_KEY_IP_ADDRESS = "PREF_KEY_IP_ADDRESS"; // key to store the IP address

    private EditText editIp;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ip); // specify the layout to be inflated
        initViews(); // initialize views
    }

    private void initViews() {
        editIp = (EditText) findViewById(R.id.enter_ip);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this); // handle button click
    }

    @Override
    public void onClick(View v) {
        // if the input is not empty, store the IP address
        if (!editIp.getText().toString().equals("")){
            PreferenceManager.getDefaultSharedPreferences(getApplication()).edit()
                    .putString(PREF_KEY_IP_ADDRESS, editIp.getText().toString())
                    .commit();
            finish();
        }else{
            Toast.makeText( getApplicationContext(), "PLEASE ENTER VALID IP", Toast.LENGTH_SHORT).show(); // display error message
        }

    }
}
