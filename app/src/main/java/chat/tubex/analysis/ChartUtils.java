package chat.tubex.analysis;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class ChartUtils {
    public static void setChartTextColor(LineChart lineChart) {
        // 设置X轴标签颜色
        lineChart.getXAxis().setTextColor(Color.WHITE);

        // 设置左侧Y轴标签颜色
        lineChart.getAxisLeft().setTextColor(Color.WHITE);

        // 设置右侧Y轴标签颜色（如果存在）
        lineChart.getAxisRight().setTextColor(Color.WHITE);

        // 设置网格线颜色
        lineChart.getAxisLeft().setGridColor(Color.parseColor("#404040"));
        lineChart.getXAxis().setGridColor(Color.parseColor("#404040"));

        // 设置图例颜色
        Legend legend = lineChart.getLegend();
        if (legend != null) {
            legend.setTextColor(Color.WHITE);
        }
        LineData data = lineChart.getData();

        if (data != null){
            for(int i = 0; i < data.getDataSetCount(); i++){
                LineDataSet set = (LineDataSet) data.getDataSetByIndex(i);
                //设置数据点标签颜色
                set.setValueTextColor(Color.WHITE);
            }
        }
    }
}