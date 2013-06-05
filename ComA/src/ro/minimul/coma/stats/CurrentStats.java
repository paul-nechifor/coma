package ro.minimul.coma.stats;

import java.io.Serializable;
import ro.minimul.coma.stats.LocationAggregator.LPoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class CurrentStats implements Serializable {
    private static final long serialVersionUID = -8456524417153928951L;

    private double southwestLat;
    private double southwestLng;
    private double northeastLat;
    private double northeastLng;
    private long startTime;
    private long endTime;
    
    private transient LatLngBounds travelBounds;
    
    private CurrentStats() {
    }
    
    private void computeFrom(LPoint[] points) {
        double minLat = points[0].latitude;
        double maxLat = points[0].latitude;
        double minLng = points[0].longitude;
        double maxLng = points[0].longitude;
        
        for (LPoint p : points) {
            if (p.accuracy <= 16.0) {
                if (p.latitude < minLat) {
                    minLat = p.latitude;
                }
                if (p.latitude > maxLat) {
                    maxLat = p.latitude;
                }
                if (p.longitude < minLng) {
                    minLng = p.longitude;
                }
                if (p.longitude > maxLng) {
                    maxLng = p.longitude;
                }
            }
        }

        southwestLat = minLat;
        southwestLng = minLng;
        northeastLat = maxLat;
        northeastLng = maxLng;
        
        startTime = points[0].time;
        endTime = points[points.length - 1].time;
    }
    
    public static CurrentStats compute(LocationAggregator aggregator) {
        CurrentStats ret = new CurrentStats();
        ret.computeFrom(aggregator.getList());
        ret.restoreTransientFields();
        return ret;
    }
    
    public void restoreTransientFields() {
        travelBounds = new LatLngBounds(
                new LatLng(southwestLat, southwestLng),
                new LatLng(northeastLat, northeastLng));
    }
    
    public LatLngBounds getTravelBounds() {
        return this.travelBounds;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
}
