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
        public String addPlacments(LocalDate date, boolean is_morning, int shift_manager, List<Integer> ids, List<Integer> jobs){
            if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
            }
            else if(ids==null||jobs==null){
                return "faild, some info is missing";
            }
            else if(ids.size()!=jobs.size()){
                return "faild, there are mistmatch sizes between the workers and jobs that sent";
            }
            else if(!workers.isShiftManager(shift_manager)){
                return "faild, placement does not contain shift manager";
            }
            else if(!allJobs.containAllJobs(date, is_morning, jobs)){
                return "faild, all job in placment did not match the shift sets jobs ";
            }
            //else if(!canidates.containAllWorkers(date, is_morning, ids)){
            //    return "faild, all workers in placment did not match the shift sets workers canidates ";
            //}
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
                    if(shiftPlacement.getShiftManager()!=shift_manager){
                        shiftPlacement.setShiftManager(shift_manager);

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
            if(new_Placement.getShiftManager()!=shift_manager){
                new_Placement.setShiftManager(shift_manager);

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
        public String changePlacment(LocalDate date, boolean is_morning, int id_to_out, int id_to_in){
            //else if(!canidates.containWorker(date, is_morning, id_to_in)){
            //    return "faild, all workers in placment did not match the shift sets workers canidates ";
            //}
            if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
            }
            Shift shift = new Shift(date, is_morning);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    if(shiftPlacement.getShiftManager()==id_to_out){
                        if(!workers.isShiftManager(id_to_in)){
                            return "faild, placement cant change shift manager";
                        }
                        shiftPlacement.setShiftManager(id_to_in);
                    }

                }
                return shiftPlacement.changePlacment(id_to_out, id_to_in);
            }
            return "failed, shift not found";
        }


}
