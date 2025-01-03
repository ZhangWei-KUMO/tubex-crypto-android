package chat.tubex.analysis.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import chat.tubex.analysis.model.AlertsResponse;

// 币安新闻API
public interface AlertsApi {
    @GET("/bapi/apex/v1/public/apex/cms/article/list/query")
    Call<AlertsResponse> getAlerts(@Query("pageNo") int pageNo,
                                 @Query("type") int type,
                                 @Query("pageSize") int pageSize,
                                 @Query("catalogId") int catalogId);
}

// 测试URL：https://www.binance.com/bapi/apex/v1/public/apex/cms/article/list/query?type=1&pageNo=1&pageSize=10&catalogId=49