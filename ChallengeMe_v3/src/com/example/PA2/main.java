package com.example.PA2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import nus.dtn.app.broadcast.R;

/**
 * Created by darylrodrigo on 28/03/2014.
 */
public class main extends Activity {

    public final static String CHALLANGE_TYPE = "com.example.PA2.MESSAGE";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_djr);

        if(MyActivity.users[0].getBike() == false){
            Button btn = (Button) findViewById(R.id.buttonBike);
            btn.setVisibility(View.GONE);
        }

        if(MyActivity.users[0].getRandom() == false){
            Button btn = (Button) findViewById(R.id.buttonRandom);
            btn.setVisibility(View.GONE);
        }

        if(MyActivity.users[0].getSki() == false){
            Button btn = (Button) findViewById(R.id.buttonSki);
            btn.setVisibility(View.GONE);
        }
    }

    public void selectOpponent(View view) {

        Intent intent = new Intent(this, BroadcastAppActivity.class);

        Button b = (Button)view;
        String buttonText = b.getText().toString();

        intent.putExtra(CHALLANGE_TYPE, buttonText);
        startActivity(intent);
    }

    public void update(View view) {
        Intent intent = new Intent(this, updateSettings.class);
        startActivity(intent);
    }
}