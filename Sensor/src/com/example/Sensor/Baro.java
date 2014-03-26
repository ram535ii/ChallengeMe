package com.example.Sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

/**
 * Created by ConstantijnSchepens on 20/03/14.
 * Service source:Android Tutorial Simply Easy Learning by tutorialspoint.com
 *                https://www.websmithing.com/2011/02/01/how-to-update-the-ui-in-an-android-activity-using-data-from-a-background-service/comment-page-1/#comment-734
 */
public class Baro extends Service implements SensorEventListener{

    /** Variables **/
    private static final String TAG = "SensorApp";
    public static final String BROADCAST_ACTION = "com.example.Sensor.broadcastHeight"; //identifies broadcast
    private final Handler handler = new Handler();                                      //handler to broadcast within thread
    Intent intent;
    int counter = 0;

    private SensorManager sensorManager;
    private Sensor barometerSensor;
    private float height;


    /** Lifecyle Methods **/
    @Override
    public void onCreate(){
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        //startup code
        //Barometer Setup
        try {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null)
                barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            else
                throw new Exception( "Sensor not available" );

            sensorManager.registerListener(this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

            height = 0;

            /** Broadcast Stuff **/
            Toast.makeText(this, "Baro Service Started", Toast.LENGTH_SHORT).show();

            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
        }
        catch (Exception e){
            Log.e(TAG, "Unable to start barometer", e);
            Toast.makeText(this, "Unable to start barometer: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //called when started
        intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("height", height);
        sendBroadcast(intent);

        return 1;
    }


    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Baro Service Destroyed", Toast.LENGTH_LONG).show();
    }

    /** Listener Methods **/
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
    }

    /** Helper methods **/
    //public float getHeight(){ return height; } //now unnecessary

    /** Helper method to convert millibar to metres.  - Courtesy of Kartik **/
    private static float convertMillibarToMetres( float millibar ) {
        // Calculate the altitude in metres above sea level
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, millibar);
    }

    /** Method to handle handler update**/
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            sendData();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    private void sendData() {
        Log.d(TAG, "entered DisplayLoggingInfo");

        intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("height", height);
        //sendBroadcast(intent);
    }
}
