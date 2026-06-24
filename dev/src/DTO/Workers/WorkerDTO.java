package DTO.Workers;

import java.time.LocalDate;

public record WorkerDTO(
        String name,
        int id,
        String bank_info,
        double salary,
        String salary_condition,
        LocalDate start_job_date,
        boolean is_shift_manager,
        boolean isDriver,
        int license
) {}