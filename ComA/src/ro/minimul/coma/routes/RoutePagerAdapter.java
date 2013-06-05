package ro.minimul.coma.routes;

import ro.minimul.coma.fragment.RouteLocationsFragment;
import ro.minimul.coma.fragment.RouteArrivalFragment;
import ro.minimul.coma.fragment.RouteTransportFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class RoutePagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments = new Fragment[3];

    public RoutePagerAdapter(FragmentManager fm) {
        super(fm);
        
        fragments[0] = new RouteLocationsFragment();
        fragments[1] = new RouteArrivalFragment();
        fragments[2] = new RouteTransportFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
