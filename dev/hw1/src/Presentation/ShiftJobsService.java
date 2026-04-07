package Presentation;
import java.time.LocalDate;

import Domain.ShiftJobsFacade;

public class ShiftJobsService {
    private ShiftJobsFacade jobs_facade;
    public ShiftJobsService(ShiftJobsFacade jobs){
        jobs_facade = jobs;
    }

    public String addJob(LocalDate date, boolean is_morning, int job){
        String result = jobs_facade.addJob(date, is_morning,job);
        return ("the result for adding new job are: "+result);
    }

    public String removeJob(LocalDate date, boolean is_morning, int job){
        String result = jobs_facade.removeJob(date, is_morning,job);
        return ("the result for removing a job are: "+result);
    }

    public String changeJob(LocalDate date, boolean is_morning, int job_to_be_changed, int job_to_change_to){
        String result = jobs_facade.changeJob(date, is_morning,job_to_be_changed,job_to_change_to);
        return ("the result for adding new job are: "+result);
    }
    public String getShift(LocalDate date, boolean is_morning){
        return jobs_facade.getShiftJobs(date, is_morning);
    }
}
