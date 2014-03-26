package com.example.Sensor;

import android.app.Activity;
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
 * GPS Code source:http://www.vogella.com/tutorials/AndroidLocationAPI/article.html
 */

public class SensorActivity extends Activity{
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"; //message identifier

    //GPS Variables
    private TextView latitudeField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;

    boolean loclak = true;
    Location location1;
    Location location2;
    long time1;
    long time2;
    private TextView distanceField;
    private TextView crudeAvgSpeedField;

    private TextView baroField;
    BaroTracker baro;
    private Intent baroIntent;
    private Intent gpsIntent;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //GPS added
        latitudeField       = (TextView)findViewById(R.id.TextView02);
        longitudeField      = (TextView)findViewById(R.id.TextView04);
        distanceField       = (TextView)findViewById(R.id.TextView05);
        crudeAvgSpeedField  = (TextView)findViewById(R.id.TextView08);
        baroField           = (TextView)findViewById(R.id.TextView09);
/***
        //location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //criteria definition to select loc provider
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
        Location location = locationManager.getLastKnownLocation(provider);

        if(location != null){
            //System.out.println("Provider " + provider + " has been selected");
            System.out.println("A provider has been selected.");
            onLocationChanged(location);
        } else {
            latitudeField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
***/

//        GPSTracker gps = new GPSTracker(this);
//
//        if(gps.canGetLocation()){
//            latitudeField.setText(String.valueOf(gps.getLatitude()));
//            longitudeField.setText(String.valueOf(gps.getLongitude()));
//
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + gps.getLatitude() + "\nLong: " + gps.getLongitude(), Toast.LENGTH_LONG).show();
//            //baro = new BaroTracker(this);
//        }

        /** Start the barometer service **/
        startService(new Intent(getBaseContext(), Baro.class));
        baroIntent = new Intent(this, Baro.class);

        /** Start the GPS service **/
        startService(new Intent(getBaseContext(), GPS.class));
        gpsIntent = new Intent(this, GPS.class);
    }
    //random functions
    @Override
    protected void onResume(){
        super.onResume();
        //locationManager.requestLocationUpdates(provider, 400, 1, this);
        startService(baroIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(Baro.BROADCAST_ACTION));

        startService(gpsIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(GPS.BROADCAST_ACTION));
    }

    @Override
    protected void onPause(){
        super.onPause();
        //locationManager.removeUpdates(this);
        unregisterReceiver(broadcastReceiver);
        stopService(baroIntent);

        stopService(gpsIntent);
    }
   /**

    @Override
    public void onLocationChanged(Location location){
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudeField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        //TODO
    }

    @Override
    public void onProviderEnabled(String provider){
        Toast.makeText(this, "Enabled new provider" + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider){
        Toast.makeText(this, "Disabled provider" + provider, Toast.LENGTH_SHORT).show();
    }
**/
    @Override
    public void onDestroy(){
        /** Start the barometer service**/
        stopService(new Intent(getBaseContext(), Baro.class));
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText)findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void updateGPS(View view){
//        GPSTracker gps = new GPSTracker(this);
//
//        if(gps.canGetLocation()){
//            latitudeField.setText(String.valueOf(gps.getLatitude()));
//            longitudeField.setText(String.valueOf(gps.getLongitude()));
//
//            if(loclak){
//                location1 = gps.getLocation();
//                time1 = System.nanoTime();
//                loclak = false;
//            } else {
//                location2 = gps.getLocation();
//                time2 = System.nanoTime();
//                loclak = true;
//
//                //longs acts weird so use floats.
//                float distanceBn = gps.distanceBetween(location2,location1);
//                float timeBn = ((float)time2 - (float)time1) / 1000000000; //ns -> s
//                float avgSpeed = distanceBn / timeBn;
//
//                Toast.makeText(getApplicationContext(),"Distance:" + distanceBn + "\nTime:" + timeBn + "\nSpeed:" + avgSpeed ,Toast.LENGTH_LONG).show();
//
//                distanceField.setText(String.valueOf(distanceBn));
//                crudeAvgSpeedField.setText(String.valueOf(avgSpeed));
//            }
//            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + gps.getLatitude() + "\nLong: " + gps.getLongitude()+ "\n: " + gps.getLongitude(), Toast.LENGTH_LONG).show();
//        }
        startService(gpsIntent);

    }
    public void updateBaro(View view){
        //float height = baro.getHeight();

        //float height = Baro

        //baroField.setText(String.valueOf(height));

        /**  **/
//        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        Sensor barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//
//        sensorManager.registerListener(this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        startService(baroIntent);
   }

//    public final void onSensorChanged(SensorEvent event) {
//        float millibars_of_pressure = event.values[0];
//        float height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,millibars_of_pressure);
//        String s = Float.toString(height);
//
//        baroField.setText(s);
//    }
//    @Override
//    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Do something here if sensor accuracy changes.
//    }

    /** Receiver to receive updated info from barometer **/
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** Code to handle new received height**/
            //TODO
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        String action = intent.getAction();
        if(action.equals("com.example.Sensor.broadcastHeight")){
            float height = intent.getFloatExtra("height", 0);

            baroField.setText(String.valueOf(height));
        } else if(action.equals("com.example.Sensor.broadcastGPS")){
            //TODO
            float lat = intent.getFloatExtra("latitude", 0);
            float lon = intent.getFloatExtra("longitude", 0);

            latitudeField.setText(String.valueOf(String.valueOf(lat)));
            longitudeField.setText(String.valueOf(String.valueOf(lon)));
        }
    }
}
