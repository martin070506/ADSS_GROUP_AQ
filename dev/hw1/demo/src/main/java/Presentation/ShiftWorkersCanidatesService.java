package Presentation;

import java.time.LocalDate;

import Domain.ShiftCanidatesWorkersFacade;

public class ShiftWorkersCanidatesService {
    private ShiftCanidatesWorkersFacade canidates;
    public ShiftWorkersCanidatesService(ShiftCanidatesWorkersFacade canidates){
        this.canidates=canidates;
    }

    public String addCandidate(LocalDate date, boolean is_morning, int id){
        return canidates.addCandidate(date,is_morning,id);
    }
    public String removeCandidate(LocalDate date, boolean is_morning, int id){
        return canidates.removeCandidate(date, is_morning, id);
    }
    public String getCandidatesForShift(LocalDate date, boolean is_morning){
        return canidates.getCandidatesForShift(date,is_morning);
    }
    


}
