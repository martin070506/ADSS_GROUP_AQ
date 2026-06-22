package DTO;

import java.time.LocalDate;

public record ShiftJobsDTO(
        LocalDate date,
        boolean is_morning_shift,
        int locationId
) {}