package ro.minimul.coma.fragment;

import java.io.File;
import java.io.IOException;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.prefs.AppPrefs;
import ro.minimul.coma.service.ComaService;
import ro.minimul.coma.stats.CurrentStats;
import ro.minimul.coma.stats.LocationAggregator;
import ro.minimul.coma.stats.LocationAggregator.LPoint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class HistoryMapFragment extends MapFragment {
    @SuppressWarnings("unused")
    private static final CancelableCallback DUMMY_CANCELABLE_CALLBACK
            = new CancelableCallback() {
        @Override
        public void onCancel() {
        }

        @Override
        public void onFinish() {
        }
    };
    
    private static final long UPDATE_EVERY = 1000 * 60 * 30; // 30 minutes.
    
    private static class DrawLineParam {
        public long time;
        public long interval;
        
        public DrawLineParam(long time, long interval) {
            this.time = time;
            this.interval = interval;
        }
    }
    
    private static class DrawLineResults {
        public PolylineOptions lineOpt;
        public LatLng centerPosition;
    }
    
    private class DrawLineTask extends AsyncTask<DrawLineParam, Void,
            DrawLineResults> {
        @Override
        protected DrawLineResults doInBackground(DrawLineParam... params) {
            updateAggregator();
            
            DrawLineResults results = computeDrawLineResults(params[0]);
            
            return results;
        }
        
        protected void onPostExecute(DrawLineResults results) {
            drawResults(results);
        }
    }
    
    private GoogleMap map;
    private MainActivity mainActivity;
    private Polyline lastLine = null;
    private DrawLineTask drawLineTask;
    private LocationAggregator aggregator = null;
    private long lastUpdatedAggregator = 0;
    private Marker lastMarker;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View ret = super.onCreateView(inflater, container, savedInstanceState);
        
        map = getMap();
        mainActivity = (MainActivity) getActivity();
        
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();
        
//        // Hack for setting the initial bounds.
//        map.setOnCameraChangeListener(new OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition arg0) {
//                CurrentStats stats = mainActivity.getPrefs().getCurrentStats();
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(
//                        stats.getTravelBounds(), 10);
//                map.animateCamera(cameraUpdate, 1, DUMMY_CANCELABLE_CALLBACK);
//                map.setOnCameraChangeListener(null);
//            }
//        });
    }
    
    public void updateTimeShown(long time, long interval) {
        if (drawLineTask != null) {
            drawLineTask.cancel(true);
        }
        
        DrawLineParam params = new DrawLineParam(time, interval);
        drawLineTask = new DrawLineTask();
        drawLineTask.execute(params);
    }
    
    
    
    private void drawResults(DrawLineResults results) {
        if (results == null) {
            return;
        }
        
        if (lastLine != null) {
            lastLine.remove();
        }
        
        lastLine = map.addPolyline(results.lineOpt);
        
        updateMarker(results.centerPosition);
        
        map.animateCamera(CameraUpdateFactory.newLatLng(
                results.centerPosition));
    }
    
    private void updateAggregator() {
        long updateTimeDelta = System.nanoTime() - lastUpdatedAggregator;
        if (aggregator == null || updateTimeDelta > UPDATE_EVERY) {
            reloadAggregatorData();
        }
    }
    
    public void reloadAggregatorData() {
        File locationDir = new File(Environment.getExternalStorageDirectory(),
                ComaService.LOCATION_DIR);
        aggregator = new LocationAggregator(locationDir);
        
        try {
            aggregator.loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        AppPrefs prefs = mainActivity.getPrefs();
        CurrentStats stats = CurrentStats.compute(aggregator);
        prefs.setCurrentStats(stats);
        
        lastUpdatedAggregator = System.nanoTime();
    }
    
    private DrawLineResults computeDrawLineResults(DrawLineParam param) {
        int index = aggregator.getIndexClosestTo(param.time);
        
        if (index == -1) {
            return null;
        }

        long leftTime = param.time - param.interval;
        long rightTime = param.time + param.interval;
        
        int leftIndex = aggregator.getLeftInterval(index, leftTime);
        int rightIndex = aggregator.getRightInterval(index, rightTime);

        LPoint[] list = aggregator.getList();
        PolylineOptions lineOpt = new PolylineOptions();
        
        for (int i = leftIndex; i <= rightIndex; i++) {
            lineOpt.add(list[i].getLatLng());
        }
        
        DrawLineResults results = new DrawLineResults();
        results.lineOpt = lineOpt;
        results.centerPosition = list[index].getLatLng();
        
        return results;
    }
    
    private void updateMarker(LatLng latLng) {
        if (lastMarker == null) {
            MarkerOptions opt = new MarkerOptions().position(latLng);
            lastMarker = map.addMarker(opt);
        } else {
            lastMarker.setPosition(latLng);
        }
    }
}
