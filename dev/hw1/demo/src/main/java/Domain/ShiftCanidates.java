package Domain;

import java.time.LocalDate;
import java.util.List;

public class ShiftCanidates {
    private Shift shift;
    private List<Integer> workers_ids;
    private boolean placement_started;

    public ShiftCanidates(Shift shift,List<Integer> workers_ids){
        this.shift=shift;
        this.workers_ids=workers_ids;
        placement_started=false;
    }
    public void placementStarted(){
        placement_started=true;

    }
    public String addCandidate(int id){
        if(placement_started){
            return "failed, cannot change shift jobs after pacement started";
        }
        if(workers_ids.contains(id))
            return ("Failed, the worker with id: "+id+
            " is already a candidate for the shift.");
            workers_ids.add(id);

        return ("Success, a new candidate with id: "+id+" has been added.");

    }


    protected Shift getShift(){
        return this.shift;
    }

    protected List<Integer> getWorkersIds(){
        return this.workers_ids;
    }
    
    public boolean containWorker(int id){
        return workers_ids.contains(id);
    }
    public String removeCandidate(Integer id) {
        if(placement_started){
            return "failed, cannot change shift jobs after pacement started";
        }
        if(!workers_ids.contains(id))
            return ("Failed, the worker with id: "+id+
            " is not a candidate for the shift.");
        workers_ids.remove(id);
        return ("succeed, a new candidate with id: "+id+" has been removed.");
    }

    public boolean containAllWorkers(LocalDate date, boolean is_morning, List<Integer> ids) {
        if(!this.shift.equals(new Shift(date, is_morning)))
            return false;
        return this.workers_ids.containsAll(ids);
    }




}
