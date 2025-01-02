package chat.tubex.analysis.model;

import com.google.gson.annotations.SerializedName;

public class Basis {

    @SerializedName("indexPrice")
    private String indexPrice;

    @SerializedName("contractType")
    private String contractType;

    @SerializedName("basisRate")
    private String basisRate;

    @SerializedName("futuresPrice")
    private String futuresPrice;

    @SerializedName("annualizedBasisRate")
    private String annualizedBasisRate;

    @SerializedName("basis")
    private String basis;

    @SerializedName("pair")
    private String pair;

    @SerializedName("timestamp")
    private long timestamp;

    public String getIndexPrice() {
        return indexPrice;
    }

    public void setIndexPrice(String indexPrice) {
        this.indexPrice = indexPrice;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getBasisRate() {
        return basisRate;
    }

    public void setBasisRate(String basisRate) {
        this.basisRate = basisRate;
    }

    public String getFuturesPrice() {
        return futuresPrice;
    }

    public void setFuturesPrice(String futuresPrice) {
        this.futuresPrice = futuresPrice;
    }

    public String getAnnualizedBasisRate() {
        return annualizedBasisRate;
    }

    public void setAnnualizedBasisRate(String annualizedBasisRate) {
        this.annualizedBasisRate = annualizedBasisRate;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }


    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
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
                "indexPrice='" + indexPrice + '\'' +
                ", contractType='" + contractType + '\'' +
                ", basisRate='" + basisRate + '\'' +
                ", futuresPrice='" + futuresPrice + '\'' +
                ", annualizedBasisRate='" + annualizedBasisRate + '\'' +
                ", basis='" + basis + '\'' +
                ", pair='" + pair + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}