package com.example.controller;

import com.example.domain.DayPlan;
import com.example.domain.Location;
import com.example.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/days")
@RequiredArgsConstructor
public class DayPlanController {

    private final PlanService planService;

    @GetMapping("/{id}")
    public ResponseEntity<DayPlan> getDayPlan(@PathVariable Long id){
        DayPlan dayPlan = planService.getDayPlan(id);

        return new ResponseEntity<>(dayPlan, HttpStatus.OK);
    }
}
