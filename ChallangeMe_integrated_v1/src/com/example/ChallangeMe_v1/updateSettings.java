package com.example.ChallangeMe_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by darylrodrigo on 28/03/2014.
 */
public class updateSettings extends Activity {
    EditText username;
    EditText age;
    EditText location;

    CheckBox ski;
    CheckBox bike;
    CheckBox random;

    user currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        currentUser = MyActivity.users[0];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user);

        username = (EditText) findViewById ( R.id.et_username );
        age = (EditText) findViewById ( R.id.et_age );
        location = (EditText) findViewById ( R.id.et_location );

        ski = (CheckBox) findViewById (R.id.cb_ski);
        bike = (CheckBox) findViewById (R.id.cb_bike);
        random = (CheckBox) findViewById (R.id.cb_random);


        username.setText(currentUser.getName());
        age.setText(Integer.toString(currentUser.getAge()));
        location.setText(currentUser.getLocation());

        ski.setChecked(currentUser.getSki());
        bike.setChecked(currentUser.getBike());
        random.setChecked(currentUser.getRandom());

    }

    public void update_profile(View view){
        Intent intent = new Intent(this, main.class);

        MyActivity.users[0].setName(username.getText().toString());
        MyActivity.users[0].setLocation(location.getText().toString());

        //Put in error handeling
        MyActivity.users[0].setAge((Integer.parseInt(age.getText().toString())));

        MyActivity.users[0].setBike(bike.isChecked());
        MyActivity.users[0].setSki(ski.isChecked());
        MyActivity.users[0].setRandom(random.isChecked());

        startActivity(intent);
    }
}