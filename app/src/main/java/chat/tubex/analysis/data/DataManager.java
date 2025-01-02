package chat.tubex.analysis.data;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import chat.tubex.analysis.CryptoFeatureActivity;
import chat.tubex.analysis.chart.ChartManager;
import chat.tubex.analysis.data.api.BasisApi;
import chat.tubex.analysis.data.api.KlinesApi;
import chat.tubex.analysis.data.api.TopTakerPositionApi;
import chat.tubex.analysis.model.Basis;
import chat.tubex.analysis.model.KlineItem;
import chat.tubex.analysis.model.TopTakerPosition;
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
    private final TextView CVar;
    private final TextView volatilityTextView;


    public DataManager(CryptoFeatureActivity activity, SwipeRefreshLayout swipeRefreshLayout,
                       TextView loadingTextView, TextView highPriceTextView, TextView lowPriceTextView,
                       TextView medianPriceTextView, TextView maxVolumeTextView, TextView minVolumeTextView,
                       TextView pearsonCorrelationTextView, TextView currentBasisTextView, TextView currentPriceTextView,
                       TextView currentVolumeTextView, TextView historicalVar, TextView CVar, TextView volatilityTextView) {
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
        this.CVar = CVar;
        this.volatilityTextView = volatilityTextView;
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
        // Calculate Volatility
        double volatility = AlgorithmUtils.calculateVolatility(priceList);

        // Calculate VaR and CVaR
        double[] returns = AlgorithmUtils.calculateReturns(priceList);
        double var = AlgorithmUtils.calculateHistoricalVaR(returns, 0.99);
        double cvar = AlgorithmUtils.calculateConditionalVaR(returns, 0.99);

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
        double finalCvar = cvar;
        double finalVolatility = volatility;

        activity.runOnUiThread(() -> {
            highPriceTextView.setText(String.format("%.5f", finalMaxPrice));
            lowPriceTextView.setText(String.format("%.5f", finalMinPrice));
            medianPriceTextView.setText(String.format("%.5f", finalPriceMedian));
            maxVolumeTextView.setText(String.format("%.2f", finalMaxVolume));
            minVolumeTextView.setText(String.format("%.2f", finalMinVolume));
            pearsonCorrelationTextView.setText(String.format("%.4f", finalCorrelation));
            currentPriceTextView.setText(String.format("%.5f", finalLatestPrice));
            currentVolumeTextView.setText(String.format("%.2f", finalLatestVolume));
            TextView maxDrawdownTextView = activity.findViewById(chat.tubex.analysis.R.id.maxDrawdownTextView);
            double percentageDrawdown = finalMaxDrawdown * 100; //Convert to percentage
            maxDrawdownTextView.setText(String.format("%.2f%%", percentageDrawdown)); // Format as percentage with 2 decimal places
            historicalVar.setText(String.format("%.4f", finalVar));
            CVar.setText(String.format("%.4f", finalCvar));
            volatilityTextView.setText(String.format("%.4f", finalVolatility));
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

}