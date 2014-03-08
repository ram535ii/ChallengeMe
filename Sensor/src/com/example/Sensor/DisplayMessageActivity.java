package com.example.Sensor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by ConstantijnSchepens on 08/03/14.
 */
public class DisplayMessageActivity extends Activity {
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(SensorActivity.EXTRA_MESSAGE);

        //create a textview to display the message
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        //set textview as the full layout of the activity
        setContentView(textView);
    }

}