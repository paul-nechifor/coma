package ro.minimul.coma.routes;

import java.io.Serializable;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ro.minimul.coma.R;
import ro.minimul.coma.util.Util;

public class Transport implements Serializable {
    private static final long serialVersionUID = -4873445752057350787L;

    public static enum Mean {
        TRAIN(R.drawable.normal_train, R.drawable.small_train),
        TRAM(R.drawable.normal_tram, R.drawable.small_tram),
        SUBWAY(R.drawable.normal_subway, R.drawable.small_subway),
        BUS(R.drawable.normal_bus, R.drawable.small_bus),
        FOOT(R.drawable.normal_foot, R.drawable.small_foot);
        
        private int bigId;
        private int smallId;
        
        private Mean(int bigId, int smallId) {
            this.bigId = bigId;
            this.smallId = smallId;
        }
        
        public int getBigId() {
            return bigId;
        }
        
        public int getSmallId() {
            return smallId;
        }
    }
    
    public String name;
    public int[][] edges;
    public Calendar[] arrs;
    public Calendar[] deps;
    public int[] sts;
    public String type;
    public int id;
    public int mean;
    public Mean meanType;
    
    public static Transport getFromJsonObject(JSONObject obj)
            throws JSONException {
        Transport ret = new Transport();
        
        ret.id = obj.getInt("id");
        ret.name = obj.getString("name");
        
        JSONArray edges = Util.getJsonArray(obj, "edges");
        if (edges != null) {
            ret.edges = new int[edges.length()][];
            for (int i = 0; i < ret.edges.length; i++) {
                String pp = edges.getString(i);
                if (pp.isEmpty()) {
                    ret.edges[i] = new int[0];
                    continue;
                }
                String[] p = pp.split(",");
                ret.edges[i] = new int[p.length];
                for (int j = 0; j < p.length; j++) {
                    ret.edges[i][j] = Integer.parseInt(p[j]);
                }
            }
        }
        
        JSONArray arrs = Util.getJsonArray(obj, "arrs");
        if (arrs != null) {
            ret.arrs = fromArray(arrs);
        }
        
        JSONArray deps = Util.getJsonArray(obj, "deps");
        if (deps != null) {
            ret.deps = fromArray(deps);
        }
        
        JSONArray sts = Util.getJsonArray(obj, "sts");
        if (sts != null) {
            ret.sts = new int[sts.length()];
            for (int i = 0; i < ret.sts.length; i++) {
                ret.sts[i] = Integer.parseInt(sts.getString(i));
            }
        }
        
        ret.type = Util.getString(obj, "type");
        ret.mean = obj.getInt("mean");
        ret.meanType = Mean.values()[ret.mean];
        
        return ret;
    }
    
    public static Transport[] getFromJsonArray(JSONArray array)
            throws JSONException {
        Transport[] ret = new Transport[array.length()];
        
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getFromJsonObject(array.getJSONObject(i));
        }
        
        return ret;
    }
    
    private static Calendar[] fromArray(JSONArray a) throws JSONException {
        Calendar[] ret = new Calendar[a.length()];
        
        for (int i = 0; i < ret.length; i++) {
            String[] p = ((String) a.get(i)).split(":");
            ret[i] = Calendar.getInstance();
            ret[i].set(Calendar.HOUR_OF_DAY, Integer.parseInt(p[0]));
            ret[i].set(Calendar.MINUTE, Integer.parseInt(p[1]));
            ret[i].set(Calendar.SECOND, Integer.parseInt(p[2]));
        }
        
        return ret;
    }
}
