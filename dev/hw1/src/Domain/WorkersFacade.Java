package Domain;
import java.time.LocalDate; 
import java.util.HashMap;
public class WorkersFacade{
    private HashMap<Integer,Worker> workers;

    public WorkersFacade(){
        workers = new HashMap<Integer, Worker>();
    }

    public String addWorker(String name, int id, String bank_account, float salary, String salary_condision, LocalDate start_date, boolean is_shift_manager){
        //Worker worker = new Worker(name, bank_account, salary, salary_condision, start_date, is_shift_manager );
        if(workers.containsKey(id)){
            return "faild, worker with id: "+id+ " already exists.";
        }
       // workers.put(id, worker);
        return "success";
    }

    public String editWorkerName(int id, String new_name){
        if(!workers.containsKey(id)){
            return "faild, worker with id: "+id+ " doesn't exist.";
        }
       // workers.get(id).setName(new_name);
        return ("success, name changed to: "+new_name);
    }

    public String removeWorker(int id){
        if(!workers.containsKey(id))
            return "faild, worker with id: "+id+ " doesn't exist.";

        workers.remove(id);
        return ("success, worker removed");

    }




}