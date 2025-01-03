package chat.tubex.analysis.chart;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;

// 做市商UI的设计
public class ChartUtils {
    public static void setChartTextColor(LineChart chart) {
        chart.getXAxis().setTextColor(Color.WHITE); // X-axis labels
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getLegend().setTextColor(Color.WHITE); // Legend text
        chart.getDescription().setTextColor(Color.WHITE); // Description text
        chart.setNoDataTextColor(Color.WHITE); // "No data" text

    }
}