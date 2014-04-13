package com.example.PA2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.os.SystemClock;
import android.widget.ToggleButton;
import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;
import nus.dtn.api.fwdlayer.MessageListener;
import nus.dtn.app.broadcast.R;
import nus.dtn.middleware.api.DtnMiddlewareInterface;
import nus.dtn.middleware.api.DtnMiddlewareProxy;
import nus.dtn.middleware.api.MiddlewareEvent;
import nus.dtn.middleware.api.MiddlewareListener;
import nus.dtn.util.Descriptor;
import nus.dtn.util.DtnMessage;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowResult extends Activity {
	
	TextView result;
	Button btn;
	private Handler handler;
	 int messageType;
	// DTN Middleware API.
    private DtnMiddlewareInterface middleware;
    /** Fwd layer API. */
    private ForwardingLayerInterface fwdLayer;
    private Communication comm;
    private String source;

    /** Challenge variables */

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


    /** Sender's descriptor. */
    private Descriptor descriptor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /** Game Data */

        setContentView(R.layout.challange_layout);

        //initialise sensor controller
        controller = new SensorController(this,true,false);

        gameButton = (ToggleButton) findViewById(R.id.toggleButton);

        /** INTENT Taken OUT but should be placed back in asap */
        intent = getIntent();
        gameType = "bla"; //intent.getStringExtra(selectOpponent.CHALLANGE_GAME_TYPE);
        challenger = 201; //Integer.parseInt(intent.getStringExtra(selectOpponent.CHALLANGE_OPPONENT));

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


        try {

        /** Communication Code */
        	

//		setContentView(R.layout.challange_layout);
		handler = new Handler();
     
		comm = Communication.getInstance( getApplicationContext() , handler );
		fwdLayer = comm.getFwdLayer();
		descriptor = comm.getDescriptor();
		
	/*	Intent intent = getIntent();
		source = intent.getStringExtra("source");
		createToast( "Source: " + source );
	*/	
        result = (TextView) findViewById(R.id.result);
    /*    btn = (Button) findViewById(R.id.stop);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 // Good practise to do I/O in a new thread
                Thread clickThread = new Thread() {
                        public void run() {

                            try {

                                // Construct the DTN message
                                DtnMessage message = new DtnMessage();
                                messageType = 3;
                                Person person = new Person();
                                person.setName("Dummy");
                                person.setAge(22);
                                person.setSpeed("14km/h");
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                ObjectOutput out = null;
                                out = new ObjectOutputStream(bos);   
                                out.writeObject(person);
                                out.close();
                                byte[] bt = bos.toByteArray();
                                // Data part
                                message.addData()                  // Create data chunk
                                    .writeBytes(bt);  // Chat message

                                // Broadcast the message using the fwd layer interface
                                fwdLayer.sendMessage ( descriptor , message , "everyone" , null );

                                // Tell the user that the message has been sent
                                createToast ( "Chat message broadcast!" );
                            }
                            catch ( Exception e ) {
                                // Log the exception
                                Log.e ( "BroadcastApp" , "Exception while sending message" , e );
                                // Inform the user
                                createToast ( "Exception while sending message, check log" );
                            }
                        }
                    };
                clickThread.start();

                // Inform the user
                createToast ( "Broadcasting message..." );
            } 
        } );
     */
         // Register a listener for received chat messages
        Listener messageListener = new Listener();
        fwdLayer.addMessageListener ( descriptor , messageListener );
	}  catch ( Exception e ) {
        // Log the exception
        Log.e ( "BroadcastApp" , "Exception in onCreate()" , e );
        // Inform the user
        createToast ( "Exception in onCreate(), check log" );
    }
	}
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
/*
	        try {
	        }
	        catch ( Exception e ) {
	            // Log the exception
	            Log.e ( "BroadcastApp" , "Exception on stopping middleware" , e );
	            // Inform the user
	            createToast ( "Exception while stopping middleware, check log" );
	        }*/
	    }
   
	 /** Helper method to create toasts. */
    private void createToast ( String toastMessage ) {

        // Use a 'final' local variable, otherwise the compiler will complain
        final String toastMessageFinal = toastMessage;

        // Post a runnable in the Main UI thread
        handler.post ( new Runnable() {
                @Override
                public void run() {
                    Toast.makeText ( getApplicationContext() , 
                                     toastMessageFinal , 
                                     Toast.LENGTH_SHORT ).show();
                }
            } );
    }

    public void StartGame(View view){
        if(!gameButton.isChecked()){
            Toast.makeText(this, "wait till ready", Toast.LENGTH_SHORT).show();
        } else {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

            TVmaxHeight.setText("Max Height: " + (controller.getMaxHeight()));
            TVheight.setText("Current Height: " + (controller.getHeight()));

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


                /** Communication bullshit */


                try {

                    // Construct the DTN message
                    DtnMessage message = new DtnMessage();
                    messageType = 3;
                    Person person = new Person();



                    person.setName(player.getName());
                    person.setAge(player.getAge());
                    person.setSpeed(Double.toString(finalHeight));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutput out = null;
                    out = new ObjectOutputStream(bos);
                    out.writeObject(person);
                    out.close();
                    byte[] bt = bos.toByteArray();
                    // Data part
                    message.addData()                  // Create data chunk
                            .writeBytes(bt);  // Chat message

                    // Broadcast the message using the fwd layer interface
                    fwdLayer.sendMessage ( descriptor , message , "everyone" , null );

                    // Tell the user that the message has been sent
                    createToast ( "Chat message broadcast!" );
                    
                }
                catch ( Exception e ) {
                    // Log the exception
                    Log.e ( "BroadcastApp" , "Exception while sending message" , e );
                    // Inform the user
                    createToast ( "Exception while sending message, check log" );
                }
            }
        }

    };
    
    private class Listener implements MessageListener{
   	 /** {@inheritDoc} */
       public void onMessageReceived ( String source , 
                                       String destination , 
                                       DtnMessage message ){
       	try{
       		
       	/*	if( source.equals( comm.getMyImei() )) 
       			return;
       		*/
       		 // Read the DTN message
               // Data part
               message.switchToData();
              
              if ( messageType == 3 ){
               // Put this code in handleUserMessage()
               byte[] msg = message.readBytes();
               ByteArrayInputStream bis = new ByteArrayInputStream(msg);
               ObjectInput in = null;
               in = new ObjectInputStream(bis);
               Person p = (Person)in.readObject(); 
               String info = p.showResults();
               // Append to the message list
               final String newText = 
                   result.getText() + 
                   "\n" + info;

               //check own results vs their results
               if(finalHeight > Double.parseDouble("1234")){
                   handler.post ( new Runnable() {
                       public void run() {
                           result.setText ( "You Win" );
                       }
                   } );
               } else {
                   handler.post ( new Runnable() {
                       public void run() {
                           result.setText ( "They Win" );
                       }
                   } );
               }
		
               // Update the text view in Main UI thread
         /*      handler.post ( new Runnable() {
                   public void run() {
                       result.setText ( newText );
              }
               }); */
               }
       	}
       	catch ( Exception e ) {
               // Log the exception
               Log.e ( "BroadcastApp" , "Exception on message event" , e );
               // Tell the user
               createToast ( "Exception on message event, check log" );
           }
   }
   }
    private Intent intent;


}


