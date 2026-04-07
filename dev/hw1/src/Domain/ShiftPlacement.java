package Domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ShiftPlacement{
    private Shift shift;
    private HashMap<Integer,Jobs> placements;
    public ShiftPlacement(LocalDate date, boolean is_morning){
        shift = new Shift(date, is_morning);
        placements = new HashMap<>();
    }

    public Shift getShift() {
        return shift;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("==== Shift Detail ====\n the shift date: "+shift.toString()+"\n the shift placement \n ");
        placements.forEach((job, count) -> {
            result.append("  - ").append(job).append(" -> ").append(count).append(" workers\n");
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