package ro.minimul.coma.routes;

import java.text.SimpleDateFormat;
import java.util.List;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.util.MapUtils;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;

public class RouteItemAdapter extends ArrayAdapter<Route> {
    private final MainActivity ma;

    public RouteItemAdapter(MainActivity mainActivity, List<Route> objects) {
        super(mainActivity, R.layout.item_route, objects);
        this.ma = mainActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        if (position == 0) {
            return getPreItem(inflater, parent);
        }
        
        Route route = getItem(position);
                
        View view = inflater.inflate(R.layout.item_route, parent, false);
        
        ImageView routeMapIv = (ImageView) view.findViewById(R.id.routeMapIv);
        
        TextView beginingIconTv = 
                (TextView) view.findViewById(R.id.beginingIconTv);
        TextView itemRouteFromTv =
                (TextView) view.findViewById(R.id.itemRouteFromTv);
        TextView endingIconTv = 
                (TextView) view.findViewById(R.id.endingIconTv);
        TextView itemRouteToTv =
                (TextView) view.findViewById(R.id.itemRouteToTv);
        TextView itemRouteTimeTv =
                (TextView) view.findViewById(R.id.itemRouteTimeTv);
        TextView itemRouteExtraTv =
                (TextView) view.findViewById(R.id.itemRouteExtraTv);
                
        itemRouteFromTv.setText(route.getFrom().getAddress());
        itemRouteToTv.setText(route.getTo().getAddress());
        itemRouteTimeTv.setText(getArrivalTime(route));
        itemRouteExtraTv.setText(getExtraText(route));
        
        LatLng[] points = new LatLng[] {
            route.getFrom().getLatLng(),
            route.getTo().getLatLng()
        };
        
        MapUtils.setImageViewMapAfterLayout(routeMapIv, points);
        
        Typeface typeface = Typeface.createFromAsset(ma.getAssets(),
                "fonts/Entypo.ttf");
        
        beginingIconTv.setTypeface(typeface);
        endingIconTv.setTypeface(typeface);
        
        return view;
    }
    
    private View getPreItem(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_route_add, parent,
                false);
        
        Button addNewRouteB = (Button) view.findViewById(R.id.addNewRouteB);
        addNewRouteB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.showRouteEditor(Route.newEmptyRoute());
            }
        });
        
        return view;
    }
    
    private String getArrivalTime(Route route) {
        String time = SimpleDateFormat.getTimeInstance().format(
                route.getArrival().getTime());
        
        return ma.getString(R.string.label_route_arrival_time, time);
    }
    
    private String getExtraText(Route route) {
        String extraText;
        
        if (route.isRepeatingWeekly()) {
            extraText = ma.getString(R.string.label_route_repeating,
                    getRepeating(route));
        } else {
            extraText = ma.getString(R.string.label_route_arrival_date,
                    getArrivalDate(route));
        }
        
        return extraText;
    }
    
    private String getRepeating(Route route) {
        return "TODO";
    }
    
    private String getArrivalDate(Route route) {
        String date = SimpleDateFormat.getDateInstance().format(
                route.getArrival().getTime());
        
        return ma.getString(R.string.label_route_arrival_date, date);
    }
}
