package ro.minimul.coma.menu;

import ro.minimul.coma.R;
import ro.minimul.coma.activity.MainActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MenuView extends View implements View.OnTouchListener {
    private static final int DELAY_MILLI = 20;
    private static final double TAKE_ON_FRAME = 0.4;
    private static final String ACTIVE_TAB = "activeTab";
    private static final String SUPER_STATE = "superState";
    
    private class AnimationThread extends Thread {
        private transient boolean keepRunning = true;
        
        @Override
        public void run() {
            while (keepRunning) {
                if (updateAnimation) {
                    MenuView.this.postInvalidate();
                }
                try {
                    Thread.sleep(DELAY_MILLI);
                } catch (InterruptedException e) {
                }
            }
        }
    }
    
    public static interface OnTabSelectedListener {
        public void onTabSelected(int index);
    }
    
    private static final OnTabSelectedListener TAB_SELECTED_DUMMY
            = new OnTabSelectedListener() {
        @Override
        public void onTabSelected(int index) {
        }
    };
    
    private final Context context;
    private MenuViewOption.Loaded[] tabs;
    private double[] extras;
    private int activeTab;
    private Paint[] paints;
    private Paint activePaint;
    private Paint symbolPaint;
    private Paint textPaint;
    private Paint tabBarPaint;
    private int symbolSize;
    private AnimationThread animationThread;
    private transient boolean updateAnimation = false;
    private OnTabSelectedListener onTabSelectedListener = TAB_SELECTED_DUMMY;
    
    
    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    
    public void init() {
        
        Resources res = context.getResources();
        symbolSize = res.getDimensionPixelSize(
                R.dimen.menu_symbol_size);
        
        symbolPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        symbolPaint.setColor(0xFF413e37);
        symbolPaint.setTextSize(symbolSize);
        symbolPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/Entypo.ttf"));
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(res.getDimensionPixelSize(
                R.dimen.menu_text_size));
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/OpenSans-CondLight.ttf"));
        
        tabBarPaint = new Paint();
        tabBarPaint.setColor(0xFFdcd5c6);
        
        activePaint = new Paint();
        activePaint.setColor(0xFFfed65b);
        
        tabs = MenuViewOption.getAll(MainActivity.OPTIONS, getContext());
        
        extras = new double[tabs.length];
        initSelected(0);
        
        paints = new Paint[2];
        paints[0] = new Paint();
        paints[0].setColor(res.getColor(R.color.menu_a));
        paints[1] = new Paint();
        paints[1].setColor(res.getColor(R.color.menu_b));
        
        setOnTouchListener(this);
        
        animationThread = new AnimationThread();
        animationThread.start();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        animationThread.keepRunning = false;
        animationThread.interrupt();
        
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (updateAnimation) {
            double collect = 0.0;
            double collectOnTab;
            for (int i = 0; i < extras.length; i++) {
                if (extras[i] > 0.0 && i != activeTab) {
                    collectOnTab = TAKE_ON_FRAME;
                    if (collectOnTab >= extras[i]) {
                        collectOnTab = extras[i];
                        extras[i] = 0.0;
                    } else {
                        extras[i] -= collectOnTab;
                    }
                    collect += collectOnTab;
                }
            }
            extras[activeTab] += collect;
            if (extras[activeTab] >= 1.0) {
                extras[activeTab] = 1.0;
                updateAnimation = false;
            }
        }
        
        int width = getWidth();
        int height = getHeight();
        int optionNr = tabs.length;
        int optionSize = height;
        int nonSymbolSpace = width - (optionSize * optionNr);
        int leftPos = 0;
        
        
        
        canvas.drawRect(0, 0, width, height, tabBarPaint);
        
        Paint paint;
        
        
        for (int i = 0; i < optionNr; i++) {
            if (i == activeTab) {
                paint = activePaint;
            } else {
                paint = paints[i % 2];
            }
            
            int optionWidth = (int) Math.round(optionSize + extras[i] * nonSymbolSpace);
            canvas.drawRect(leftPos, (int)(optionSize * 0.9), leftPos + optionWidth, optionSize, paint);
            
            canvas.drawText(tabs[i].icon,
                    leftPos+optionSize/2.0f - symbolSize * 0.25f,
                    optionSize/2 + symbolSize * 0.25f,
                    symbolPaint);
            
            if (extras[i] > 0.0) {
                canvas.drawText(tabs[i].name,
                        leftPos+optionSize/2.0f + symbolSize * (0.25f + 0.15f),
                        optionSize/2 + symbolSize * 0.25f,
                        textPaint);
            }
            
            leftPos += optionWidth;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != android.view.MotionEvent.ACTION_UP) {
            return true;
        }
        int x = (int) event.getX();
        int width = getWidth();
        int height = getHeight();
        int optionNr = tabs.length;
        int optionSize = height;
        int nonSymbolSpace = width - (optionSize * optionNr);
        int leftPos = 0;
        
        for (int i = 0; i < tabs.length; i++) {
            int optionWidth = (int) Math.round(optionSize + extras[i] * nonSymbolSpace);
            leftPos += optionWidth;
            if (x < leftPos) {
                setSelected(i);
                break;
            }
        }
        
        return true;
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE));
        initSelected(bundle.getInt(ACTIVE_TAB, 0));
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        
        Bundle bundle = new Bundle();
        bundle.putInt(ACTIVE_TAB, activeTab);
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState());
        
        return bundle;
    }
    
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        if (listener == null) {
            this.onTabSelectedListener = TAB_SELECTED_DUMMY;
        } else { 
            this.onTabSelectedListener = listener;
        }
    }
    
    private void setSelected(int index) {
        activeTab = index;
        onTabSelectedListener.onTabSelected(index);
        updateAnimation = true;
    }
    
    private void initSelected(int index) {
        activeTab = index;
        
        for (int i = 0; i < extras.length; i++) {
            extras[i] = 0.0;
        }
        
        extras[activeTab] = 1.0;
        onTabSelectedListener.onTabSelected(index);
    }
}
