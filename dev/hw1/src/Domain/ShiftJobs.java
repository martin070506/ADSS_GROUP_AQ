package Domain;
import java.time.LocalDate; 
import java.util.HashMap;

public class ShiftJobs{
    private Shift shift;
    private HashMap<Jobs, Integer> jobs;

    public ShiftJobs(LocalDate date, boolean is_morning){
        shift = new Shift(date, is_morning);
        jobs = new HashMap<>();
    }

    public String addJob(int job){
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
}