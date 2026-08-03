package me.xjanua.spring.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DailyClickCountDto {
    private LocalDate day;
    private Long count;
}