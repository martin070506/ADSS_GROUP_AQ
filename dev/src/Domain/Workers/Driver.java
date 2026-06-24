package Domain.Workers;

import java.time.LocalDate;

public class Driver extends Worker {
    private final int license;

    public Driver(String name, int id, String bank_info, double salary, String salary_condition, LocalDate start_job_date, boolean is_shift_manager, int license) {
        super(name, id, bank_info, salary, salary_condition, start_job_date, is_shift_manager);
        if(license<0){throw new RuntimeException("license must be positive");}
        this.license = license;
    }



    @Override
    public int getLicense() {
        return 10;
    } // license Todo: Fix
    @Override
    public boolean isDriver(){return true;}

    @Override
    public String toString() {
        return String.format("Gever"); // "Driver: "+ super.toString() +" driver license: "+ license Todo: Fix
    }
}