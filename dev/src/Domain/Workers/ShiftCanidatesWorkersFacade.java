package Domain.Workers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DAO.ShiftCandidateIdDAO;
import DAO.ShiftCandidatesDAO;
import DTO.ShiftCandidateIdDTO;
import DTO.ShiftCandidatesDTO;
import Domain.Workers.Shift;
import Domain.Workers.ShiftCanidates;
import Domain.Transportation.Location;

public class ShiftCanidatesWorkersFacade {
    private List<ShiftCanidates> canidates_list;
    private WorkersFacade workers;
    private ShiftCandidatesDAO shift_candidates_dao;
    private ShiftCandidateIdDAO shift_candidate_id_dao;

    public ShiftCanidatesWorkersFacade(WorkersFacade workers) {
        this.canidates_list = new ArrayList<>();
        this.workers= workers;
        this.shift_candidates_dao = new ShiftCandidatesDAO(DatabaseManager.getConnection());
        this.shift_candidate_id_dao = new ShiftCandidateIdDAO(DatabaseManager.getConnection());
    }
    public boolean containWorker(LocalDate date, boolean is_morning, Location location, int id){
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return shiftCanidates.containWorker(id);
        }
        return false;
    }
    public List<Integer> getAllAvialableDrivers(LocalDate date, boolean is_morning, Location location){
        Shift shift = new Shift(date, is_morning, location);
        List<Integer> avialable_drivers=new ArrayList<>();
        List<Integer> avialable_workers =new ArrayList<>();

        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                avialable_workers.addAll(shiftCanidates.getWorkersIds());
        }
        for (int i=0; i<avialable_workers.size(); i++){
            if(workers.isDriver(avialable_workers.get(i))){
                avialable_drivers.add(avialable_workers.get(i));
            }
        }
        return avialable_drivers;
    }

    public void startPlacement(LocalDate date,boolean is_morning, Location location){
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                 shiftCanidates.placementStarted();
        }
    }
    public String addCandidate(LocalDate date, boolean is_morning,  Location location, int id){
        if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
        }
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
            {
                String result =  shiftCanidates.addCandidate(id);;

                try{

                    ShiftCandidateIdDTO can_id = new ShiftCandidateIdDTO(date, is_morning,location.id(),id);
                    shift_candidate_id_dao.add(can_id);
                    return result;
                }
                catch (Exception e){
                    return "failed, did not add canidate to data base";
                }
            }
        }
        List<Integer>new_id_list=new ArrayList<>();
        ShiftCanidates canidates_of_new_shift=new ShiftCanidates(shift, new_id_list);
        String result=canidates_of_new_shift.addCandidate(id);
        canidates_list.add(canidates_of_new_shift);

        try{
            ShiftCandidatesDTO can = new ShiftCandidatesDTO(date,is_morning,location.id());
            shift_candidates_dao.add(can);
            ShiftCandidateIdDTO can_id = new ShiftCandidateIdDTO(date, is_morning,location.id(),id);
            shift_candidate_id_dao.add(can_id);
            return result;
        }
        catch (Exception e){
            return "failed, did not add canidate to data base";
        }

    }
    public String removeCandidate(LocalDate date, boolean is_morning, Location location, int id){
       if(!LocalDate.now().isBefore(date)){
                return "failed, now is too late to change placement";
        }
        Shift shift = new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift())) {


                String result = shiftCanidates.removeCandidate(id);
                try {

                    ShiftCandidateIdDTO can_id = new ShiftCandidateIdDTO(date, is_morning, location.id(), id);
                    shift_candidate_id_dao.remove(can_id);
                    return result;
                } catch (Exception e) {
                    return "failed, did not removed canidate to data base";
                }
            }
        }
        return "Failed, there is no shift: "+shift.toString();
    }

    public boolean containAllWorkers(LocalDate date,boolean is_morning,Location location,List<Integer> ids){
        Shift shift=new Shift(date, is_morning, location);
        for (ShiftCanidates canidates : canidates_list) {
            if(shift.equals(canidates.getShift()))
                return canidates.containAllWorkers(date,is_morning,location,ids);
        }
        return false;
    }

    public String getCandidatesForShift(LocalDate date, boolean is_morning, Location location) {
        Shift shift=new Shift(date, is_morning, location);
        for (ShiftCanidates shiftCanidates : canidates_list) {
            if(shift.equals(shiftCanidates.getShift()))
                return ("The candidates for the shift: "+shiftCanidates.getShift().toString()
            +" ,are: "+shiftCanidates.getWorkersIds().toString());
        }

        return ("The shift: "+shift.toString()+", has no candidates yet.");
    }

    public String loadAllJobs(){
        List<ShiftCandidatesDTO> list = shift_candidates_dao.loadAll();
        for ( int i=0;i<list.size(); i++){
            Location location = Location.getLocation(list.get(i).locationId());
            Shift shift = new Shift(list.get(i).date(), list.get(i).is_morning_shift(), location);
            ShiftCanidates shiftCanidates = new ShiftCanidates(shift, new ArrayList<Integer>());
            canidates_list.add(shiftCanidates);
        }
        List<ShiftCandidateIdDTO> list_count = shift_candidate_id_dao.loadAll();
        for( int i=0; i< list_count.size(); i++){
            Location location = Location.getLocation(list_count.get(i).locationId());
            Shift shift = new Shift(list_count.get(i).date(), list_count.get(i).is_morning_shift(), location);
            for (ShiftCanidates shiftCanidates : canidates_list) {
                if(shiftCanidates.getShift().equals(shift)){
                    shiftCanidates.addCandidate(list_count.get(i).worker_id());
                }
            }
        }
        return "succeed, loaded all shift candidates data";

    }
}
