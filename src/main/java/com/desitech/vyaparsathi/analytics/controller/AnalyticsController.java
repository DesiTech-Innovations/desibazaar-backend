package com.desitech.vyaparsathi.analytics.controller;

import com.desitech.vyaparsathi.analytics.service.AnalyticsExportService;
import com.desitech.vyaparsathi.analytics.service.AnalyticsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.desitech.vyaparsathi.common.exception.ApplicationException;
import com.desitech.vyaparsathi.common.exception.ExportAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.analytics.dto.ItemDemandPredictionDto;
import com.desitech.vyaparsathi.analytics.dto.CustomerTrendDto;
import com.desitech.vyaparsathi.analytics.dto.PurchaseOrderSuggestionDto;
import com.desitech.vyaparsathi.analytics.dto.TopItemDto;
import com.desitech.vyaparsathi.analytics.dto.SeasonalTrendDto;
import com.desitech.vyaparsathi.analytics.dto.ChurnPredictionDto;
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

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private AnalyticsExportService analyticsExportService;
    @GetMapping("/export/item-demand")
    @Operation(summary = "Export item demand analytics", description = "Download item demand prediction.")
    public ResponseEntity<Resource> exportItemDemand(
            @RequestParam(required = false) Long itemId,
            @RequestParam(defaultValue = "csv") String format
    ) {
        try {
            var data = analyticsService.predictItemDemand(itemId);
            byte[] file = analyticsExportService.exportItemDemand(data, format);
            String filename = "item-demand." + format;
            logger.info("Exported item demand analytics as {} ({} bytes)", format, file.length);
            String contentType;

            switch (format.toLowerCase()) {
                case "xlsx":
                case "excel":
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    break;
                case "pdf":
                    contentType = "application/pdf";
                    break;
                case "csv":
                default:
                    contentType = "text/csv";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new ByteArrayResource(file));
        } catch (ExportAppException e) {
            logger.error("Failed to export item demand analytics: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during item demand export", e);
            throw new ApplicationException("Failed to export item demand analytics", e);
        }
    }

    @GetMapping("/item-demand")
    @Operation(summary = "Predict item demand", description = "Returns demand prediction for each item based on sales history.")
    public ResponseEntity<List<ItemDemandPredictionDto>> getItemDemandPrediction(
            @RequestParam(required = false) Long itemId
    ) {
        try {
            var result = analyticsService.predictItemDemand(itemId);
            logger.info("Fetched item demand prediction for itemId={}", itemId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching item demand prediction for itemId={}: {}", itemId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/customer-trends")
    @Operation(summary = "Customer buying trends", description = "Returns insights on customer buying patterns.")
    public ResponseEntity<List<CustomerTrendDto>> getCustomerTrends(
            @RequestParam(required = false) Long customerId
    ) {
        try {
            var result = analyticsService.getCustomerTrends(customerId);
            logger.info("Fetched customer trends for customerId={}", customerId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching customer trends for customerId={}: {}", customerId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/future-purchase-orders")
    @Operation(summary = "Future purchase order suggestions", description = "Suggests future purchase orders based on analytics.")
    public ResponseEntity<List<PurchaseOrderSuggestionDto>> getFuturePurchaseOrderSuggestions() {
        try {
            var result = analyticsService.suggestFuturePurchaseOrders();
            logger.info("Fetched future purchase order suggestions");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching future purchase order suggestions: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/top-items")
    @Operation(summary = "Top rising/falling items", description = "Returns top rising and falling items based on sales trends.")
    public ResponseEntity<List<TopItemDto>> getTopRisingFallingItems() {
        try {
            var result = analyticsService.getTopRisingFallingItems();
            logger.info("Fetched top rising/falling items");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching top rising/falling items: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/seasonal-trends")
    @Operation(summary = "Seasonal trends", description = "Returns seasonal sales trends.")
    public ResponseEntity<List<SeasonalTrendDto>> getSeasonalTrends() {
        try {
            var result = analyticsService.getSeasonalTrends();
            logger.info("Fetched seasonal trends");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching seasonal trends: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/churn-prediction")
    @Operation(summary = "Churn prediction", description = "Predicts customer churn based on analytics.")
    public ResponseEntity<List<ChurnPredictionDto>> getChurnPrediction() {
        try {
            var result = analyticsService.predictChurn();
            logger.info("Fetched churn prediction");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching churn prediction: {}", e.getMessage(), e);
            throw e;
        }
    }
}