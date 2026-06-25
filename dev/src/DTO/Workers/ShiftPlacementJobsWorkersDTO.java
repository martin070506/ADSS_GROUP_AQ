package DTO.Workers;

import java.time.LocalDate;

public record ShiftPlacementJobsWorkersDTO(
        int job,
        LocalDate date,
        boolean is_morning_shift,
        int locationId,
        int worker_id
) {}