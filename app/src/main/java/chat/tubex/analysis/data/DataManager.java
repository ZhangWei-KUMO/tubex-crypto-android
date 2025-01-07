package chat.tubex.analysis.data;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import chat.tubex.analysis.CryptoFeatureActivity;
import chat.tubex.analysis.chart.ChartManager;
import chat.tubex.analysis.data.api.BasisApi;
import chat.tubex.analysis.data.api.FundingRateApi;
import chat.tubex.analysis.data.api.KlinesApi;
import chat.tubex.analysis.data.api.TopTakerPositionApi;
import chat.tubex.analysis.model.Basis;
import chat.tubex.analysis.model.KlineItem;
import chat.tubex.analysis.model.TopTakerPosition;
import chat.tubex.analysis.model.FundingRateResponse;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import chat.tubex.analysis.utils.AlgorithmUtils;

public class DataManager {
    private static final String BASE_URL = "https://fapi.binance.com";
    private static final String TAG = "DataManager";
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final CryptoFeatureActivity activity;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final TextView loadingTextView;
    private Call<ResponseBody> currentCall;
    private Call<List<FundingRateResponse>> fundingRateCall;
    private Call<List<TopTakerPosition>> takerPositionCall;
    private Call<List<Basis>> basisCall;
    private ChartManager chartManager;
    // New TextViews for statistics
    private final TextView highPriceTextView;
    private final TextView lowPriceTextView;
    private final TextView medianPriceTextView;
    private final TextView maxVolumeTextView;
    private final TextView minVolumeTextView;
    private final TextView pearsonCorrelationTextView;
    private final TextView currentBasisTextView;
    private final TextView currentPriceTextView;
    private final TextView currentVolumeTextView;
    private final TextView historicalVar;
    private final TextView volatilityTextView;
    private final TextView fundingRateTextView;


    public DataManager(CryptoFeatureActivity activity, SwipeRefreshLayout swipeRefreshLayout,
                       TextView loadingTextView, TextView highPriceTextView, TextView lowPriceTextView,
                       TextView medianPriceTextView, TextView maxVolumeTextView, TextView minVolumeTextView,
                       TextView pearsonCorrelationTextView, TextView currentBasisTextView, TextView currentPriceTextView,
                       TextView currentVolumeTextView, TextView historicalVar, TextView volatilityTextView, TextView fundingRateTextView) {
        this.activity = activity;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.loadingTextView = loadingTextView;
        this.highPriceTextView = highPriceTextView;
        this.lowPriceTextView = lowPriceTextView;
        this.medianPriceTextView = medianPriceTextView;
        this.maxVolumeTextView = maxVolumeTextView;
        this.minVolumeTextView = minVolumeTextView;
        this.pearsonCorrelationTextView = pearsonCorrelationTextView;
        this.currentBasisTextView = currentBasisTextView;
        this.currentPriceTextView = currentPriceTextView;
        this.currentVolumeTextView = currentVolumeTextView;
        this.historicalVar = historicalVar;
        this.volatilityTextView = volatilityTextView;
        this.fundingRateTextView = fundingRateTextView;
    }

    public void setChartManager(ChartManager chartManager) {
        this.chartManager = chartManager;
    }


    public void fetchBasis(String pair) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BasisApi api = retrofit.create(BasisApi.class);
        basisCall = api.getBasis(pair, "1d", 365, "PERPETUAL");
        basisCall.enqueue(new Callback<List<Basis>>() {
            @Override
            public void onResponse(Call<List<Basis>> call, Response<List<Basis>> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> {
                        try {
                            List<Basis> basisItems = response.body();
                            if (basisItems != null && !basisItems.isEmpty()) {
                                Log.d(TAG, "Fetched basis data successfully, count: " + basisItems.size());
                                activity.runOnUiThread(() -> {
                                    setBasisText(basisItems);
                                    chartManager.setBasisChartData(basisItems);
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    Log.d(TAG, "basis data is empty");
                                    Toast.makeText(activity, "basis data is empty", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "Failed to parse basis data: " + e.getMessage());
                                Toast.makeText(activity, "Failed to parse basis data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } finally {
                            activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch basis data: " + response.message());
                    Toast.makeText(activity, "Failed to fetch basis data: " + response.message(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Basis>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch basis data: " + t.getMessage());
                Toast.makeText(activity, "Failed to fetch basis data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void setBasisText(List<Basis> basisItems) {
        if (basisItems == null || basisItems.isEmpty()) {
            currentBasisTextView.setText("basis data is empty");
            return;
        }
        double latestBasis = Double.parseDouble(basisItems.get(basisItems.size() - 1).getBasis());
        // 将最新基差显示在TextView上，如果负数则显示红色
        if (latestBasis < 0) {
            currentBasisTextView.setTextColor(Color.RED);
        } else {
            currentBasisTextView.setTextColor(Color.WHITE);
        }
        currentBasisTextView.setText(String.format("%.4f", latestBasis));
    }

    public void fetchTopTakerPosition(String symbol) {
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
                                Log.d(TAG, "Fetched taker position data successfully, count: " + takerPositionItems.size());
                                activity.runOnUiThread(() -> {
                                    chartManager.setTakerPositionChartData(takerPositionItems);
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    Log.d(TAG, "Taker position data is empty");
                                    Toast.makeText(activity, "Taker position data is empty", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "Failed to parse taker position data: " + e.getMessage());
                                Toast.makeText(activity, "Failed to parse taker position data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } finally {
                            activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch taker position data: " + response.message());
                    Toast.makeText(activity, "Failed to fetch taker position data: " + response.message(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<TopTakerPosition>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch taker position data: " + t.getMessage());
                Toast.makeText(activity, "Failed to fetch taker position data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void fetchFundingRate(String symbol) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FundingRateApi api = retrofit.create(FundingRateApi.class);
        fundingRateCall = api.getFundingRate(symbol);
        fundingRateCall.enqueue(new Callback<List<FundingRateResponse>>() {
            @Override
            public void onResponse(Call<List<FundingRateResponse>> call, Response<List<FundingRateResponse>> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> {
                        try {
                            List<FundingRateResponse> responseBody = response.body(); // 修改这里
                            if (responseBody != null && !responseBody.isEmpty()) {
                                // 这里使用第一个数据, 可以根据symbol进行判断使用哪个数据
                                FundingRateResponse firstResponse = responseBody.get(0);
                                String fundingRate = firstResponse.getFundingRate();
                                // 转换成浮点值并转换成百分比
                                double fundingRateValue = Double.parseDouble(fundingRate) * 100;
                                activity.runOnUiThread(() -> {
                                    // 显示当前合约手续费，如果小于0颜色为红色
                                    if (fundingRateValue < 0) {
                                        fundingRateTextView.setTextColor(Color.RED);
                                    } else {
                                        fundingRateTextView.setTextColor(Color.WHITE);
                                    }
                                    String formattedFundingRate = String.format("%.2f%%", fundingRateValue);
                                    fundingRateTextView.setText(formattedFundingRate);
                                    chartManager.setFundingRateChartData(responseBody);
                                });


                            } else {
                                activity.runOnUiThread(() -> {
                                    Log.d(TAG, "合约手续费 is null or empty");
                                    Toast.makeText(activity, "合约手续费 is null or empty", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JsonSyntaxException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
                                loadingTextView.setText("Failed to parse JSON");
                                Toast.makeText(activity, "Failed to parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "IOException: " + e.getMessage());
                                loadingTextView.setText("IOException");
                                Toast.makeText(activity, "IOException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } finally {
                            activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch funding rate: " + response.message());
                    Toast.makeText(activity, "Failed to fetch funding rate: " + response.message(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<FundingRateResponse>> call, Throwable t) { // 修改这里
                Log.e(TAG, "Failed to fetch funding rate: " + t.getMessage());
                Toast.makeText(activity, "Failed to fetch funding rate: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void fetchKlines(String symbol) {
        // Clear existing data
        chartManager.resetViewVisibility();
        loadingTextView.setVisibility(View.VISIBLE);
        loadingTextView.setText("Loading data...");
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
        currentCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> {
                        try {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                String json = responseBody.string();
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<List<String>>>() {
                                }.getType();
                                List<List<String>> klines = gson.fromJson(json, listType);

                                if (klines != null && !klines.isEmpty()) {
                                    List<KlineItem> klineItems = parseKlineData(klines);
                                    if (klineItems != null && !klineItems.isEmpty()) {
                                        activity.runOnUiThread(() -> {
                                            loadingTextView.setVisibility(View.GONE);
                                            chartManager.setViewVisibility();
                                            chartManager.setLineChartData(klineItems);
                                        });
                                    } else {
                                        activity.runOnUiThread(() -> {
                                            loadingTextView.setText("Kline data is empty");
                                            Toast.makeText(activity, "Kline data is empty", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } else {
                                    activity.runOnUiThread(() -> {
                                        Log.d(TAG, "Response data is empty");
                                        loadingTextView.setText("Response data is empty");
                                        Toast.makeText(activity, "Response data is empty", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                activity.runOnUiThread(() -> {
                                    Log.d(TAG, "Response body is null");
                                    loadingTextView.setText("Response body is null");
                                    Toast.makeText(activity, "Response body is null", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JsonSyntaxException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
                                loadingTextView.setText("Failed to parse JSON");
                                Toast.makeText(activity, "Failed to parse JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } catch (IOException e) {
                            activity.runOnUiThread(() -> {
                                Log.e(TAG, "IOException: " + e.getMessage());
                                loadingTextView.setText("IOException");
                                Toast.makeText(activity, "IOException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        } finally {
                            activity.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                        }
                    });
                } else {
                    Log.e(TAG, "Response error: " + response.message());
                    loadingTextView.setText("Failed to fetch kline data");
                    Toast.makeText(activity, "Failed to fetch kline data: " + response.message(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to fetch kline data: " + t.getMessage());
                loadingTextView.setText("Failed to fetch kline data");
                Toast.makeText(activity, "Failed to fetch kline data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Log.w(TAG, "Data size mismatch at index: " + i);
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
                    // 成交额
                    double volume = Double.parseDouble(item.get(7))/1000000;
                    maxPrice = Math.max(maxPrice, close);
                    minPrice = Math.min(minPrice, close);
                    priceList.add(close);
                    latestPrice = close;
                    maxVolume = Math.max(maxVolume, volume);
                    minVolume = Math.min(minVolume, volume);
                    volumeList.add(volume);
                    latestVolume = volume;

                    double volatility = 0;
                    int m = closePrices.length; // 假设 n 是 closePrices 的长度
                    double[] dailyReturns = new double[m - 1];

                    for (int j = 1; j < closePrices.length; j++) {
                        double prevClosePrice = closePrices[j - 1];
                        double closeP = closePrices[j];
                        double dailyReturn = (closeP - prevClosePrice) / prevClosePrice;
                        dailyReturns[j - 1] = dailyReturn; // 使用索引赋值
                    }

                    StandardDeviation standardDeviation = new StandardDeviation();
                    double dailyVolatility = standardDeviation.evaluate(dailyReturns);
                    volatility = dailyVolatility * Math.sqrt(365) * 100;

                    klineItems.add(new KlineItem(timestamp, open, high, low, close, (float) volume, (float) volatility));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Failed to parse numbers", e);
                    return null;
                }
            } else {
                Log.w(TAG, "Data size mismatch");
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
        double correlation = AlgorithmUtils.calculatePearsonCorrelation(closePrices, volumeValues);

        // Calculate Max Drawdown
        double maxDrawdown = AlgorithmUtils.calculateMaxDrawdown(priceList);
        // 从Klines数组中取最后一个元素，取其中的volatility
        double volatility = klineItems.get(klineItems.size()-1).getVolatility();

        // 计算单日收益率
        double[] returns = AlgorithmUtils.calculateReturns(priceList);
        double var = AlgorithmUtils.calculateEVT(returns);

        // Update TextViews on UI thread
        double finalMaxPrice = maxPrice;
        double finalMinPrice = minPrice;
        double finalPriceMedian = priceMedian;
        double finalMaxVolume = maxVolume;
        double finalMinVolume = minVolume;
        double finalCorrelation = correlation;
        double finalLatestPrice = latestPrice;
        double finalLatestVolume = latestVolume;
        double finalMaxDrawdown = maxDrawdown;
        double finalVar = var;
        double finalVolatility = volatility;
        // 数据绑定至UI

        activity.runOnUiThread(() -> {

            // 设置最高高价
            setColoredText(highPriceTextView, String.format("%.5f", finalMaxPrice), finalMaxPrice);

            // 设置最低价
            setColoredText(lowPriceTextView, String.format("%.5f", finalMinPrice), finalMinPrice);

            // 设置中位数价格
            setColoredText(medianPriceTextView, String.format("%.5f", finalPriceMedian), finalPriceMedian);

            // 设置历史最大交易量
            setColoredText(maxVolumeTextView, String.format("%.2f", finalMaxVolume*1000000), finalMaxVolume*1000000);

            // 设置最小交易量
            setColoredText(minVolumeTextView, String.format("%.2f", finalMinVolume*1000000), finalMinVolume*1000000);

            // 设置皮尔逊相关系数
            setColoredText(pearsonCorrelationTextView, String.format("%.4f", finalCorrelation), finalCorrelation);

            // 设置当前价格
            setColoredText(currentPriceTextView, String.format("%.5f", finalLatestPrice), finalLatestPrice);

            // 设置当前交易量
            setColoredText(currentVolumeTextView, String.format("%.2f", finalLatestVolume), finalLatestVolume);

            // 设置最大回撤
            TextView maxDrawdownTextView = activity.findViewById(chat.tubex.analysis.R.id.maxDrawdownTextView);
            double percentageDrawdown = finalMaxDrawdown * 100;
            setColoredText(maxDrawdownTextView, String.format("%.2f%%", percentageDrawdown), percentageDrawdown);

            // 设置 VaR
            double percentageFinalVar = finalVar * 100;
            TextView finalTextView = activity.findViewById(chat.tubex.analysis.R.id.extremeRiskTextView);
            setColoredText(finalTextView, String.format("%.2f%%", percentageFinalVar), percentageFinalVar);

            // 设置波动率
            TextView volatilityTextView = activity.findViewById(chat.tubex.analysis.R.id.volatilityTextView);
            double percentageVolatility = finalVolatility;
            setColoredText(volatilityTextView, String.format("%.2f%%", percentageVolatility), percentageVolatility);
        });



        return klineItems;
    }
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (currentCall != null) {
            currentCall.cancel();
        }
        if (takerPositionCall != null) {
            takerPositionCall.cancel();
        }
        if (basisCall != null) {
            basisCall.cancel();
        }

    }

    private void setColoredText(TextView textView, String formattedText, double value) {
        textView.setText(formattedText);
        if (value < 0) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.WHITE);
        }
    }

}