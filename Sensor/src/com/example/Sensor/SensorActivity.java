package com.example.Sensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //GPS added
        latitudeField = (TextView)findViewById(R.id.TextView02);
        longitudeField = (TextView)findViewById(R.id.TextView04);
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

        GPSTracker gps = new GPSTracker(this);

        if(gps.canGetLocation()){
            latitudeField.setText(String.valueOf(gps.getLatitude()));
            longitudeField.setText(String.valueOf(gps.getLongitude()));

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + gps.getLatitude() + "\nLong: " + gps.getLongitude(), Toast.LENGTH_LONG).show();
        }
    }
    /**
    @Override
    protected void onResume(){
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        locationManager.removeUpdates(this);
    }

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
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText)findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
