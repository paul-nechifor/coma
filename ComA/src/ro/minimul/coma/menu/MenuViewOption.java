package ro.minimul.coma.menu;

import android.content.Context;

public class MenuViewOption {
    private final int iconId;
    private final int nameId;
    
    public static class Loaded {
        public final String icon;
        public final String name;
        
        private Loaded(String icon, String name) {
            this.icon = icon;
            this.name = name;
        }
    }
    
    public MenuViewOption(int iconId, int nameId) {
        this.iconId = iconId;
        this.nameId = nameId;
    }
    
    public Loaded getLoaded(Context context) {
        return new Loaded(context.getString(iconId), context.getString(nameId));
    }
    
    public static Loaded[] getAll(MenuViewOption[] options, Context context) {
        Loaded[] ret = new Loaded[options.length];
        
        for (int i = 0; i < options.length; i++) {
            ret[i] = options[i].getLoaded(context);
        }
        
        return ret;
    }
}