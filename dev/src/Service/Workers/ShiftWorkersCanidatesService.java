package Service.Workers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Domain.Workers.ShiftCanidatesWorkersFacade;
import Domain.Transportation.Location;

public class ShiftWorkersCanidatesService {
    private ShiftCanidatesWorkersFacade canidates;
    public ShiftWorkersCanidatesService(ShiftCanidatesWorkersFacade canidates){
        this.canidates=canidates;
    }

    public String addCandidate(LocalDate date, boolean is_morning, Location location, int id){
        return canidates.addCandidate(date,is_morning,location, id);
    }
    public String removeCandidate(LocalDate date, boolean is_morning, Location location,  int id){
        return canidates.removeCandidate(date, is_morning, location, id);
    }
    public String getCandidatesForShift(LocalDate date, boolean is_morning, Location location){
        return canidates.getCandidatesForShift(date,is_morning, location);
    }
    public List<Integer> getAllAvialableDrivers(LocalDate date, boolean is_morning, Location location){
        return canidates.getAllAvialableDrivers(date,is_morning, location);
    }

    public String loadAllJobs(){
        return canidates.loadAllJobs();
    }
}
