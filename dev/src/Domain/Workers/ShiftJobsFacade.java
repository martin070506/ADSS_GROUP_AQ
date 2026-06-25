package Domain.Workers;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import DAO.Workers.ShiftJobsCountDAO;
import DAO.Workers.ShiftJobsDAO;
import DTO.Workers.ShiftJobsCountDTO;
import DTO.Workers.ShiftJobsDTO;
import Domain.Transportation.Location;
import DB.DatabaseManager;
import Service.Transportation.LocationService;

public class ShiftJobsFacade {
    private final List<ShiftJobs> shifts;
    private ShiftJobsCountDAO jobs_count_dao;
    private ShiftJobsDAO jobs_dao;
    private LocationService locationService;
    public ShiftJobsFacade(LocationService locationService){
        Connection dbConnection = DatabaseManager.getConnection();
        jobs_count_dao = new ShiftJobsCountDAO(dbConnection);
        jobs_dao = new ShiftJobsDAO(dbConnection);
        shifts= new ArrayList<>();
        this.locationService=locationService;
    }
    public String addJob(LocalDate date, boolean is_morning, Location location, int job){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to add shift jobs";
        }
        for (ShiftJobs shiftJobs : shifts) {
            if(shiftJobs.getShift().equals(new Shift(date, is_morning, location))){
                String result = shiftJobs.addJob(job);

                try{
                    ShiftJobsCountDTO jobs_count = new ShiftJobsCountDTO(job,date,is_morning, location.id(),1);
                    jobs_count_dao.add(jobs_count);
                }
                catch (Exception e){
                    return "failed, add job to data base";
                }
                return result;
            }
        }
        ShiftJobs new_shift_job = new ShiftJobs(date, is_morning, location);
        String result =new_shift_job.addJob(job);
        shifts.add(new_shift_job);
        try{
            ShiftJobsDTO jobs = new ShiftJobsDTO(date,is_morning, location.id());
            jobs_dao.add(jobs);
            ShiftJobsCountDTO jobs_count = new ShiftJobsCountDTO(job,date,is_morning, location.id(),1);
            jobs_count_dao.add(jobs_count);
        }
        catch (Exception e){
            return "failed, add job to data base";
        }
        return result;
    }
    public String loadAllJobs(){
        List<ShiftJobsDTO> list = jobs_dao.loadAll();
        for (int i=0;i<list.size(); i++){
            Location location = locationService.getLocation(list.get(i).locationId());
            ShiftJobs shift = new ShiftJobs(list.get(i).date(), list.get(i).is_morning_shift(), location);
            shifts.add(shift);
        }
        List<ShiftJobsCountDTO> list_count = jobs_count_dao.loadAll();
        for( int i=0; i< list_count.size(); i++){
            Location location = locationService.getLocation(list_count.get(i).locationId());
            Shift shift = new Shift(list_count.get(i).date(), list_count.get(i).is_morning_shift(), location);
            for (ShiftJobs shiftJobs : shifts) {
                if(shiftJobs.getShift().equals(shift)){
                    for( int j=0; j<list_count.get(i).count(); j++){
                        shiftJobs.addJob(list_count.get(i).job());

                    }
                }
            }
        }
        return "succeed, loaded all shifts jobs data";

    }

    public void startPlacement(LocalDate date, boolean is_morning, Location location){
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftJobs shift_job : shifts) {
            if(shift.equals(shift_job.getShift()))
                 shift_job.placementStarted();
        }
    }
    public String removeJob(LocalDate date, boolean is_morning, Location location, int job){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to remove shift jos";
        }
        for (ShiftJobs shiftJobs : shifts) {
            if(shiftJobs.getShift().equals(new Shift(date, is_morning, location ))){
                String result=  shiftJobs.removeJob(job);
                if(result.startsWith("failed")){
                    return "failed, could not removed this job";
                }
                ShiftJobsCountDTO job_count = new ShiftJobsCountDTO(job, date,is_morning, location.id(), shiftJobs.getJobCount(job)-1);
                jobs_count_dao.update(job_count);
            }
        }
        return "we did not found such shift, so we could not removed the job";
    }
    public boolean containAllJobs(LocalDate date, boolean is_morning, Location location, List<Integer> jobs){
        Shift shift = new Shift(date, is_morning, location);
            for (ShiftJobs shift_job : shifts) {
                if(shift_job.getShift().equals(shift)){

                    return shift_job.containAllJobs(jobs);
                }

            }
            return false;
    }
    public boolean hashShopKeeper(LocalDate date, boolean is_morning, Location location){
        Shift shift = new Shift(date, is_morning, location);
 
        for (ShiftJobs shift_job : shifts) {
            if(shift_job.getShift().equals(shift)){
                return shift_job.hashShopKeeper();
            }

        }
        return false;
    }
    public String changeJob(LocalDate date, boolean is_morning, Location location, int job_to_be_changed, int job_to_change_to){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change job";
        }
        String remove_result = removeJob(date,is_morning, location, job_to_be_changed);
        if(!remove_result.startsWith("succeed")){
            return "faild at chainging, "+remove_result;
        }
        String added_result = addJob(date, is_morning, location, job_to_change_to);
        if(!added_result.startsWith("succeed")){
            return "faild at chainging, "+added_result;
        }
        return "succeed, "+remove_result+" , "+added_result;
    }
    public String getShiftJobs(LocalDate date, boolean is_morning, Location location){
          Shift shift = new Shift(date, is_morning, location);
            for (ShiftJobs shift_job : shifts) {
                if(shift_job.getShift().equals(shift)){
                    return shift_job.toString();
                }

            }
            return "faild, no shift founded";
    }
}
