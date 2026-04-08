package Domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftCanidatesWorkersFacade {
    private List<ShiftCanidates> canidates_list;

    public ShiftCanidatesWorkersFacade() {
        this.canidates_list = new ArrayList<>();
    }

    public String addCandidate(Shift shift, int id){
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
    public String removeCandidate(Shift shift, int id){
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

    
}
