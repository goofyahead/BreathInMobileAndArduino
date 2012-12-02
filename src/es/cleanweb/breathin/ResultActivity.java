package es.cleanweb.breathin;

import java.io.IOException;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.google.android.maps.MapActivity;

import es.cleanweb.breathin.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class ResultActivity extends Activity {

    public static final String DISTANCE = "DISTANCE";
    public static int caloriesKm = 30;
    private String unityMeasure = "";
    private RelativeLayout distanceBar;
    private RelativeLayout caloriesBar;
    private RelativeLayout heartBar;
    private RelativeLayout pollution;
    private RelativeLayout footprint;
    private TextView heartBeatText;
    private TextView distanceText;
    private TextView caloriesText;
    private TextView points;    
    private String bundleDistance;
    Facebook facebook = new Facebook("173796479433716");
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        //FB STUFF
      //LOGIN FB
        facebook.authorize(this,new String[] { "publish_stream" },new DialogListener() {
            @Override
            public void onComplete(Bundle values) {}

            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}
        });
        
        distanceBar = (RelativeLayout) findViewById(R.id.distanceBar);
        caloriesBar = (RelativeLayout) findViewById(R.id.calories_bar);
        heartBar = (RelativeLayout) findViewById(R.id.heart);
        pollution = (RelativeLayout) findViewById(R.id.pollution);
        footprint = (RelativeLayout) findViewById(R.id.footprint);
        
        distanceText = (TextView) findViewById(R.id.distance_text);
        caloriesText = (TextView) findViewById(R.id.calories_text);
        heartBeatText = (TextView) findViewById(R.id.heart_text);
        points = (TextView) findViewById(R.id.points);
        
        heartBeatText.setText("120p");
        
        bundleDistance = getIntent().getExtras().getString(DISTANCE);

        String numericDistance = bundleDistance.split(" ")[0];
        if (bundleDistance.split(" ")[1].equals("m")){
            unityMeasure = "meters";
        } else {
            unityMeasure = "kilometers";
        }
        int realDistance = (int) Float.parseFloat(numericDistance);
        Log.d("MI_DEP","numeric distance: " + numericDistance);
        
        if (unityMeasure.equals("kilometers")){
            realDistance = realDistance * 1000;
        }
        
        distanceText.setText(bundleDistance);
        caloriesText.setText(""+(realDistance * caloriesKm) + "cal.");
        
        int startingPoints = 157905;
        points.setText("" + startingPoints);
        final int pointsTo = 213423;

        final Handler control = new Handler();
        control.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                if (Integer.parseInt(points.getText().toString()) < pointsTo){
                    points.setText("" + (Integer.parseInt(points.getText().toString()) + 179));
                    control.postDelayed(this, 1);
                } else {
                    
                }
            }
        }, 1);
    }

    @Override
    protected void onResume() {

        caloriesBar.animate().scaleYBy(8).setDuration(500).withEndAction(new Runnable() {
            
            @Override
            public void run() {
                heartBar.animate().scaleYBy(3).setDuration(500).withEndAction(new Runnable() {
                    
                    @Override
                    public void run() {
                        pollution.animate().scaleYBy(4).setDuration(500).withEndAction(new Runnable() {
                            
                            @Override
                            public void run() {
                               footprint.animate().scaleYBy(9).setDuration(500).withEndAction(new Runnable() {

                                @Override
                                public void run() {
                                    distanceBar.animate().scaleYBy(10).setDuration(500).withEndAction(new Runnable() {
                                        
                                        @Override
                                        public void run() {
                                            postMessageOnWall("I`ve just cycle with BreathIn and made " + bundleDistance + 
                                                    " and burned " + caloriesText.getText().toString() + "\n I´ve got " +
                                                    points.getText().toString() + " points to get cinema tickets and other stuff!");
                                        }
                                    });
                                }
                                });
                            }
                        });
                    }
                });
            }
        });
        super.onResume();
    }
    
    public void postMessageOnWall(final String msg) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                if (facebook.isSessionValid()) {
                    Bundle parameters = new Bundle();
                    parameters.putString("message", msg);
                    parameters.putString("picture", "http://www.resourcefulus.com/wp-content/uploads/2011/10/girl-chic-bike-riding.jpg");
                    parameters.putString("link", "http://elchudi.xen.prgmr.com/breath.html");
                    try {
                        String response = facebook.request("me/feed", parameters,"POST");
                        Log.d("MI_DEP", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("MI_DEP","FAIL TOKEN");
                }
                return null;
            }
            
        }.execute();
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
