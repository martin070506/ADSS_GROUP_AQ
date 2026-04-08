package Domain;

import java.time.LocalDate;
import java.util.List;

public class ShiftCanidates {
    private Shift shift;
    private List<Integer> workers_ids;
    public ShiftCanidates(Shift shift,List<Integer> workers_ids){
        this.shift=shift;
        this.workers_ids=workers_ids;
    }

    public String addCandidate(int id){
            if(workers_ids.contains(id))
                return ("Failed, the worker with id: "+id+
            " is already a candidate for the shift.");
            workers_ids.add(id);

            return ("Success, a new candidate with id: "+id+" has been added.");

    }


    public Shift getShift(){
        return this.shift;
    }

    public String removeCandidate(Integer id) {
        if(!workers_ids.contains(id))
            return ("Failed, the worker with id: "+id+
            " is not a candidate for the shift.");
        workers_ids.remove(id);
        return ("Success, a new candidate with id: "+id+" has been removed.");
    }

    public boolean containAllWorkers(LocalDate date, boolean is_morning, List<Integer> ids) {
        if(!this.shift.equals(new Shift(date, is_morning)))
            return false;
        return this.workers_ids.containsAll(ids);
    }




}
