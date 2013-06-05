package ro.minimul.coma.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.routes.RouteItemAdapter;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RoutesFragment extends Fragment {
    private MainActivity mainActivity;
    private RouteItemAdapter adapter;
    private ListView routesLv;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_rutes, container, false);
        
        mainActivity = (MainActivity) getActivity();
        
        adapter = new RouteItemAdapter(mainActivity, getRoutes());
        routesLv = (ListView) ret.findViewById(R.id.routesLv);
        routesLv.setAdapter(adapter);
        
        // Remove the divider since none is needed.
        routesLv.setDivider(null);
        routesLv.setDividerHeight(0);
        
        routesLv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                showItemMenu(position);
                
                return true;
            }
        });
        
        routesLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                editRoute(position);
            }
        });
        
        return ret;
    }
    
    public void addNewRoute(Route route) {
        adapter.add(route);
    }
    
    public void updateRoute(Route route) {
        // Just update them all...
        adapter.clear();
        adapter.addAll(getRoutes());
    }
    
    private List<Route> getRoutes() {
        Map<String, Route> routes = mainActivity.getPrefs().getRoutes();
        List<Route> ret = new ArrayList<Route>(routes.size() + 1);
        ret.add(null);
        
        for (Route r : routes.values()) {
            ret.add(r);
        }
        
        return ret;
    }
    
    private void showItemMenu(final int position) {
        final String[] option = new String[] {
            mainActivity.getString(R.string.label_edit),
            mainActivity.getString(R.string.label_delete),
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mainActivity, android.R.layout.select_dialog_item, option);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        builder.setTitle(R.string.label_route);
        
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editRoute(position);
                } else {
                    deleteRoute(position);
                }
            }
        });
        
        builder.create().show();
    }
    
    private void editRoute(int position) {
        mainActivity.showRouteEditor(adapter.getItem(position));
    }
    
    private void deleteRoute(int position) {
        Map<String, Route> routes = mainActivity.getPrefs().getRoutes();
        
        Route route = adapter.getItem(position);
        routes.remove(route.getId());
        
        adapter.remove(route);
        
    }
}