package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextSetting extends SettingItem {
    public TextSetting(int stringId, SettingListener listener) {
        super(stringId, listener);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context context) {
        View ret = inflater.inflate(R.layout.item_text_setting, parent,
                false);
        TextView tv = (TextView) ret.findViewById(R.id.itemTextSettingTv);

        tv.setText(context.getString(stringId));
        tv.setTypeface(typeface);
        
        return ret;
    }

    @Override
    public void updateSelf() {        
    }
}
