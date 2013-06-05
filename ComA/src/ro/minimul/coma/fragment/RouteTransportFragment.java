package ro.minimul.coma.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ro.minimul.coma.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.DragNDropSimpleAdapter;

public class RouteTransportFragment extends Fragment {
    private DragNDropListView transportDdlv;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_route_transport,
                container, false);
        
        transportDdlv = (DragNDropListView) ret.findViewById(R.id.transportDdlv);
        

        ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 30; ++i) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", "item" + i);
            item.put("_id", i);
            
            items.add(item);
        }
        
        transportDdlv.setDragNDropAdapter(new DragNDropSimpleAdapter(
                getActivity(),
                items,
                R.layout.item_transport,
                new String[]{"name"},
                new int[]{R.id.transportNameTv},
                R.id.dragHandlerIv));
        
        return ret;
    }
}