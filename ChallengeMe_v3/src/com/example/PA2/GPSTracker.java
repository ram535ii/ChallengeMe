package com.example.PA2;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ConstantijnSchepens on 09/03/14.
 * Source of GPS help: http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
 */
public class GPSTracker extends Service implements LocationListener{

    //variable declaration
    private final Context mContext;


    boolean isGPSEnabled    = false;    //GPS status flag
    boolean isNetworkEnabled = false;    //Network status flag
    boolean canGetLocation  = false;    //GPS status

    Location location;                  //stores full location
    double   latitude;                  //lat
    double   longitude;                 //long

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES   = 1;   //10m
    private static final long MIN_TIME_BW_UPDATES               = 1000; //1min

    protected LocationManager locationManager;      //location manager declaration

    //constructor
    public GPSTracker(Context context){
        this.mContext = context;
        getLocation();
    }

    //getter/setter
    //they return 0.0 if failed
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public Location getLocation(){
        try{
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //get gps status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                //no network provider is enabled
                Toast.makeText(this, "No network provider enabled", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                //get location from net provider
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    Log.d("Network","Network");
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

            if(isGPSEnabled){
                if(location == null){
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    Log.d("GPS Enabled","GPS Enabled");
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public float distanceBetween(Location currentLoc, Location prevLoc){
        return currentLoc.distanceTo(prevLoc);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider){

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //alert to enable GPS if necessary
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        //set title
        alertDialog.setTitle("GPS settings");
        //set msg
        alertDialog.setMessage("GPS not enabled. Do you want to go to settings menu?");

        //set appropriate buttons
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    //GPS stop function
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
}
