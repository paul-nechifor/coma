package ro.minimul.coma.fragment;

import java.util.Calendar;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.RouteSelectionActivity;
import ro.minimul.coma.routes.Route;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

public class RouteArrivalFragment extends Fragment {
    private RouteSelectionActivity activity;
    private Route route;
    private TimePicker timeTp;
    private CheckBox repeatCb;
    private LinearLayout fixedDate;
    private DatePicker dateDp;
    private LinearLayout weekdayList;
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_route_arrival, container,
                false);
        
        activity = (RouteSelectionActivity) getActivity();
        route = activity.getRoute();
        
        timeTp = (TimePicker) ret.findViewById(R.id.timeTp);
        repeatCb = (CheckBox) ret.findViewById(R.id.repeatCb);
        fixedDate = (LinearLayout) ret.findViewById(R.id.fixedDate);
        dateDp = (DatePicker) ret.findViewById(R.id.dateDp);
        weekdayList = (LinearLayout) ret.findViewById(R.id.weekdayList);

        timeTp.setIs24HourView(true);
        dateDp.setCalendarViewShown(false);
        
        initAcordingToRoute();
        
        repeatCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton bv, boolean isChecked) {
                activateRepeat(isChecked);
            }
        });
        
        return ret;
    }
    
    private void initAcordingToRoute() {
        Calendar calendar = route.getArrival();
        
        timeTp.setCurrentHour(calendar.get(Calendar.HOUR));
        timeTp.setCurrentMinute(calendar.get(Calendar.MINUTE));
        
        repeatCb.setChecked(route.isRepeatingWeekly());
        activateRepeat(route.isRepeatingWeekly());
        
        dateDp.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }
    
    private void activateRepeat(boolean activate) {
        if (activate) {
            fixedDate.setVisibility(View.GONE);
            weekdayList.setVisibility(View.VISIBLE);
        } else {
            fixedDate.setVisibility(View.VISIBLE);
            weekdayList.setVisibility(View.GONE);
        }
    }
}