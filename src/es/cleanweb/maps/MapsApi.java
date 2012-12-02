package es.cleanweb.maps;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import es.cleanweb.model.Coords;

public class MapsApi {
    static final String API_URL = "http://maps.googleapis.com/maps/api/directions/json?";
    private static final String ORIGIN_LAT = "{ORIGIN_LAT}";
    private static final String ORIGIN_LON = "{ORIGIN_LON}";
    private static final String DEST_LAT = "{DEST_LAT}";
    private static final String DEST_LON = "{DEST_LON}";
    static final String ORIGIN_DEST = "origin=" + ORIGIN_LAT + "," + ORIGIN_LON + "&destination=" + DEST_LAT + ","
            + DEST_LON + "&sensor=false&units=metric";
    static final String WAYPOINTS = "&waypoints=";
    static final String WAYPOINT_JOIN = "|";

    public static JSONObject getRoute(List<Coords> waypoints, Coords origin, Coords destiny) {
        try {
            HttpClient client = new DefaultHttpClient();
            String getURL = API_URL + ORIGIN_DEST;
            getURL = getURL.replace(ORIGIN_LAT, "" + origin.getLatitude());
            getURL = getURL.replace(ORIGIN_LON, "" + origin.getLongitude());
            getURL = getURL.replace(DEST_LAT, "" + destiny.getLatitude());
            getURL = getURL.replace(DEST_LON, "" + destiny.getLongitude());
            if (waypoints.size() > 0) {
                getURL = getURL + WAYPOINTS;
            }
            for (Coords coord : waypoints) {
                getURL = getURL + coord.getLatitude() + "," + coord.getLatitude();
                if (waypoints.get(waypoints.size() - 1) != coord)
                    getURL = getURL + WAYPOINT_JOIN;
            }
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                return new JSONObject(EntityUtils.toString(resEntityGet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
