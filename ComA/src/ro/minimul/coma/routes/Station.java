package ro.minimul.coma.routes;

public class Station {
    public final int id;
    public final String name;
    public final double lat;
    public final double lng;
    
    public Station(int id, String name, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}
