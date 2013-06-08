package ro.minimul.coma.routes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Segment {
    public LatLng[] points;
    public int arrival;
    public int departure;
    
    public static Segment[] getSegmentsFrom(RouteUnit routeUnit, TransportJsonData tjd) {
        int[][] edgess = routeUnit.transport.edges;
        Segment[] segments = new Segment[edgess.length - 1];
        Calendar[] arrs = routeUnit.transport.arrs;
        Calendar[] deps = routeUnit.transport.deps;
        
        for (int i = 0; i < segments.length; i++) {
            Segment segment = new Segment();
            segment.points = displayLine(edgess[i + 1], tjd);
            segment.departure = secondOfDay(deps[i]);
            segment.arrival = secondOfDay(arrs[i]);
            segments[i] = segment;
        }
        
        return segments;
    }
    
    private static LatLng[] displayLine(int[] edges, TransportJsonData tjd) {
        List<LatLng> list = new ArrayList<LatLng>();
        
        for (int id : edges) {
            addLinePart(list, Math.abs(id), id < 0, tjd);
        }
        
        return list.toArray(new LatLng[0]);
    }
    
    private static void addLinePart(List<LatLng> list, int id,
            boolean reverse, TransportJsonData tjd) {
        float[] points = tjd.edges.get(Integer.toString(id));
        
        if (reverse) {
            for (int i = points.length - 2; i >= 0; i -= 2) {
                list.add(new LatLng(points[i + 1], points[i]));
            }
        } else {
            for (int i = 0; i < points.length; i += 2) {
                list.add(new LatLng(points[i + 1], points[i]));
            }
        }
    }
    
    public static int secondOfDay(Calendar calendar) {
        int total = 0;
        
        total += calendar.get(Calendar.HOUR_OF_DAY) * (60 * 60);
        total += calendar.get(Calendar.MINUTE) * 60;
        total += calendar.get(Calendar.SECOND);
        
        return total;
    }
    
    public static LatLngBounds getBounds(Segment[] segments) {
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLng = Double.MAX_VALUE;
        double maxLng = Double.MIN_VALUE;
        
        for (Segment s : segments) {
            for (LatLng l : s.points) {
                if (l.latitude < minLat) {
                    minLat = l.latitude;
                }
                if (l.latitude > maxLat) {
                    maxLat = l.latitude;
                }
                if (l.longitude < minLng) {
                    minLng = l.longitude;
                }
                if (l.longitude > maxLng) {
                    maxLng = l.longitude;
                }
            }
        }
        
        LatLng southwest = new LatLng(minLat, minLng);
        LatLng northeast = new LatLng(maxLat, maxLng);
        
        return new LatLngBounds(southwest, northeast);
    }
    
    public static LatLng getLast(Segment[] segments) {
        LatLng[] points = segments[segments.length - 1].points;
        return points[points.length - 1];
    }
    
    public static LatLng getFirst(Segment[] segments) {
        return segments[0].points[0];
    }
}
