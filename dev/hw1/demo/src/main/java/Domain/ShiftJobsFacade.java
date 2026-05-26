package Domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Presentation.Location;

public class ShiftJobsFacade {
    private List<ShiftJobs> shifts;
    public ShiftJobsFacade(){
        shifts= new ArrayList<>();
    }
    public String addJob(LocalDate date, boolean is_morning, Location location, int job){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to add shift jobs";
        }
        for (ShiftJobs shiftJobs : shifts) {
            if(shiftJobs.getShift().equals(new Shift(date, is_morning, location))){
                return shiftJobs.addJob(job);
            }
        }
        ShiftJobs new_shift_job = new ShiftJobs(date, is_morning, location);
        String result =new_shift_job.addJob(job);
        shifts.add(new_shift_job);
        return result;
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
                return shiftJobs.removeJob(job);
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
