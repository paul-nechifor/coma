package ro.minimul.coma.stats;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
import com.google.android.gms.maps.model.LatLng;

public class LocationAggregator {
    public static final long MAX_TIME = Integer.MAX_VALUE * 1000L;
    
    public static class LPoint {
        public final long time;
        public final double latitude;
        public final double longitude;
        public final float accuracy;
        private LatLng latLng = null;
        
        public LPoint(long time, double latitude, double longitude,
                float accuracy) {
            this.time = time;
            this.latitude = latitude;
            this.longitude = longitude;
            this.accuracy = accuracy;
        }
        
        public LatLng getLatLng() {
            if (latLng == null) {
                latLng = new LatLng(latitude, longitude);
            }
            
            return latLng;
        }
    }
    
    private static class LPointComparator implements Comparator<LPoint> {
        @Override
        public int compare(LPoint l, LPoint r) {
            if (l.time < r.time) {
                return -1;
            } else if (l.time > r.time) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
    private final File parentDir;
    private LinkedList<LPoint> tempList;
    private LPoint[] list;
    
    public LocationAggregator(File parentDir) {
        this.parentDir = parentDir;
    }
    
    public void loadData() throws IOException {
        list = null;
        tempList = new LinkedList<LPoint>();

        // Reading all the data files and filling the points.
        for (File dataFile : parentDir.listFiles()) {
            loadDataFile(dataFile);
        }
        
        list = new LPoint[tempList.size()];

        // Transferring all the points to an array.
        int k = 0;
        for (LPoint p : tempList) {
            list[k] = p;
            k++;
        }
        
        // Clearing the list.
        tempList.clear();
        tempList = null;
        
        // Sorting the array by time.
        Arrays.sort(list, new LPointComparator());
    }
    
    public LPoint[] getList() {
        return list;
    }
    
    public int getIndexClosestTo(long time) {
        int index = -1;
        long indexAbsDiff = Long.MAX_VALUE;
        LPoint p;
        long absDiff;
        
        for (int i = 0; i < list.length; i++) {
            p = list[i];
            absDiff = Math.abs(time - p.time);
            if (absDiff < indexAbsDiff) {
                index = i;
                indexAbsDiff = absDiff;
            }
        }
        
        return index;
    }
    
    public int getLeftInterval(int startIndex, long minTime) {
        int leftIndex = startIndex;
        
        for (int i = startIndex - 1; i >= 0; i--) {
            if (list[i].time < minTime) {
                break;
            }
            leftIndex = i;
        }
        
        return leftIndex;
    }
    
    public int getRightInterval(int startIndex, long maxTime) {
        int rightIndex = startIndex;
        
        for (int i = startIndex + 1; i < list.length; i++) {
            if (list[i].time > maxTime) {
                break;
            }
            rightIndex = i;
        }
        
        return rightIndex;
    }
    
    private void loadDataFile(File file) throws IOException {
        Scanner in = new Scanner(file);
        String line;
        String[] numbers;
        LPoint point;
        
        while (in.hasNext()) {
            line = in.nextLine().trim();
            if (line.length() == 0) {
                continue;
            }
            
            numbers = line.split(",");
            if (numbers.length != 4) {
                continue;
            }
            
            try {
                point = new LPoint(
                    Long.parseLong(numbers[0]),
                    Double.parseDouble(numbers[1]),
                    Double.parseDouble(numbers[2]),
                    Float.parseFloat(numbers[3])
                );
                
                if (point.time > MAX_TIME || point.accuracy > 16) {
                    continue;
                }
                
                tempList.add(point);
            } catch (NumberFormatException ex) {
                // Ignore.
            }
        }
    }
}
