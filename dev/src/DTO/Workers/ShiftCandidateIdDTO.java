package DTO.Workers;
import java.time.LocalDate;

public record ShiftCandidateIdDTO(
        LocalDate date,
        boolean is_morning_shift,
        int locationId,
        int worker_id
) {}
