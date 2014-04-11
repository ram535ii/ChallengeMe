package com.example.PA2;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;

import nus.dtn.middleware.api.DtnMiddlewareInterface;
import nus.dtn.middleware.api.DtnMiddlewareProxy;
import nus.dtn.middleware.api.MiddlewareEvent;
import nus.dtn.middleware.api.MiddlewareListener;
import nus.dtn.util.Descriptor;

public class Communication {
	
	private Communication( Context context , 
			Handler handler ) {
		this.context = context;
		this.handler = handler;
	}
	
	public static Communication getInstance( Context context , 
			Handler handler ) {
	    if ( communication == null ) {
	    	communication = new Communication( context ,
	    			                           handler );
	    }
	    return communication;
	}

	public void startMiddleware( final MiddlewareListener middlewareListener )
	    throws Exception {
		// Start the middleware
        middleware = new DtnMiddlewareProxy ( context );
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
                            (TelephonyManager) context.getSystemService ( Context.TELEPHONY_SERVICE );
                        myImei = telephonyManager.getDeviceId();
                        descriptor = fwdLayer.getDescriptor ( "nus.dtn.app.broadcast" , myImei );

                        // Set the broadcast address
                        fwdLayer.setBroadcastAddress ( "nus.dtn.app.broadcast" , "everyone" );

                        middlewareListener.onMiddlewareEvent( event );
                       
                    }
                    catch ( Exception e ) {
                        // Log the exception
                        Log.e ( "BroadcastApp" , "Exception in middleware start listener" , e );
                        // Inform the user
                        createToast ( "Exception in middleware start listener, check log" );
                    }
                }
            } );
	}
	
	public void stopMiddleware()
		    throws Exception {
		// Stop the middleware
        // Note: This automatically stops the API proxies, and releases descriptors/listeners
        middleware.stop();
	}
	
	public ForwardingLayerInterface getFwdLayer() {
		return fwdLayer;
	}
	
	public Descriptor getDescriptor() {
		return descriptor;
	}
	public String getMyImei() {
		return myImei;
	}
	/** Helper method to create toasts. */
    private void createToast ( String toastMessage ) {

        // Use a 'final' local variable, otherwise the compiler will complain
        final String toastMessageFinal = toastMessage;

        // Post a runnable in the Main UI thread
        handler.post ( new Runnable() {
                @Override
                public void run() {
                    Toast.makeText ( context , 
                                     toastMessageFinal , 
                                     Toast.LENGTH_SHORT ).show();
                }
            } );
    }
	/** DTN Middleware API. */
    private DtnMiddlewareInterface middleware;
    /** Fwd layer API. */
    private ForwardingLayerInterface fwdLayer;

    /** Sender's descriptor. */
    private Descriptor descriptor;
    private Context context;
    /** Handler to the main thread to do UI stuff. */
    private Handler handler;
    private String myImei;
    private static Communication communication = null;
}
