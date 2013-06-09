package ro.minimul.coma.routes;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

public class RouteUnit implements Serializable {
    private static final long serialVersionUID = -1709726911548037282L;
    
    public RouteUnit() {
    }
    
    public RouteData routeData;
    public Transport transport;
    
    public int startStation;
    public int endStation;
    public Calendar startTime;
    public Calendar endTime;
    public String startStationName;
    public String endStationName;
    
    public void setNames() {
        startStationName = getStation(startStation);
        endStationName = getStation(endStation);
    }
    
    public String getStartStation() {
        return startStationName;
    }
    
    public String getEndStation() {
        return endStationName;
    }
    
    private String getStation(int index) {
        if (transport.mean != 0) {
            return nondescriptName(index);
        }
        
        TransportJsonData tjd = TransportJsonData.getGlobalInstance();
        if (tjd == null) {
            return nondescriptName(index);
        }
        
        Map<Integer, Station> stations = tjd.stations;
        Station station = stations.get(transport.sts[index]);
        
        return station.name + " station";
    }
    
    private String nondescriptName(int n) {
        return "Station " + (n + 1);
    }
}
