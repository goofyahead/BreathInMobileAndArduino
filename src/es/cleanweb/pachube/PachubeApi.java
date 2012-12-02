package es.cleanweb.pachube;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

public class PachubeApi {
    static final String PACHUBE_URL = "http://api.cosm.com/v2/feeds/";
//    static final String FEED_ID = "89392";//GUS
    static final String FEED_ID = "89389";//ALE
    static final String API_KEY = "bG6AVQou1cNpqkVK1PCuHY1aTISSAKxHdVZBVzVLQlgrMD0g";
    static final String HEADEAR_API_KEY = "X-ApiKey";
    
    public static void updateFeed(JSONObject json) throws IOException{
        Log.d("MI_DEP", "updating feed in pachube...................................");
        Log.d("MI_DeP", "to: " + PACHUBE_URL + FEED_ID);
        URL url = new URL(PACHUBE_URL + FEED_ID);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.addRequestProperty(HEADEAR_API_KEY, API_KEY);
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(
            httpCon.getOutputStream());
        out.write(json.toString());
        out.close();
        String response = httpCon.getResponseMessage();
        Log.d("MI_DEP","response: " + response);
    }

}
