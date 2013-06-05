package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import ro.minimul.coma.stats.DoughnutChartView;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DoughnutSetting extends SettingItem {
    public DoughnutSetting() {
        super(0, null);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context context) {
        ViewGroup ret = (ViewGroup) inflater.inflate(R.layout.item_doughnut,
                parent, false);
        
        DoughnutChartView.inflateInto(ret.getContext(), ret);
        
        return ret;
    }

    @Override
    public void updateSelf() {        
    }
}
