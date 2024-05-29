package com.example.controller;

import com.example.DayPlan;
import com.example.TravelPlan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/plans")
public class PlanController {

    private Map<Long, TravelPlan> plans = new ConcurrentHashMap<>();
    private AtomicLong id = new AtomicLong(1);
    private AtomicLong dayPlanId = new AtomicLong(1);

    @PostMapping("/create")
    public ResponseEntity<TravelPlan> createPlan(@RequestBody TravelPlan travelPlan){
        travelPlan.setId(id.getAndIncrement());

        List<DayPlan> dayPlans = generateDayPlans(travelPlan.getStartDate(), travelPlan.getEndDate());
        travelPlan.setDayPlanList(dayPlans);
        // travelPlan 저장 로직 추가 예정
        plans.put(travelPlan.getId(), travelPlan);
        return new ResponseEntity<>(travelPlan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TravelPlan>> getAllPlans(){

        return new ResponseEntity<>(new ArrayList<>(plans.values()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPlan> getPlanById(@PathVariable Long id){

        TravelPlan travelPlan = plans.get(id);
        return new ResponseEntity<>(travelPlan, HttpStatus.OK);
    }
  
    private List<DayPlan> generateDayPlans(LocalDate startDate, LocalDate endDate) {
        List<DayPlan> dayPlans = new CopyOnWriteArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)){
            DayPlan dayPlan = new DayPlan();
            dayPlan.setId(dayPlanId.getAndIncrement());
            dayPlan.setDate(currentDate);

            dayPlans.add(dayPlan);
            currentDate = currentDate.plusDays(1);
        }
        dayPlanId.set(1);
        return dayPlans;
    }
}