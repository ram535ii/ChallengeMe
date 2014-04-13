package com.example.PA2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.SystemClock;
import android.os.Handler;
import nus.dtn.app.broadcast.R;

/**
 * Created by darylrodrigo on 28/03/2014.
 */


public class challengeScreen extends Activity {
    private int challenger;
    private String gameType;

    private TextView TVchallenger;
    private TextView TVgameType;
    private TextView TVmaxHeight;
    private TextView TVheight;

    private user player;
    private user opponent;

    private double initialHeight;
    private double finalHeight;

    private ToggleButton gameButton;
    private boolean gameStarted;

    SensorController controller;

    //TimerVariables
    private TextView timerValue;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challange_layout);

        //initialise sensor controller
        //controller = new SensorController(this,true,true);

        gameButton = (ToggleButton) findViewById(R.id.toggleButton);

        //get text
        intent = getIntent();
        gameType = intent.getStringExtra(selectOpponent.CHALLANGE_GAME_TYPE);
        challenger = Integer.parseInt(intent.getStringExtra(selectOpponent.CHALLANGE_OPPONENT));

        //set correct id
        challenger = challenger - 200;

        //set opponent and user
        player = MyActivity.users[0];
        opponent = MyActivity.users[challenger];

        //Bind text views
        TVchallenger = (TextView) findViewById(R.id.game_challenger);
        TVgameType = (TextView) findViewById(R.id.game_gametype);
        TVheight = (TextView) findViewById(R.id.maxHeightChallange);
        TVmaxHeight = (TextView) findViewById(R.id.heightChallange);

        TVchallenger.setText("Challenger: " + opponent.getName());
        TVgameType.setText("Game Type: " + gameType);

        //game variables
        gameStarted = false;
        timerValue = (TextView) findViewById(R.id.timerValue);
    }

    public void StartGame(View view){
        if(!gameButton.isChecked()){
            Toast.makeText(this, "wait till ready", Toast.LENGTH_SHORT).show();
        } else {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

            //TVmaxHeight.setText("Max Height: " + (controller.getMaxHeight()));
            //TVheight.setText("Current Height: " + (controller.getHeight()));

            //Set text for done button
            gameButton.setText("Finished?");
            gameStarted = true;
        }
    }

    public void update(View view){
        //check if ready
        if(!gameButton.isChecked()){
            Toast.makeText(this, "wait till ready", Toast.LENGTH_SHORT).show();
        } else {
            //TVmaxHeight.setText("Max Height: " + (controller.getMaxHeight()- initialHeight));
            //TVheight.setText("Starting Height: " + (controller.getHeight() - initialHeight));

            TVmaxHeight.setText("Max Height: " + (controller.getMaxHeight()));
            TVheight.setText("Current Height: " + (controller.getHeight()));
        }
    }

    public void FinishGame(View view){

    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            //count down in milliseconds
            timeInMilliseconds = 10000 - timeInMilliseconds;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);

            //DONE
            if(secs == 0 && milliseconds < 20){
                customHandler.removeCallbacks(updateTimerThread);
                TVmaxHeight.setText("FINAL SCORE: " + controller.getMaxHeight());
                TVheight.setVisibility(View.GONE);
                timerValue.setVisibility(View.GONE);

                //Get opponents score!
            }
        }

    };

    private Intent intent;
}