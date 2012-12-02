package com.example.breathin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import es.cleanweb.model.FeedUpdate;
import es.cleanweb.pachube.PachubeApi;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class ReceiveData extends Activity {
    protected static final String USER_ID = "626292957";
    private TextView humidity;
    private TextView temperature;
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private TextView altitude;
    private String currentData = "";
    private EditText capturer;
    private int currentTemp = 0;
    private int currentHumidity = 0;
    private int currentCo = 200;
    private int currentHeartBeat = 120;
    private int currentMagnet = 5;
    private int currentDbNoise = 30;
    private TextWatcher watcher;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);

        humidity = (TextView) findViewById(R.id.humidity);
        temperature = (TextView) findViewById(R.id.temperature);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        speed = (TextView) findViewById(R.id.speed);
        altitude = (TextView) findViewById(R.id.altitude);
        capturer = (EditText) findViewById(R.id.capturer);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location
                // provider.
                Log.d("MI_DEP", "updated Location");
                longitude.setText("" + location.getLongitude());
                latitude.setText("" + location.getLatitude());
                altitude.setText("" + location.getAltitude());
                speed.setText("" + (int)(location.getSpeed() * 3.6) + "Km/H");
                // set x y on interface and call pachube updating feed.
                FeedUpdate feed = new FeedUpdate(USER_ID, "" + location.getLatitude(), "" + location.getLongitude(), ""
                        + location.getAltitude(), "" + currentHumidity, "" + currentTemp, "" + currentCo, ""
                        + currentHeartBeat, "" + currentMagnet, "" + currentDbNoise);

                new PachubeCall(feed).execute();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {
                Log.d("MI_DEP", "provider enabled");
            }

            public void onProviderDisabled(String provider) {
                Log.d("MI_DEP", "provider disabled");
            }
        };

        // Register the listener with the Location Manager to receive location
        // updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("MI_DEP", "changing");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("MI_DEP", "before");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 34) {
                    capturer.removeTextChangedListener(watcher);
                    String json = s.toString();
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // process and clean json
                    try {
                        currentTemp = jsonObj.getInt("temperature");
                        currentHumidity = jsonObj.getInt("humidity");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    humidity.setText("" + currentHumidity + "%");
                    temperature.setText("" + currentTemp + "Cº");
                    capturer.setText("");
                    capturer.addTextChangedListener(watcher);
                }
            }
        };

        capturer.addTextChangedListener(watcher);

    }

    @Override
    protected void onStop() {
        locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    class PachubeCall extends AsyncTask<Void, Void, Void> {
        private FeedUpdate feed;

        public PachubeCall(FeedUpdate feed) {
            this.feed = feed;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PachubeApi.updateFeed(feed.toJson());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

    }
}
