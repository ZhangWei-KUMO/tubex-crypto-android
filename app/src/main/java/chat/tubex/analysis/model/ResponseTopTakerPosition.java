package chat.tubex.analysis.model;
import java.util.List;

public class ResponseTopTakerPosition {
    private List<List<String>> klines; // 修改为 String 类型
    public List<List<String>> getKlines() {
        return klines;
    }

    public void setKlines(List<List<String>> klines) {
        this.klines = klines;
    }
}