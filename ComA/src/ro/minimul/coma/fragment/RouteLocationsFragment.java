package ro.minimul.coma.fragment;

import ro.minimul.coma.R;
import ro.minimul.coma.activity.ChooseLocationActivity.OnLocationChoosenCallback;
import ro.minimul.coma.activity.RouteSelectionActivity;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.util.MapUtils;
import ro.minimul.coma.util.NamedLatLng;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;

public class RouteLocationsFragment extends Fragment {
    private RouteSelectionActivity activity;
    private Route route;
    private ImageView fromMapIv;
    private TextView fromAddressTv;
    private ImageView toMapIv;
    private TextView toAddressTv;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_route_locations,
                container, false);
        
        activity = (RouteSelectionActivity) getActivity();
        route = activity.getRoute();
        
        fromMapIv = (ImageView) ret.findViewById(R.id.fromMapIv);
        fromAddressTv = (TextView) ret.findViewById(R.id.fromAddressTv);
        toMapIv = (ImageView) ret.findViewById(R.id.toMapIv);
        toAddressTv = (TextView) ret.findViewById(R.id.toAddressTv);
        
        addActions(fromMapIv, fromAddressTv, route.getFrom());
        addActions(toMapIv, toAddressTv, route.getTo());
        
        return ret;
    }
    
    private void addActions(final ImageView imageView, final TextView textView,
            final NamedLatLng namedLatLng) {
        textView.setText(namedLatLng.getAddress());
        MapUtils.setImageViewMapAfterLayout(imageView,
                new LatLng[] { namedLatLng.getLatLng() });
        
        final OnLocationChoosenCallback cb = new OnLocationChoosenCallback() {
            @Override
            public void onLocationChoosen(LatLng location) {
                if (location == null) {
                    return;
                }
                
                activity.setSomethingChanged(true);
                
                namedLatLng.setLatLng(location, getActivity());
                
                textView.setText(namedLatLng.getAddress());
                MapUtils.setImageViewMapAfterLayout(imageView,
                        new LatLng[] { namedLatLng.getLatLng() });
            }
        };
        
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLocationChooser(cb, namedLatLng);
            }
        });
    }
}