package ro.minimul.coma.routes;

import java.io.Serializable;
import java.util.Calendar;
import ro.minimul.coma.prefs.AppPrefs;
import ro.minimul.coma.util.NamedLatLng;
import com.google.android.gms.maps.model.LatLng;

public class Route implements Serializable {
    private static final long serialVersionUID = 8769437991581756094L;
    
    private Route() {
    }
    
    private String id;
    private boolean added = false;
    private final NamedLatLng from = new NamedLatLng();
    private final NamedLatLng to = new NamedLatLng();
    private boolean repeatWeekly = true;
    private Calendar arrival = Calendar.getInstance();
    private final boolean[] repeatingDays = new boolean[7];
    private RouteData routeData;
    
    public static Route newEmptyRoute() {
        Route ret = new Route();
        
        AppPrefs prefs = AppPrefs.getGlobalInstance();
        
        LatLng last = prefs.getLastInputLocationLatLng();
        
        ret.from.setLatLng(last);
        ret.to.setLatLng(last);
        
        ret.repeatingDays[0] = true;
        
        return ret;
    }
    
    public void setAddedId(String id) {
        this.id = id;
        this.added = true;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isAdded() {
        return added;
    }
    
    public NamedLatLng getFrom() {
        return from;
    }
    
    public NamedLatLng getTo() {
        return to;
    }
    
    public boolean isRepeatingWeekly() {
        return repeatWeekly;
    }
    
    public void setRepeatingWeekly(boolean value) {
        this.repeatWeekly = value;
    }
    
    public Calendar getArrival() {
        return arrival;
    }
    
    public boolean[] getRepeatingDays() {
        return repeatingDays;
    }
    
    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
    }
    
    public RouteData getRouteData() {
        return this.routeData;
    }
}
