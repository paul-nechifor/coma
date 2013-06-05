package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import ro.minimul.coma.util.MapUtils;
import ro.minimul.coma.util.NamedLatLng;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;

public class TextSettingWithMap extends TextSetting {
    public static class MapValue implements IValue {
        private final NamedLatLng location;
        private final Context context;
        
        public MapValue(NamedLatLng location, Context context) {
            this.location = location;
            this.context = context;
        }
        
        @Override
        public String getValue(SettingItem item) {
            if (location.isValid()) {
                return location.getAddress();
            } else {
                return context.getString(R.string.address_not_set);
            }
        }
        
        public LatLng getLatLng() {
            return location.getLatLng();
        }
    }
    
    private MapValue value;
    private TextView valueTv;
    private ImageView mapIv;
    
    public TextSettingWithMap(int stringId, SettingListener listener,
            MapValue value, Context context) {
        super(stringId, listener);
        this.value = (MapValue) value;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context context) {
        View ret = inflater.inflate(R.layout.item_text_setting_with_map,
                parent, false);
        TextView tv = (TextView) ret.findViewById(R.id.itemTextSettingTv);

        tv.setText(context.getString(stringId));
        tv.setTypeface(typeface);
        
        valueTv = (TextView) ret.findViewById(R.id.itemTextSettingValueTv);
        valueTv.setTypeface(typeface);
        
        mapIv = (ImageView) ret.findViewById(R.id.mapIv);
        
        return ret;
    }

    @Override
    public void updateSelf() {
        valueTv.setText(value.getValue(this));
        
        MapUtils.setImageViewMapAfterLayout(mapIv,
                new LatLng[] { value.getLatLng() });
    }
}
