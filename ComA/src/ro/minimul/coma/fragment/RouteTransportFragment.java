package ro.minimul.coma.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.RouteSelectionActivity;
import ro.minimul.coma.activity.TrainSimulatorActivity;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.routes.RouteData;
import ro.minimul.coma.routes.RouteUnit;
import ro.minimul.coma.routes.RouteUnitItemAdapter;
import ro.minimul.coma.util.WebApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;

public class RouteTransportFragment extends Fragment {
    private RouteSelectionActivity activity;
    private Route route;
    private TextView statusTv;
    private ProgressBar workingPb;
    private ListView transportList;
    private RouteUnitItemAdapter listAdapter;
    
    private class GetTransportsTask extends AsyncTask<Route, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Route... params) {
            try {
                return getNewPath(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // This will trigger the failed message.
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result == null) {
                showFailed();
                return;
            }
            
            showTransports(result);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_route_transport,
                container, false);

        activity = (RouteSelectionActivity) getActivity();
        route = activity.getRoute();
        
        statusTv = (TextView) ret.findViewById(R.id.statusTv);
        workingPb = (ProgressBar) ret.findViewById(R.id.workingPb);
        transportList = (ListView) ret.findViewById(R.id.transportList);
        
        listAdapter = new RouteUnitItemAdapter(activity, 0,
                new ArrayList<RouteUnit>());
        transportList.setAdapter(listAdapter);
        
        statusTv.setVisibility(View.VISIBLE);
        workingPb.setVisibility(View.GONE);
        transportList.setVisibility(View.GONE);
        
        if (route.getRouteData() != null) {
            listAdapter.clear();
            listAdapter.addAll(route.getRouteData().routeUnits);
            activity.setSomethingChanged(false);
            activity.setRouteCalculationComplete(true);
        }
        
        transportList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, 
                    int position, long id) {
                RouteUnit ru = listAdapter.getItem(position);
                if (ru.transport.mean == 0) {
                    openSimulatorFor(ru);
                }
            }
        });
        
        return ret;
    }
    
    public void computePath() {
        if (activity == null || !activity.hasSomethingChanged()) {
            return; 
        }
        
        activity.setRouteCalculationComplete(false);
        activity.setSomethingChanged(false);
        
        showWorking();
        
        new GetTransportsTask().execute(route);
    }
    
    private JSONObject getNewPath(Route r) throws IOException, JSONException {
        LatLng from = r.getFrom().getLatLng();
        LatLng to = r.getTo().getLatLng();
        Calendar arrival = r.getArrival();
        
        JSONObject json = WebApi.getTransports(
                from.latitude,
                from.longitude,
                to.latitude,
                to.longitude,
                arrival.get(Calendar.HOUR_OF_DAY),
                arrival.get(Calendar.MINUTE));
        
        return json;
    }
    
    private void showWorking() {
        statusTv.setVisibility(View.VISIBLE);
        workingPb.setVisibility(View.VISIBLE);
        transportList.setVisibility(View.GONE);
        
        statusTv.setText(R.string.label_computing_path);
        workingPb.setActivated(true);
    }
    
    private void showFailed() {
        statusTv.setVisibility(View.VISIBLE);
        workingPb.setVisibility(View.GONE);
        transportList.setVisibility(View.GONE);
        
        statusTv.setText(R.string.label_problems_computing_path);
        
        activity.setRouteCalculationComplete(true);
        
        workingPb.setActivated(false);
    }
    
    private void showTransports(JSONObject result) {
        statusTv.setVisibility(View.GONE);
        workingPb.setVisibility(View.GONE);
        transportList.setVisibility(View.VISIBLE);
        
        activity.setRouteCalculationComplete(true);
        
        listAdapter.clear();
        
        workingPb.setActivated(false);
        
        RouteData routeData = null;
        try {
            routeData = RouteData.getFromJsonObject(result);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        
        route.setRouteData(routeData);
        
        listAdapter.addAll(routeData.routeUnits);
    }
    
    private void openSimulatorFor(RouteUnit routeUnit) {
        TrainSimulatorActivity.PASSING_CHEAT = routeUnit;
        Intent intent = new Intent(activity, TrainSimulatorActivity.class);
        activity.startActivity(intent);
    }
}