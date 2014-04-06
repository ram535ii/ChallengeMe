package com.example.ChallangeMe_v1;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ConstantijnSchepens on 25/03/14.
 */
public class GPS extends Service implements LocationListener {
    /** Variables **/
    private static final String TAG = "SensorApp";
    public static final String BROADCAST_ACTION = "com.example.Sensor.broadcastGPS"; //identifies broadcast
    private final Handler handler = new Handler();                                      //handler to broadcast within thread
    Intent intent;

    boolean isGPSEnabled    = false;    //GPS status flag
    boolean isNetworkEnabled = false;    //Network status flag
    boolean canGetLocation  = false;    //GPS status

    Location currentLoc;                  //stores full location
    double   latitude;                  //lat
    double   longitude;                 //long

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES   = 1; //1m
    private static final long MIN_TIME_BW_UPDATES               = 1; //1ms

    protected LocationManager locationManager;      //location manager declaration
    Criteria c;

    /** Lifecyle Methods" **/
    @Override
    public void onCreate(){
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        //startup code
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            c = new Criteria();
            c.setAccuracy(Criteria.ACCURACY_LOW); //TODO fine?

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //TODO use pending intent?
//            locationManager.requestLocationUpdates(locationManager.getBestProvider(c,true),MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
//            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0,0.0F,this);
            locationManager.requestLocationUpdates(locationManager.getBestProvider(c,true),0,0.0F,this);

            Toast.makeText(this, "GPS Service Started", Toast.LENGTH_LONG).show();

            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
//            Toast.makeText(this, "GPS Service Started", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Log.e(TAG, "Unable to start GPS", e);
            Toast.makeText(this, "Unable to start GPS: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //called when started
        currentLoc = locationManager.getLastKnownLocation(locationManager.getBestProvider(c,true));

        if(currentLoc != null){
            latitude = currentLoc.getLatitude();
            longitude = currentLoc.getLongitude();

            /** Ensure app doesn't crash on start with in first location **/
            intent = new Intent(BROADCAST_ACTION); //clear intent
            intent.putExtra("latitude", (float)currentLoc.getLatitude());
            intent.putExtra("longitude", (float)currentLoc.getLongitude());
            sendBroadcast(intent);
        } else {
            Toast.makeText(this, "current==NULL", Toast.LENGTH_SHORT).show();
        }

        return 1;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "GPS Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider){

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    //TODO
        if(locationManager != null){
            currentLoc = location;
//            locationManager.getLastKnownLocation(locationManager.getBestProvider(c,true));
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(status != 2){ //2 means available
            /** Always use the best provider**/
            locationManager.requestLocationUpdates(locationManager.getBestProvider(c,true), MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
    }

    /** Method to handle handler update**/
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            //sendData();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    private void sendData() {
        Log.d(TAG, "entered DisplayLoggingInfo");

        if(currentLoc != null){
            intent = new Intent(BROADCAST_ACTION); //clear intent
            intent.putExtra("latitude", (float)currentLoc.getLatitude());
            intent.putExtra("longitude", (float)currentLoc.getLongitude());
            //sendBroadcast(intent);
        }
    }
}