package DTO.Workers;

import java.time.LocalDate;

public record ShiftPlacementDTO(
        LocalDate date,
        boolean is_morning_shift,
        int locationId,
        int shift_manager_id
) {}