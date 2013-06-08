package ro.minimul.coma.routes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ro.minimul.coma.R;
import ro.minimul.coma.util.Util;
import android.content.Context;
import android.os.AsyncTask;

public class TransportJsonData {
    private static TransportJsonData GLOBAL_INSTANCE;
    
    private static class LoadTask extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(Context... params) {
            Context context = params[0];
            try {
                GLOBAL_INSTANCE.edges = getEdges(context);
                GLOBAL_INSTANCE.stations = getStations(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
    
    public Map<Integer, Station> stations;
    public Map<String, float[]> edges;
    
    public static Map<String, float[]> getEdges(Context context)
            throws IOException, JSONException {
        Map<String, float[]> ret = new HashMap<String, float[]>();
        
        InputStream is = context.getResources().openRawResource(R.raw.edges);
        
        String json = Util.readInputStream(is);
        JSONObject obj = new JSONObject(json);
        
        @SuppressWarnings("unchecked")
        Iterator<String> iter = obj.keys();
        
        while (iter.hasNext()) {
            String key = iter.next();
            JSONArray array = (JSONArray) obj.get(key);
            float[] fArray = new float[array.length()];
            for (int i = 0; i < fArray.length; i++) {
                fArray[i] = (float) array.getDouble(i);
            }
            ret.put(key, fArray);
        }
        
        return ret;
    }
    
    public static Map<Integer, Station> getStations(Context context)
            throws IOException, JSONException {
        Map<Integer, Station> ret = new HashMap<Integer, Station>();
        
        InputStream is = context.getResources().openRawResource(R.raw.stations);
        
        String json = Util.readInputStream(is);
        JSONArray array = new JSONArray(json);
        int id;
        JSONArray item;
        
        for (int i = 0, len = array.length(); i < len; i++) {
            item = array.getJSONArray(i);
            id = item.getInt(0);
            ret.put(id, new Station(
                    id,
                    item.getString(1),
                    item.getDouble(3),
                    item.getDouble(2)));
        }
        
        return ret;
    }
    
    public static void loadGlobalInstance(Context context) {
        if (GLOBAL_INSTANCE != null) {
            return;
        }
        
        GLOBAL_INSTANCE = new TransportJsonData();
        new LoadTask().execute(context);
    }
    
    public static TransportJsonData getGlobalInstance() {
        return GLOBAL_INSTANCE;
    }
}
