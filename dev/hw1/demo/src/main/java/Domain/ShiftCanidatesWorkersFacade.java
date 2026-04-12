package Domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftCanidatesWorkersFacade {
    private List<ShiftCanidates> canidates_list;

    public ShiftCanidatesWorkersFacade() {
        this.canidates_list = new ArrayList<>();
    }
    public boolean containWorker(LocalDate date, boolean is_morning, int id){
        Shift shift = new Shift(date, is_morning);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return shiftCanidates.containWorker(id);
        }
        return false;
    }
    public void startPlacement(LocalDate date,boolean is_morning){
        Shift shift = new Shift(date, is_morning);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                 shiftCanidates.placementStarted();
        }
    }
    public String addCandidate(LocalDate date, boolean is_morning, int id){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
        }
        Shift shift = new Shift(date, is_morning);
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
    public String removeCandidate(LocalDate date, boolean is_morning, int id){
       if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
        }
        Shift shift = new Shift(date, is_morning);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return shiftCanidates.removeCandidate(id);
            
        }
        return "Failed, there is no shift: "+shift.toString();
    }

    public boolean containAllWorkers(LocalDate date,boolean is_morning,List<Integer> ids){
        Shift shift=new Shift(date, is_morning);
        for (ShiftCanidates canidates : canidates_list) {
            if(shift.equals(canidates.getShift()))
                return canidates.containAllWorkers(date,is_morning,ids);
        }
        return false;
    }

    public String getCandidatesForShift(LocalDate date, boolean is_morning) {
        Shift shift=new Shift(date, is_morning);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return ("The candidates for the shift: "+shiftCanidates.getShift().toString()
            +" ,are: "+shiftCanidates.getWorkersIds().toString());
        }

        return ("The shift: "+shift.toString()+", has no candidates yet.");
    }

    
}
