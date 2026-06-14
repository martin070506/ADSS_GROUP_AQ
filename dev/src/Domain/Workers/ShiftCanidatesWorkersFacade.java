package Domain.Workers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Domain.Workers.Shift;
import Domain.Workers.ShiftCanidates;
import Domain.Transportation.Location;

public class ShiftCanidatesWorkersFacade {
    private List<ShiftCanidates> canidates_list;
    private WorkersFacade workers;
    public ShiftCanidatesWorkersFacade(WorkersFacade workers) {

        this.canidates_list = new ArrayList<>();
        this.workers= workers;
    }
    public boolean containWorker(LocalDate date, boolean is_morning, Location location, int id){
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return shiftCanidates.containWorker(id);
        }
        return false;
    }
    public List<Integer> getAllAvialableDrivers(LocalDate date, boolean is_morning, Location location){
        Shift shift = new Shift(date, is_morning, location);
        List<Integer> avialable_drivers=new ArrayList<>();
        List<Integer> avialable_workers =new ArrayList<>();

        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                avialable_workers.addAll(shiftCanidates.getWorkersIds());
        }
        for (int i=0; i<avialable_workers.size(); i++){
            if(workers.isDriver(avialable_workers.get(i))){
                avialable_drivers.add(avialable_workers.get(i));
            }
        }
        return avialable_drivers;
    }

        public void startPlacement(LocalDate date,boolean is_morning, Location location){
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                 shiftCanidates.placementStarted();
        }
    }
    public String addCandidate(LocalDate date, boolean is_morning,  Location location, int id){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
        }
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return shiftCanidates.addCandidate(id);;
        }
        List<Integer>new_id_list=new ArrayList<>();
        ShiftCanidates canidates_of_new_shift=new ShiftCanidates(shift, new_id_list);
        String result=canidates_of_new_shift.addCandidate(id);
        canidates_list.add(canidates_of_new_shift);
        return result;
    }
    public String removeCandidate(LocalDate date, boolean is_morning, Location location, int id){
       if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
        }
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return shiftCanidates.removeCandidate(id);
            
        }
        return "Failed, there is no shift: "+shift.toString();
    }

    public boolean containAllWorkers(LocalDate date,boolean is_morning,Location location,List<Integer> ids){
        Shift shift=new Shift(date, is_morning, location);
        for (ShiftCanidates canidates : canidates_list) {
            if(shift.equals(canidates.getShift()))
                return canidates.containAllWorkers(date,is_morning,location,ids);
        }
        return false;
    }

    public String getCandidatesForShift(LocalDate date, boolean is_morning, Location location) {
        Shift shift=new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return ("The candidates for the shift: "+shiftCanidates.getShift().toString()
            +" ,are: "+shiftCanidates.getWorkersIds().toString());
        }

        return ("The shift: "+shift.toString()+", has no candidates yet.");
    }

    
}
