package ro.minimul.coma.stats;

import java.lang.reflect.Field;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PieChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import ro.minimul.coma.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class DoughnutChartView {
//    private static final int[] COLORS = {
//        0xFFe8583e,
//        0xFF1ca4aa,
//        0xFFdd9944,
//        0xFF913828,
//        0xFF726846,
//        0xFF8cc15d,
//        0xFF33352f,
//        0xFFf2de57,
//        0xFFa7b174,
//    };

    private static final int[] COLORS = {
        0xFF43b0cd,
        0xFF278585,
        0xFFe2543c,
        0xFFe96e4a,
        0xFFffd75d,
    };
    
    private static final String[] LABELS = {
        "Sleeping",
        "In transit",
        "Other",
        "At home",
        "At work",
    };
    
    private DoughnutChartView() {
    }
    
    private static class PieHoleView extends View {
        private final Paint holePaint = new Paint();
        private final Paint translucentHolePaint = new Paint();
        private float centerX = -1;
        private float centerY = -1;
        private boolean firstDraw = true;
        private GraphicalView graphicalView;

        public PieHoleView(Context context) {
            super(context);
            Resources res = context.getResources();
            int basicBackground = res.getColor(R.color.basic_background);
            holePaint.setColor(basicBackground);
            holePaint.setAntiAlias(true);
            translucentHolePaint.setColor(0x33FFFFFF & basicBackground);
            translucentHolePaint.setAntiAlias(true);
        }
        
        public void setGraphicalView(GraphicalView view) {
            graphicalView = view;
        }
        
        private void changeCenter() {
            try {
                Field field = GraphicalView.class.getDeclaredField("mChart");
                field.setAccessible(true);
                PieChart pieChart = (PieChart) field.get(graphicalView);
                centerX = pieChart.getCenterX();
                centerY = pieChart.getCenterY();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (firstDraw) {
                firstDraw = false;
                changeCenter();
            }
    
            canvas.drawCircle(centerX, centerY, 210, translucentHolePaint);
            canvas.drawCircle(centerX, centerY, 100, holePaint);
        }
    }
    
    public static void inflateInto(Context context, ViewGroup viewGroup) {
        final GraphicalView view = getTimeSpent(context);
        viewGroup.addView(view);
        
        PieHoleView pieHoleView = new PieHoleView(context);
        pieHoleView.setGraphicalView(view);
        viewGroup.addView(pieHoleView);
    }

    
    private static GraphicalView getTimeSpent(Context context) {
        int[] values = randomValues();
        CategorySeries series = new CategorySeries("");
        int k = 0;
        for (int value : values) {
            series.add(LABELS[k], value);
            k++;
        }

        DefaultRenderer renderer = new DefaultRenderer();
        
        for (int i = 0; i < values.length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(COLORS[i]);
            renderer.addSeriesRenderer(r);
        }
        
        renderer.setLabelsTextSize(22);
        renderer.setLabelsColor(0xFF222222);
        renderer.setShowLegend(false);
        renderer.setZoomButtonsVisible(false);
        renderer.setExternalZoomEnabled(false);
        
        GraphicalView view = ChartFactory.getPieChartView(context, series,
                renderer);
        
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        
        return view;
    }
    
    private static int[] randomValues() {
        double left = 24000;
        int[] ret = new int[5];
        
        ret[0] = (int) extract(left, 0.3, 0.33);
        left -= ret[0];
        ret[1] = (int) extract(left, 0.05, 0.1);
        left -= ret[1];
        ret[2] = (int) extract(left, 0.1, 0.15);
        left -= ret[2];
        ret[3] = (int) extract(left, 0.4, 0.6);
        left -= ret[3];
        ret[4] = (int) left;
        
        return ret;
    }
    
    private static double extract(double left, double startRatio,
            double endRatio) {
        double ratio = Math.random() * (endRatio - startRatio) + startRatio;
        return left * ratio;
    }
}
