package com.example.PA2SenderApp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import nus.dtn.util.DtnMessage;
import nus.dtn.util.Descriptor;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;
import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.MessageListener;
import nus.dtn.middleware.api.DtnMiddlewareInterface;
import nus.dtn.middleware.api.DtnMiddlewareProxy;
import nus.dtn.middleware.api.MiddlewareEvent;
import nus.dtn.middleware.api.MiddlewareListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SenderActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.main);

            //handler to current UI thread
            handler = new Handler();

            //reference to GUI widgets
            textView_Message = (TextView) findViewById ( R.id.TextView_Message );



            middleware = new DtnMiddlewareProxy(getApplicationContext());
            middleware.start ( new MiddlewareListener() {
                public void onMiddlewareEvent ( MiddlewareEvent event ) {
                    try {

                        // Check if the middleware failed to start
                        if ( event.getEventType() != MiddlewareEvent.MIDDLEWARE_STARTED ) {
                            throw new Exception( "Middleware failed to start, is it installed?" );
                        }

                        // Get the fwd layer API
                        fwdLayer = new ForwardingLayerProxy ( middleware );

                        // Get a descriptor for this user
                        // Typically, the user enters the username, but here we simply use IMEI number
                        TelephonyManager telephonyManager =
                                (TelephonyManager) getSystemService ( Context.TELEPHONY_SERVICE );
                        descriptor = fwdLayer.getDescriptor ( "nus.cs4222.pa2" , telephonyManager.getDeviceId() );

                        // Set the broadcast address
                        fwdLayer.setBroadcastAddress("nus.dtn.app.broadcast", "everyone");

                        // Register a listener for received chat messages
                        ChatMessageListener messageListener = new ChatMessageListener();
                        fwdLayer.addMessageListener ( descriptor , messageListener );
                    }
                    catch ( Exception e ) {
                        // Log the exception
                        Log.e ( "BroadcastApp" , "Exception in middleware start listener" , e );
                        // Inform the user
                        createToast ( "Exception in middleware start listener, check log" );
                    }
                }
            });
        }
        catch( Exception e ){
            // Log the exception
            Log.e ( "BroadcastApp" , "Exception in onCreate()" , e );
            // Inform the user
            createToast ( "Exception in onCreate(), check log" );
        }
    }

    public void sendMessage(View view){
        // Good practise to do I/O in a new thread
        Thread clickThread = new Thread() {
            public void run() {

                try {

                    // Construct the DTN message
                    DtnMessage message = new DtnMessage();
                    int grpNum = 12;
                    // Data part
                    Date date = new Date ();
                    long unixtime = date.getTime();

                    createToast("date is: " + Long.toString(unixtime));

                    message.addData()                  // Create data chunk
                            .writeLong(unixtime).writeInt(grpNum);

                    // Broadcast the message using the fwd layer interface
                    fwdLayer.sendMessage(descriptor, message, "Kartik", null);
    
                // Tell the user that the message has been sent
                    createToast("Chat message broadcast!");
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

    /** Called when the activity is destroyed. */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            // Stop the middleware
            // Note: This automatically stops the API proxies, and releases descriptors/listeners
            middleware.stop();
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
        public void onMessageReceived ( String source ,
                                        String destination ,
                                        DtnMessage message ) {

            try {

                // Read the DTN message
                // Data part
                //need to extract data and quote seperately
                message.switchToData();
                long date = (long) message.readLong();
                String chatMessage = message.readString();

                //create human readable time
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy h:mm a");
                String newDate = sdf.format(date);

                // Append to the message list
                final String newText =
                        textView_Message.getText() +
                                "\n" + source + " @ " + newDate + " says: " + chatMessage;

                // Update the text view in Main UI thread
                handler.post ( new Runnable() {
                    public void run() {
                        textView_Message.setText ( newText );
                    }
                } );
            }
            catch ( Exception e ) {
                // Log the exception
                Log.e ( "BroadcastApp" , "Exception on message event" , e );
                // Tell the user
                createToast ( "Exception on message event, check log" );
            }
        }
    }

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

    /** DTN Middleware API. */
    private DtnMiddlewareInterface middleware;
    /** Fwd layer API. */
    private ForwardingLayerInterface fwdLayer;

    /** Sender's descriptor. */
    private Descriptor descriptor;

    /** Handler to the main thread to do UI stuff. */
    private Handler handler;
}