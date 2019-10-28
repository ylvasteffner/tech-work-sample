package com.example.hereweather;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GetWeatherForecast extends AsyncTask<Object, String, String> {
    private String weatherData;
    GoogleMap mMap;
    LatLng usrLatLng;
    String WEATHER_API_KEY;
    TextView weatherDateText;
    TextView weatherDescText;
    TextView weatherTempMinText;
    TextView weatherTempMaxText;


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        usrLatLng = (LatLng) objects[1];
        WEATHER_API_KEY = (String) objects[2];
        weatherDateText = (TextView) objects[3];
        weatherDescText = (TextView) objects[4];
        weatherTempMinText = (TextView) objects[5];
        weatherTempMaxText = (TextView) objects[6];

        DownloadUrl downloadUrl = new DownloadUrl();
        String url = getUrl(usrLatLng, WEATHER_API_KEY);
        queryWeatherForecast(url, downloadUrl);

        return weatherData;
    }


    private void queryWeatherForecast(String url, DownloadUrl downloadUrl){
        try {
            weatherData = downloadUrl.ReadTheURL(url);
            JSONObject forecastJson = new JSONObject(weatherData);
            updateWeatherInfo(forecastJson); //Put weather information on screen for user to see

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public void updateWeatherInfo(JSONObject forecastJson){
        try {
            JSONArray forecastArray = forecastJson.getJSONArray("list");
            double minTemp, maxTemp, minTempAverage =0 , maxTempAverage =0;
            String time;
            String date;
            String newDate;
            String[] splitStr;
            List<Double> minTempArray = new ArrayList<>();
            List<Double> maxTempArray = new ArrayList<>();


            // Obtain start-date
            JSONObject firstForecast = forecastArray.getJSONObject(0);
            time = firstForecast.getString("dt_txt");
            splitStr = time.trim().split("\\s+");
            date = splitStr[0];

            // Obtain closest in time weather description
            JSONObject listObject = forecastArray.getJSONObject(0);
            JSONObject weather = listObject.getJSONArray("weather").getJSONObject(0);
            String weatherDesc = weather.getString("description");

            // Obtain max and min temperature for day one (possible extension for several days)
            for(int i = 0; i < forecastArray.length(); i++) {
                JSONObject dailyForecast = forecastArray.getJSONObject(i);
                time = dailyForecast.getString("dt_txt");
                splitStr = time.trim().split("\\s+");
                newDate = splitStr[0];

                JSONObject tempObject = dailyForecast.getJSONObject("main");
                minTemp = tempObject.getDouble("temp_min");
                maxTemp = tempObject.getDouble("temp_max");

                if (date.equals(newDate)){
                    minTempArray.add(minTemp);
                    maxTempArray.add(maxTemp);
                }
                else {
                    minTempAverage = getMinValue(minTempArray);
                    maxTempAverage = getMaxValue(maxTempArray);
                    minTempArray.clear();
                    maxTempArray.clear();
                    break; // Remove break to continue to read forecast for 4 more days, add logic to save and view data for several days
                    //date=newDate;
                }

            }

            weatherDateText.setText(date);
            weatherDescText.setText(weatherDesc);
            weatherTempMinText.setText(String.valueOf(minTempAverage)+"\u00B0C");
            weatherTempMaxText.setText(String.valueOf(maxTempAverage)+"\u00B0C");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // Obtain the maximum value in array
    public static double getMaxValue( List<Double> list) {
        double maxValue = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > maxValue) {
                maxValue = list.get(i);
            }
        }
        return maxValue;
    }

    // Obtain the miniumum value in array
    public static double getMinValue( List<Double> list) {
        double minValue = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < minValue) {
                minValue = list.get(i);
            }
        }
        return minValue;
    }


    @Override
    public void onPostExecute(String s) {

    }


    public String getUrl(LatLng usrLatLng, String WEATHER_API_KEY){

        StringBuilder query = new StringBuilder("http://worksample-api.herokuapp.com/forecast?" +
                "lat="+ usrLatLng.latitude+"&lon="+usrLatLng.longitude);
        query.append("&units=metric");
        query.append("&key="+WEATHER_API_KEY);
        String queryStr = query.toString();
        Log.d("url string", "url string; "+ queryStr);
        Log.i("url string", "url string; "+ queryStr);
        return  queryStr;
    }


}
