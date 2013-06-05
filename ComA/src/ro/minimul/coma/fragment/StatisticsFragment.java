package ro.minimul.coma.fragment;

import java.util.ArrayList;
import java.util.List;
import ro.minimul.coma.R;
import ro.minimul.coma.prefs.DoughnutSetting;
import ro.minimul.coma.prefs.MeansOfTransportSetting;
import ro.minimul.coma.prefs.NamedSeparator;
import ro.minimul.coma.prefs.SettingItem;
import ro.minimul.coma.prefs.SettingItem.SettingListener;
import ro.minimul.coma.prefs.SettingItemAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class StatisticsFragment extends Fragment {
    private static final int[] COLORS = {
        0xFF43b0cd,
        0xFF278585,
        0xFFe2543c,
        0xFFed4255, // 0xFFef4057
        0xFFe96e4a,
        0xFFffd75d,
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(
                R.layout.fragment_statistics, container, false);

        final SettingItem[] settingItems = getItems();
        
        SettingItemAdapter adapter = new SettingItemAdapter(getActivity(),
                settingItems);
        ListView statisticsLv = (ListView) ret.findViewById(R.id.statisticsLv);
        statisticsLv.setAdapter(adapter);
        
        // Remove the divider since custom ones are used.
        statisticsLv.setDivider(null);
        statisticsLv.setDividerHeight(0);
        
        statisticsLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                SettingItem item = settingItems[position];
                SettingListener listener = item.getListener();
                if (listener != null) {
                    listener.onSelected(item);
                }
                item.updateSelf();
            }
        });
        
        return ret;
    }
    
    private SettingItem[] getItems() {
        List<SettingItem> items = new ArrayList<SettingItem>();
        
        items.add(new NamedSeparator(R.string.label_time_spent));
        items.add(new DoughnutSetting());
        items.add(new NamedSeparator(R.string.label_means_of_transport));
        addMeans(items);
        
        return items.toArray(new SettingItem[0]);
    }
    
    private void addMeans(List<SettingItem> items) {
        int[] ids = {
            R.drawable.small_train,
            R.drawable.small_tram,
            R.drawable.small_bus,
            R.drawable.small_subway,
            R.drawable.small_foot,
            R.drawable.small_sum,
        };
        
        double[] meters = new double[6];
        double[] seconds = new double[6];
        
        meters[0] = r(10000, 20000);
        meters[1] = r(10000, 20000);
        meters[2] = r(0, 0);
        meters[3] = r(10000, 20000);
        meters[4] = r(10000, 20000);
        
        seconds[0] = meters[0] / 25;
        seconds[1] = meters[1] / 15;
        seconds[2] = meters[2] / 19;
        seconds[3] = meters[3] / 20;
        seconds[4] = meters[4] / 1.5;
        
        for (int i = 0; i < 5; i++) {
            meters[5] += meters[i];
            seconds[5] += seconds[i];
        }
        
        for (int i = 0; i < 6; i++) {
            items.add(new MeansOfTransportSetting(ids[i], meters[i], seconds[i],
                    seconds[i] / seconds[5], COLORS[i]));
        }
    }
    
    private double r(double a, double b) {
        return a + (b - a) * Math.random();
    }
}