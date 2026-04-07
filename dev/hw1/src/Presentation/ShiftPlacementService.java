package Presentation;

import java.time.LocalDate;
import java.util.List;
import Domain.ShiftPlacmentFacade;

public class ShiftPlacementService {
    private ShiftPlacmentFacade shift_placement_facade;
    public ShiftPlacementService( ShiftPlacmentFacade place){
        shift_placement_facade = place;

    }
    public String addPlacement(LocalDate date, boolean is_morning, List<Integer> ids, List<Integer> jobs){
        String result = shift_placement_facade.addPlacments(date, is_morning, ids, jobs);
        return result;
    }
    public String getShift(LocalDate date, boolean is_morning){
        return shift_placement_facade.getShiftPlacment(date, is_morning);
    }
}
