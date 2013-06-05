package ro.minimul.coma.dialog;

import ro.minimul.coma.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class SleepIntervalDialog extends Dialog {
    public static interface OnSetCallback {
        public void onSet(double hours);
    }
    
    private Button upHourBtn;
    private EditText valueHourEt;
    private Button downHourBtn;
    private Button upMinuteBtn;
    private EditText valueMinuteEt;
    private Button downMinuteBtn;
    private Button cancelBtn;
    private Button setBtn;
    
    private boolean hourHasFocus = false;
    private boolean minuteHasFocus = false;
    
    private int hour;
    private int minute;
    
    public SleepIntervalDialog(Context context, double previous,
            final OnSetCallback onSetCallback) {
        super(context);
        setContentView(R.layout.dialog_sleep_interval_picker);
        
        upHourBtn = (Button) findViewById(R.id.upHourBtn);
        valueHourEt = (EditText) findViewById(R.id.valueHourEt);
        downHourBtn = (Button) findViewById(R.id.downHourBtn);
        upMinuteBtn = (Button) findViewById(R.id.upMinBtn);
        valueMinuteEt = (EditText) findViewById(R.id.valueMinEt);
        downMinuteBtn = (Button) findViewById(R.id.downMinBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        setBtn = (Button) findViewById(R.id.setBtn);

        hour = (int) previous;
        minute = (int) Math.round((previous - hour) * 60);

        updateHour();
        updateMinute();
        
        upHourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour++;
                updateHour();
            }
        });
        
        downHourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hour > 0) {
                    hour--;
                    updateHour();
                }
            }
        });
        
        upMinuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minute = (int) Math.floor((minute / 15.0 + 1.0) * 15);
                if (minute >= 60) {
                    minute = minute - 60;
                    hour++;
                    updateHour();
                }
                updateMinute();
            }
        });
        
        downMinuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minute = (int) Math.floor((minute / 15.0 - 1.0) * 15);
                if (minute < 0) {
                    minute = minute + 60;
                    if (hour > 0) {
                        hour--;
                        updateHour();
                    }
                }
                updateMinute();
            }
        });
        
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                
                double hours = hour + (minute / 60.0);
                onSetCallback.onSet(hours);
            }
        });
        
        valueHourEt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hourHasFocus = hasFocus;
                
                if (!hasFocus) {
                    try {
                        hour = Integer.parseInt(
                                valueHourEt.getText().toString());
                    } catch (NumberFormatException ex) {
                    }
                }
                
                updateHour();
            }
        });
        
        valueMinuteEt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                minuteHasFocus = hasFocus;
                
                if (!hasFocus) {
                    try {
                        minute = Integer.parseInt(
                                valueMinuteEt.getText().toString());
                    } catch (NumberFormatException ex) {
                    }
                }
                
                updateMinute();
            }
        });
    }
    
    private void updateHour() {
        if (hourHasFocus) {
            valueHourEt.setText(Integer.toString(hour));
        } else {
            valueHourEt.setText(getContext().getResources()
                    .getQuantityString(R.plurals.hour_count, hour, hour));
        }
    }
    
    private void updateMinute() {
        if (minuteHasFocus) {
            valueMinuteEt.setText(Integer.toString(minute));
        } else {
            valueMinuteEt.setText(getContext().getResources()
                    .getQuantityString(R.plurals.minute_count, minute, minute));
        }
    }
}
