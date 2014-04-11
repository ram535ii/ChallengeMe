package com.example.PA2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import nus.dtn.util.DtnMessage;
import nus.dtn.util.Descriptor;
import nus.dtn.util.DtnMessageException;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;
import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.MessageListener;
import nus.dtn.middleware.api.DtnMiddlewareInterface;
import nus.dtn.middleware.api.DtnMiddlewareProxy;
import nus.dtn.middleware.api.MiddlewareEvent;
import nus.dtn.middleware.api.MiddlewareListener;

/** App that broadcasts messages to everyone using a Mobile DTN. */
public class BroadcastAppActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try {

            // Specify what GUI to use
            setContentView ( R.layout.main );

            // Set a handler to the current UI thread
            handler = new Handler();

            // Get references to the GUI widgets
            textView_Message = (TextView) findViewById ( R.id.TextView_Message );
            editText_Message = (EditText) findViewById ( R.id.EditText_Message );
            button_Send = (Button) findViewById ( R.id.Button_Send );
            btn_Invite = (Button) findViewById (R.id.btnInvite);

            // Set the button's click listener
            button_Send.setOnClickListener ( new View.OnClickListener() {
                    public void onClick ( View v ) {

                        // Good practise to do I/O in a new thread
                        Thread clickThread = new Thread() {
                                public void run() {

                                    try {

                                        // Construct the DTN message
                                        DtnMessage message = new DtnMessage();
                                        messageType = 1;
                                        String chatMessage = editText_Message.getText().toString();
                                        Person person = new Person();
                                        person.setName("Constantijn");
                                        person.setAge(22);
                                        person.setChatMsg(chatMessage);
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
                                        comm.getFwdLayer().sendMessage ( comm.getDescriptor() , message , "everyone" , null );

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
            btn_Invite.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					 Thread clickThread = new Thread() {
                         public void run() {

                             try {

                                 // Construct the DTN message
                                 DtnMessage message = new DtnMessage();
                                 messageType = 2;
                                 
                                
                                 // Data part
                                 message.addData()                  // Create data chunk
                                     .writeInt(messageType);  // Chat message

                                 // Broadcast the message using the fwd layer interface
                                 comm.getFwdLayer().sendMessage ( comm.getDescriptor() , message , "everyone" , null );

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
                 createToast ( "Sending Challenge..." );
             } 
         } );

            comm = Communication.getInstance( getApplicationContext() , handler );
            comm.startMiddleware( new MiddlewareListener() {
                public void onMiddlewareEvent ( MiddlewareEvent event ) {
                    try {
                    	// Register a listener for received chat messages
                        ChatMessageListener messageListener = new ChatMessageListener();
                        comm.getFwdLayer().addMessageListener ( comm.getDescriptor() , messageListener );
                       
                    }
                    catch ( Exception e ) {
                        // Log the exception
                        Log.e ( "BroadcastApp" , "Exception in middleware start listener" , e );
                        // Inform the user
                        //createToast ( "Exception in middleware start listener, check log" );
                    }
                }
            });
  
        }
        catch ( Exception e ) {
            // Log the exception
            Log.e ( "BroadcastApp" , "Exception in onCreate()" , e );
            // Inform the user
            createToast ( "Exception in onCreate(), check log" );
        }
    }

    /** Called when the activity is destroyed. */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            comm.stopMiddleware();
        }
        catch ( Exception e ) {
            // Log the exception
            Log.e ( "BroadcastApp" , "Exception on stopping middleware" , e );
            // Inform the user
            createToast ( "Exception while stopping middleware, check log" );
        }
    }

    /** Listener for received chat messages. */
    private class ChatMessageListener 
        implements MessageListener {

        /** {@inheritDoc} */
        public void onMessageReceived ( final String source , 
                                        String destination , 
                                        DtnMessage message ) {

            try { 
            	
        		
                // Read the DTN message
                // Data part
                message.switchToData();
               
               if ( messageType == 1 ){
                // Put this code in handleUserMessage()
                byte[] msg = message.readBytes();
                ByteArrayInputStream bis = new ByteArrayInputStream(msg);
                ObjectInput in = null;
                in = new ObjectInputStream(bis);
                Person p = (Person)in.readObject();
                String info = p.infotoString();
                // Append to the message list
                final String newText = 
                    textView_Message.getText() + 
                    "\n" + source + " 's info " + info;
               
                // Update the text view in Main UI thread
                handler.post ( new Runnable() {
                        public void run() {
                            textView_Message.setText ( newText );
                        }
                       
                    } );
              
               }
               if (messageType == 2) {
            	  AlertDialog.Builder ad = new AlertDialog.Builder(context);
            	    ad.setTitle("ChallengeMe");
            	    ad.setMessage("Do you want to start a competition?");
            	    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            	        public void onClick(DialogInterface dialog, int which) { 
            	        	  messageType = 0;
            	           Intent intent = new Intent("android.intent.action.showResult");
            	         //  intent.putExtra("source", source);
            	            startActivity(intent); 
            	          
            	        }
            	     });
            	    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            	        public void onClick(DialogInterface dialog, int which) { 
            	           dialog.cancel();
            	        }
            	     });
      
            	    AlertDialog alertDialog = ad.create();
            	    alertDialog.show();
            	   
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
    
   

	/** Text View (displays messages). */
    private TextView textView_Message;
    /** Edit Text (user enters message here). */
    private EditText editText_Message;
    /** Button to trigger action (sending message). */
    private Button button_Send;
    private Button btn_Invite;
    
    private Communication comm;

    
    
    /** Handler to the main thread to do UI stuff. */
    private Handler handler;
    
    // flags for the request and accept option
    int Request_Flag = 0;
    int Accept_Flag = 0;
    final Context context = this;
    int messageType;
}

