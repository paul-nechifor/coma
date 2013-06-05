package ro.minimul.coma.fragment;

import ro.minimul.coma.R;
import ro.minimul.coma.activity.MainActivity;
import ro.minimul.coma.stats.HistoryAxisView;
import ro.minimul.coma.stats.HistoryControllerView;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryFragment extends Fragment {
    private static View view;

    private MainActivity mainActivity;
    private HistoryControllerView historyControllerView;
    private HistoryAxisView historyAxisView;
    private HistoryMapFragment historyMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_history, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        
        mainActivity = (MainActivity) getActivity();
        historyControllerView = (HistoryControllerView) view.findViewById(
                R.id.historyControllerView);
        historyAxisView = (HistoryAxisView) view.findViewById(
                R.id.historyAxisView);
        historyMapFragment = (HistoryMapFragment) getFragmentManager()
                .findFragmentById(R.id.historyMapFragment);
        

        historyControllerView.setAxisView(historyAxisView);
        historyControllerView.setMapFragment(historyMapFragment);
        historyControllerView.initMapFragment();
        
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                historyMapFragment.reloadAggregatorData();
                return null;
            }
            
            protected void onPostExecute(Void results) {
                historyControllerView.setCurrentStats(
                        mainActivity.getPrefs().getCurrentStats());
                historyControllerView.initTimeValues();
            }
        };
        
        task.execute(new Void[0]);
        
        return view;
    }
}
