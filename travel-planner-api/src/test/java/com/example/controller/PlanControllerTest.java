package com.example.controller;

import com.example.domain.DayPlan;
import com.example.domain.Location;
import com.example.domain.TravelPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlanControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private TravelPlan firstPlan;
    private TravelPlan secondPlan;
    private Location firstPlace;
    private Location secondPlace;
    private Location thirdPlace;

    @BeforeEach
    public void setUp() {
        testRestTemplate.delete("/plans");

        firstPlan = createTravelPlan("first plan", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 3));
        secondPlan = createTravelPlan("second plan", LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 13));

        firstPlace = createLocation("광화문", 37.5723, 126.979);
        secondPlace = createLocation("경복궁", 37.5797, 126.9769);
        thirdPlace = createLocation("남산타워", 37.5511, 126.9882);

    }

    private TravelPlan createTravelPlan(String name, LocalDate startDate, LocalDate endDate) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setName(name);
        travelPlan.setStartDate(startDate);
        travelPlan.setEndDate(endDate);
        return travelPlan;
    }

    private Location createLocation(String name, double latitude, double longitude) {
        Location location = new Location();
        location.setName(name);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    @Test
    @DirtiesContext
    @DisplayName("일정 생성 테스트")
    public void testCreatePlan() throws Exception{

        ResponseEntity<String> response =  testRestTemplate.postForEntity("/plans", firstPlan, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        TravelPlan travelPlan = objectMapper.readValue(response.getBody(), TravelPlan.class);
        assertThat(travelPlan.getId()).isNotNull();
        assertThat(travelPlan.getDayPlanList()).hasSize(3);

    }

    @Test
    @DirtiesContext
    @DisplayName("Id 값으로 일정 조회")
    public void testGetPlanById() throws Exception {
        ResponseEntity<TravelPlan> response1 = testRestTemplate.postForEntity("/plans", firstPlan, TravelPlan.class);
        ResponseEntity<TravelPlan> response2 = testRestTemplate.postForEntity("/plans", secondPlan, TravelPlan.class);

        // id값으로 일정조회
        ResponseEntity<TravelPlan> getResponse1 = testRestTemplate.getForEntity("/plans/{id}", TravelPlan.class, 1);
        ResponseEntity<TravelPlan> getResponse2 = testRestTemplate.getForEntity("/plans/{id}", TravelPlan.class, 2);
        assertThat(getResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);

        TravelPlan travelPlan1 = getResponse1.getBody();
        TravelPlan travelPlan2 = getResponse2.getBody();

        assertThat(travelPlan1.getName()).isEqualTo("first plan");
        assertThat(travelPlan1.getDayPlanList()).hasSize(3);
        assertThat(travelPlan2.getName()).isEqualTo("second plan");
        assertThat(travelPlan2.getDayPlanList()).hasSize(4);
    }

    @Test
    @DirtiesContext
    @DisplayName("여행 장소 추가")
    public void postLocation() throws Exception {
        ResponseEntity<TravelPlan> response = testRestTemplate.postForEntity("/plans", firstPlan, TravelPlan.class);
        Long planId = response.getBody().getId();
        List<DayPlan> dayPlans = response.getBody().getDayPlanList();
        assertThat(dayPlans).isNotNull();
        assertThat(dayPlans).hasSize(3);

        ResponseEntity<String> response1 = testRestTemplate.postForEntity("/{dayPlanId}/location", firstPlace, String.class, 1, "2024-06-01");
        ResponseEntity<String> response2 = testRestTemplate.postForEntity("/{dayPlanId}/location", secondPlace, String.class, 1, "2024-06-01");
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List locations = objectMapper.readValue(response2.getBody(), List.class);
        assertThat(locations).hasSize(2);
    }

    @Test
    @DirtiesContext
    @DisplayName("여행 일자에 해당하는 장소 반환")
    public void getLocationByDate() throws Exception{
        ResponseEntity<TravelPlan> response = testRestTemplate.postForEntity("/plans", firstPlan, TravelPlan.class);
        List<DayPlan> dayPlans = response.getBody().getDayPlanList();
        Long dayId = dayPlans.get(0).getId();

        testRestTemplate.postForEntity("/{dayPlanId}/location", firstPlace, Location[].class, dayId);
        testRestTemplate.postForEntity("/{dayPlanId}/location", secondPlace, Location[].class, dayId);
        testRestTemplate.postForEntity("/{dayPlanId}/location", thirdPlace, Location[].class, dayId);

        ResponseEntity<DayPlan> getResponse = testRestTemplate.getForEntity("/days/{id}", DayPlan.class, dayId);
        DayPlan dayPlan = getResponse.getBody();

        assertThat(dayPlan.getLocationList()).hasSize(3);
    }
}