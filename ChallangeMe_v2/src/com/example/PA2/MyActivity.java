package com.example.PA2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyActivity extends Activity {

    public static user[] users = new user[7];

    /**
     * Called when the activity is first created.
     *
     * WHEN ADDING OR REMOVING PEOPLE MAKE SURE TO EDIT THE ARRAY COUNT!!!!!
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        users[0] = new user("Daryl", 21, "London");
        users[1] = new user("Const", 21, "London");
        users[2] = new user("Ryan", 20, "Cali");
        users[3] = new user("Rushika", 20, "Srilanka");
        users[4] = new user("Schweps", 20, "Swits");
        users[5] = new user("Vince", 20, "Portugal");
        users[6] = new user("Bea", 20, "Italy");

        Intent intent = new Intent(this, main.class);;
        startActivity(intent);

    }


}
