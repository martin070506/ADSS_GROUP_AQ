package Domain;

import java.time.LocalDate; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkersFacade {
    private HashMap<Integer, Worker> workers;

    public WorkersFacade() {
        workers = new HashMap<>(); 
    }

    public String addWorker(String name, int id, String bankAccount, double salary, String salaryCondition, LocalDate startDate, boolean isShiftManager) {
        if (workers.containsKey(id)) {
            return "failed, worker with id: " + id + " already exists.";
        }
        Worker worker = new Worker(name, id, bankAccount, salary, salaryCondition, startDate, isShiftManager);
        workers.put(id, worker);
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

}