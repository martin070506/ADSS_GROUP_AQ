package DTO;

import java.time.LocalDate;

public record ShiftJobsCountDTO(
        int job,
        LocalDate date,
        boolean is_morning_shift,
        int locationId,
        int count
) {}
