package chat.tubex.analysis.utils;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;
import java.util.Arrays;
public class AlgorithmUtils {
    // 计算极值理论
    public static double calculateEVT(double[] returns) {
        if (returns == null || returns.length == 0) {
            throw new IllegalArgumentException("收益率数组不能为空");
        }

        int n = returns.length;
        // 1. 复制收益率数组并进行排序
        double[] sortedReturns = Arrays.copyOf(returns, n);
        Arrays.sort(sortedReturns);

        // 2. 计算 1% 分位数位置
        double percentile = 0.01;
        int index = (int) Math.ceil(n * percentile);

        // 如果计算出的索引是0，则索引设置为1，因为我们需要至少一个返回值
        if (index == 0) {
            index = 1;
        }

        // 3. 获取 1% 分位数处的收益率值（从尾部开始取值,因为排序之后是从小到大）
        double valueAtRisk = sortedReturns[n - index];


        return valueAtRisk * -1; // 返回VaR，由于是损失，所以取负值
    }
    // 计算年化波动率
    public static double calculateVolatility(List<Double> prices) {
        System.out.println(prices);
        if (prices == null || prices.size() < 2) {
            return 0.0;
        }

        int n = prices.size();
        BigDecimal[] dailyReturns = new BigDecimal[n - 1];

        // 正确计算日收益率，并四舍五入
        for (int i = 1; i < n; i++) {
            BigDecimal priceCurrent = BigDecimal.valueOf(prices.get(i));
            BigDecimal pricePrevious = BigDecimal.valueOf(prices.get(i - 1));
            dailyReturns[i - 1] = priceCurrent.subtract(pricePrevious)
                    .divide(pricePrevious, 10, RoundingMode.HALF_UP)
                    .setScale(4, RoundingMode.HALF_UP); // 修正公式并保留4位小数
        }

        // 计算每日波动率 (需要将BigDecimal[]转换为double[])
        double[] dailyReturnsDouble = new double[n-1];
        for(int i = 0; i < dailyReturns.length; i++){
            dailyReturnsDouble[i] = dailyReturns[i].doubleValue();
        }

        StandardDeviation standardDeviation = new StandardDeviation();
        double dailyVolatility = standardDeviation.evaluate(dailyReturnsDouble);

        // 计算年化波动率 (假设一年有252个交易日)
        double annualizedVolatility = dailyVolatility * Math.sqrt(252);

        return annualizedVolatility;
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
        if (prices == null || prices.size() < 2) {
            return new double[0]; // 如果价格列表为空或少于两个价格，无法计算收益率，返回空数组
        }

        int numDays = prices.size() - 1;
        double[] returns = new double[numDays];

        for (int i = 0; i < numDays; i++) {
            double currentPrice = prices.get(i + 1); // 当天价格
            double previousPrice = prices.get(i);   // 前一天价格

            if (previousPrice == 0) {
                returns[i]=0; //如果前一天价格是0，避免除以0的错误，收益率设置为0
            } else {
                returns[i] = (currentPrice - previousPrice) / previousPrice; // 计算日收益率
            }
        }

        return returns;
    }
}