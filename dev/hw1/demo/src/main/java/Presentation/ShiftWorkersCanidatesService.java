package Presentation;

import java.time.LocalDate;

import Domain.Shift;
import Domain.ShiftCanidatesWorkersFacade;

public class ShiftWorkersCanidatesService {
    private ShiftCanidatesWorkersFacade canidates;
    public ShiftWorkersCanidatesService(ShiftCanidatesWorkersFacade canidates){
        this.canidates=canidates;
    }

    public String addCandidate(Shift shift, int id){
        return canidates.addCandidate(shift,id);
    }
    public String removeCandidate(Shift shift, int id){
        return canidates.removeCandidate(shift, id);
    }
    public String getCandidatesForShift(LocalDate date, boolean is_morning){
        return canidates.getCandidatesForShift(date,is_morning);
    }
    


}
