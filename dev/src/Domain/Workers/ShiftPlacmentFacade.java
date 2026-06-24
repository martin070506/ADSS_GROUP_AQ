package Domain.Workers;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DB.DatabaseManager;
import DTO.ShiftPlacementDTO;
import DTO.ShiftPlacementJobsWorkersDTO;
import DAO.ShiftPlacementDAO;
import DAO.ShiftPlacementJobsWorkersDAO;
import Domain.Transportation.Location;

public class ShiftPlacmentFacade {
        private List<ShiftPlacement> shifts;
        private WorkersFacade workers;
        private ShiftJobsFacade allJobs;
        private ShiftCanidatesWorkersFacade canidates;
        private ShiftPlacementJobsWorkersDAO placement_job_worker;
        private ShiftPlacementDAO placement_dao;
        public ShiftPlacmentFacade(WorkersFacade workers, ShiftJobsFacade allJobs, ShiftCanidatesWorkersFacade canidates) throws SQLException {
            shifts = new ArrayList<>();
            this.workers=workers;
            this.allJobs=allJobs;
            this.canidates=canidates;
            Connection dbConnection = DatabaseManager.getConnection();
            placement_job_worker = new ShiftPlacementJobsWorkersDAO(dbConnection);
            placement_dao = new ShiftPlacementDAO(dbConnection);
        }
    public String loadAllJobs(){
        List<ShiftPlacementDTO> list = placement_dao.loadAll();
        for ( int i=0;i<list.size(); i++){
            Location location = new Location(0, null, null, null); // Todo: Fix this
            ShiftPlacement shift = new ShiftPlacement(list.get(i).date(), list.get(i).is_morning_shift(), location);
            shift.setShiftManager(list.get(i).shift_manager_id());
            shifts.add(shift);
        }
        List<ShiftPlacementJobsWorkersDTO> list_count = placement_job_worker.loadAll();
        for( int i=0; i< list_count.size(); i++){
            Location location = new Location(0, null, null, null); // Todo: Fix this
            Shift shift = new Shift(list_count.get(i).date(), list_count.get(i).is_morning_shift(), location);
            for (ShiftPlacement shift_placement : shifts) {
                if(shift_placement.getShift().equals(shift)){
                    shift_placement.addPlacement(list_count.get(i).worker_id(),list_count.get(i).job());
                }
            }
        }
        return "succeed, loaded all placement data";

    }

    public String addPlacments(LocalDate date, boolean is_morning, Location location, int shift_manager, List<Integer> ids, List<Integer> jobs){
            if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
            }
            //else if(ids==null||jobs==null){
              //  return "failed, some info is missing";
            //}
            else if(ids.size()!=jobs.size()){
                return "failed, there are mistmatch sizes between the workers and jobs that sent";
            }
            else if(!workers.isShiftManager(shift_manager)){
                return "failed, "+shift_manager+" can not be shift manager";
            }
            else if(!allJobs.containAllJobs(date, is_morning, location, jobs)){
                return "failed, all job in placment did not match the shift sets jobs ";
            }
            else if(!canidates.containAllWorkers(date, is_morning, location, ids)){
              return "failed, all workers in placment did not match the shift sets workers canidates ";
            }
            int length = jobs.size();
            Shift shift = new Shift(date, is_morning, location);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    for( int i=0; i<length; i++){
                        if(jobs.getFirst()==2 && !workers.isShiftManager(ids.getFirst())){
                            return "failed, "+ids.getFirst()+" is not a driver";
                        }
                        String result = shiftPlacement.addPlacement(ids.get(i), jobs.get(i));
                        if(!result.startsWith("succeed")){
                            return "failed at adding, "+result;
                        }
                    }
                    if(shiftPlacement.getShiftManager()!=shift_manager){
                        shiftPlacement.setShiftManager(shift_manager);

                    }
                    canidates.startPlacement(date, is_morning, location);
                    allJobs.startPlacement(date,is_morning, location);
                    try{
                        for(int i=0; i< ids.size();i++){
                            ShiftPlacementJobsWorkersDTO place_dto_iw = new ShiftPlacementJobsWorkersDTO(jobs.get(i),date,is_morning,location.id(), ids.get(i));
                            placement_job_worker.add(place_dto_iw);
                        }
                    }
                    catch (Exception e){
                        return "failed, to add to data base";
                    }
                    return "succeed, added all placment \n"+ shiftPlacement.toString();
                }

            }
            ShiftPlacement new_Placement = new ShiftPlacement(date, is_morning, location);
            for( int i=0; i<length; i++){
                if(jobs.getFirst()==2 && !workers.isShiftManager(ids.getFirst())){
                    return "failed, "+ids.getFirst()+" is not a driver";
                }
                String result = new_Placement.addPlacement(ids.get(i), jobs.get(i));
                if(!result.startsWith("succeed")){
                        return "failed at adding, "+result;
                }
            }
            if(new_Placement.getShiftManager()!=shift_manager){
                new_Placement.setShiftManager(shift_manager);

            }
            shifts.add(new_Placement);
            canidates.startPlacement(date, is_morning, location);
            allJobs.startPlacement(date,is_morning, location);
            try{
                ShiftPlacementDTO placment_dto = new ShiftPlacementDTO(date,is_morning,location.id(),shift_manager);
                placement_dao.add(placment_dto);
                for(int i=0; i< ids.size();i++){
                    ShiftPlacementJobsWorkersDTO place_dto_iw = new ShiftPlacementJobsWorkersDTO(jobs.get(i),date,is_morning,location.id(), ids.get(i));
                    placement_job_worker.add(place_dto_iw);
                }
            }
            catch (Exception e){
                return "failed, to add to data base";
            }
            return "succeed, added all placment \n"+ new_Placement.toString();
    }
    public String PlaceDriver(LocalDate date, boolean is_morning, Location location, int driver_id){
            if(!workers.isShiftManager(driver_id)){
                return "failed, "+driver_id+" is not a driver";
            }
            else if (!allJobs.containAllJobs(date, is_morning, location, List.of(2))){
                return "failed, there is no job for a driver in this shift.";
            }
            Shift shift = new Shift(date, is_morning, location);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    if(shiftPlacement.getShiftManager()<0){return "failed, shift manager not found";}
                    shiftPlacement.addPlacement(driver_id, 2);
                    try{
                        ShiftPlacementJobsWorkersDTO place_dto_iw = new ShiftPlacementJobsWorkersDTO(2,date,is_morning,location.id(), driver_id);
                        placement_job_worker.add(place_dto_iw);
                    }
                    catch (Exception e){
                        return "failed, did not palced driver in the data base";
                    }
                    return "succeed, "+driver_id+" is now the driver in the shift";
                }
            }
            return "failed, shift manager not found";
            //ShiftPlacement new_Placement = new ShiftPlacement(date, is_morning, location);
            //new_Placement.addPlacement(driver_id, 2);
            //shifts.add(new_Placement);
            //return "succeed, "+driver_id+" is now the driver in the shift";

    }
    public String getShiftPlacment(LocalDate date, boolean is_morning, Location location){
            Shift shift = new Shift(date, is_morning, location);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    return shiftPlacement.toString();
                }

            }
            return "failed, shift placment not found";
    }
    public String changePlacment(LocalDate date, boolean is_morning, Location location, int id_to_out, int id_to_in){
            if(!canidates.containWorker(date, is_morning,location, id_to_in)){
                return "failed, all workers in placment did not match the shift sets workers canidates ";
            }
            if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
            }
            Shift shift = new Shift(date, is_morning, location);
            for (ShiftPlacement shiftPlacement : shifts) {
                if(shiftPlacement.getShift().equals(shift)){
                    if(shiftPlacement.getShiftManager()==id_to_out){
                        if(!workers.isShiftManager(id_to_in)){
                            return "failed, placement cant change shift manager";
                        }
                        shiftPlacement.setShiftManager(id_to_in);

                    }

                }
                //updateWorkerInShift

                String result =  shiftPlacement.changePlacment(id_to_out, id_to_in);
                try{
                    ShiftPlacementJobsWorkersDTO place_dto_iw = new ShiftPlacementJobsWorkersDTO(shiftPlacement.getJob(id_to_out),date,is_morning,location.id(), id_to_out);
                    placement_job_worker.updateWorkerInShift(place_dto_iw,id_to_in);
                }
                catch (Exception e){
                    return "failed, did not palced driver in the data base";
                }
                return result;
            }
            return "failed, shift not found";
    }


}
