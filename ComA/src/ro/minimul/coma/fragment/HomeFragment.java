package ro.minimul.coma.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.home.Card;
import ro.minimul.coma.home.CardItemAdapter;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.routes.RouteUnit;
import ro.minimul.coma.routes.Transport;
import ro.minimul.coma.util.Util;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeFragment extends Fragment {
    private static final long MILLIS_IN_HOUR = 60 * 60 * 1000;
    private static final long MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;
    private ListView cardsLv;
    private CardItemAdapter adapter;
    private MainActivity ma;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_home, container, false);
        
        ma = (MainActivity) getActivity();
        
        cardsLv = (ListView) ret.findViewById(R.id.cardsLv);
        adapter = new CardItemAdapter(ma, new ArrayList<Card>());
        cardsLv.setAdapter(adapter);
        
        // Remove the divider since none is needed.
        cardsLv.setDivider(null);
        cardsLv.setDividerHeight(0);
        
        recomputeCards();
        
        return ret;
    }
    
    public void recomputeCards() {
        adapter.clear();
        adapter.addAll(computeCards());
    }
    
    private List<Card> computeCards() {
        List<Card> cards = new ArrayList<Card>();
        
        List<Route> routes = new ArrayList<Route>(
                ma.getPrefs().getRoutes().values());
        
        for (Route route : routes) {
            addCardsForRoute(route, cards);
        }
        
        Collections.sort(cards);
        
        addSleep(cards);
        
        return cards;
    }
    
    private void addCardsForRoute(Route route, List<Card> cards) {
        if (route.isRepeatingWeekly()) {
            addWeeklyRoute(route, cards);
        } else {
            addOneTime(route, cards);
        }
    }
    
    private void addWeeklyRoute(Route route, List<Card> cards) {
        Calendar now = Calendar.getInstance();
        int nowWeek = Util.indexOfWeek(now);
        
        boolean[] repeatingDays = route.getRepeatingDays();
        
        for (int i = 0; i < repeatingDays.length; i++) {
            int daysDelta = (repeatingDays.length - 1 + nowWeek + i);
            Calendar day = Calendar.getInstance();
            long millis = day.getTimeInMillis() + daysDelta * MILLIS_IN_DAY;
            day.setTimeInMillis(millis);
            addRouteOnDay(day, route, cards);
        }
    }
    
    private void addOneTime(Route route, List<Card> cards) {
        addRouteOnDay(route.getArrival(), route, cards);
    }
    
    private void addRouteOnDay(Calendar day, Route route, List<Card> cards) {
        RouteUnit[] routeUnits = route.getRouteData().routeUnits;
        
        for (int i = 0; i < routeUnits.length; i++) {
            RouteUnit routeUnit = routeUnits[i];
            
            addRouteUnit(routeUnit, day, cards);
            
            if (i + 1 < routeUnits.length) {
                addWalking(routeUnit, routeUnits[i + 1], day, cards);
            }
        }
    }
    
    private void addRouteUnit(RouteUnit routeUnit, Calendar day,
            List<Card> cards) {
        Card card = new Card();
        
        Transport transport = routeUnit.transport;        
        
        card.startTime = getNewUnited(day, routeUnit.startTime);
        card.iconId = routeUnit.transport.meanType.getBigId();
        card.topMessage = "Take " + transport.name + " until " +
                routeUnit.getEndStation() + ".";
        
        if (Math.random() < 0.2) {
            int m = (int) (5 + Math.random() * 10);
            card.subMessage = "(will be late by " + m + " minutes)";
        }
        
        if (Math.random() < 0.2) {
            card.weatherId = R.drawable.weather_rain;
        }
        
        cards.add(card);
    }

    
    private void addWalking(RouteUnit a, RouteUnit b, Calendar day,
            List<Card> cards) {
        Card card = new Card();      
        
        card.startTime = getNewUnited(day, a.endTime);
        card.iconId = R.drawable.normal_foot;
        card.topMessage = "Walk to station " + b.getStartStation()
                + " of " + b.transport.name + ".";
        
        cards.add(card);
    }
    
    private Calendar getNewUnited(Calendar date, Calendar time) {
        Calendar c = Calendar.getInstance();
        
        if (time == null) {
            return c;
        }
        
        c.setTimeInMillis(date.getTimeInMillis());
        c.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        c.set(Calendar.SECOND, 0);
        
        return c;
    }
    
    private void addSleep(List<Card> cards) {
        double sleepHours = ma.getPrefs().getSleepInterval();
        
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        
        Calendar firstDay = cards.get(0).startTime;
        
        List<Card> newCards = new ArrayList<Card>();
        
        for (int i = 0; i < 7; i++) {
            Calendar d = getNewUnited(firstDay, midnight);
            long millis = d.getTimeInMillis() + i * MILLIS_IN_DAY;
            Calendar wakeUp = getSafeTime(cards, millis);
            Calendar sleepDown = Calendar.getInstance();
            sleepDown.setTimeInMillis((long) (wakeUp.getTimeInMillis()
                    - sleepHours * MILLIS_IN_HOUR));
            
            Card sleepCard = new Card();
            sleepCard.startTime = sleepDown;
            sleepCard.iconId = R.drawable.normal_clock;
            sleepCard.topMessage = "Sleep";
            newCards.add(sleepCard);
            
            Card wakeCard = new Card();
            wakeCard.startTime = wakeUp;
            wakeCard.iconId = R.drawable.normal_clock;
            wakeCard.topMessage = "Wake up";
            newCards.add(wakeCard);
        }
        
        cards.addAll(newCards);
        Collections.sort(cards);
    }
    
    private Calendar getSafeTime(List<Card> cards, long millis) {
        Calendar next = getAfter(cards, millis);
        long future;
        
        if (next == null) {
            future = Long.MAX_VALUE;
        } else {
            future = next.getTimeInMillis();
        }
        
        if (future - millis > 8 * MILLIS_IN_HOUR) {
            future = millis + 8 * MILLIS_IN_HOUR;
        }
        
        future -= 1 * MILLIS_IN_HOUR;
        
        Calendar ret = Calendar.getInstance();
        ret.setTimeInMillis(future);
        
        return ret;
    }
    
    private Calendar getAfter(List<Card> cards, long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        
        for (Card card : cards) {
            if (card.startTime.compareTo(c) >= 0) {
                return card.startTime;
            }
        }
        
        return null;
    }
}