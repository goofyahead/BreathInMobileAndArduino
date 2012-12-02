package es.cleanweb.breathin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;

import es.cleanweb.breathin.R;

import es.cleanweb.maps.MapsApi;
import es.cleanweb.model.Coords;
import es.cleanweb.model.FeedUpdate;
import es.cleanweb.pachube.PachubeApi;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.facebook.android.Facebook.*;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class ReceiveData extends MapActivity {
    // protected static final String USER_ID = "652022076";//GUS
    protected static final String USER_ID = "626292957";// ALE
    private TextView humidity;
    private TextView temperature;
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private TextView altitude;
    private Button start;
    private Button end;
    private String currentData = "";
    private EditText capturer;
    private int currentTemp = 0;
    private int currentHumidity = 0;
    private int currentCo = 200;
    private int currentHeartBeat = 120;
    private int currentMagnet = 5;
    private int currentDbNoise = 30;
    private long before = 0;
    private long TIMESTAMP = 10000;
    private TextWatcher watcher;
    private LocationManager locationManager;
    private MapView map;
    private LocationListener locationListener;
    private List<Coords> waypoints = new LinkedList<Coords>();
    Facebook facebook = new Facebook("173796479433716");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_data);
        
        //LOGIN FB
        facebook.authorize(this, new DialogListener() {
            @Override
            public void onComplete(Bundle values) {}

            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}
        });
        //LOGIN FB
        
        map = (MapView) findViewById(R.id.mapView);
        map.getController().setZoom(16);
        humidity = (TextView) findViewById(R.id.humidity);
        temperature = (TextView) findViewById(R.id.temperature);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        speed = (TextView) findViewById(R.id.speed);
        altitude = (TextView) findViewById(R.id.altitude);
        capturer = (EditText) findViewById(R.id.capturer);
        start = (Button) findViewById(R.id.start_button);
        end = (Button) findViewById(R.id.stop_button);

        start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Register the listener with the Location Manager to receive
                // location
                // updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });

        end.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new GoogleApiRequest().execute();
            }
        });
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location
                // provider.
                Log.d("MI_DEP", "updated Location");
                GeoPoint myGeoPoint = new GeoPoint(
                        (int)(location.getLatitude()*1000000),
                        (int)(location.getLongitude()*1000000));
                map.getController().animateTo(myGeoPoint);
               
                longitude.setText("" + location.getLongitude());
                latitude.setText("" + location.getLatitude());
                altitude.setText("" + location.getAltitude());
                speed.setText("" + (int) (location.getSpeed() * 3.6) + "Km/H");
                // set x y on interface and call pachube updating feed.
                if (System.currentTimeMillis() - before > TIMESTAMP) {
                    before = System.currentTimeMillis();
                    FeedUpdate feed = new FeedUpdate(USER_ID, "" + location.getLatitude(),
                            "" + location.getLongitude(), "" + location.getAltitude(), "" + currentHumidity, ""
                                    + currentTemp, "" + currentCo, "" + currentHeartBeat, "" + currentMagnet, ""
                                    + currentDbNoise);
                    new PachubeCall(feed).execute();
                    // save possible waypoints
                    waypoints.add(new Coords("" + location.getLatitude(), "" + location.getLongitude()));
                }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        if (locationManager.getProviders(true).size() > 0) {
            locationManager.removeUpdates(locationListener);
        }
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

    class GoogleApiRequest extends AsyncTask<Void, Void, Void> {
        private String distance = "";

        @Override
        protected void onPostExecute(Void result) {
            Intent resultactivity = new Intent(ReceiveData.this, ResultActivity.class);
            resultactivity.putExtra(ResultActivity.DISTANCE, distance);
            startActivity(resultactivity);
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (locationManager.getProviders(true).size() > 0) {
                locationManager.removeUpdates(locationListener);
            }
            List<Coords> finalWaypoints = new LinkedList<Coords>();
            int selector = waypoints.size() / 8;
            if (selector >= 1) {
                for (int z = 0; z < 8; z++) {
                    finalWaypoints.add(waypoints.get(selector * z));
                }
            }
            int size = waypoints.size();
            JSONObject response = MapsApi.getRoute(finalWaypoints, waypoints.get(0),
                    waypoints.get(waypoints.size() - 1));

            try {
                distance = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
                        .getJSONObject("distance").getString("text");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("MI_DEP", distance);
            return null;
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

}
