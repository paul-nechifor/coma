package ro.minimul.coma.routes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RouteData implements Serializable {
    private static final long serialVersionUID = 4469255474389750103L;
    
    public Transport[] transports;
    public RouteUnit[] routeUnits;
    
    public static RouteData getFromJsonObject(JSONObject obj)
            throws JSONException {
        RouteData ret = new RouteData();
        
        JSONArray transports = obj.getJSONArray("transports");
        ret.transports = Transport.getFromJsonArray(transports);
        
        ret.generateFakeData();
        
        return ret;
    }
    
    private void generateFakeData() {
        List<RouteUnit> list = new ArrayList<RouteUnit>();
        
        for (Transport t : transports) {
            if (t.mean == 0) {
                list.add(generateFakeUnit(t));
            } else {
                list.add(generateFakeNonTrainUnit(t));
            }
        }
        
        routeUnits = list.toArray(new RouteUnit[0]);
    }
    
    private RouteUnit generateFakeUnit(Transport t) {
        RouteUnit ret = new RouteUnit();
        
        ret.routeData = this;
        ret.transport = t;
        
        ret.startStation = 0;
        ret.endStation = t.sts.length - 1;
        
        return ret;
    }
    
    private RouteUnit generateFakeNonTrainUnit(Transport t) {
        RouteUnit ret = new RouteUnit();
        
        ret.routeData = this;
        ret.transport = t;
        
        ret.startStation = 0;
        ret.endStation = 0; // 8888888888888888888888888888888888888888888888888888888888888888888888888888888888888 TODO
        
        return ret;
    }
}
