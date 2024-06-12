package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.domain.DayPlan;
import com.example.domain.Location;
import com.example.domain.TravelPlan;
import com.example.repository.DayPlanRepository;
import com.example.repository.LocationRepository;
import com.example.repository.TravelPlanRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PlanServiceTest {
  @InjectMocks
  private PlanService planService;

  @Mock
  private TravelPlanRepository travelPlanRepository;

  @Mock
  private DayPlanRepository dayPlanRepository;

  @Mock
  private LocationRepository locationRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  private TravelPlan createTravelPlan() {
    TravelPlan travelPlan = new TravelPlan();
    travelPlan.setStartDate(LocalDate.of(2024, 6, 1));
    travelPlan.setEndDate(LocalDate.of(2024, 6, 3));
    return travelPlan;
  }

  @Test
  public void testCreatePlan() {
    TravelPlan travelPlan = createTravelPlan();
    when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(travelPlan);
    when(dayPlanRepository.save(any(DayPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TravelPlan result = planService.createPlan(travelPlan);

    assertNotNull(result);
    assertEquals(3, result.getDayPlanList().size());
    verify(travelPlanRepository, times(1)).save(travelPlan);
    verify(dayPlanRepository, times(3)).save(any(DayPlan.class));
  }

  @Test
  public void testGetAllPlans() {
    TravelPlan travelPlan = createTravelPlan();
    when(travelPlanRepository.findAll()).thenReturn(List.of(travelPlan));

    List<TravelPlan> allPlans = planService.getAllPlans();

    assertFalse(allPlans.isEmpty());
    assertEquals(1, allPlans.size());
    verify(travelPlanRepository, times(1)).findAll();
  }

  @Test
  public void testGetPlansById() {
    TravelPlan travelPlan = createTravelPlan();
    when(travelPlanRepository.findById(anyLong())).thenReturn(Optional.of(travelPlan));

    TravelPlan plan = planService.getPlanById(1L);

    assertNotNull(plan);
    assertEquals(travelPlan.getStartDate(), plan.getStartDate());
    verify(travelPlanRepository, times(1)).findById(1L);
  }

  @Test
  public void testAddPlace() {
    TravelPlan travelPlan = createTravelPlan();
    DayPlan dayPlan = new DayPlan();
    dayPlan.setId(1L);
    dayPlan.setDate(LocalDate.of(2024, 6, 2));
    dayPlan.setLocationList(new CopyOnWriteArrayList<>());
    travelPlan.setDayPlanList(List.of(dayPlan));

    when(dayPlanRepository.findById(anyLong())).thenReturn(Optional.of(dayPlan));
    when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(dayPlanRepository.save(any(DayPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Location location = new Location();
    location.setName("경복궁");
    location.setLatitude(34.546);
    location.setLongitude(78.546);

    List<Location> locations = planService.addPlace(dayPlan.getId(), location);

    assertNotNull(locations);
    assertEquals(1, locations.size());
    assertEquals("경복궁", locations.get(0).getName());
    verify(dayPlanRepository, times(1)).findById(dayPlan.getId());
    verify(locationRepository, times(1)).save(location);
    verify(dayPlanRepository, times(1)).save(dayPlan);
  }

}