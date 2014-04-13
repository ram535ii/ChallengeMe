package com.example.PA2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ConstantijnSchepens on 11/03/14.
 */
public class BaroTracker extends Service implements SensorEventListener {

    /** Barometer Stuff **/
    private final Context mContext;
    private static final String TAG = "SensorApp";

    /** Sensor Manager. */
    private SensorManager sensorManager;
    private Sensor barometerSensor;
    private float height;
    private float maxHeight;
    private static final int BAROMETER_SAMPLING_RATE = 1000;    /** Barometer sampling rate (millisec). */

    /** Flag to indicate that barometer sensing is going on. */
    private boolean isBarometerOn;

    /** Handler to the main thread. */
    private Handler handler;

    /** Constructor **/
    public BaroTracker(Context context){
        this.mContext = context;
        handler = new Handler();

        try {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null)
                barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            else
                throw new Exception( "Sensor not available" );

            //should it be 'this'?
            sensorManager.registerListener(this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

            height = 0;

            //getMeasurement();
        }
        catch (Exception e){
            Log.e(TAG, "Unable to start barometer", e);
            createToast ( "Unable to start barometer: " + e.toString() );
        }
    }

    //get/set
    public float getHeight(){
        return height;
    }

    public float getMaxHeight() {return maxHeight; }

    public void getMeasurement(){

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //called when barometer changes accuracy
    @Override
    public void onAccuracyChanged( Sensor sensor , int accuracy ) {
        //Nothing to do here
    }

    //called when barometer changes value, implement this!
    @Override
    public void onSensorChanged( SensorEvent event ) {
        //TODO
        // Validity check: This must be the barometer sensor
        if ( event.sensor.getType() != Sensor.TYPE_PRESSURE )
            return;

        height = convertMillibarToMetres( event.values[0] );
        if(height > maxHeight){ maxHeight = height; }
    }

    public void resetBaroMaxHeight(){
        maxHeight = 0;
    }

    /** Helper method to convert millibar to metres.  - Courtesy of Kartik*/
    private static float convertMillibarToMetres( float millibar ) {
        // Calculate the altitude in metres above sea level
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, millibar);
    }

    /** Helper method to create toasts for the user. */
    private void createToast ( final String toastMessage ) {

        // Post a runnable in the Main UI thread
        handler.post ( new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        } );
    }
}
