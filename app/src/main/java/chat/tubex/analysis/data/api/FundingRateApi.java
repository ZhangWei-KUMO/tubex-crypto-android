package chat.tubex.analysis.data.api;

import chat.tubex.analysis.model.FundingRateResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface FundingRateApi {
    @GET("/fapi/v1/fundingRate")
    Call<List<FundingRateResponse>> getFundingRate(@Query("symbol") String symbol);
}

// 测试接口：https://www.binance.com/fapi/v1/fundingRate?symbol=BTCUSDT