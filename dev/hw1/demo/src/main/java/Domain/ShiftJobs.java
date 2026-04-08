package Domain;
import java.time.LocalDate; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftJobs{
    private Shift shift;
    private HashMap<Jobs, Integer> jobs;
    private boolean placement_started;

    public ShiftJobs(LocalDate date, boolean is_morning){
        shift = new Shift(date, is_morning);
        jobs = new HashMap<>();
        placement_started=false;
    }
    public void placementStarted(){
        placement_started=true;

    }
    public String addJob(int job){
        if(placement_started){
            return "failed, cannot change shift jobs after pacement started";
        }
        if(job == 0){
            int currentCount = jobs.getOrDefault(Jobs.CASHEER, 0);
            jobs.put(Jobs.CASHEER, currentCount + 1);
            return "succeed, added job casheer to shift";
        }
        else if(job ==1){
            int currentCount = jobs.getOrDefault(Jobs.SHOPKEEPER, 0);
            jobs.put(Jobs.SHOPKEEPER, currentCount + 1);
            return "succeed, added job shop keeper to shift";
        }
        return "faild, job not founded";
    }
    
    public String removeJob(int job){
        if(placement_started){
            return "failed, cannot change shift jobs after pacement started";
        }
        if(job == 0){
            int currentCount = jobs.getOrDefault(Jobs.CASHEER, 0);
            if( currentCount == 0 ){
                return "faild to lower casheer jobs, because it already 0";
            }
            jobs.put(Jobs.CASHEER, currentCount - 1);
            return "succeed, removed job casheer from shift";
        }
        else if(job ==1){
            int currentCount = jobs.getOrDefault(Jobs.SHOPKEEPER, 0);
            if( currentCount == 0 ){
                return "faild to lower shop keeper jobs, because it already 0";
            }
            jobs.put(Jobs.SHOPKEEPER, currentCount - 1);
            return "succeed, removed job shop keeper from shift";
        }
        return "faild, job not founded";
    }
    public Shift getShift(){return shift;}
    public boolean containAllJobs(List<Integer> jobs){
        ShiftJobs new_shift = new ShiftJobs(this.shift.getDate(), this.shift.getIsMorning());
        for (int id : jobs) {
            new_shift.addJob(id);
        }
        return new_shift.mapEquals(this.jobs);
    }
    public boolean mapEquals(HashMap<Jobs, Integer> others){
        for (Map.Entry<Jobs, Integer> entry : jobs.entrySet()) {
            if(!others.containsKey(entry.getKey())){
                return false;
            }
            if(others.get(entry.getKey())!=entry.getValue()){
                return false;
            }
        }
        return true;

    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("==== Shift Jobs Detail ====\n the shift date: "+shift.toString()+"\n the shift jobs: \n ");
        jobs.forEach((job, count) -> {
            result.append("  - ").append(job).append(" -> ").append(count).append(" workers\n");
        });
        return result.toString();
    }

}