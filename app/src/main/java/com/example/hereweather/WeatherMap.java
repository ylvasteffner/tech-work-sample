package com.example.hereweather;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class WeatherMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private View view;
    LatLng usrLatLng;
    Marker usrMarker;
    GetWeatherForecast weatherForecast;
    TextView weatherDateText;
    TextView weatherDescText;
    TextView weatherTempMinText;
    TextView weatherTempMaxText;
    private String WEATHER_API_KEY;
    Boolean firstPosition = true;

    public WeatherMap() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_maps, container, false);
        return view;
    }


    public void setUserPosition(){
        if(firstPosition){
            usrMarker = mMap.addMarker(new MarkerOptions().position(usrLatLng).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)).anchor(0.5f,0.5f));
            firstPosition = false;
        }
        usrMarker.setPosition(usrLatLng);
        int zoomLevel = 8;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usrLatLng, zoomLevel));
    }

    protected void getWeather(){
        Log.i("getWeather",  "getWeather Enabled");

        Object transferData[] = new Object[7];
        transferData[0] = mMap;
        transferData[1] = usrLatLng;
        transferData[2] = WEATHER_API_KEY;
        transferData[3] = weatherDateText;
        transferData[4] = weatherDescText;
        transferData[5] = weatherTempMinText;
        transferData[6] = weatherTempMaxText;

        weatherForecast = new GetWeatherForecast();
        AsyncTask<Object, String, String> myTask = weatherForecast.execute(transferData);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("onMapReady",  "onMapReady Enabled");
        mMap = googleMap;

        // Add a marker at user position
        setUserPosition();

        // Obtain weather forecast for user position
        getWeather();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                usrLatLng = point;
                // Add a marker at users choice
                setUserPosition();
                // Obtain weather forecast for user choice
                getWeather();
            }
        });

    }

    public void setUsrLatLng(LatLng usrLatLng){
        this.usrLatLng = usrLatLng;
    }

    public void setWeatherDateText(TextView weatherDateText){
        this.weatherDateText = weatherDateText;
    }

    public void setWeatherDescText(TextView weatherDescText){
        this.weatherDescText = weatherDescText;
    }

    public void setWwatherTempMinText(TextView weatherTempMinText){
        this.weatherTempMinText = weatherTempMinText;
    }

    public void setWeatherTempMaxText(TextView weatherTempMaxText){
        this.weatherTempMaxText = weatherTempMaxText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        WEATHER_API_KEY = getResources().getString(R.string.weather_key);
    }

}
