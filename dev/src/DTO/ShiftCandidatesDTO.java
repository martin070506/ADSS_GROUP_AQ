package DTO;
import java.time.LocalDate;

public record ShiftCandidatesDTO(
        LocalDate date,
        boolean is_morning_shift,
        int locationId
) {}