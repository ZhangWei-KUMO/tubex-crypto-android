package chat.tubex.analysis.model;

import com.google.gson.annotations.SerializedName;

public class TopTakerPosition {

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("longAccount")
    private String longAccount;

    @SerializedName("longShortRatio")
    private String longShortRatio;

    @SerializedName("shortAccount")
    private String shortAccount;

    @SerializedName("timestamp")
    private long timestamp;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLongAccount() {
        return longAccount;
    }

    public void setLongAccount(String longAccount) {
        this.longAccount = longAccount;
    }

    public String getLongShortRatio() {
        return longShortRatio;
    }

    public void setLongShortRatio(String longShortRatio) {
        this.longShortRatio = longShortRatio;
    }

    public String getShortAccount() {
        return shortAccount;
    }

    public void setShortAccount(String shortAccount) {
        this.shortAccount = shortAccount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TopTakerPosition{" +
                "symbol='" + symbol + '\'' +
                ", longAccount='" + longAccount + '\'' +
                ", longShortRatio='" + longShortRatio + '\'' +
                ", shortAccount='" + shortAccount + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}