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
 * Created by ConstantijnSchepens on 11/04/14.
 */
public class Accel extends Service implements SensorEventListener {

    private static final String TAG = "SensorApp";
    public static final String BROADCAST_ACTION = "com.example.Sensor.broadcastAccel"; //identifies broadcast
    private final Handler handler = new Handler();                                      //handler to broadcast within thread
    Intent intent;

    private SensorManager sensorManager;
    private Sensor accelSensor;
    float x,y,z;

    public void onCreate(){
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        //startup code
        //Barometer Setup
        try {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
                accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            else
                throw new Exception( "Sensor not available" );

            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);

            x = y = z = 0;

            /** Broadcast Stuff **/
            Toast.makeText(this, "Accel Service Started", Toast.LENGTH_SHORT).show();

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
        intent = new Intent(BROADCAST_ACTION);  //<- put in onChanged
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        intent.putExtra("z", z);
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
        Toast.makeText(this, "Accel Service Destroyed", Toast.LENGTH_LONG).show();
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
        if ( event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
            return;

        float[] values = event.values;
        // Movement
        x = values[0];
        y = values[1];
        z = values[2];
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
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        intent.putExtra("z", z);
        sendBroadcast(intent);
    }
}
