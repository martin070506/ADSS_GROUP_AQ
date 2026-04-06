package Domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class ShiftJobsFacade {
    private List<ShiftJobs> shifts;
    public ShiftJobsFacade(){
        shifts= new ArrayList<>();
    }
    public String addJob(LocalDate date, boolean is_morning, int job){
        for (ShiftJobs shiftJobs : shifts) {
            if(shiftJobs.getShift().equals(new Shift(date, is_morning))){
                return shiftJobs.addJob(job);
            }
        }
        ShiftJobs new_shift_job = new ShiftJobs(date, is_morning);
        String result =new_shift_job.addJob(job);
        shifts.add(new_shift_job);
        return result;
    }

    public String removeJob(LocalDate date, boolean is_morning, int job){
        for (ShiftJobs shiftJobs : shifts) {
            if(shiftJobs.getShift().equals(new Shift(date, is_morning))){
                return shiftJobs.removeJob(job);
            }
        }
        return "we did not found such shift, so we could not removed the job";
    }

    public String changeJob(LocalDate date, boolean is_morning, int job_to_be_changed, int job_to_change_to){
        String remove_result = removeJob(date,is_morning,job_to_be_changed);
        if(!remove_result.startsWith("succeed")){
            return "faild at chainging, "+remove_result;
        }
        String added_result = addJob(date, is_morning, job_to_change_to);
        if(!added_result.startsWith("succeed")){
            return "faild at chainging, "+added_result;
        }
        return "succeed, "+remove_result+" , "+added_result;
    }
}
