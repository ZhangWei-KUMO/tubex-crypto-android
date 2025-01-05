package chat.tubex.analysis;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import chat.tubex.analysis.chart.ChartManager;
import chat.tubex.analysis.data.DataManager;
import chat.tubex.analysis.utils.SystemBarUtils;

public class CryptoFeatureActivity extends AppCompatActivity {
    private String currentSymbol;
    private DataManager dataManager;
    // 主图管理
    private ChartManager chartManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView loadingTextView;
    private TextView takerPositionRatioTextView;
    private TextView basisTextView;

    private TextView highPriceTextView;
    private TextView lowPriceTextView;
    private TextView medianPriceTextView;
    private TextView maxVolumeTextView;
    private TextView minVolumeTextView;
    private TextView pearsonCorrelationTextView;
    private TextView currentBasisTextView;
    private TextView currentPriceTextView;
    private TextView currentVolumeTextView;
    private TextView historicalVar;
    private TextView volatilityTextView;
    private TextView fundingRateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        currentSymbol = getIntent().getStringExtra("symbol");
        TextView symbolTextView = findViewById(R.id.symbolTextView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        loadingTextView = findViewById(R.id.loadingTextView);
        takerPositionRatioTextView = findViewById(R.id.takerPositionRatioTextView);
        basisTextView = findViewById(R.id.basisTextView);
        // Initialize new TextViews
        highPriceTextView = findViewById(R.id.highPriceTextView);
        lowPriceTextView = findViewById(R.id.lowPriceTextView);
        medianPriceTextView = findViewById(R.id.medianPriceTextView);
        maxVolumeTextView = findViewById(R.id.maxVolumeTextView);
        minVolumeTextView = findViewById(R.id.minVolumeTextView);
        pearsonCorrelationTextView = findViewById(R.id.pearsonCorrelationTextView);
        currentBasisTextView = findViewById(R.id.currentBasisTextView);
        currentPriceTextView = findViewById(R.id.currentPriceTextView);
        currentVolumeTextView = findViewById(R.id.currentVolumeTextView);
        historicalVar = findViewById(R.id.extremeRiskTextView);
        volatilityTextView = findViewById(R.id.volatilityTextView);
        fundingRateTextView = findViewById(R.id.fundingRateTextView);

        if (symbolTextView != null && currentSymbol != null) {
            symbolTextView.setText(currentSymbol + "合约分析");
        }

        // Initialize DataManager and ChartManager
        dataManager = new DataManager(this, swipeRefreshLayout, loadingTextView,
                highPriceTextView, lowPriceTextView, medianPriceTextView,
                maxVolumeTextView, minVolumeTextView, pearsonCorrelationTextView,
                currentBasisTextView, currentPriceTextView, currentVolumeTextView,
                historicalVar,volatilityTextView,fundingRateTextView);

        chartManager = new ChartManager(this);
        // Set DataManager's ChartManager instance
        dataManager.setChartManager(chartManager);
        // Fetch initial data
        if (currentSymbol != null) {
            fetchData();
        }
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        swipeRefreshLayout.setOnRefreshListener(this::fetchData);
        SystemBarUtils.setSystemBarsColor(this);
    }
    private void fetchData(){
        // 获取K线数据
        dataManager.fetchKlines(currentSymbol);
        // 获取做市商数据
        dataManager.fetchTopTakerPosition(currentSymbol);
        // 获取基差数据
        dataManager.fetchBasis(currentSymbol);
        // 获取手续费数据
        dataManager.fetchFundingRate(currentSymbol);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dataManager != null){
            dataManager.onDestroy();
        }

    }
}