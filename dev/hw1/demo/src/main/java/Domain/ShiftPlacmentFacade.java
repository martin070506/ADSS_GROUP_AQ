package Domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShiftPlacmentFacade {
        private List<ShiftPlacement> shifts;
        private WorkersFacade workers;
        private ShiftJobsFacade allJobs;
        private ShiftCanidatesWorkersFacade canidates;
        public ShiftPlacmentFacade(WorkersFacade workers, ShiftJobsFacade allJobs, ShiftCanidatesWorkersFacade canidates){
            shifts = new ArrayList<>();
            this.workers=workers;
            this.allJobs=allJobs;
            this.canidates=canidates;
        }
        public String addPlacments(LocalDate date, boolean is_morning, List<Integer> ids, List<Integer> jobs){
            if(ids==null||jobs==null){
                return "faild, some info is missing";
            }
            else if(ids.size()!=jobs.size()){
                return "faild, there are mistmatch sizes between the workers and jobs that sent";
            }
            else if(!workers.hasShiftManager(ids)){//It should be in "canidates".
                return "faild, placement does not contain shift manager";
            }
            else if(!allJobs.containAllJobs(date, is_morning, jobs)){
                return "faild, all job in placment did not match the shift sets jobs ";
            }
            else if(!canidates.containAllWorkers(date, is_morning, ids)){
              return "faild, all workers in placment did not match the shift sets workers canidates ";
            }
            int length = jobs.size();
            Shift shift = new Shift(date, is_morning);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    for( int i=0; i<length; i++){
                        String result = shiftPlacement.addPlacement(ids.removeFirst(), jobs.removeFirst());
                        if(!result.startsWith("succeed")){
                            return "faild at adding, "+result;
                        }
                    }
                    return "succeed, added all placment \n"+ shiftPlacement.toString();
                }

            }
            ShiftPlacement new_Placement = new ShiftPlacement(date, is_morning);
            for( int i=0; i<length; i++){
                String result = new_Placement.addPlacement(ids.removeFirst(), jobs.removeFirst());
                if(!result.startsWith("succeed")){
                        return "faild at adding, "+result;
                }
            }
            shifts.add(new_Placement);
            return "succeed, added all placment \n"+ new_Placement.toString();
            
        }
        public String getShiftPlacment(LocalDate date, boolean is_morning){
            Shift shift = new Shift(date, is_morning);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    return shiftPlacement.toString();
                }

            }
            return "faild, shift placment not found";
        }
        

}
