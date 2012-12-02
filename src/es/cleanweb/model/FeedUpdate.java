package es.cleanweb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FeedUpdate {
    static final String PACHUBE_TITLE = "title";
    static final String TITLE = "BreathInSensor";
    private static final String PACHUBE_VERSION = "version";
    private static final String VERSION = "0.1.0";
    private static final String PACHUBE_DISPOSITION = "disposition";
    private static final String PACHUBE_NAME = "name";
    private static final String PACHUBE_DOMAIN = "domain";
    private static final String PACHUBE_LATITUDE = "lat";
    private static final String PACHUBE_LONGITUDE = "lon";
    private static final String PACHUBE_ELEVATION = "ele";
    private static final String PACHUBE_LOCATION = "location";
    private static final String DOMAIN = "physical";
    private static final String DISPOSITION = "mobile";
    private static final String PACHUBE_DATASTREAM_ID = "id";
    private static final String HUMIDITY = "hum";
    private static final String PACHUBE_CURRENT_VALUE = "current_value";
    private static final String TEMPERATURE = "temp";
    private static final String CO = "co";
    private static final String HEARTBEAT = "hbeat";
    private static final String PACHUBE_DATASTREAM = "datastreams";
    private static final String DB_NOISE = "db";
    private static final String MAGNETISM = "emag";
    private String latitude;
    private String longitude;
    private String humidity;
    private String temperature;
    private String co;
    private String heartBeat;
    private String userId;
    private String altitude;
    private String magnet;
    private String dbNoise;

    public FeedUpdate(String userId, String latitude, String longitude, String altitude, String humidity, String temperature, String co,
            String heartBeat, String magnet, String dbNoise) {
        super();
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.humidity = humidity;
        this.temperature = temperature;
        this.co = co;
        this.heartBeat = heartBeat;
        this.altitude = altitude;
        this.magnet = magnet;
        this.dbNoise = dbNoise;
    }
    
    public JSONObject toJson() throws JSONException{
        JSONObject current = new JSONObject();
        current.put(PACHUBE_TITLE,TITLE);
        current.put(PACHUBE_VERSION, VERSION);
        JSONObject location = new JSONObject();
        location.put(PACHUBE_DISPOSITION, DISPOSITION);
        location.put(PACHUBE_NAME, userId);
        location.put(PACHUBE_DOMAIN, DOMAIN);
        location.put(PACHUBE_LATITUDE, latitude);
        location.put(PACHUBE_LONGITUDE, longitude);
        location.put(PACHUBE_ELEVATION, altitude);
        
        current.put(PACHUBE_LOCATION, location);
        
        JSONArray datastream = new JSONArray();
        
        JSONObject humidityJson = new JSONObject();
        humidityJson.put(PACHUBE_DATASTREAM_ID, HUMIDITY);
        humidityJson.put(PACHUBE_CURRENT_VALUE, humidity);
        
        datastream.put(humidityJson);
        
        JSONObject temperatureJson = new JSONObject();
        temperatureJson.put(PACHUBE_DATASTREAM_ID, TEMPERATURE);
        temperatureJson.put(PACHUBE_CURRENT_VALUE, temperature);
        
        datastream.put(temperatureJson);
        
        JSONObject coJson = new JSONObject();
        coJson.put(PACHUBE_DATASTREAM_ID, CO);
        coJson.put(PACHUBE_CURRENT_VALUE, co);
        
        datastream.put(coJson);
        
        JSONObject heartBeatJson = new JSONObject();
        heartBeatJson.put(PACHUBE_DATASTREAM_ID, HEARTBEAT);
        heartBeatJson.put(PACHUBE_CURRENT_VALUE, heartBeat);
        
        datastream.put(heartBeatJson);
        
        JSONObject dbNoiseJson = new JSONObject();
        dbNoiseJson.put(PACHUBE_DATASTREAM_ID, DB_NOISE);
        dbNoiseJson.put(PACHUBE_CURRENT_VALUE, dbNoise);
        
        datastream.put(dbNoiseJson);
        
        JSONObject magnetismJson = new JSONObject();
        magnetismJson.put(PACHUBE_DATASTREAM_ID, MAGNETISM);
        magnetismJson.put(PACHUBE_CURRENT_VALUE, dbNoise);
        
        datastream.put(magnetismJson);
        
        current.put(PACHUBE_DATASTREAM, datastream);
       
        return current;
    }

}
