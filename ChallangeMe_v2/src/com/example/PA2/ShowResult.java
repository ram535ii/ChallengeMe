package com.example.PA2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;
import nus.dtn.api.fwdlayer.MessageListener;
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
    
    /** Sender's descriptor. */
    private Descriptor descriptor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
		setContentView(R.layout.result);
		handler = new Handler();
     
		comm = Communication.getInstance( getApplicationContext() , handler );
		fwdLayer = comm.getFwdLayer();
		descriptor = comm.getDescriptor();
		
	/*	Intent intent = getIntent();
		source = intent.getStringExtra("source");
		createToast( "Source: " + source );
	*/	
        result = (TextView) findViewById(R.id.result);
        btn = (Button) findViewById(R.id.stop);
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
               
                // Update the text view in Main UI thread
                handler.post ( new Runnable() {
                        public void run() {
                            result.setText ( newText );
                        }
                    } );
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
   
        
    }


