package com.example.service;

import com.example.domain.DayPlan;
import com.example.domain.Location;
import com.example.domain.TravelPlan;
import com.example.repository.DayPlanRepository;
import com.example.repository.LocationRepository;
import com.example.repository.TravelPlanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final DayPlanRepository dayPlanRepository;
    private final LocationRepository locationRepository;

    public TravelPlan createPlan(TravelPlan travelPlan) {
        List<DayPlan> dayPlans = generateDayPlans(travelPlan.getStartDate(), travelPlan.getEndDate());
        travelPlan.setDayPlanList(dayPlans);
        return travelPlanRepository.save(travelPlan);
    }

    public List<TravelPlan> getAllPlans() {
        return travelPlanRepository.findAll();
    }

    public TravelPlan getPlanById(Long id) {
        return travelPlanRepository.findById(id).orElse(null);
    }

    public List<DayPlan> generateDayPlans(LocalDate startDate, LocalDate endDate) {
        List<DayPlan> dayPlans= new CopyOnWriteArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)){
            DayPlan dayPlan = new DayPlan();
            dayPlan.setDate(currentDate);
            dayPlan.setLocationList(new CopyOnWriteArrayList<>());

            dayPlans.add(dayPlan);
            dayPlanRepository.save(dayPlan);
            currentDate = currentDate.plusDays(1);
        }
        return dayPlans;
    }

    public DayPlan getDayPlan(Long id) {
        // date에 해당하는 dayPlan 반환
        return dayPlanRepository.findById(id).orElse(null);
    }

    public List<Location> addPlace(Long id, Location location) {
        DayPlan dayPlan = dayPlanRepository.findById(id).orElse(null);
        Location savedLocation = locationRepository.save(location);
        dayPlan.getLocationList().add(savedLocation);

        dayPlanRepository.save(dayPlan);
        return dayPlan.getLocationList();
    }

    public List<Location> getLocations(Long id) {
        DayPlan dayPlan = dayPlanRepository.findById(id).orElse(null);

        return dayPlan.getLocationList();
    }
}
