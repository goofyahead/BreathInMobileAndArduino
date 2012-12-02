package com.example.breathin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
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
        
        String distance = getIntent().getExtras().getString(DISTANCE);

        String numericDistance = distance.split(" ")[0];
        if (distance.split(" ")[1].equals("m")){
            unityMeasure = "meters";
        } else {
            unityMeasure = "kilometers";
        }
        int realDistance = (int) Float.parseFloat(numericDistance);
        Log.d("MI_DEP","numeric distance: " + numericDistance);
        
        if (unityMeasure.equals("kilometers")){
            realDistance = realDistance * 1000;
        }
        
        distanceText.setText(distance);
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
                }
            }
        }, 1);
    }

    @Override
    protected void onResume() {

        caloriesBar.animate().scaleYBy(11).setDuration(500).withEndAction(new Runnable() {
            
            @Override
            public void run() {
                heartBar.animate().scaleYBy(6).setDuration(500).withEndAction(new Runnable() {
                    
                    @Override
                    public void run() {
                        pollution.animate().scaleYBy(8).setDuration(500).withEndAction(new Runnable() {
                            
                            @Override
                            public void run() {
                               footprint.animate().scaleYBy(13).setDuration(500).withEndAction(new Runnable() {

                                @Override
                                public void run() {
                                    distanceBar.animate().scaleYBy(11).setDuration(500);
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
}
