package com.desitech.vyaparsathi.analytics.controller;

import com.desitech.vyaparsathi.analytics.dto.ItemDemandPredictionDto;
import com.desitech.vyaparsathi.analytics.dto.CustomerTrendDto;
import com.desitech.vyaparsathi.analytics.dto.PurchaseOrderSuggestionDto;
import com.desitech.vyaparsathi.analytics.dto.TopItemDto;
import com.desitech.vyaparsathi.analytics.dto.SeasonalTrendDto;
import com.desitech.vyaparsathi.analytics.dto.ChurnPredictionDto;
import com.desitech.vyaparsathi.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
@Tag(name = "Business Analytics", description = "Predictive analytics and insights for business growth.")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/item-demand")
    @Operation(summary = "Predict item demand", description = "Returns demand prediction for each item based on sales history.")
    public ResponseEntity<List<ItemDemandPredictionDto>> getItemDemandPrediction(
            @RequestParam(required = false) Long itemId
    ) {
        return ResponseEntity.ok(analyticsService.predictItemDemand(itemId));
    }

    @GetMapping("/customer-trends")
    @Operation(summary = "Customer buying trends", description = "Returns insights on customer buying patterns.")
    public ResponseEntity<List<CustomerTrendDto>> getCustomerTrends(
            @RequestParam(required = false) Long customerId
    ) {
        return ResponseEntity.ok(analyticsService.getCustomerTrends(customerId));
    }

    @GetMapping("/future-purchase-orders")
    @Operation(summary = "Future purchase order suggestions", description = "Suggests future purchase orders based on analytics.")
    public ResponseEntity<List<PurchaseOrderSuggestionDto>> getFuturePurchaseOrderSuggestions() {
        return ResponseEntity.ok(analyticsService.suggestFuturePurchaseOrders());
    }

    @GetMapping("/top-items")
    @Operation(summary = "Top rising/falling items", description = "Returns top rising and falling items based on sales trends.")
    public ResponseEntity<List<TopItemDto>> getTopRisingFallingItems() {
        return ResponseEntity.ok(analyticsService.getTopRisingFallingItems());
    }

    @GetMapping("/seasonal-trends")
    @Operation(summary = "Seasonal trends", description = "Returns seasonal sales trends.")
    public ResponseEntity<List<SeasonalTrendDto>> getSeasonalTrends() {
        return ResponseEntity.ok(analyticsService.getSeasonalTrends());
    }

    @GetMapping("/churn-prediction")
    @Operation(summary = "Churn prediction", description = "Predicts customer churn based on analytics.")
    public ResponseEntity<List<ChurnPredictionDto>> getChurnPrediction() {
        return ResponseEntity.ok(analyticsService.predictChurn());
    }
}