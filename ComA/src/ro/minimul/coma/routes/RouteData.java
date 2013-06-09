package ro.minimul.coma.routes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
        
        Calendar lastTime = Calendar.getInstance();
        
        for (Transport t : transports) {
            if (t.mean == 0) {
                list.add(generateFakeUnit(t, lastTime));
            } else {
                list.add(generateFakeNonTrainUnit(t, lastTime));
            }
        }
        
        routeUnits = list.toArray(new RouteUnit[0]);
    }
    
    private RouteUnit generateFakeUnit(Transport t, Calendar lastTime) {
        RouteUnit ret = new RouteUnit();
        
        ret.routeData = this;
        ret.transport = t;
        
        ret.startStation = 0;
        ret.endStation = t.sts.length - 1;
        
        ret.startTime = t.deps[0];
        ret.endTime = t.arrs[t.arrs.length - 1];
        
        ret.setNames();
        
        lastTime.setTime(ret.endTime.getTime());
        copyCalendar(lastTime, ret.endTime, 0);
        
        return ret;
    }
    
    private RouteUnit generateFakeNonTrainUnit(Transport t, Calendar lastTime) {
        RouteUnit ret = new RouteUnit();
        
        ret.routeData = this;
        ret.transport = t;
        
        ret.startStation = 0;
        ret.endStation = (int) (Math.random() * 4 + 1);
        
        ret.startTime = Calendar.getInstance();
        ret.endTime = Calendar.getInstance();

        ret.setNames();
        
        copyCalendar(ret.startTime, lastTime, randomMillis(5*60, 15*60));
        copyCalendar(ret.endTime, ret.startTime, randomMillis(10*60, 30*60));
        
        copyCalendar(lastTime, ret.endTime, 0);
        
        return ret;
    }
    
    private void copyCalendar(Calendar to, Calendar from, long delta) {
        long milliseconds = from.getTimeInMillis() + delta;
        to.setTimeInMillis(milliseconds);
    }
    
    private long randomMillis(int minSec, int maxSec) {
        double r = Math.random() * (maxSec - minSec) + minSec;
        return (long) (r * 1000);
    }
}
