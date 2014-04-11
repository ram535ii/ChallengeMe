package com.example.PA2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.*;

/**
 * Created by darylrodrigo on 11/03/2014.
 */
public class selectOpponent extends Activity {
    public final static String CHALLANGE_OPPONENT = "com.example.ChallangeMe_v1.OPPONENT";
    public final static String CHALLANGE_GAME_TYPE = "com.example.ChallangeMe_v1.GAMETYPE";
    protected TableLayout tl;
    protected ScrollView sv;

    private  String gameType;
    private int currentUserId;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.selectopponent);

        sv = new ScrollView(this);
        populateTable();
        setContentView(sv);

        //setContentView(R.layout.selectopponent);
        // - - - - Update Game type in view
        //textView_GameType = (TextView) findViewById ( R.id.gametype );

        // Get Selected Gametype
        intent = getIntent();
        gameType = intent.getStringExtra(main.CHALLANGE_TYPE);
    }

    public void selectGame(View view) {

        //sv = (ScrollView) view.findViewById(R.id.scrollView1);



        //setContentView(R.layout.selectopponent);

        /*
        Intent intent = new Intent(this, startGame.class);

        Button b = (Button)view;

        String buttonText = b.getText().toString();

        intent.putExtra(CHALLANGE_OPPONENT, buttonText);
        startActivity(intent);
        */
    }

    public void populateTable(){

        TableLayout ll=new TableLayout(this);
        HorizontalScrollView hsv = new HorizontalScrollView(this);

        float displayDensity = getResources().getDisplayMetrics().density;
        float scaling =  1;

        user[] users = MyActivity.users;


        for(int i=1;i<users.length;i++) {


            // Create a TableRow and give it an ID
            TableRow tr = new TableRow(this);
            tr.setId(i);



            // Create a TextView to house the name of the province
            RelativeLayout imgvrl = new RelativeLayout(this);
            imgvrl.setId(900+i);
            TableRow.LayoutParams trp = new TableRow.LayoutParams(Math.round(80 * scaling), Math.round(80 * scaling));
            int margin = (int)(2*scaling);
            trp.setMargins(2*margin,margin,margin,margin);
            imgvrl.setLayoutParams(trp);
            tr.addView(imgvrl);



            //Set name
            TextView tv1=new TextView(this);
            //tv1.setText("fuckoff");
            tv1.setText(users[i].getName());
            tv1.setId(i+100);



            //Set button
            Button btn1 = new Button(this);
            btn1.setText("Challange");
            btn1.setId(i + 200);

            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Intent activityChangeIntent = new Intent(selectOpponent.this, challengeScreen.class);

                    int someid = v.getId();

                    activityChangeIntent.putExtra(CHALLANGE_GAME_TYPE, gameType);
                    activityChangeIntent.putExtra(CHALLANGE_OPPONENT, Integer.toString(someid));
                    // currentContext.startActivity(activityChangeIntent);

                    selectOpponent.this.startActivity(activityChangeIntent);
                }
            });

            tr.addView(tv1);
            tr.addView(btn1);

            ll.addView(tr);
        }
        hsv.addView(ll);
        sv.addView(hsv);
    }




    //Private Variables
    private TextView textView_GameType;

    private TableLayout challengeTable;

    private Intent intent;
}