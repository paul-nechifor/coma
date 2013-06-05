package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LineSeparator extends SettingItem {
    public LineSeparator() {
        super(0, null);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context contextm) {
        return inflater.inflate(R.layout.item_line_separator, parent, false);
    }

    @Override
    public void updateSelf() {        
    }
}
