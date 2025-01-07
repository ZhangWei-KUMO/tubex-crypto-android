package chat.tubex.analysis.ui.home;

import retrofit2.Call;
import retrofit2.http.GET;
// 定义一个公共接口
public interface BinanceApi {
    @GET("/bapi/composite/v1/public/future/external/topSearchList?businessEnum=USDT_FUTURES")
    Call<BinanceResponse> getTopSearchList();
}