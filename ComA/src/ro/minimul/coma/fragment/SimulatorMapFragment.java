package ro.minimul.coma.fragment;

import java.util.Calendar;
import java.util.Locale;
import ro.minimul.coma.R;
import ro.minimul.coma.routes.RouteUnit;
import ro.minimul.coma.routes.Segment;
import ro.minimul.coma.routes.SegmentsWalker;
import ro.minimul.coma.routes.Station;
import ro.minimul.coma.routes.TransportJsonData;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class SimulatorMapFragment extends MapFragment {
    private static final int UPDATE_INTERVAL = 1000;
    private GoogleMap map;
    private RouteUnit routeUnit;
    private TransportJsonData tjd;
    private Segment[] segments;
    private Marker trainMarker;
    private Handler handler;
    private volatile boolean ended = false;
    private SegmentsWalker segmentsWalker = null;
    private boolean isLocked = false;
    
    private Runnable updateTrainRunnable = new Runnable() {
        @Override
        public void run() {
            updateTrainPosition();
            if (!ended) {
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View ret = super.onCreateView(inflater, container, savedInstanceState);
        
        map = getMap();
        tjd = TransportJsonData.getGlobalInstance();
        
        return ret;
    }

    @Override
    public void onDestroy() {
        ended = true;
        super.onDestroy();
    }

    public void setRoute(RouteUnit routeUnit) {
        this.routeUnit = routeUnit;
        
        displayRouteUnit();
    }
    
    public void lock(boolean lock) {
        this.isLocked = lock;
    }
    
    private void displayRouteUnit() {
        int[] sts = routeUnit.transport.sts;
        Calendar[] deps = routeUnit.transport.deps;
        Calendar[] arrs = routeUnit.transport.arrs;
        Calendar dep;
        Calendar arr;
        
        for (int i = 0; i < sts.length; i++) {
            int s = sts[i];
            Station station = tjd.stations.get(s);
            LatLng latLng = new LatLng(station.lat, station.lng);
            dep = (i + 1 < sts.length) ? deps[i] : null;
            arr = (i > 0) ? arrs[i - 1] : null;
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet(getSnippet(dep, arr))
                    .title((i+1) + ". " + station.name));
        }
        
        markLines();
        
        centerCamera();

        trainMarker = map.addMarker(new MarkerOptions()
                .position(segments[0].points[0])
                .title("Train")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.small_train)));
        
        handler = new Handler();
        handler.postDelayed(updateTrainRunnable, UPDATE_INTERVAL);
    }
    
    private void markLines() {
        segments = Segment.getSegmentsFrom(routeUnit, tjd);
        
        for (Segment s : segments) {
            PolylineOptions lineOpts = new PolylineOptions();
            lineOpts.add(s.points);
            lineOpts.color(0xFF9999FF);
            lineOpts.width(5);
            map.addPolyline(lineOpts);
        }
    }
    
    private void centerCamera() {
        final LatLngBounds bounds = Segment.getBounds(segments);
        
        map.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                map.setOnCameraChangeListener(null);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
            }
        });
    }

    // <CHEATING>
    private int startSecondOfDay = Segment.secondOfDay(Calendar.getInstance());
    
    private void updateTrainPosition() {
        int delta = Segment.secondOfDay(Calendar.getInstance()) - startSecondOfDay;
        int secondOfDay = segments[0].departure + delta + 50;
        // </CHEATING>
        
        if (segmentsWalker == null) {
            segmentsWalker = new SegmentsWalker(segments, secondOfDay);
        }
        
        LatLng newPos = segmentsWalker.getPositionAt(secondOfDay);

        trainMarker.setPosition(newPos);
        
        if (isLocked) {
            map.moveCamera(CameraUpdateFactory.newLatLng(newPos));
        }
    }
    
    private String getSnippet(Calendar departs, Calendar arrives) {
        String ret = "";
        
        if (departs != null) {
            ret = "Departs: " + time(departs);
        }
        
        if (arrives != null) {
            if (departs != null) {
                ret += "\n";
            }
            
            ret += "Arrives: " +time(arrives);
        }
        
        return ret;
    }
    
    private String time(Calendar c) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));
    }
}
