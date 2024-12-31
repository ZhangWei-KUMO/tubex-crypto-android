package chat.tubex.analysis;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import chat.tubex.analysis.data.api.TopTakerPositionApi;
import chat.tubex.analysis.model.KlineItem;
import chat.tubex.analysis.model.TopTakerPosition;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.tubex.analysis.data.api.KlinesApi;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;


public class CryptoFeatureActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://fapi.binance.com";
    private static final String TAG = "CryptoFeatureActivity";
    private LineChart priceLineChart;
    private LineChart volatilityLineChart;
    private LineChart volumeLineChart;
    private LineChart takerPositionLineChart;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView loadingTextView;
    private TextView statisticsTextView;
    private TextView takerPositionRatioTextView; // 新增多空比 TextView
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private String currentSymbol;
    private Call<okhttp3.ResponseBody> currentCall;
    private Call<List<TopTakerPosition>> takerPositionCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        currentSymbol = getIntent().getStringExtra("symbol");
        TextView symbolTextView = findViewById(R.id.symbolTextView);
        priceLineChart = findViewById(R.id.priceLineChart);
        volatilityLineChart = findViewById(R.id.volatilityLineChart);
        volumeLineChart = findViewById(R.id.volumeLineChart);
        takerPositionLineChart = findViewById(R.id.takerPositionLineChart);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        loadingTextView = findViewById(R.id.loadingTextView);
        statisticsTextView = findViewById(R.id.statisticsTextView);
        takerPositionRatioTextView = findViewById(R.id.takerPositionRatioTextView); // 获取多空比 TextView


        if (symbolTextView != null && currentSymbol != null) {
            symbolTextView.setText(currentSymbol + "合约分析");
            fetchKlines(currentSymbol);
            fetchTopTakerPosition(currentSymbol);
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchKlines(currentSymbol);
            fetchTopTakerPosition(currentSymbol);
        });
        setSystemBarsColor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (currentCall != null) {
            currentCall.cancel();
        }
        if (takerPositionCall != null) {
            takerPositionCall.cancel();
        }
    }


    private void fetchTopTakerPosition(String symbol) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TopTakerPositionApi api = retrofit.create(TopTakerPositionApi.class);
        takerPositionCall = api.getTopTakerPosition(symbol, "1d", 10);
        takerPositionCall.enqueue(new Callback<List<TopTakerPosition>>() {
            @Override
            public void onResponse(Call<List<TopTakerPosition>> call, Response<List<TopTakerPosition>> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> {
                        try {
                            List<TopTakerPosition> takerPositionItems = response.body();
                            if (takerPositionItems != null && !takerPositionItems.isEmpty()) {
                                Log.d(TAG, "获取做市商数据成功，数据条数:" + takerPositionItems.size());
                                runOnUiThread(() -> {
                                    setTakerPositionChartData(takerPositionItems); // 调用设置takerPosition图表的方法
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "做市商数据为空");
                                    Toast.makeText(CryptoFeatureActivity.this, "获取做市商数据为空", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Log.e(TAG, "解析做市商数据失败:" + e.getMessage());
                                Toast.makeText(CryptoFeatureActivity.this, "解析做市商数据失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } finally {
                            runOnUiThread(() -> {
                                swipeRefreshLayout.setRefreshing(false);
                            });
                        }
                    });
                } else {
                    Log.e(TAG, "获取做市商信息失败:" + response.message());
                    Toast.makeText(CryptoFeatureActivity.this, "获取做市商信息失败:" + response.message(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<TopTakerPosition>> call, Throwable t) {
                Log.e(TAG, "获取做市商信息失败:" + t.getMessage());
                Toast.makeText(CryptoFeatureActivity.this, "获取做市商信息失败:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void fetchKlines(String symbol) {
        statisticsTextView.setVisibility(View.GONE);
        priceLineChart.setVisibility(View.GONE);
        volatilityLineChart.setVisibility(View.GONE);
        volumeLineChart.setVisibility(View.GONE);
        takerPositionRatioTextView.setVisibility(View.GONE);
        takerPositionLineChart.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.VISIBLE);
        loadingTextView.setText("加载中...");
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KlinesApi api = retrofit.create(KlinesApi.class);
        currentCall = api.getKlines(symbol, 365, "1d");
        currentCall.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> {
                        try {
                            okhttp3.ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                String json = responseBody.string();
                                Log.d(TAG, "Raw JSON: " + json);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<List<String>>>() {
                                }.getType();
                                List<List<String>> klines = gson.fromJson(json, listType);

                                if (klines != null && !klines.isEmpty()) {
                                    List<KlineItem> klineItems = parseKlineData(klines);
                                    if (klineItems != null && !klineItems.isEmpty()) {
                                        Log.d(TAG, "获取成功，数据条数:" + klineItems.size());
                                        runOnUiThread(() -> {
                                            loadingTextView.setVisibility(View.GONE);
                                            statisticsTextView.setVisibility(View.VISIBLE);
                                            priceLineChart.setVisibility(View.VISIBLE);
                                            volatilityLineChart.setVisibility(View.VISIBLE);
                                            volumeLineChart.setVisibility(View.VISIBLE);
                                            takerPositionRatioTextView.setVisibility(View.VISIBLE);
                                            takerPositionLineChart.setVisibility(View.VISIBLE);
                                            setLineChartData(klineItems);
                                        });
                                    } else {
                                        runOnUiThread(() -> {
                                            Log.d(TAG, "数据为空");
                                            loadingTextView.setText("获取数据为空");
                                            Toast.makeText(CryptoFeatureActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> {
                                        Log.d(TAG, "response data为空");
                                        loadingTextView.setText("获取数据为空");
                                        Toast.makeText(CryptoFeatureActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                runOnUiThread(() -> {
                                    Log.d(TAG, "response data为空");
                                    loadingTextView.setText("获取数据为空");
                                    Toast.makeText(CryptoFeatureActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JsonSyntaxException e) {
                            runOnUiThread(() -> {
                                Log.e(TAG, "解析原始 JSON 数据失败，JSON 格式错误:", e);
                                loadingTextView.setText("解析数据失败");
                                Toast.makeText(CryptoFeatureActivity.this, "解析原始 JSON 数据失败，JSON 格式错误:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                Log.e(TAG, "IOException:", e);
                                loadingTextView.setText("解析数据失败");
                                Toast.makeText(CryptoFeatureActivity.this, "IOException" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } finally {
                            runOnUiThread(() -> {
                                swipeRefreshLayout.setRefreshing(false);
                            });
                        }
                    });
                } else {
                    Log.e(TAG, "response error:" + response.message());
                    loadingTextView.setText("获取数据失败");
                    Toast.makeText(CryptoFeatureActivity.this, "获取数据失败:" + response.message(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Log.e(TAG, "获取kline信息失败:" + t.getMessage());
                loadingTextView.setText("获取数据失败");
                Toast.makeText(CryptoFeatureActivity.this, "获取数据失败:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<KlineItem> parseKlineData(List<List<String>> rawData) {
        if (rawData == null || rawData.isEmpty()) {
            return null;
        }
        List<KlineItem> klineItems = new ArrayList<>();
        int n = rawData.size();
        if (n <= 1) {
            return null;
        }

        double[] closePrices = new double[n];
        double[] volumeValues = new double[n];
        for (int i = 0; i < n; i++) {
            if (rawData.get(i).size() >= 5) {
                closePrices[i] = Double.parseDouble(rawData.get(i).get(4));
                volumeValues[i] = Double.parseDouble(rawData.get(i).get(5));
            } else {
                Log.w(TAG, "数据长度不匹配");
            }

        }
        double maxPrice = Double.MIN_VALUE;
        double minPrice = Double.MAX_VALUE;
        List<Double> priceList = new ArrayList<>();
        double latestPrice = 0;

        double maxVolume = Double.MIN_VALUE;
        double minVolume = Double.MAX_VALUE;
        List<Double> volumeList = new ArrayList<>();
        double latestVolume = 0;

        for (int i = 0; i < n; i++) {
            List<String> item = rawData.get(i);
            if (item.size() >= 5) {
                try {
                    long timestamp = Long.parseLong(item.get(0));
                    double open = Double.parseDouble(item.get(1));
                    double high = Double.parseDouble(item.get(2));
                    double low = Double.parseDouble(item.get(3));
                    double close = Double.parseDouble(item.get(4));
                    double volume = Double.parseDouble(item.get(5))/1000000;
                    maxPrice = Math.max(maxPrice, close);
                    minPrice = Math.min(minPrice, close);
                    priceList.add(close);
                    latestPrice = close;
                    maxVolume = Math.max(maxVolume, volume);
                    minVolume = Math.min(minVolume, volume);
                    volumeList.add(volume);
                    latestVolume = volume;

                    double volatility = 0;
                    if (i > 0) {
                        double prevClosePrice = closePrices[i - 1];
                        double dailyReturn = (close - prevClosePrice) / prevClosePrice;
                        double dailyVolatility = Math.sqrt(dailyReturn * dailyReturn);
                        volatility = dailyVolatility * Math.sqrt(365);
                    }

                    klineItems.add(new KlineItem(timestamp, open, high, low, close, (float) volume, (float) volatility));

                } catch (NumberFormatException e) {
                    Log.e(TAG, "解析数字失败", e);
                    return null;
                }
            } else {
                Log.w(TAG, "数据长度不匹配");
            }
        }
        // Calculate price median
        Collections.sort(priceList);
        double priceMedian;
        int priceSize = priceList.size();
        if (priceSize % 2 == 0) {
            priceMedian = (priceList.get(priceSize / 2 - 1) + priceList.get(priceSize / 2)) / 2.0;
        } else {
            priceMedian = priceList.get(priceSize / 2);
        }

        //Calculate volume median
        Collections.sort(volumeList);
        double volumeMedian;
        int volumeSize = volumeList.size();
        if (volumeSize % 2 == 0) {
            volumeMedian = (volumeList.get(volumeSize / 2 - 1) + volumeList.get(volumeSize / 2)) / 2.0;
        } else {
            volumeMedian = volumeList.get(volumeSize / 2);
        }

        double priceMultiple = 0;
        if (minPrice != 0) {
            priceMultiple = maxPrice / minPrice;
        }

        double volumeMultiple = 0;
        if (minVolume != 0) {
            volumeMultiple = maxVolume / minVolume;
        }
        // Calculate Pearson Correlation Coefficient
        double correlation = calculatePearsonCorrelation(closePrices, volumeValues);


        String statistics = "最新价: " + String.format("%.5f", latestPrice) +
                "\n最高价: " + String.format("%.5f", maxPrice) +
                "\n最低价: " + String.format("%.5f", minPrice) +
                "\n中位数: " + String.format("%.5f", priceMedian) +
                "\n最大/最小倍数: " + String.format("%.2f", priceMultiple) +
                "\n\n最新交易量: " + String.format("%.2f", latestVolume) +
                "\n最大交易量: " + String.format("%.2f", maxVolume) +
                "\n最小交易量: " + String.format("%.2f", minVolume) +
                "\n交易量中位数: " + String.format("%.2f", volumeMedian) +
                "\n交易量最大/最小倍数: " + String.format("%.2f", volumeMultiple) +
                "\n\n量价皮尔逊相关性系数: " + String.format("%.4f", correlation);
        runOnUiThread(() -> {
            statisticsTextView.setText(statistics);
        });

        return klineItems;
    }

    // Method to calculate Pearson correlation coefficient
    private double calculatePearsonCorrelation(double[] prices, double[] volumes) {
        if (prices == null || volumes == null || prices.length != volumes.length || prices.length < 2) {
            return Double.NaN; // Return NaN if input arrays are invalid
        }

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        return pearsonsCorrelation.correlation(prices, volumes);
    }

    private void setTakerPositionChartData(List<TopTakerPosition> takerPositionItems) {
        List<Entry> takerPositionEntries = new ArrayList<>();
        float latestLongShortRatio = 0f;
        if (takerPositionItems != null && !takerPositionItems.isEmpty()) {

            for (TopTakerPosition item : takerPositionItems) {
                float timestamp = (float) item.getTimestamp();
                // getLongShortRatio获取多空比（类型为字符串）
                float longShortRatio = Float.parseFloat(item.getLongShortRatio());
                latestLongShortRatio = longShortRatio;

                takerPositionEntries.add(new Entry(timestamp, longShortRatio));
            }

            // 更新 TextView
            String ratioText = "当前多空比: " + String.format("%.4f", latestLongShortRatio);
            runOnUiThread(() -> {
                takerPositionRatioTextView.setText(ratioText);
            });
        }else{
            runOnUiThread(() -> {
                takerPositionRatioTextView.setText("当前多空比: N/A");
            });
        }
        LineDataSet takerPositionDataSet = new LineDataSet(takerPositionEntries, "做市商多空比例");
        takerPositionDataSet.setColor(Color.CYAN);
        takerPositionDataSet.setLineWidth(2f);
        takerPositionDataSet.setDrawCircles(false);

        LineData takerPositionData = new LineData(takerPositionDataSet);
        takerPositionLineChart.setData(takerPositionData);
        takerPositionLineChart.getAxisRight().setEnabled(false); // 禁用右侧 Y 轴

        XAxis takerPositionXAxis = takerPositionLineChart.getXAxis();
        takerPositionXAxis.setValueFormatter(new TimeAxisValueFormatter());
        takerPositionXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        takerPositionXAxis.setGranularity(1f);
        takerPositionXAxis.setLabelCount(5, true);
        takerPositionXAxis.setAvoidFirstLastClipping(true);
        // 设置 X 轴文字颜色
        takerPositionXAxis.setTextColor(Color.WHITE);
        // 设置 Y 轴文字颜色
        takerPositionLineChart.getAxisLeft().setTextColor(Color.WHITE);
        // 设置图例文字颜色 (如果有图例)
        if(takerPositionLineChart.getLegend() != null) {
            takerPositionLineChart.getLegend().setTextColor(Color.WHITE);
        }
        takerPositionLineChart.setDragEnabled(true);
        takerPositionLineChart.setScaleEnabled(true);
        takerPositionLineChart.setTouchEnabled(true);
        takerPositionLineChart.getDescription().setEnabled(false);
        takerPositionLineChart.invalidate();

    }

    private void setLineChartData(List<KlineItem> klineItems) {
        List<Entry> priceEntries = new ArrayList<>();
        List<Entry> volatilityEntries = new ArrayList<>();
        List<Entry> volumeEntries = new ArrayList<>();

        for (int i = 0; i < klineItems.size(); i++) {
            KlineItem klineItem = klineItems.get(i);
            // 获取时间戳
            float timestamp = (float) klineItem.getTimestamp();
            // 获取每日收盘价
            float price = (float) klineItem.getClose();
            // 获取年化波动率
            float volatility = klineItem.getVolatility();
            // 获取交易量(Coin)
            float volume = klineItem.getVolume();

            priceEntries.add(new Entry(timestamp, price));
            volatilityEntries.add(new Entry(timestamp, volatility));
            volumeEntries.add(new Entry(timestamp, volume));
        }


        // 设置价格图表数据
        LineDataSet priceDataSet = new LineDataSet(priceEntries, "价格");
        priceDataSet.setColor(Color.BLUE);
        priceDataSet.setLineWidth(2f);
        priceDataSet.setDrawCircles(false);

        LineData priceLineData = new LineData(priceDataSet);
        priceLineChart.setData(priceLineData);
        priceLineChart.getAxisRight().setEnabled(false); // 禁用右侧 Y 轴

        XAxis priceXAxis = priceLineChart.getXAxis();
        priceXAxis.setValueFormatter(new TimeAxisValueFormatter());
        priceXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        priceXAxis.setGranularity(1f);
        priceXAxis.setLabelCount(5, true);
        priceXAxis.setAvoidFirstLastClipping(true);
        // 设置 X 轴文字颜色
        priceXAxis.setTextColor(Color.WHITE);
        //  设置 Y 轴文字颜色
        priceLineChart.getAxisLeft().setTextColor(Color.WHITE);
        // 设置图例文字颜色 (如果有图例)
        if(priceLineChart.getLegend() != null) {
            priceLineChart.getLegend().setTextColor(Color.WHITE);
        }
        priceLineChart.setDragEnabled(true);
        priceLineChart.setScaleEnabled(true);
        priceLineChart.setTouchEnabled(true);
        priceLineChart.getDescription().setEnabled(false);
        priceLineChart.invalidate();


        // 设置波动率图表数据
        LineDataSet volatilityDataSet = new LineDataSet(volatilityEntries, "年化波动率");
        volatilityDataSet.setColor(Color.RED);
        volatilityDataSet.setLineWidth(2f);
        volatilityDataSet.setDrawCircles(false);

        LineData volatilityLineData = new LineData(volatilityDataSet);
        volatilityLineChart.setData(volatilityLineData);
        volatilityLineChart.getAxisRight().setEnabled(false); // 禁用右侧 Y 轴

        XAxis volatilityXAxis = volatilityLineChart.getXAxis();
        volatilityXAxis.setValueFormatter(new TimeAxisValueFormatter());
        volatilityXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        volatilityXAxis.setGranularity(1f);
        volatilityXAxis.setLabelCount(5, true);
        volatilityXAxis.setAvoidFirstLastClipping(true);
        // 设置 X 轴文字颜色
        volatilityXAxis.setTextColor(Color.WHITE);
        // 设置 Y 轴文字颜色
        volatilityLineChart.getAxisLeft().setTextColor(Color.WHITE);
        // 设置图例文字颜色 (如果有图例)
        if(volatilityLineChart.getLegend() != null) {
            volatilityLineChart.getLegend().setTextColor(Color.WHITE);
        }
        volatilityLineChart.setDragEnabled(true);
        volatilityLineChart.setScaleEnabled(true);
        volatilityLineChart.setTouchEnabled(true);
        volatilityLineChart.getDescription().setEnabled(false);
        volatilityLineChart.invalidate();
        // 设置交易量图表数据
        LineDataSet volumeDataSet = new LineDataSet(volumeEntries, "交易量(million)");
        volumeDataSet.setColor(Color.GREEN);
        volumeDataSet.setLineWidth(2f);
        volumeDataSet.setDrawCircles(false);

        LineData volumeLineData = new LineData(volumeDataSet);
        volumeLineChart.setData(volumeLineData);
        volumeLineChart.getAxisRight().setEnabled(false); // 禁用右侧 Y 轴

        XAxis volumeXAxis = volumeLineChart.getXAxis();
        volumeXAxis.setValueFormatter(new TimeAxisValueFormatter());
        volumeXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        volumeXAxis.setGranularity(1f);
        volumeXAxis.setLabelCount(5, true);
        volumeXAxis.setAvoidFirstLastClipping(true);
       //  设置 X 轴文字颜色
        volumeXAxis.setTextColor(Color.WHITE);
        // 设置 Y 轴文字颜色
        volumeLineChart.getAxisLeft().setTextColor(Color.WHITE);
        // 设置图例文字颜色 (如果有图例)
        if(volumeLineChart.getLegend() != null) {
            volumeLineChart.getLegend().setTextColor(Color.WHITE);
        }
        volumeLineChart.setDragEnabled(true);
        volumeLineChart.setScaleEnabled(true);
        volumeLineChart.setTouchEnabled(true);
        volumeLineChart.getDescription().setEnabled(false);
        volumeLineChart.invalidate();
    }

    private static class TimeAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {
            return dateFormat.format(new Date((long) value));
        }
    }

    private void setSystemBarsColor() {
        Window window = getWindow();
        //如果系统版本高于 Android L (API 21)，使用以下方式设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // 设置状态栏为黑色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

            //设置导航栏为黑色
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

            //设置状态栏字体为白色(api>=23,  API<23 需要调整布局)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~uiOptions);

                //设置导航栏字体为白色 (API>=26, API < 26需要调整布局)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
            }
        }
    }
}