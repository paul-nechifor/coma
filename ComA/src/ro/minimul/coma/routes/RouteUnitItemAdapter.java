package ro.minimul.coma.routes;

import java.util.Calendar;
import java.util.List;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.RouteSelectionActivity;
import ro.minimul.coma.util.Util;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteUnitItemAdapter extends ArrayAdapter<RouteUnit> {
    private RouteSelectionActivity activity;
    
    public RouteUnitItemAdapter(RouteSelectionActivity activity,
            int textViewResourceId, List<RouteUnit> objects) {
        super(activity, textViewResourceId, objects);
        this.activity = activity;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
                
        View view = inflater.inflate(R.layout.item_transport, parent, false);
        
        ImageView transportTypeIv =
                (ImageView) view.findViewById(R.id.transportTypeIv);
        TextView transportNameTv = 
                (TextView) view.findViewById(R.id.transportNameTv);
        TextView transportOtherInfoTv =
                (TextView) view.findViewById(R.id.transportOtherInfoTv);
        
        TextView startStationTv = 
                (TextView) view.findViewById(R.id.startStationTv);
        TextView endStationTv =
                (TextView) view.findViewById(R.id.endStationTv);
        
        TextView startTimeTv = 
                (TextView) view.findViewById(R.id.startTimeTv);
        TextView endTimeTv =
                (TextView) view.findViewById(R.id.endTimeTv);
        
        RouteUnit routeUnit = getItem(position);
        Transport tr = routeUnit.transport;
        transportNameTv.setText(tr.name);
        transportTypeIv.setImageResource(tr.meanType.getBigId());
        
        startStationTv.setText(routeUnit.getStartStation());
        endStationTv.setText(routeUnit.getEndStation());
        
        startTimeTv.setText(Util.getShortTime(routeUnit.startTime));
        endTimeTv.setText(Util.getShortTime(routeUnit.endTime));
        
        transportOtherInfoTv.setText(
                delta(routeUnit.startTime, routeUnit.endTime));

        TextView beginingIconTv = 
                (TextView) view.findViewById(R.id.beginingIconTv);
        TextView endingIconTv = 
                (TextView) view.findViewById(R.id.endingIconTv);
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(),
                "fonts/Entypo.ttf");
        
        beginingIconTv.setTypeface(typeface);
        endingIconTv.setTypeface(typeface);
        
        return view;
    }
    
    private String delta(Calendar a, Calendar b) {
        long t = (b.getTimeInMillis() - a.getTimeInMillis()) / 1000;
        long m = t / 60;
        
        String ret = "";
        if (m == 1) {
            ret += m + " minute";
        } else {
            ret += m + " minutes";
        }
        
        return ret;
    }
}
