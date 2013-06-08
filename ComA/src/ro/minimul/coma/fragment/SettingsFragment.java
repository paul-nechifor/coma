package ro.minimul.coma.fragment;

import ro.minimul.coma.R;
import ro.minimul.coma.activity.ChooseLocationActivity.OnLocationChoosenCallback;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.activity.MainActivity.OnFacebookLoginCallback;
import ro.minimul.coma.dialog.SleepIntervalDialog;
import ro.minimul.coma.prefs.AppPrefs;
import ro.minimul.coma.prefs.FacebookLoginSetting;
import ro.minimul.coma.prefs.LineSeparator;
import ro.minimul.coma.prefs.NamedSeparator;
import ro.minimul.coma.prefs.SettingItem;
import ro.minimul.coma.prefs.SettingItem.IValue;
import ro.minimul.coma.prefs.SettingItem.SettingListener;
import ro.minimul.coma.prefs.SettingItemAdapter;
import ro.minimul.coma.prefs.TextSetting;
import ro.minimul.coma.prefs.TextSettingWithMap;
import ro.minimul.coma.prefs.TextSettingWithMap.MapValue;
import ro.minimul.coma.prefs.TextSettingWithValue;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.google.android.gms.maps.model.LatLng;

public class SettingsFragment extends Fragment {
    private SettingItem[] settingItems;
    private MainActivity mainActivity;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_settings, container,
                false);

        mainActivity = (MainActivity) getActivity();
        final AppPrefs prefs = mainActivity.getPrefs();
        
        // Register account ////////////////////////////////////////////////////
        SettingListener registerAccount = new SettingListener() {
            @Override
            public void onSelected(SettingItem item) {
            }
        };
        IValue regsiterAccountValue = new IValue() {
            @Override
            public String getValue(SettingItem item) {
                return null; // TODO
            }
        };

        // Login into Facebook /////////////////////////////////////////////////
        SettingListener loginIntoFacebook = new SettingListener() {
            @Override
            public void onSelected(final SettingItem item) {
                mainActivity.loginToFacebook(new OnFacebookLoginCallback() {
                    @Override
                    public void onFacebookLogin() {
                        item.updateSelf();
                    }
                });
            }
        };
        
//        IValue loginIntoFacebookValue = new IValue() {
//            @Override
//            public String getValue(SettingItem item) {
//                if (prefs.facebookEmail == null) {
//                    return mainActivity.getString(
//                            R.string.facebook_not_connected);
//                } else {
//                    return mainActivity.getString(R.string.facebook_connected,
//                            prefs.facebookEmail);
//                }
//            }
//        };

        // Home location ///////////////////////////////////////////////////////
        SettingListener homeLocation = new SettingListener() {
            @Override
            public void onSelected(final SettingItem item) {
                OnLocationChoosenCallback cb = new OnLocationChoosenCallback() {
                    @Override
                    public void onLocationChoosen(LatLng location) {
                        if (location != null) {
                            prefs.getHomeAddress().setLatLng(location,
                                    mainActivity);
                            item.updateSelf();
                        }
                    }
                };
                
                mainActivity.showLocationChooser(cb, prefs.getHomeAddress());
            }
        };
        MapValue homeLocationValue = new MapValue(prefs.getHomeAddress(),
                mainActivity);

        // Work location ///////////////////////////////////////////////////////
        SettingListener workLocation = new SettingListener() {
            @Override
            public void onSelected(final SettingItem item) {
                OnLocationChoosenCallback cb = new OnLocationChoosenCallback() {
                    @Override
                    public void onLocationChoosen(LatLng location) {
                        if (location != null) {
                            prefs.getWorkAddress().setLatLng(location,
                                    mainActivity);
                            item.updateSelf();
                        }
                    }
                };
                
                mainActivity.showLocationChooser(cb, prefs.getWorkAddress());
            }
        };
        MapValue workLocationValue = new MapValue(prefs.getWorkAddress(),
                mainActivity);
        
        // Sleep interval //////////////////////////////////////////////////////
        SettingListener sleepInterval = new SettingListener() {
            @Override
            public void onSelected(final SettingItem item) {
                
                SleepIntervalDialog.OnSetCallback callback =
                        new SleepIntervalDialog.OnSetCallback() {
                            @Override
                    public void onSet(double hours) {
                        prefs.setSleepInterval(hours);
                        item.updateSelf();
                    }
                };
                
                SleepIntervalDialog dialog = new SleepIntervalDialog(
                        mainActivity,
                        prefs.getSleepInterval(),
                        callback);
                dialog.show();
            }
        };
        IValue sleepIntervalValue = new IValue() {
            @Override
            public String getValue(SettingItem item) {
                double time = prefs.getSleepInterval();
                Resources res = mainActivity.getResources();
                int hour = (int) time;
                int minute = (int) Math.round((time - hour) * 60);
                
                String ret = 
                        res.getQuantityString(R.plurals.hour_count, hour, hour);
                
                if (minute != 0) {
                    ret += ", " + res.getQuantityString(
                            R.plurals.minute_count, minute, minute);
                }
                
                return ret;
            }
        };        

        // Erase preferences ///////////////////////////////////////////////////
        SettingListener erasePreferences = new SettingListener() {
            @Override
            public void onSelected(SettingItem item) {
                mainActivity.erasePreferencesAndRestart();
            }
        };

        settingItems = new SettingItem[] {
            new NamedSeparator(R.string.label_accounts),
            new TextSettingWithValue(R.string.label_register_account,
                    registerAccount, regsiterAccountValue),
            new LineSeparator(),
//            new TextSettingWithValue(R.string.label_login_into_facebook,
//                    loginIntoFacebook, loginIntoFacebookValue),
            new FacebookLoginSetting(R.string.label_login_into_facebook,
                    loginIntoFacebook),
            new NamedSeparator(R.string.label_locations),
            new TextSettingWithMap(R.string.label_home_location,
                    homeLocation, homeLocationValue, mainActivity),
            new LineSeparator(),
            new TextSettingWithMap(R.string.label_work_location,
                    workLocation, workLocationValue, mainActivity),
            new NamedSeparator(R.string.label_time),
            new TextSettingWithValue(R.string.label_sleep_interval,
                    sleepInterval, sleepIntervalValue),
            new NamedSeparator(R.string.label_developer_tools),
            new TextSetting(R.string.label_erase_preferences, erasePreferences),
            new LineSeparator(),
        };
        
        SettingItemAdapter adapter = new SettingItemAdapter(getActivity(),
                settingItems);
        ListView settingsLv = (ListView) ret.findViewById(R.id.settingsLv);
        settingsLv.setAdapter(adapter);
        
        // Remove the divider since custom ones are used.
        settingsLv.setDivider(null);
        settingsLv.setDividerHeight(0);
        
        settingsLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                SettingItem item = settingItems[position];
                SettingListener listener = item.getListener();
                if (listener != null) {
                    listener.onSelected(item);
                }
                item.updateSelf();
            }
        });
        
        return ret;
    }
}