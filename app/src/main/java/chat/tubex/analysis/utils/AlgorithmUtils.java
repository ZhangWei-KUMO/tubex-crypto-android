package chat.tubex.analysis.utils;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.distribution.NormalDistribution;
import java.util.List;

public class AlgorithmUtils {
    // 计算风险价值
    public static double calculateConditionalVaR(double[] returns, double confidenceLevel) {
        if (returns == null || returns.length == 0) {
            return Double.NaN; // Or handle the case as needed
        }
        int n = returns.length;
        if (n < 2){
            return Double.NaN;
        }
        java.util.Arrays.sort(returns);
        int varIndex = (int) Math.floor(n * confidenceLevel);
        if (varIndex >= n) {
            varIndex = n-1;
        }
        double var = returns[varIndex];
        double sumOfTailLosses = 0;
        int count = 0;

        for (int i = 0; i < n; i++) {
            if (returns[i] <= var) {
                sumOfTailLosses += returns[i];
                count++;
            }

        }

        return count > 0 ? sumOfTailLosses / count : Double.NaN;
    }

    // 计算风险价值
    public static double calculateHistoricalVaR(double[] returns, double confidenceLevel) {
        System.out.println("置信区间"+confidenceLevel);
        System.out.println(returns);
        if (returns == null || returns.length < 2) {
            return Double.NaN;
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (double returnVal : returns) {
            stats.addValue(returnVal);
        }

        double mean = stats.getMean();
        double stdDev = stats.getStandardDeviation();

        //Handle cases where standard deviation is zero to avoid division by zero errors.
        if (stdDev == 0) {
            return 0; // Or another appropriate value like mean or Double.NaN
        }


        NormalDistribution normalDistribution = new NormalDistribution();
        double zAlpha = normalDistribution.inverseCumulativeProbability(1 - confidenceLevel); //Note: 1-confidenceLevel because inverseCDF gives the probability less than x

        return mean - stdDev * zAlpha;
    }
    // 计算年化波动率
    public static double calculateVolatility(List<Double> prices) {
        if (prices == null || prices.size() < 2) {
            return 0.0;
        }
        int n = prices.size();
        double[] dailyReturns = new double[n - 1];

        for(int i= 1; i< n; i++){
            dailyReturns[i-1] = (prices.get(i) - prices.get(i-1))/prices.get(i-1);
        }

        StandardDeviation standardDeviation = new StandardDeviation();
        double stdDev =  standardDeviation.evaluate(dailyReturns);
        return stdDev * Math.sqrt(365);

    }

    // 计算最大回撤
    public static double calculateMaxDrawdown(List<Double> prices) {
        if (prices == null || prices.size() < 2) {
            return 0.0;
        }

        double maxDrawdown = 0.0;
        double peak = prices.get(0);
        double minPriceSincePeak = peak;

        for (double price : prices) {
            if (price > peak) {
                peak = price;
            } else if (price < minPriceSincePeak) {
                minPriceSincePeak = price; //Update only if a lower price is encountered since last peak
            }

            double drawdown = (peak - minPriceSincePeak) / peak;
            if (peak != 0) { // Avoid division by zero
                maxDrawdown = Math.max(maxDrawdown, drawdown);
            }
        }
        return maxDrawdown;
    }

    // 计算量价关系的皮尔逊系数
    public static double calculatePearsonCorrelation(double[] prices, double[] volumes) {
        if (prices == null || volumes == null || prices.length != volumes.length || prices.length < 2) {
            return Double.NaN; // Return NaN if input arrays are invalid
        }

        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        return pearsonsCorrelation.correlation(prices, volumes);
    }

    // 计算预期回报率
    public static double[] calculateReturns(List<Double> prices) {
        System.out.println(prices);
        if (prices == null || prices.size() < 2) {
            return new double[0]; // Return an empty array if input is invalid
        }

        int size = prices.size();
        double[] returns = new double[size - 1];

        for (int i = 1; i < size; i++) {
            returns[i - 1] = (prices.get(i) - prices.get(i - 1)) / prices.get(i - 1);
        }
        System.out.println("预期回报率");
        System.out.println(returns);
        return returns;
    }
}