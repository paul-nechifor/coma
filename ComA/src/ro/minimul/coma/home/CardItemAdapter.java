package ro.minimul.coma.home;

import java.util.List;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.util.Util;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardItemAdapter extends ArrayAdapter<Card> {
    public CardItemAdapter(MainActivity ma, List<Card> cards) {
        super(ma, R.layout.item_card);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View view = inflater.inflate(R.layout.item_card, parent, false);
        Card card = getItem(position);
        
        TextView timeTv = (TextView) view.findViewById(R.id.timeTv);
        TextView dateTv = (TextView) view.findViewById(R.id.dateTv);
        ImageView transportTypeIv =
                (ImageView) view.findViewById(R.id.transportTypeIv);
        TextView transportNameTv =
                (TextView) view.findViewById(R.id.transportNameTv);
        TextView subMessageTv =
                (TextView) view.findViewById(R.id.subMessageTv);
        

        ImageView weatherIv =
                (ImageView) view.findViewById(R.id.weatherIv);
        
        
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/OpenSans-CondLight.ttf");

        timeTv.setText(Util.getShortTime(card.startTime));
        timeTv.setTypeface(typeface);
        
        dateTv.setText(Util.getShortDate(card.startTime));
        dateTv.setTypeface(typeface);
        
        transportTypeIv.setImageResource(card.iconId);
        transportNameTv.setText(card.topMessage);
        
        if (card.subMessage != null) {
            subMessageTv.setText(card.subMessage);
        }
        
        if (card.weatherId != -1) {
            weatherIv.setImageResource(card.weatherId);
        }
        
        return view;
    }
}
