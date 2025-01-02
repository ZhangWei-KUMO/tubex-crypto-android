package chat.tubex.analysis.data.api;
import java.util.List;

import chat.tubex.analysis.model.Basis;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 永续合约基差
public interface BasisApi {
    @GET("/futures/data/basis")
    Call<List<Basis>> getBasis(@Query("pair") String pair,
                                              @Query("period") String period,
                                              @Query("limit") int limit,
                                              @Query("contractType") String contractType);
}

// contractType=PERPETUAL
// 测试URL：https://fapi.binance.com/futures/data/basis?pair=BTCUSDT&period=1d&contractType=PERPETUAL&limit=365