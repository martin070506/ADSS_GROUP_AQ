package Domain;
import java.time.LocalDate; 
import java.util.HashMap;
public class WorkersFacade{
    private HashMap<Integer,Worker> workers;

    public WorkersFacade(){
        workers = new HashMap<Integer, Worker>();
    }

    public String addWorker(String name, int id, String bank_account, double salary, String salary_condition, LocalDate start_date, boolean is_shift_manager){
        Worker worker = new Worker(
            name,id,bank_account,salary,salary_condition, start_date,is_shift_manager
        );
        if(workers.containsKey(id)){
            return "failed, worker with id: "+id+ " already exists.";
        }
        workers.put(id, worker);
        return ("success, worker with id: "+id+" Has been added.");
    }

    public String editWorkerName(int id, String new_name){
        if(!workers.containsKey(id)){
            return "failed, worker with id: "+id+ " doesn't exist.";
        }
        Worker worker=workers.get(id);
        worker.setName(new_name);
        workers.put(id,worker);
        return ("success, name changed to: "+new_name);
    }

    public String removeWorker(int id){
        if(!workers.containsKey(id))
            return "failed, worker with id: "+id+ " doesn't exist.";

        workers.remove(id);
        return ("success, worker with id: "+id+" Has been removed.");

    }

    public String editWorkerId(int old_id, int new_id){
        if(!workers.containsKey(old_id))
            return "failed, worker with id: "+old_id+ " doesn't exist.";

        Worker worker=workers.get(old_id);
        workers.remove(old_id);
        worker.setId(new_id);
        workers.put(new_id,worker);
        return ("success, id changed to: "+new_id);
    }
    public String editWorkerBankAccount(int id, String new_bank_account){
        if(!workers.containsKey(id))
            return "failed, worker with id: "+id+ " doesn't exist.";

        Worker worker=workers.get(id);
        worker.setBank_info(new_bank_account);
        workers.put(id,worker);
        return ("success, bank account information changed to: "+new_bank_account);
    }
    public String editWorkerSalary(int id, double new_salary){
        if(!workers.containsKey(id))
            return "failed, worker with id: "+id+ " doesn't exist.";
        Worker worker=workers.get(id);
        worker.setSalary(new_salary);
        workers.put(id,worker);
        return ("success, worker's salary has been changed to: "+new_salary);
    }
    public String editWorkerSalaryCondision(int id, String new_salary_condision){
        if(!workers.containsKey(id))
            return "failed, worker with id: "+id+ " doesn't exist.";
        Worker worker=workers.get(id);
        worker.setSalary_condition(new_salary_condision);
        workers.put(id,worker);
        return ("success, worker's salary condition has been changed to: "+new_salary_condision);
    }
    public String editWorkerStartDate(int id, LocalDate new_start_date){
        if(!workers.containsKey(id))
            return "failed, worker with id: "+id+ " doesn't exist.";
        Worker worker=workers.get(id);
        worker.setStart_job_date(new_start_date);
        workers.put(id,worker);
        return ("success, worker's salary has been changed to: "+new_start_date);
    }

    public String editWorkerIsShiftManager(int id, boolean new_is_shift_manager){
        if(!workers.containsKey(id))
            return "failed, worker with id: "+id+ " doesn't exist.";
        Worker worker=workers.get(id);
        worker.setIs_shift_manager(new_is_shift_manager);
        workers.put(id,worker);
        return ("success, worker's shift manager status  has been changed to: "+new_is_shift_manager);

    }

}