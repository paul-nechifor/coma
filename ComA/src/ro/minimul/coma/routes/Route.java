package ro.minimul.coma.routes;

import java.io.Serializable;
import java.util.Calendar;
import ro.minimul.coma.util.NamedLatLng;

public class Route implements Serializable {
    private static final long serialVersionUID = 8769437991581756094L;
    
    private String id;
    private boolean added = false;
    private final NamedLatLng from = new NamedLatLng();
    private final NamedLatLng to = new NamedLatLng();
    private boolean repeatWeekly = true;
    private Calendar arrival = Calendar.getInstance();
    private final boolean[] repeatingDays = new boolean[7];
    
    public static Route newEmptyRoute() {
        return new Route();
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
    
    public Calendar getArrival() {
        return arrival;
    }
    
    public boolean[] getRepeatingDays() {
        return repeatingDays;
    }
}
