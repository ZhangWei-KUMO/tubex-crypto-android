package chat.tubex.analysis.data.api;

import chat.tubex.analysis.model.GoogleTranslatorResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleTranslatorApi {
    @GET("/translate_a/single?client=gtx&sl=en&tl=zh-CN&hl=zh-CN&dt=t&tk=946553&source=1&dj=1")
    Call<GoogleTranslatorResponse> translate(@Query("q") String query);
}