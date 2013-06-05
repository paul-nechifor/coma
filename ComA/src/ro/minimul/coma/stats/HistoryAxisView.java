package ro.minimul.coma.stats;

import ro.minimul.coma.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HistoryAxisView extends View implements OnTouchListener {
    public static interface OnScrollListener {
        public void onScroll(long currentValue);
    }
    
    private static final OnScrollListener DUMMY = new OnScrollListener() {
        @Override
        public void onScroll(long currentValue) {
        }
    };
    
    private int viewHeight;
    private int viewWidth;
    private int lineHeight;
    private int padding;
    private int lineMiddle;
    
    private int lastDragPosition;
    
    private boolean valuesSet = false;
    private long minValue;
    private long maxValue;
    private long currentValue;
    private long scale;
    
    private int centerPosition;
    private int displayableWidth;
    private int halfDisplayableWidth;
    private int degrees = 30;
    private int degreeLength;
    private int degreeOffset = 0;
    
    private boolean viewIsTouched = false;
    
    private Paint linePaint;
    private Paint markerPaint;
    private Paint markerStrokePaint;
    private Paint backgroundPaint;
    private Rect lineRect = new Rect();
    private Rect selectedRect = new Rect();
    private Rect backgroundRect = new Rect();
    private Path markerPath;
    
    private OnScrollListener onScrollListener = DUMMY;
    
    public HistoryAxisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        if (onScrollListener == null) {
            this.onScrollListener = DUMMY;
        } else {
            this.onScrollListener = onScrollListener;
        }
    }
    
    public void setScale(long scale) {
        this.scale = scale;
    }
    
    public void updateScale(long scale) {
        setScale(scale);
        modifyPosition();
        invalidate();
    }
    
    public void setValuesMillis(long minValue, long maxValue, long currentValue) {
        this.valuesSet = true;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
    }
    
    private void init() {
        Resources res = getContext().getResources();

        viewHeight = res.getDimensionPixelSize(R.dimen.axis_height);
        lineHeight = res.getDimensionPixelSize(R.dimen.axis_line_height);
        padding = res.getDimensionPixelSize(R.dimen.axis_padding);
                
        linePaint = new Paint();
        linePaint.setColor(0xFFb1927a);
        
        markerPaint = new Paint();
        markerPaint.setColor(0xFFfed65b);
        
        markerStrokePaint = new Paint();
        markerStrokePaint.setColor(0xFF000000);
        markerStrokePaint.setStyle(Paint.Style.STROKE);
        markerStrokePaint.setAntiAlias(true);
        
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFdcd5c6);
                
        setOnTouchListener(this);
    }
    
    private void modifyPosition() {
        long leftLength = currentValue - minValue;
        long rightLength = maxValue - currentValue;
        
        long scaledLeftLength = leftLength / scale;
        long scaledRightLength = rightLength / scale;
        
        if (scaledLeftLength > halfDisplayableWidth) {
            scaledLeftLength = halfDisplayableWidth;
        }
        
        if (scaledRightLength > halfDisplayableWidth) {
            scaledRightLength = halfDisplayableWidth;
        }
        
        lineRect.left = centerPosition - (int) scaledLeftLength;
        lineRect.right = centerPosition + (int) scaledRightLength;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);

        lineRect.top = (viewHeight - lineHeight) / 2;
        lineRect.bottom = lineRect.top + lineHeight;
        
        selectedRect.top = lineRect.top;
        selectedRect.bottom = lineRect.bottom;
        
        lineMiddle = viewHeight / 2;
        centerPosition = viewWidth / 2;
        
        displayableWidth = viewWidth - 2 * padding;
        halfDisplayableWidth = displayableWidth / 2;
        degreeLength = displayableWidth / degrees;
        
        backgroundRect.left = 0;
        backgroundRect.top = 0;
        backgroundRect.right = viewWidth;
        backgroundRect.bottom = viewHeight;
        
        markerPath = new Path();
        RectF arcRect = new RectF();
        arcRect.left = centerPosition - 10;
        arcRect.right = centerPosition + 10;
        arcRect.top = lineMiddle - 35;
        arcRect.bottom = lineMiddle - 10;
        markerPath.addArc(arcRect, -220f, 260f);
        markerPath.lineTo(centerPosition, lineMiddle);
        markerPath.close();
        
        modifyPosition();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!valuesSet) {
            return;
        }
        
        if (viewIsTouched) {
            canvas.drawRect(backgroundRect, backgroundPaint);
        }
        
        canvas.drawRect(lineRect, linePaint);
        
        float left = padding + degreeOffset;
        float right = padding + 3 + degreeOffset;
        float top = lineMiddle - 20;
        float bottom = lineMiddle + 20;
        
        while (right < viewWidth - padding) {
            canvas.drawRect(left, top, right, bottom, linePaint);
            left += degreeLength;
            right += degreeLength;
        }
        
        canvas.drawPath(markerPath, markerPaint);
        canvas.drawPath(markerPath, markerStrokePaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        
        int action = event.getAction();
        
        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
            onActionDown(x, y);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            onActionUp(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            onActionMove(x, y);
            break;
        }
        
        invalidate();
        
        return true;
    }
    
    private void onActionDown(int x, int y) {
        lastDragPosition = x;
        viewIsTouched = true;
    }
    
    private void onActionUp(int x, int y) {
        viewIsTouched = false;
    }
    
    private void onActionMove(int x, int y) {
        int diff = lastDragPosition - x;
        lastDragPosition = x;
        
        if (diff == 0) {
            return;
        }

        long nextCurrentValue = (long) (currentValue + diff * scale);
        
        // Making sure the next value is between the min and the max.
        if (diff > 0 && nextCurrentValue > maxValue) {
            diff = (int) ((maxValue - currentValue) / scale);
            nextCurrentValue = maxValue;
        } else if (diff < 0 && nextCurrentValue < minValue) {
            diff = (int) ((minValue - currentValue) / scale);
            nextCurrentValue = minValue;
        }
        
        if (currentValue == nextCurrentValue) {
            return;
        }
        
        currentValue = nextCurrentValue;
        degreeOffset = (degreeOffset - (diff % degreeLength) + degreeLength)
                % degreeLength;
        
        modifyPosition();
        
        onScrollListener.onScroll(currentValue);
    }
}