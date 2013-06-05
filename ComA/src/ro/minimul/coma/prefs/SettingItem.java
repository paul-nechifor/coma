package ro.minimul.coma.prefs;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class SettingItem {
    public static interface IValue {
        public String getValue(SettingItem item);
    }

    public interface SettingListener {
        public void onSelected(SettingItem item);
    }
    
    protected final int stringId;
    protected final SettingListener listener;
    
    public SettingItem(int stringId, SettingListener listener) {
        this.stringId = stringId;
        this.listener = listener;
    }
    
    public String getName(Context context) {
        return context.getString(stringId);
    }
    
    public SettingListener getListener() {
        return listener;
    }
    
    public abstract void updateSelf();
    
    public abstract View getView(int position, View convertView,
            ViewGroup parent, LayoutInflater inflater, Typeface typeface,
            Context context);
}
