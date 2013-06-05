package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SettingItemAdapter extends ArrayAdapter<SettingItem> {
    private Context context;
    private SettingItem[] items;
    private Typeface typeface;
    
    public SettingItemAdapter(Context context, SettingItem[] items) {
        super(context, R.layout.item_setting, items);
        this.context = context;
        this.items = items;
        this.typeface = Typeface.createFromAsset(context.getAssets(),
              "fonts/OpenSans-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SettingItem item = items[position];
        
        View view = item.getView(position, convertView, parent, inflater,
                typeface, context);
        
        item.updateSelf();
        
        return view;
    }
}
