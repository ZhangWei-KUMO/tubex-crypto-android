package chat.tubex.analysis.ui.alerts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import chat.tubex.analysis.data.api.AlertsApi;
import chat.tubex.analysis.data.api.GoogleTranslatorApi;
import chat.tubex.analysis.model.AlertsResponse;
import chat.tubex.analysis.model.GoogleTranslatorResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertsViewModel extends ViewModel {

    private final MutableLiveData<AlertsResponse> alertsResponseLiveData;
    private static final String BASE_URL = "https://www.binance.com";
    private static final String GOOGLE_TRANSLATOR_URL = "https://translate.googleapis.com";
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit googleTranslatorRetrofit = new Retrofit.Builder()
            .baseUrl(GOOGLE_TRANSLATOR_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final GoogleTranslatorApi googleTranslatorApi = googleTranslatorRetrofit.create(GoogleTranslatorApi.class);


    public AlertsViewModel() {
        alertsResponseLiveData = new MutableLiveData<>();
        fetchData(null); // Initial fetch
    }

    public LiveData<AlertsResponse> getNewsList() {
        return alertsResponseLiveData;
    }

    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    public interface DataFetchCallback {
        void onSuccess();

        void onFailure(String message);
    }


    public void fetchData(DataFetchCallback callback) {
        isRefreshing.setValue(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AlertsApi api = retrofit.create(AlertsApi.class);
        Call<AlertsResponse> call = api.getAlerts(1, 1, 10, 49);
        call.enqueue(new Callback<AlertsResponse>() {
            @Override
            public void onResponse(Call<AlertsResponse> call, Response<AlertsResponse> response) {
                isRefreshing.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    AlertsResponse alertsResponse = response.body();
                    translateArticleTitles(alertsResponse, callback);
                } else {
                    if (callback != null) {
                        callback.onFailure("服务器返回数据为空或者解析失败");
                    }
                }
            }

            @Override
            public void onFailure(Call<AlertsResponse> call, Throwable t) {
                isRefreshing.setValue(false);
                if (callback != null) {
                    callback.onFailure("请求失败：" + t.getMessage());
                }
                t.printStackTrace();
            }
        });
    }

    //  翻译文章标题的方法
    private void translateArticleTitles(AlertsResponse alertsResponse, DataFetchCallback callback) {
        if (alertsResponse == null || alertsResponse.getData() == null || alertsResponse.getData().getCatalogs() == null) {
            alertsResponseLiveData.setValue(alertsResponse);
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }
        List<AlertsResponse.Catalog> catalogs = alertsResponse.getData().getCatalogs();
        int totalArticles = 0;
        final int[] translatedArticles = {0};

        for (AlertsResponse.Catalog catalog : catalogs) {
            if (catalog.getArticles() != null) {
                totalArticles += catalog.getArticles().size();
            }
        }
        if (totalArticles == 0) {
            alertsResponseLiveData.setValue(alertsResponse);
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }

        for (AlertsResponse.Catalog catalog : catalogs) {
            if (catalog.getArticles() != null) {
                for (AlertsResponse.Article article : catalog.getArticles()) {
                    String originalTitle = article.getTitle();
                    Call<GoogleTranslatorResponse> call = googleTranslatorApi.translate(originalTitle);
                    int finalTotalArticles = totalArticles;
                    call.enqueue(new Callback<GoogleTranslatorResponse>() {
                        @Override
                        public void onResponse(Call<GoogleTranslatorResponse> call, Response<GoogleTranslatorResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getSentences() != null && response.body().getSentences().length > 0) {
                                String translatedTitle = response.body().getSentences()[0].getTranslation();
                                article.setTitle(translatedTitle);
                            }
                            translatedArticles[0]++;
                            if (translatedArticles[0] == finalTotalArticles) {
                                alertsResponseLiveData.setValue(alertsResponse);
                                if (callback != null) {
                                    callback.onSuccess();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GoogleTranslatorResponse> call, Throwable t) {
                            // 翻译失败处理, 保留原title
                            translatedArticles[0]++;
                            if (translatedArticles[0] == finalTotalArticles) {
                                alertsResponseLiveData.setValue(alertsResponse);
                                if (callback != null) {
                                    callback.onSuccess();
                                }
                            }
                            t.printStackTrace();
                        }
                    });
                }
            }
        }
    }
}