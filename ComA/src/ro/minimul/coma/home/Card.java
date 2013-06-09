package ro.minimul.coma.home;

import java.util.Calendar;

public class Card implements Comparable<Card> {
    public Calendar startTime;
    public int iconId;
    public String topMessage;
    public String subMessage;
    public int weatherId = -1;
    
    @Override
    public int compareTo(Card another) {
        return startTime.compareTo(another.startTime);
    }
}
