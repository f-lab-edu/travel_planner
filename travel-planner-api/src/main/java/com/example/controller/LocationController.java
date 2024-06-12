package com.example.controller;

import com.example.domain.Location;
import com.example.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LocationController {

    private final PlanService planService;


    @PostMapping("/{dayPlanId}/location")
    public ResponseEntity<List<Location>> addPlace(@PathVariable Long dayPlanId, @RequestBody Location location){

        List<Location> locations = planService.addPlace(dayPlanId, location);
        return new ResponseEntity<>(locations, HttpStatus.CREATED);

    }

}
