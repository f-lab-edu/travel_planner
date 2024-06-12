package com.example.controller;

import com.example.service.PlanService;
import com.example.domain.TravelPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<TravelPlan> createPlan(@RequestBody TravelPlan travelPlan){

        TravelPlan plan = planService.createPlan(travelPlan);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TravelPlan>> getAllPlans(){

        List<TravelPlan> plans = planService.getAllPlans();
        return new ResponseEntity<>(new ArrayList<>(plans), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPlan> getPlanById(@PathVariable Long id){

        TravelPlan plan = planService.getPlanById(id);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }


}
