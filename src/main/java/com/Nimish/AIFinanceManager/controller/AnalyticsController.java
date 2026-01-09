package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.model.FinancialInsight;
import com.Nimish.AIFinanceManager.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private AnalysisService analysisService;


    @GetMapping("/{id}")
    public ResponseEntity<Map<String,String>> analysis(@PathVariable Long id){
        Map<String,String> saved = analysisService.getInsights(id);
        return ResponseEntity.status(HttpStatus.OK).body(saved);

    }

}
