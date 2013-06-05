package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import ro.minimul.coma.util.Util;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeansOfTransportSetting extends SettingItem {
    private int iconId;
    private double meters;
    private double seconds;
    private double ratio;
    private int color;

    public MeansOfTransportSetting(int iconId, double meters, double seconds,
            double ratio, int color) {
        super(0, null);
        this.iconId = iconId;
        this.meters = meters;
        this.seconds = seconds;
        this.ratio = ratio;
        this.color = color;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context context) {
        View ret = inflater.inflate(R.layout.item_means_of_transport,
                parent, false);
        
        ImageView meansIconIv = (ImageView) ret.findViewById(R.id.meansIconIv);
        TextView lengthTv = (TextView) ret.findViewById(R.id.lengthTv);
        TextView timeTv = (TextView) ret.findViewById(R.id.timeTv);
        LinearLayout barLl = (LinearLayout) ret.findViewById(R.id.barLl);
        final View barV = ret.findViewById(R.id.barV);
        
        meansIconIv.setImageResource(iconId);
        lengthTv.setText(Util.getHumanDistance(meters));
        timeTv.setText(Util.getHumanTime(seconds));
        barLl.setBackgroundColor(color);
        
        lengthTv.setTypeface(typeface);
        timeTv.setTypeface(typeface);
        
        final ViewTreeObserver vto = barV.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                barV.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int newWidth = (int) (barV.getWidth() * ratio);
                barV.getLayoutParams().width = newWidth;
                barV.requestLayout();
                barV.invalidate();
            }
        });
        
        return ret;
    }

    @Override
    public void updateSelf() {
    }
}
