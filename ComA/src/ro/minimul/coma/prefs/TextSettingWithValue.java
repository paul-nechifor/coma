package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextSettingWithValue extends TextSetting {
    private IValue value;
    private TextView valueTv;
    
    public TextSettingWithValue(int stringId, SettingListener listener,
            IValue value) {
        super(stringId, listener);
        this.value = value;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context context) {
        View ret = inflater.inflate(R.layout.item_text_setting_with_value,
                parent, false);
        TextView tv = (TextView) ret.findViewById(R.id.itemTextSettingTv);

        tv.setText(context.getString(stringId));
        tv.setTypeface(typeface);
        
        valueTv = (TextView) ret.findViewById(R.id.itemTextSettingValueTv);
        valueTv.setTypeface(typeface);
        
        return ret;
    }

    @Override
    public void updateSelf() {
        valueTv.setText(value.getValue(this));
    }
}
