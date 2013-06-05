package ro.minimul.coma.stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ro.minimul.coma.R;
import ro.minimul.coma.fragment.HistoryMapFragment;
import ro.minimul.coma.stats.HistoryAxisView.OnScrollListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HistoryControllerView extends View implements OnTouchListener,
        OnScrollListener {
    private static final int YEAR = 0;
    private static final int INNER1 = 1;
    private static final int MONTH = 2;
    private static final int INNER2 = 3;
    private static final int DAY = 4;
    private static final int OUTER1 = 5;
    private static final int HOUR = 6;
    private static final int INNER3 = 7;
    private static final int MINUTE = 8;
    private static final int OUTER2 = 9;
    private static final int INTERVAL = 10;
        
    // The ones that can be selected (i.e. not the spaces between).
    private static final boolean[] SELECTABLE = {true, false, true, false, true,
            false, true, false, true, false, true};
    
    private static final long[] MAX = {
        1000L * 60 * 60 * 24 * 365,
        -1,
        1000L * 60 * 60 * 24 * 30,
        -1,
        1000L * 60 * 60 * 24,
        -1,
        1000L * 60 * 60,
        -1,
        1000L * 60,
        -1,
        1000L,
    };
    
    private static final SimpleDateFormat FORMAT
            = new SimpleDateFormat("yyyy;MM;dd;HH;mm", Locale.US);
    
    private Paint textPaint;
    private Paint selectedPaint;
    
    private int dateHeight;
    private int digitWidth;
    private int innerSpace;
    private int outerSpace;
    private int dateOffset;
    private int selectorWidth;
    
    private int[] spaces;
    private int[] offsets;
    private int[] textOffsets;
    private String[] text;
    private int selectedPart = 8;
    
    private final Date activeTime = new Date();
    private long timeInterval = 1000 * 60 * 60 * 24;

    private CurrentStats currentStats;
    private HistoryAxisView historyAxisView;
    private HistoryMapFragment historyMapFragment;
    
    public HistoryControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public void setCurrentStats(CurrentStats currentStats) {
        this.currentStats = currentStats;
    }
    
    public void setAxisView(HistoryAxisView historyAxisView) {
        this.historyAxisView = historyAxisView;
        this.historyAxisView.setOnScrollListener(this);
        
        this.historyAxisView.setScale(MAX[selectedPart]);
    }
    
    public void initTimeValues() {
        this.historyAxisView.setValuesMillis(
                currentStats.getStartTime(),
                currentStats.getEndTime(),
                currentStats.getEndTime());
        this.historyAxisView.invalidate();
    }
    
    public void setMapFragment(HistoryMapFragment historyMapFragment) {
        this.historyMapFragment = historyMapFragment;
    }
    
    private void init() {
        Resources res = getContext().getResources();
        
        initPaints(res);
        initXmlDimensions(res);
        initSelectableWidths();
        initTexts();
        initSelectableOffsets();
        
        dateOffset = (int) (dateHeight - (textPaint.descent()) / 2);
        
        setOnTouchListener(this);
    }

    private void initPaints(Resources res) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(res.getDimensionPixelSize(
                R.dimen.date_text_size));
        textPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/OpenSans-CondLight.ttf"));
        
        selectedPaint = new Paint();
        selectedPaint.setColor(0xFFdcd5c6);
    }

    private void initXmlDimensions(Resources res) {
        dateHeight = res.getDimensionPixelSize(R.dimen.date_height);
        digitWidth = res.getDimensionPixelSize(R.dimen.date_digit_width);
        innerSpace = res.getDimensionPixelSize(R.dimen.date_inner_space);
        outerSpace = res.getDimensionPixelSize(R.dimen.date_outer_space);
    }

    private void initSelectableWidths() {
        spaces = new int[11];
        spaces[YEAR] = 4 * digitWidth;
        spaces[INNER1] = innerSpace;
        spaces[MONTH] = 2 * digitWidth;
        spaces[INNER2] = innerSpace;
        spaces[DAY] = 2 * digitWidth;
        spaces[OUTER1] = outerSpace;
        spaces[HOUR] = 2 * digitWidth;
        spaces[INNER3] = innerSpace;
        spaces[MINUTE] = 2 * digitWidth;
        // Unknowable before determining the width.
        spaces[OUTER2] = 0; 
        spaces[INTERVAL] = 0;
    }

    private void initTexts() {
        text = new String[spaces.length];
        text[YEAR] = "9999";
        text[INNER1] = "-";
        text[MONTH] = "99";
        text[INNER2] = "-";
        text[DAY] = "99";
        text[OUTER1] = null;
        text[HOUR] = "99";
        text[INNER3] = ":";
        text[MINUTE] = "99";
        text[OUTER2] = null;
        text[INTERVAL] = null;
    }

    private void initSelectableOffsets() {
        offsets = new int[spaces.length];
        for (int i = 1; i <= MINUTE; i++) {
            offsets[i] = offsets[i - 1] + spaces[i - 1];
        }
        
        textOffsets = new int[spaces.length];
        float tWidth;
        for (int i = 0; i <= MINUTE; i++) {
            if (text[i] == null) {
                continue;
            }
            tWidth = textPaint.measureText(text[i]);
            textOffsets[i] = (int) (offsets[i] + (spaces[i] - tWidth) / 2);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        selectorWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(selectorWidth, dateHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        drawSelectedPart(canvas);
        updateDateText();
        drawDateText(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        //int y = (int) event.getY();
        
        checkSelection(x);
        
        return true;
    }
    
    private void checkSelection(int x) {
        int sum = 0;
        
        for (int i = 0; i < spaces.length; i++) {
            sum += spaces[i];
            
            if (x < sum) {
                if (SELECTABLE[i] && selectedPart != i) {
                    setActiveScale(i);
                }
                return;
            }
        }
    }
    
    private void setActiveScale(int index) {
        selectedPart = index;
        invalidate();
        historyAxisView.updateScale(MAX[selectedPart]);
    }
    
    private void drawSelectedPart(Canvas canvas) {
        int left = offsets[selectedPart];
        int right = left + spaces[selectedPart];
        canvas.drawRect(left, 0, right, dateHeight, selectedPaint);
    }
    
    private void updateDateText() {
        String[] dateParts = FORMAT.format(activeTime).split(";");
        
        text[YEAR] = dateParts[0];
        text[MONTH] = dateParts[1];
        text[DAY] = dateParts[2];
        text[HOUR] = dateParts[3];
        text[MINUTE] = dateParts[4];
    }

    private void drawDateText(Canvas canvas) {
        for (int i = 0; i <= MINUTE; i++) {
            if (text[i] == null) {
                continue;
            }
            canvas.drawText(text[i], textOffsets[i], dateOffset, textPaint);
        }
    }

    @Override
    public void onScroll(long currentValue) {
        activeTime.setTime(currentValue);
        invalidate();
        updateMapFragmentTimeShown();
    }
    
    private void updateMapFragmentTimeShown() {
        historyMapFragment.updateTimeShown(activeTime.getTime(), timeInterval);
    }
    
    public void initMapFragment() {
        updateMapFragmentTimeShown();
    }
}
