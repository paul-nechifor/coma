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
        int[] values = { 1, 2, 3, 4, 5 };
        CategorySeries series = new CategorySeries("");
        int k = 0;
        for (int value : values) {
            series.add("Section " + ++k, value);
        }

        DefaultRenderer renderer = new DefaultRenderer();
        
        for (int i = 0; i < values.length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(COLORS[i]);
            renderer.addSeriesRenderer(r);
        }
        
        renderer.setLabelsTextSize(18);
        renderer.setLabelsColor(0xFF000000);
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
}
