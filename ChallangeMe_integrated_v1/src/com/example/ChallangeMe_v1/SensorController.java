package com.example.ChallangeMe_v1;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by ConstantijnSchepens on 26/03/14.
 * The correct way to do this is to bind the services to the main activity and access data directly
 *  - this will be done if there is time left at the end, as this works for now
 *  - the sensors broadcast updated values every second, so it shouldn't be too intense (i.e. not too many intents)
 */
public class SensorController{

    /** class variables **/
    private Intent baroIntent;
    private Intent gpsIntent;

    private float maxHeight;
    private float height;
    private float latitude;
    private float longitude;
    private Location location;

    /** constructors **/
    public SensorController(Context context, boolean baroOn, boolean gpsOn){
        if(baroOn){
            baroIntent = new Intent(context, Baro.class);
            context.startService(baroIntent);
            context.registerReceiver(broadcastReceiver, new IntentFilter(com.example.ChallangeMe_v1.Baro.BROADCAST_ACTION));
        }
        if (gpsOn){
            gpsIntent = new Intent(context, GPS.class);
            context.startService(gpsIntent);
            context.registerReceiver(broadcastReceiver, new IntentFilter(GPS.BROADCAST_ACTION));
        }
        //initialise variables
        longitude = 0;
        latitude = 0;
        height = 0;
        maxHeight = 0;
    }

    /** get/set
     * they return 0.0 if failed**/

    public float getHeight(){ return height; }
    public float getMaxHeight() {return maxHeight; }
    public float getLatitude(){ return latitude; }
    public float getLongitude(){ return longitude; }
//    public Location getLocation() { return location; }


    /** class methods **/
    public void startSensor(Context context, boolean baro, boolean gps){
        if(baro){
            context.startService(baroIntent);
            context.registerReceiver(broadcastReceiver, new IntentFilter(Baro.BROADCAST_ACTION));
        }
        if(gps){
            context.startService(gpsIntent);
            context.registerReceiver(broadcastReceiver, new IntentFilter(GPS.BROADCAST_ACTION));
        }
    }

    public void stopSensor(Context context, boolean baro, boolean gps){
        if(baro)
            context.stopService(baroIntent);

        if(gps)
            context.stopService(gpsIntent);

        if(baro && gps)
            context.unregisterReceiver(broadcastReceiver);
    }

    /** broadcast methods **/
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** Code to handle new received sensor data**/
            //TODO
            updateData(intent);
        }
    };

    private void updateData(Intent intent) {
        String action = intent.getAction();

        if(action.equals("com.example.Sensor.broadcastHeight")){
            height = intent.getFloatExtra("height", 0);
            maxHeight = intent.getFloatExtra("maxHeight", 0);

        } else if(action.equals("com.example.Sensor.broadcastGPS")){
            //TODO
            latitude = intent.getFloatExtra("latitude", 0);
//            location.setLatitude(latitude);

            longitude = intent.getFloatExtra("longitude", 0);
//            location.setLongitude(longitude);

        }
    }
}
