package chat.tubex.analysis.model;

public class FundingRateResponse {
    private String symbol;
    private String fundingRate;
    private long fundingTime;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getFundingRate() {
        return fundingRate;
    }

    public void setFundingRate(String fundingRate) {
        this.fundingRate = fundingRate;
    }

    public long getFundingTime() {
        return fundingTime;
    }

    public void setFundingTime(long fundingTime) {
        this.fundingTime = fundingTime;
    }

    // 获取时间戳
    public long getTimestamp() {
        return fundingTime;
    }

}