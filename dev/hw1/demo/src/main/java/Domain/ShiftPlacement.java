package Domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ShiftPlacement{
    private Shift shift;
    private HashMap<Integer,Jobs> placements;
    private int shift_manager;
    public ShiftPlacement(LocalDate date, boolean is_morning){
        shift = new Shift(date, is_morning);
        placements = new HashMap<>();
        shift_manager=-1;
    }
    public String changePlacment(int id_to_out, int id_to_in){
        if(placements.containsKey(id_to_in)){
            return "failed, worker already in shift, can not have two jobs"; 
        }
        Jobs job = placements.get(id_to_out);
        placements.remove(id_to_out);
        placements.put(id_to_in, job);
        return "secceed, changed "+job+" from "+id_to_out+" to "+id_to_in;
    }
    public Shift getShift() {
        return shift;
    }
    public int getShiftManager(){
        return shift_manager;
    }
    public void setShiftManager(int id){
        shift_manager=id;
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("==== Shift Detail ====\n the shift date: "+shift.toString()+"\n the shift manager is: "+ shift_manager +"\n the shift placement \n ");
        placements.forEach((job, count) -> {
            result.append("  - ").append(job).append(" -> ").append(count).append(" worker\n");
        });
        return result.toString();
    }
    public String addPlacement(int id, int job){
        if(placements.containsKey(id)){
            return "faild, worker already have job in this shift";
        }
        else if(job==0){
            placements.put(id, Jobs.CASHEER);
            return "succeed, placment added for"+ id +" -> casheer";
        }
        else if (job ==1 ){
            placements.put(id, Jobs.SHOPKEEPER);
            return "succeed, placment added for"+ id +" -> shop keeper";
        }
        return "faild, job not found";
    }

    public List<Integer> getAllShiftWorkers(){
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, Jobs> entry : placements.entrySet()) {
            ids.add(entry.getKey());
        }
        return ids;
    }

}