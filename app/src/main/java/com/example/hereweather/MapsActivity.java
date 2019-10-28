package com.example.hereweather;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapsActivity extends FragmentActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng usrLatLng;
    WeatherMap weatherMap;
    public static final String TAG_TASK_FRAGMENT = "task_fragment";
    TextView weatherDateText;
    TextView weatherDescText;
    TextView weatherTempMinText;
    TextView weatherTempMaxText;
    Boolean helpTextDisplayed = false;

    public void onClicHelpButton(View view){
        Button helpButton = findViewById(R.id.helpButton);
        TextView helpText = findViewById(R.id.helpText);

        if (!helpTextDisplayed){
            helpText.setVisibility(VISIBLE);
            helpButton.setText("Hide help");
            helpTextDisplayed=true;
        }
        else{
            helpText.setVisibility(GONE);
            helpButton.setText("Show help");
            helpTextDisplayed=false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("helpTextDisplayed", helpTextDisplayed);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState!=null ) {

            helpTextDisplayed = savedInstanceState.getBoolean("helpTextDisplayed");

            if (helpTextDisplayed) {
                TextView helpText = findViewById(R.id.helpText);
                Button helpButton = findViewById(R.id.helpButton);
                helpButton.setText("Hide help");
                helpText.setVisibility(VISIBLE);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("onCreate",  "onCreate Enabled");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        weatherDateText = (TextView)findViewById(R.id.weatherDateText);
        weatherDescText = (TextView)findViewById(R.id.weatherDescText);
        weatherTempMinText = (TextView)findViewById(R.id.weatherTempMinText);
        weatherTempMaxText = (TextView)findViewById(R.id.weatherTempMaxText);


        // Start location services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location: ", location.toString());
                usrLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                weatherMap.setUsrLatLng(usrLatLng);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Check for permission to use GPS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission to use GPS
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

        // Create fragment manager and check for existing weatherMap fragment
        // if no weatherMap fragment - create and add new weatherMap fragment
        FragmentManager fm = getSupportFragmentManager();
        weatherMap = (WeatherMap) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (weatherMap == null) {
            weatherMap = new WeatherMap();
            Location l = getLocation();
            usrLatLng = new LatLng(l.getLatitude(), l.getLongitude());
            weatherMap.setUsrLatLng(usrLatLng);
            weatherMap.setWeatherDateText(weatherDateText);
            weatherMap.setWeatherDescText(weatherDescText);
            weatherMap.setWwatherTempMinText(weatherTempMinText);
            weatherMap.setWeatherTempMaxText(weatherTempMaxText);

            fm.beginTransaction().add(weatherMap, TAG_TASK_FRAGMENT).commit();
        }

        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(weatherMap);
        mapFragment.setRetainInstance(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Location: ", "onRequestPermissionsResult: ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
        }
    }

    public Location getLocation() {
        Log.i("getLocation",  "getLocation Enabled");
        Location location = null;
        try {
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                // Check for permission to use GPS
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Ask for permission to use GPS
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled && location == null) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Ask for permission to use GPS
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            Log.i("getLocation",  "Location Not Found");
        }
        return location;
    }

}
