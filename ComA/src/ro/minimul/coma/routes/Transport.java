package ro.minimul.coma.routes;

import ro.minimul.coma.R;

public class Transport {
    public static enum Type {
        TRAIN(R.drawable.normal_train, R.drawable.small_train),
        TRAM(R.drawable.normal_tram, R.drawable.small_tram),
        SUBWAY(R.drawable.normal_subway, R.drawable.small_subway),
        BUS(R.drawable.normal_bus, R.drawable.small_bus),
        FOOT(R.drawable.normal_foot, R.drawable.small_foot);
        
        private int bigId;
        private int smallId;
        
        private Type(int bigId, int smallId) {
            this.bigId = bigId;
            this.smallId = smallId;
        }
        
        public int getBigId() {
            return bigId;
        }
        
        public int getSmallId() {
            return smallId;
        }
    }
}
