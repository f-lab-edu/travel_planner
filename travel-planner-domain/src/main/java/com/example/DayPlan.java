package com.example;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class DayPlan {
    private Long id;
    private LocalDate date;
    private List<Location> locationList;

    public DayPlan(){}

    public DayPlan(LocalDate date){
        this.date = date;
    }
    @Override
    public String toString() {
        return "DayPlan{" +
                "id=" + id +
                ", date=" + date +
                ", locationList=" + locationList +
                '}';
    }
}
