package Domain.Workers;

import DAO.WorkerDAO;
import DTO.ShiftPlacementDTO;
import DTO.ShiftPlacementJobsWorkersDTO;
import DTO.WorkerDTO;
import Domain.Transportation.Location;
import Domain.Workers.Worker;
import DatabaseManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkersFacade {
    private HashMap<Integer, Worker> workers;
    private WorkerDAO workers_dao;
    public WorkersFacade() {
        workers_dao = new WorkerDAO(DatabaseManager.getConnection());
        workers = new HashMap<>();
    }
    public boolean hasWorker(int id){
        return this.workers.containsKey(id);
    }
    public String addWorker(String name, int id, String bankAccount, double salary, String salaryCondition, LocalDate startDate, boolean isShiftManager) {
        if (workers.containsKey(id)) {
            return "failed, worker with id: " + id + " already exists.";
        }
        Worker worker = new Worker(name, id, bankAccount, salary, salaryCondition, startDate, isShiftManager);
        workers.put(id, worker);
        return "success, worker with id: " + id + " has been added.";
    }
    public String addDriver(String name, int id, String bankAccount, double salary, String salaryCondition, LocalDate startDate, boolean isShiftManager, int license) {
        if (workers.containsKey(id)) {
            return "failed, worker with id: " + id + " already exists.";
        }
        try{
            Worker worker = new Driver(name, id, bankAccount, salary, salaryCondition, startDate, isShiftManager, license);
            workers.put(id, worker);

        }
        catch (Exception e){
            return "failed, "+ e + ".";

        }
        return "success, worker with id: " + id + " has been added.";
    }

    public String editWorkerName(int id, String newName) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setName(newName);
        return "success, name changed to: " + newName;
    }
    public boolean isDriver(int id){return workers.get(id).isDriver();}
    public int getLicense(int id){return workers.get(id).getLicense();}
    public String getName(int id){return workers.get(id).getName();}
    public String removeWorker(int id) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        workers.remove(id);
        return "success, worker with id: " + id + " has been removed.";
    }

    public String editWorkerId(int oldId, int newId) {
        if (!workers.containsKey(oldId)) {
            return "failed, worker with id: " + oldId + " doesn't exist.";
        }
        if (workers.containsKey(newId)) {
            return "failed, worker with id: " + newId + " already exists."; 
        }
        
        Worker worker = workers.get(oldId);
        workers.remove(oldId); 
        worker.setId(newId);   
        workers.put(newId, worker); 
        
        return "success, id changed to: " + newId;
    }

    public String editWorkerBankAccount(int id, String newBankAccount) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setBankInfo(newBankAccount);
        return "success, bank account information changed to: " + newBankAccount;
    }

    public String editWorkerSalary(int id, double newSalary) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setSalary(newSalary);
        return "success, worker's salary has been changed to: " + newSalary;
    }

    public String editWorkerSalaryCondision(int id, String newSalaryCondision) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setSalaryCondition(newSalaryCondision);
        return "success, worker's salary condition has been changed to: " + newSalaryCondision;
    }

    public String editWorkerStartDate(int id, LocalDate newStartDate) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setStartJobDate(newStartDate);
        return "success, worker's start date has been changed to: " + newStartDate;
    }

    public String editWorkerIsShiftManager(int id, boolean newIsShiftManager) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setShiftManager(newIsShiftManager);
        return "success, worker's shift manager status has been changed to: " + newIsShiftManager;
    }
    public boolean isShiftManager(int id){
        if(!workers.containsKey(id)){
            return false;
        }
        return workers.get(id).isShiftManager();
    }
    public String getAllWorkers(){
        StringBuilder result = new StringBuilder();
        result.append("==== All The Workers Details ====");
        int i=1;
        for (Map.Entry<Integer, Worker> entry : workers.entrySet()) {
            result.append("\n"+i+") "+ entry.getValue().toString());
            i++;
        }
        return result.toString();
    }
    public String getDriver(int id){
        if(workers.get(id) instanceof Driver){return workers.get(id).toString();}
        else{return "failed, "+id+" is not a driver";}
    }
    public String loadAllJobs(){
        List<WorkerDTO> list = workers_dao.loadAll();
        for ( int i=0;i<list.size(); i++){
            if(list.get(i).isDriver()){
                Worker worker = new Driver(list.get(i).name(),list.get(i).id(),list.get(i).bank_info(),list.get(i).salary(), list.get(i).salary_condition(), list.get(i).start_job_date(), list.get(i).is_shift_manager(), list.get(i).license());
                workers.put(worker.getId(),worker);
            }
            else{
                Worker worker = new Worker(list.get(i).name(),list.get(i).id(),list.get(i).bank_info(),list.get(i).salary(), list.get(i).salary_condition(), list.get(i).start_job_date(), list.get(i).is_shift_manager());
                workers.put(worker.getId(),worker);

            }
        }

        return "succeed, loaded all placement data";

    }

}