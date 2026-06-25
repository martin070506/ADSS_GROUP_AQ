package Service.Workers;

import java.time.LocalDate;
import java.util.List;

import Domain.Workers.ShiftPlacmentFacade;
import Domain.Transportation.Location;

public class  ShiftPlacementService {
    private ShiftPlacmentFacade shift_placement_facade;
    public ShiftPlacementService( ShiftPlacmentFacade place){
        shift_placement_facade = place;

    }
    public String addPlacement(LocalDate date, boolean is_morning, Location location, int shift_manager, List<Integer> ids, List<Integer> jobs){
        String result = shift_placement_facade.addPlacments(date, is_morning, location, shift_manager, ids, jobs);
        return result;
    }
    public String changePlacment(LocalDate date, boolean is_morning, Location location,  int id_to_out, int id_to_in){
        String result = shift_placement_facade.changePlacment(date, is_morning, location, id_to_out, id_to_in);
        return result;
    }
    public String getShift(LocalDate date, boolean is_morning, Location location){
        return shift_placement_facade.getShiftPlacment(date, is_morning, location);
    }
    public String PlaceDriver(LocalDate date, boolean is_morning, Location location, int driver_id){
        return shift_placement_facade.PlaceDriver(date, is_morning, location, driver_id);
    }

    public String loadAllJobs(){
        return shift_placement_facade.loadAllJobs();
    }
}
