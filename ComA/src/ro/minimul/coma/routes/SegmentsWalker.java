package ro.minimul.coma.routes;

import ro.minimul.coma.util.Util;
import com.google.android.gms.maps.model.LatLng;


public class SegmentsWalker {
    private Segment[] segments;
    private int segmentIndex;
    
    private double[] currentDistances;
    private double distancesSum;
    private double timeDeltaSum;
    
    public SegmentsWalker(Segment[] segments, int secondOfDay) {
        this.segments = segments;
        
        loadInitial(secondOfDay);
    }
    
    public LatLng getPositionAt(int secondOfDay) {
        if (segmentIndex == -1) {
            return Segment.getLast(segments);
        }
        
        
        Segment s = segments[segmentIndex];
        
        if (secondOfDay >= s.arrival) {
            // If this is the last station, return that position.
            if (segmentIndex + 1 == segments.length) {
                segmentIndex = -1;
                return Segment.getLast(segments);
            }
            
            // If it is time to depart from the next station...
            if (secondOfDay >= segments[segmentIndex + 1].departure) {
                segmentIndex++;
                loadCurrentSegmentDistances();
                return getPositionAt(secondOfDay);
            }
            
            // This means the train is pausing in this station.
            return s.points[s.points.length - 1];
        }
        
        int deltaFromStart = secondOfDay - s.departure;
        double ratio = deltaFromStart / timeDeltaSum;
        double distanceFromStart = distancesSum * ratio;
        
        double passed = 0.0;
        double newPassed;
        for (int i = 0; i < currentDistances.length; i++) {
            newPassed = passed + currentDistances[i];
            if (newPassed >= distanceFromStart) {
                double segmentPos = distanceFromStart - passed;
                double segmentRatio = segmentPos / currentDistances[i];
                return atPosition(s.points[i], s.points[i + 1], segmentRatio);
            }
            
            passed = newPassed;
        }
        
        return Segment.getLast(segments);
    }
    
    private LatLng atPosition(LatLng a, LatLng b, double ratio) {
        double dLat = b.latitude - a.latitude;
        double dLng = b.longitude - a.longitude;
        double newLat = a.latitude + dLat * ratio;
        double newLng = a.longitude + dLng * ratio;
        return new LatLng(newLat, newLng);
    }
    
    private void loadInitial(int secondOfDay) {
        Segment s;
        int startTime;
        int endTime;

        for (int i = 0; i < segments.length; i++) {
            s = segments[i];
            startTime = s.departure;
            
            if (i + 1 < segments.length) {
                endTime = segments[i + 1].departure;
            } else {
                endTime = s.arrival;
            }
            
            boolean isIn = secondOfDay >= startTime && secondOfDay < endTime;
            boolean isIn2 = endTime < startTime && secondOfDay >= startTime;
            
            if (isIn || isIn2) {
                segmentIndex = i;
                loadCurrentSegmentDistances();
                return;
            }
        }
        
        segmentIndex = -1;
    }
    
    private void loadCurrentSegmentDistances() {
        Segment s = segments[segmentIndex];
        LatLng[] points = s.points;
        LatLng a, b;
        currentDistances = new double[points.length - 1];
        distancesSum = 0;
        
        for (int i = 0; i < currentDistances.length; i++) {
            a = points[i];
            b = points[i + 1];
            
            currentDistances[i] = Util.distFrom(a.latitude, a.longitude,
                    b.latitude, b.longitude);
            distancesSum += currentDistances[i];
        }
        
        timeDeltaSum = s.arrival - s.departure;
    }
}
