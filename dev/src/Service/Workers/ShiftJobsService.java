package Service.Workers;
import java.time.LocalDate;
import java.util.List;

import Domain.Workers.ShiftJobsFacade;
import Domain.Transportation.Location;

public class ShiftJobsService {
    private ShiftJobsFacade jobs_facade;
    public ShiftJobsService(ShiftJobsFacade jobs){
        jobs_facade = jobs;
    }

    public String addJob(LocalDate date, boolean is_morning, Location location, int job){
        String result = jobs_facade.addJob(date, is_morning,location, job);
        return ("the result for adding new job are: "+result);
    }

    public String removeJob(LocalDate date, boolean is_morning, Location location,  int job){
        String result = jobs_facade.removeJob(date, is_morning, location, job);
        return ("the result for removing a job are: "+result);
    }

    public String changeJob(LocalDate date, boolean is_morning, Location location, int job_to_be_changed, int job_to_change_to){
        String result = jobs_facade.changeJob(date, is_morning, location, job_to_be_changed,job_to_change_to);
        return ("the result for adding new job are: "+result);
    }
    public String getShift(LocalDate date, boolean is_morning, Location location){
        return jobs_facade.getShiftJobs(date, is_morning, location);
    }
    public boolean hashShopKeeper(LocalDate date, boolean is_morning, Location location){
        return jobs_facade.hashShopKeeper(date, is_morning, location);
    }

    public void loadAllJobs(){
         jobs_facade.loadAllJobs();
    }
}
