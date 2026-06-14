package Presentation.Workers;

import java.time.LocalDate;

import Domain.Workers.ShiftCanidatesWorkersFacade;
import Presentation.Workers.Location;

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
    


}
