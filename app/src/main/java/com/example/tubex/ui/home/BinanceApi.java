package com.example.tubex.ui.home;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
// 定义一个公共接口
public interface BinanceApi {
    @GET("/bapi/composite/v1/public/future/external/topSearchList?businessEnum=USDT_FUTURES")
    /**
     * 这里我们定义了一个getTopSearch方法，该方法返回的对象
     * 是一个Call方法，是一个列表形式。
     * 这个所谓的CAll方法是指retrofit2的Call方法
     * */
    Call<BinanceResponse> getTopSearchList(); // 修改返回值类型
}