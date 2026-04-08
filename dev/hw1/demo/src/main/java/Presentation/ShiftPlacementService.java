package Presentation;

import java.time.LocalDate;
import java.util.List;

import Domain.ShiftPlacmentFacade;

public class ShiftPlacementService {
    private ShiftPlacmentFacade shift_placement_facade;
    public ShiftPlacementService( ShiftPlacmentFacade place){
        shift_placement_facade = place;

    }
    public String addPlacement(LocalDate date, boolean is_morning, int shift_manager, List<Integer> ids, List<Integer> jobs){
        String result = shift_placement_facade.addPlacments(date, is_morning, shift_manager, ids, jobs);
        return result;
    }
    public String changePlacment(LocalDate date, boolean is_morning, int id_to_out, int id_to_in){
        String result = shift_placement_facade.changePlacment(date, is_morning, id_to_out, id_to_in);
        return result;
    }
    public String getShift(LocalDate date, boolean is_morning){
        return shift_placement_facade.getShiftPlacment(date, is_morning);
    }
}
