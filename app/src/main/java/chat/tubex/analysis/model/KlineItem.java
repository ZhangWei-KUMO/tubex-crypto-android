package chat.tubex.analysis.model;

public class KlineItem {
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private float volume;

    private float volatility;

    public KlineItem(long timestamp, double open, double high, double low, double close, float volume, float volatility) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.volatility = volatility;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public float getVolume() {  // 修改 getter 方法的返回类型为 float
        return volume;
    }

    // 计算年化波动率
    public float getVolatility(){
        return volatility;
    }
}