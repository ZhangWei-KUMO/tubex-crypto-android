package chat.tubex.analysis.chart;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import chat.tubex.analysis.CryptoFeatureActivity;
import chat.tubex.analysis.model.Basis;
import chat.tubex.analysis.model.KlineItem;
import chat.tubex.analysis.model.TopTakerPosition;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartManager {
    private final CryptoFeatureActivity activity;
    private final LineChart priceLineChart;
    private final LineChart volatilityLineChart;
    private final BarChart volumeLineChart;
    private final LineChart takerPositionLineChart;
    private final BarChart basisBarChart;

    public ChartManager(CryptoFeatureActivity activity) {
        this.activity = activity;
        this.priceLineChart = activity.findViewById(chat.tubex.analysis.R.id.priceLineChart);
        this.volatilityLineChart = activity.findViewById(chat.tubex.analysis.R.id.volatilityLineChart);
        this.volumeLineChart = activity.findViewById(chat.tubex.analysis.R.id.volumeBarChart);
        this.takerPositionLineChart = activity.findViewById(chat.tubex.analysis.R.id.takerPositionLineChart);
        this.basisBarChart = activity.findViewById(chat.tubex.analysis.R.id.basisBarChart);
    }

    public void setTakerPositionChartData(List<TopTakerPosition> takerPositionItems) {
        List<Entry> takerPositionEntries = new ArrayList<>();
        float latestLongShortRatio = 0f;
        if (takerPositionItems != null && !takerPositionItems.isEmpty()) {

            for (TopTakerPosition item : takerPositionItems) {
                float timestamp = (float) item.getTimestamp();
                float longShortRatio = Float.parseFloat(item.getLongShortRatio());
                latestLongShortRatio = longShortRatio;
                takerPositionEntries.add(new Entry(timestamp, longShortRatio));
            }

            String ratioText = String.format("%.4f", latestLongShortRatio);
            // 渲染做市商多空比数据
            activity.runOnUiThread(() -> {
                TextView takerPositionRatioTextView = activity.findViewById(chat.tubex.analysis.R.id.takerPositionRatioTextView);
                takerPositionRatioTextView.setText(ratioText);
            });
        }else{
            activity.runOnUiThread(() -> {
                TextView takerPositionRatioTextView = activity.findViewById(chat.tubex.analysis.R.id.takerPositionRatioTextView);
                takerPositionRatioTextView.setText("N/A");
            });

        }
        LineDataSet takerPositionDataSet = new LineDataSet(takerPositionEntries, "做市商多空比");
        takerPositionDataSet.setColor(Color.CYAN);
        takerPositionDataSet.setLineWidth(2f);
        takerPositionDataSet.setDrawCircles(false);
        takerPositionDataSet.setDrawValues(false);
        LineData takerPositionData = new LineData(takerPositionDataSet);
        takerPositionLineChart.setData(takerPositionData);
        takerPositionLineChart.getAxisRight().setEnabled(false);
        ChartUtils.setChartTextColor(takerPositionLineChart);
        XAxis takerPositionXAxis = takerPositionLineChart.getXAxis();
        takerPositionXAxis.setValueFormatter(new TimeAxisValueFormatter());
        takerPositionXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        takerPositionXAxis.setGranularity(1f);
        takerPositionXAxis.setLabelCount(5, true);
        takerPositionXAxis.setAvoidFirstLastClipping(true);

        takerPositionLineChart.setDragEnabled(true);
        takerPositionLineChart.setScaleEnabled(true);
        takerPositionLineChart.setTouchEnabled(true);
        takerPositionLineChart.getDescription().setEnabled(false);
        takerPositionLineChart.invalidate();
    }
    public void setBasisChartData(List<Basis> basisItems) {
        List<BarEntry> basisEntries = new ArrayList<>();

        if (basisItems != null && !basisItems.isEmpty()) {
            for (int i = 0; i < basisItems.size(); i++) {
                Basis item = basisItems.get(i);
                float basisValue = Float.parseFloat(item.getBasis());
                basisEntries.add(new BarEntry(i, basisValue)); // Use index as x-value
            }
        }
        BarDataSet basisDataSet = new BarDataSet(basisEntries, "基差");
        basisDataSet.setColor(0xFFFFD700);
        basisDataSet.setDrawValues(false);
        BarData basisBarData = new BarData(basisDataSet);
        basisBarChart.setData(basisBarData);
        basisBarChart.getAxisRight().setEnabled(false);
        XAxis basisXAxis = basisBarChart.getXAxis();
        basisXAxis.setValueFormatter(null);
        basisXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        basisXAxis.setGranularity(1f);
        basisXAxis.setLabelCount(5, true);
        basisXAxis.setAvoidFirstLastClipping(true);
        // 设置Chart字体颜色
        basisXAxis.setTextColor(Color.WHITE); // X-axis labels
        basisBarChart.getAxisLeft().setTextColor(Color.WHITE); // Left Y-axis labels
        basisBarChart.getLegend().setTextColor(Color.WHITE); // Legend text
        basisBarChart.getDescription().setTextColor(Color.WHITE); // Description text
        basisBarChart.setNoDataTextColor(Color.WHITE); // "No data" text


        basisBarChart.setDragEnabled(true);
        basisBarChart.setScaleEnabled(true);
        basisBarChart.setTouchEnabled(true);
        basisBarChart.getDescription().setEnabled(false);
        basisBarChart.invalidate();
    }

    public void setLineChartData(List<KlineItem> klineItems) {
        List<Entry> priceEntries = new ArrayList<>();
        List<Entry> volatilityEntries = new ArrayList<>();
        List<BarEntry> volumeEntries = new ArrayList<>();


        for (int i = 0; i < klineItems.size(); i++) {
            KlineItem klineItem = klineItems.get(i);
            float timestamp = (float) klineItem.getTimestamp();
            float price = (float) klineItem.getClose();
            float volatility = klineItem.getVolatility();
            float volume = klineItem.getVolume();

            priceEntries.add(new Entry(timestamp, price));
            volatilityEntries.add(new Entry(timestamp, volatility));
            volumeEntries.add(new BarEntry(i, volume));
        }


        // 设置价格图表数据
        LineDataSet priceDataSet = new LineDataSet(priceEntries, "合约价格(USTD)");
        priceDataSet.setColor(0xFF9029FF);
        priceDataSet.setLineWidth(2f);
        priceDataSet.setDrawValues(false);
        priceDataSet.setDrawCircles(false);

        LineData priceLineData = new LineData(priceDataSet);
        priceLineChart.setData(priceLineData);
        priceLineChart.getAxisRight().setEnabled(false);
        priceLineChart.getXAxis().setTextColor(Color.WHITE);
        priceLineChart.getAxisLeft().setTextColor(Color.WHITE);
        priceLineChart.getLegend().setTextColor(Color.WHITE);
        priceLineChart.getDescription().setTextColor(Color.WHITE);
        priceLineChart.setNoDataTextColor(Color.WHITE);
        ChartUtils.setChartTextColor(priceLineChart);
        XAxis priceXAxis = priceLineChart.getXAxis();
        priceXAxis.setValueFormatter(new TimeAxisValueFormatter());
        priceXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        priceXAxis.setGranularity(1f);
        priceXAxis.setLabelCount(5, true);
        priceXAxis.setAvoidFirstLastClipping(true);

        priceLineChart.setDragEnabled(true);
        priceLineChart.setScaleEnabled(true);
        priceLineChart.setTouchEnabled(true);
        priceLineChart.getDescription().setEnabled(false);
        priceLineChart.invalidate();


        // 设置波动率图表数据
        LineDataSet volatilityDataSet = new LineDataSet(volatilityEntries, "年化波动率");
        volatilityDataSet.setColor(Color.RED);
        volatilityDataSet.setLineWidth(2f);
        volatilityDataSet.setDrawValues(false);
        volatilityDataSet.setDrawCircles(false);

        LineData volatilityLineData = new LineData(volatilityDataSet);
        volatilityLineChart.setData(volatilityLineData);
        volatilityLineChart.getAxisRight().setEnabled(false);
        volatilityLineChart.getXAxis().setTextColor(Color.WHITE);
        volatilityLineChart.getAxisLeft().setTextColor(Color.WHITE);
        volatilityLineChart.getLegend().setTextColor(Color.WHITE);
        volatilityLineChart.getDescription().setTextColor(Color.WHITE);
        volatilityLineChart.setNoDataTextColor(Color.WHITE);
        ChartUtils.setChartTextColor(volatilityLineChart);
        XAxis volatilityXAxis = volatilityLineChart.getXAxis();
        volatilityXAxis.setValueFormatter(new TimeAxisValueFormatter());
        volatilityXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        volatilityXAxis.setGranularity(1f);
        volatilityXAxis.setLabelCount(5, true);
        volatilityXAxis.setAvoidFirstLastClipping(true);

        volatilityLineChart.setDragEnabled(true);
        volatilityLineChart.setScaleEnabled(true);
        volatilityLineChart.setTouchEnabled(true);
        volatilityLineChart.getDescription().setEnabled(false);
        volatilityLineChart.invalidate();

        // 设置交易量图表数据
        BarDataSet volumeDataSet = new BarDataSet(volumeEntries, "加密货币交易量 (百万)");
        volumeDataSet.setColor(Color.GREEN);
        volumeDataSet.setDrawValues(false);
        BarData volumeBarData = new BarData(volumeDataSet);
        volumeLineChart.setData(volumeBarData);
        volumeLineChart.getAxisRight().setEnabled(false);
        volumeLineChart.getXAxis().setTextColor(Color.WHITE);
        volumeLineChart.getAxisLeft().setTextColor(Color.WHITE);
        volumeLineChart.getLegend().setTextColor(Color.WHITE);
        volumeLineChart.getDescription().setTextColor(Color.WHITE);
        volumeLineChart.setNoDataTextColor(Color.WHITE);
        XAxis volumeXAxis = volumeLineChart.getXAxis();
        volumeXAxis.setValueFormatter(null);
        volumeXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        volumeXAxis.setGranularity(1f);
        volumeXAxis.setLabelCount(5, true);
        volumeXAxis.setAvoidFirstLastClipping(true);
        volumeLineChart.setDragEnabled(true);
        volumeLineChart.setScaleEnabled(true);
        volumeLineChart.setTouchEnabled(true);
        volumeLineChart.getDescription().setEnabled(false);
        volumeLineChart.invalidate();
    }
    public void resetViewVisibility() {
        basisBarChart.setVisibility(View.GONE);
        priceLineChart.setVisibility(View.GONE);
        volatilityLineChart.setVisibility(View.GONE);
        volumeLineChart.setVisibility(View.GONE);
        TextView takerPositionRatioTextView = activity.findViewById(chat.tubex.analysis.R.id.takerPositionRatioTextView);
        takerPositionRatioTextView.setVisibility(View.GONE);
        takerPositionLineChart.setVisibility(View.GONE);
        TextView basisTextView = activity.findViewById(chat.tubex.analysis.R.id.basisTextView);
        basisTextView.setVisibility(View.GONE);

    }

    public void setViewVisibility() {
        basisBarChart.setVisibility(View.VISIBLE);
        priceLineChart.setVisibility(View.VISIBLE);
        volatilityLineChart.setVisibility(View.VISIBLE);
        volumeLineChart.setVisibility(View.VISIBLE);
        TextView takerPositionRatioTextView = activity.findViewById(chat.tubex.analysis.R.id.takerPositionRatioTextView);
        takerPositionRatioTextView.setVisibility(View.VISIBLE);
        takerPositionLineChart.setVisibility(View.VISIBLE);
        TextView basisTextView = activity.findViewById(chat.tubex.analysis.R.id.basisTextView);
        basisTextView.setVisibility(View.VISIBLE);
    }

    private static class TimeAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {
            return dateFormat.format(new Date((long) value));
        }
    }
}