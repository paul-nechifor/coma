package ro.minimul.coma.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMapFragment extends MapFragment {
    private Marker marker;
    private GoogleMap map;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View ret = super.onCreateView(inflater, container, savedInstanceState);
        
        map = getMap();
        
        marker = map.addMarker(new MarkerOptions()
            .position(new LatLng(0, 0)));
        
        map.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                marker.setPosition(point);
            }
        });
        
        return ret;
    }
    
    public LatLng getLastPosition() {
        return marker.getPosition();
    }
    
    public void setLocation(LatLng location) {
        marker.setPosition(location);
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
