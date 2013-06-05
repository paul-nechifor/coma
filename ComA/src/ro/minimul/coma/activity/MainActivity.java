package ro.minimul.coma.activity;

import java.util.Map;
import ro.minimul.coma.R;
import ro.minimul.coma.activity.ChooseLocationActivity.OnLocationChoosenCallback;
import ro.minimul.coma.fragment.HistoryFragment;
import ro.minimul.coma.fragment.HomeFragment;
import ro.minimul.coma.fragment.RoutesFragment;
import ro.minimul.coma.fragment.SettingsFragment;
import ro.minimul.coma.fragment.StatisticsFragment;
import ro.minimul.coma.menu.MenuView;
import ro.minimul.coma.menu.MenuView.OnTabSelectedListener;
import ro.minimul.coma.menu.MenuViewOption;
import ro.minimul.coma.prefs.AppPrefs;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.service.ComaService;
import ro.minimul.coma.util.NamedLatLng;
import ro.minimul.coma.util.Util;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements OnTabSelectedListener {
    public static final MenuViewOption[] OPTIONS = {
        new MenuViewOption(R.string.menu_home_icon, R.string.menu_home),
        new MenuViewOption(R.string.menu_routes_icon, R.string.menu_routes),
        new MenuViewOption(R.string.menu_history_icon, R.string.menu_history),
        new MenuViewOption(R.string.menu_statistics_icon, R.string.menu_statistics),
        new MenuViewOption(R.string.menu_settings_icon, R.string.menu_settings),
    };
    
    private static final String[] FRAGMENT_TAGS = {
        "homeFragment",
        "routesFragment",
        "historyFragment",
        "statisticsFragment",
        "settingsFragment",
    };
    
    private static final int FACEBOOK_LOGIN = 0;
    private static final int CHOOSE_LOCATION = 1;
    private static final int ROUTE_EDITOR = 2;
    
    private AppPrefs prefs;
    private MenuView menuMv;
    private int currentTabIndex = -1;
    
    private OnLocationChoosenCallback onLocationChoosenCallback;
    
    private Fragment[] tabFragments;
    
    @SuppressWarnings("unused")
    private Map<String, float[]> edges;
    
    public AppPrefs getPrefs() {
        return prefs;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        menuMv = (MenuView) findViewById(R.id.menuMv);
        menuMv.setOnTabSelectedListener(this);
        
        restorePrefs();
        
        //if (prefs.firstStart) {
            onFirstStart();
        //    prefs.firstStart = false;
        //}
            
        startService(new Intent(this, ComaService.class));
        
        tabFragments = new Fragment[OPTIONS.length];
        tryToRecoverTabFragments();
        setupInitialTab();
        
        try {
            edges = Util.getEdges(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void onStop(){
        storePrefs();
        
        super.onStop();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case (FACEBOOK_LOGIN):
            onFacebookLoginResult(resultCode, data);
            break;
        case (CHOOSE_LOCATION):
            onChooseLocationResult(resultCode, data);
            break;
        case (ROUTE_EDITOR):
            onRouteEditorResult(resultCode, data);
            break;
        }
    }
    
    public void erasePreferencesAndRestart() {
        prefs = new AppPrefs();
        
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent restartIntent = PendingIntent.getActivity(
                this.getBaseContext(), 0, new Intent(getIntent()),
                getIntent().getFlags());
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);
        
        finish();
    }
    
    private void onFirstStart() {
        tryToInitializeLastInputLocation();
    }
    
    private void tryToInitializeLastInputLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(
                Activity.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER);
        
        if (location != null) {
            prefs.setLastInputLocation(location);
        }
    }
    
    public void showLocationChooser(OnLocationChoosenCallback callback,
            NamedLatLng previous) {
        this.onLocationChoosenCallback = callback;
        
        LatLng location;
        if (previous.isValid()) {
            location = previous.getLatLng();
        } else {
            location = prefs.getLastInputLocationLatLng();
        }
        
        Intent intent = new Intent(this, ChooseLocationActivity.class);
        intent.putExtra(ChooseLocationActivity.LATITUDE, location.latitude);
        intent.putExtra(ChooseLocationActivity.LONGITUDE, location.longitude);
        
        startActivityForResult(intent, CHOOSE_LOCATION);
    }
    
    public void showLoginForFacebook() {
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        startActivityForResult(intent, FACEBOOK_LOGIN);
    }
    
    public void showRouteEditor(Route route) {
        Intent intent = new Intent(this, RouteSelectionActivity.class);
        intent.putExtra(RouteSelectionActivity.ROUTE_DATA, route);
        startActivityForResult(intent, ROUTE_EDITOR);
    }
    
    private void onFacebookLoginResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            prefs.facebookEmail = data.getStringExtra(Intent.EXTRA_EMAIL);
            prefs.facebookPassword = data.getStringExtra(Intent.EXTRA_TEXT);
        }
    }
    
    private void onChooseLocationResult(int resultCode, Intent data) {
        LatLng location;
        if (resultCode == Activity.RESULT_OK) {
            double latitude = data.getDoubleExtra(
                    ChooseLocationActivity.LATITUDE, 0);
            double longitude = data.getDoubleExtra(
                    ChooseLocationActivity.LONGITUDE, 0);
            location = new LatLng(latitude, longitude);
            prefs.setLastInputLocation(location);
        } else {
            location = null;
        }
        
        onLocationChoosenCallback.onLocationChoosen(location);
        onLocationChoosenCallback = null;
    }
    
    private void onRouteEditorResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        
        Route route = (Route) data.getSerializableExtra(
                RouteSelectionActivity.ROUTE_DATA);
        
        Map<String, Route> routes = prefs.getRoutes();
        
        boolean routeIsNew = !route.isAdded();
        
        if (routeIsNew) {
            String id = Integer.toString(routes.size());
            route.setAddedId(id);
        }
        
        routes.put(route.getId(), route);

        tryToUpdateRoutesList(route, routeIsNew);
    }
    
    private void tryToUpdateRoutesList(Route route, boolean routeIsNew) {
        Fragment f = tabFragments[1];
        if (f == null) {
            return;
        }
        
        RoutesFragment rf = (RoutesFragment) f;
        
        if (routeIsNew) {
            rf.addNewRoute(route);
        } else {
            rf.updateRoute(route);
        }
    }
    
    private void restorePrefs() {
        SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);
        String serialized = sharedPrefs.getString("prefs", null);
        
        if (serialized == null) {
            prefs = new AppPrefs();
        } else {
            prefs = AppPrefs.fromSerialization(serialized);
        }
    }
    
    private void storePrefs() {
        SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("prefs", prefs.toSerialization());
        editor.commit();
    }
    
    private void setupInitialTab() {
        setTabFragment(0);
    }

    @Override
    public void onTabSelected(int index) {
        if (index != currentTabIndex) {
            setTabFragment(index);
        }
    }
    
    private void setTabFragment(int index) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        
        if (currentTabIndex >= 0) {
            ft.remove(tabFragments[currentTabIndex]);
        }
        
        if (tabFragments[index] == null) {
            switch (index) {
            case 0:
                tabFragments[index] = new HomeFragment();
                break;
            case 1:
                tabFragments[index] = new RoutesFragment();
                break;
            case 2:
                tabFragments[index] = new HistoryFragment();
                break;
            case 3:
                tabFragments[index] = new StatisticsFragment();
                break;
            case 4:
                tabFragments[index] = new SettingsFragment();
                break;
            }
        }
        
        ft.add(R.id.contentLayout, tabFragments[index], FRAGMENT_TAGS[index]);
        ft.commit();
        
        currentTabIndex = index;
    }
    
    private void tryToRecoverTabFragments() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = null;
        Fragment f;
        
        for (int i = 0; i < FRAGMENT_TAGS.length; i++) {
            f = fm.findFragmentByTag(FRAGMENT_TAGS[i]);
            if (f != null) {
                tabFragments[i] = f;
                if (ft == null) {
                    ft = fm.beginTransaction();
                }
                ft.remove(f);
            }
        }
        
        if (ft != null) {
            ft.commit();
        }
    }
}
