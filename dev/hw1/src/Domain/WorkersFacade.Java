import java.util.Objects;
import java.time.LocalDate; 
public Class WorkersFacade{
    private Map<int,Worker> workers;

    public WorkersFacade(){
        workers = new HashMap<int, Worker>;
    }

    public String addWorker(String name, int id, String bank_account, float salary, String salary_condision, Date start_date, boolean is_shift_manager){
        worker = new Worker(name, bank_account, salary, salary_condision, start_date, is_shift_manager );
        if(workers.contain(id)){
            return "faild, worker with id: "+id+ " already exists.";
        }
        workers.add(id, worker);
        return "success";
    }

    public String editWorkerName(int id, String new_name){
        if(!workers.contain(id)){
            return "faild, worker with id: "+id+ " doesn't exist.";
        }
        workers.get(id).setName(new_name);
        return ("success, name changed to: "+new_name);
    }

    public String removeWorker(int id){
        if(!workers.contain(id))
            return "faild, worker with id: "+id+ " doesn't exist.";

        workers.remove()

    }




}