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
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ToggleButton;

public class RouteArrivalFragment extends Fragment {
    private static final int[] BUTTON_IDS = {
        R.id.week0Tb,
        R.id.week1Tb,
        R.id.week2Tb,
        R.id.week3Tb,
        R.id.week4Tb,
        R.id.week5Tb,
        R.id.week6Tb,
    };
    
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
        
        final Calendar arrival = route.getArrival();
        
        timeTp.setOnTimeChangedListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                arrival.set(Calendar.HOUR_OF_DAY, hourOfDay);
                arrival.set(Calendar.MINUTE, minute);
                activity.setSomethingChanged(true);
            }
        });
        
        repeatCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton bv, boolean isChecked) {
                activateRepeat(isChecked);
                route.setRepeatingWeekly(isChecked);
            }
        });
        
        addToggleRepeatingDays(ret);
        
        return ret;
    }
    
    private void initAcordingToRoute() {
        Calendar calendar = route.getArrival();
        
        timeTp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timeTp.setCurrentMinute(calendar.get(Calendar.MINUTE));
        
        repeatCb.setChecked(route.isRepeatingWeekly());
        activateRepeat(route.isRepeatingWeekly());
        
        OnDateChangedListener onDateChanged = new OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
                Calendar arrival = route.getArrival();
                arrival.set(Calendar.YEAR, year);
                arrival.set(Calendar.MONTH, monthOfYear);
                arrival.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                activity.setSomethingChanged(true);
            }
        };
        
        dateDp.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                onDateChanged);
    }
    
    private void addToggleRepeatingDays(final View view) {
        final boolean[] repeating = route.getRepeatingDays();
        
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            ToggleButton tb = (ToggleButton) view.findViewById(BUTTON_IDS[i]);
            final int index = i;
            tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    repeating[index] = isChecked;
                }
            });
            tb.setChecked(repeating[i]);
        }
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