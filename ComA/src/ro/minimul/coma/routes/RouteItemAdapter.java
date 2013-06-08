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
    private static final int[] WEEK_IDS = {
        R.string.short_week_name_0,
        R.string.short_week_name_1,
        R.string.short_week_name_2,
        R.string.short_week_name_3,
        R.string.short_week_name_4,
        R.string.short_week_name_5,
        R.string.short_week_name_6,
    };
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
                
        itemRouteFromTv.setText(route.getFrom().getAddress()
                .replace("\n", ", "));
        itemRouteToTv.setText(route.getTo().getAddress()
                .replace("\n", ", "));
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
        String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                .format(route.getArrival().getTime());
        
        return ma.getString(R.string.label_route_arrival_time, time);
    }
    
    private String getExtraText(Route route) {
        String extraText;
        
        if (route.isRepeatingWeekly()) {
            extraText = ma.getString(R.string.label_route_repeating,
                    getRepeating(route));
        } else {
            extraText = getArrivalDate(route);
        }
        
        return extraText;
    }
    
    private String getRepeating(Route route) {
        boolean[] r = route.getRepeatingDays();
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < r.length; i++) {
            if (r[i]) {
                builder.append(ma.getString(WEEK_IDS[i])).append(",");
            }
        }
        
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        
        return builder.toString();
    }
    
    private String getArrivalDate(Route route) {
        String date = SimpleDateFormat.getDateInstance().format(
                route.getArrival().getTime());
        
        return ma.getString(R.string.label_route_arrival_date, date);
    }
}
