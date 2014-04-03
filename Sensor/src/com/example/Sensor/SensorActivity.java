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

    SensorController controller;

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

        /** Start Sensor Controller **/
        controller = new SensorController(this,true,true);
    }
    //random functions
    @Override
    protected void onResume(){
        super.onResume();

        controller.startSensor(this, true, true);
    }

    @Override
    protected void onPause(){
        super.onPause();

        controller.stopSensor(this, true, true);
    }

    @Override
    public void onDestroy(){
        controller.stopSensor(this, true, true);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText)findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void updateGPS(View view){
        latitudeField.setText(String.valueOf(controller.getLatitude()));
        longitudeField.setText(String.valueOf(controller.getLongitude()));

        /** Speed calculation experiment **/
        if(loclak){
            location1 = controller.getLocation();
            time1 = System.nanoTime();
            loclak = false;
        } else {
            location2 = controller.getLocation();
            time2 = System.nanoTime();
            loclak = true;

            //longs acts weird so use floats.
            float distanceBn = distanceBetween(location1,location2);
            float timeBn = timeBetween(time1,time2);
            float avgSpeed = distanceBn / timeBn;

            Toast.makeText(getApplicationContext(),"Distance:" + distanceBn + "\nTime:" + timeBn + "\nSpeed:" + avgSpeed ,Toast.LENGTH_LONG).show();

            distanceField.setText(String.valueOf(distanceBn));
            crudeAvgSpeedField.setText(String.valueOf(avgSpeed));
        }
        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + gps.getLatitude() + "\nLong: " + gps.getLongitude()+ "\n: " + gps.getLongitude(), Toast.LENGTH_LONG).show();

    }
    public void updateBaro(View view){
        /** Sensor Controller Method **/
        baroField.setText(String.valueOf(controller.getHeight()));
   }


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

    /** Helper Functions **/
    private float distanceBetween(Location loc1, Location loc2){
        return loc1.distanceTo(loc2);
    }

    private float timeBetween(long t1, long t2){
        return ((float)t2 - (float)t1) / 1000000000; //ns -> s
    }

}
