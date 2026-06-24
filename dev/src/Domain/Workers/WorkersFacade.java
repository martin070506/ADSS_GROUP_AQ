package Domain.Workers;

import DAO.WorkerDAO;
import DTO.WorkerDTO;
import DB.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkersFacade {
    private HashMap<Integer, Worker> workers;
    private WorkerDAO workers_dao;
    public WorkersFacade() {
        workers_dao = new WorkerDAO(DatabaseManager.getConnectionWrapper());
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

        WorkerDTO workerDTO = new WorkerDTO(name, id, bankAccount, salary, salaryCondition, startDate, isShiftManager, false, -1);
        try {
            workers_dao.addWorker(workerDTO);
        } catch (SQLException e) {
            return "failed, to add to database";
        }

        return "success, worker with id: " + id + " has been added.";
    }
    public String addDriver(String name, int id, String bankAccount, double salary, String salaryCondition, LocalDate startDate, boolean isShiftManager, int license) {
        if (workers.containsKey(id)) {
            return "failed, worker with id: " + id + " already exists.";
        }
        try{
            Worker worker = new Driver(name, id, bankAccount, salary, salaryCondition, startDate, isShiftManager, license);
            workers.put(id, worker);
            WorkerDTO workerDTO = new WorkerDTO(name, id, bankAccount, salary, salaryCondition, startDate, isShiftManager, true, license);
            workers_dao.addWorker(workerDTO);


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

        try {
            workers_dao.updateName(id, newName);
        } catch (SQLException e) {
            return "failed, to edit name";
        }

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

        try {
            workers_dao.deleteWorker(id);
        } catch (SQLException e) {
            return "failed to remove worker";
        }

        return "success, worker with id: " + id + " has been removed.";
    }

    public String editWorkerBankAccount(int id, String newBankAccount) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setBankInfo(newBankAccount);

        try {
            workers_dao.updateBankInfo(id, newBankAccount);
        } catch (SQLException e) {
            return "failed to edit bank info";
        }

        return "success, bank account information changed to: " + newBankAccount;
    }

    public String editWorkerSalary(int id, double newSalary) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setSalary(newSalary);

        try {
            workers_dao.updateSalary(id, newSalary);
        } catch (SQLException e) {
            return "failed to edit salary";
        }

        return "success, worker's salary has been changed to: " + newSalary;
    }

    public String editWorkerSalaryCondision(int id, String newSalaryCondision) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setSalaryCondition(newSalaryCondision);

        try {
            workers_dao.updateSalaryCondition(id, newSalaryCondision);
        } catch (SQLException e) {
            return "failed to edit salary condition";
        }

        return "success, worker's salary condition has been changed to: " + newSalaryCondision;
    }

    public String editWorkerStartDate(int id, LocalDate newStartDate) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setStartJobDate(newStartDate);

        try {
            workers_dao.updateStartJobDate(id, newStartDate);
        } catch (SQLException e) {
            return "failed to edit start date";
        }

        return "success, worker's start date has been changed to: " + newStartDate;
    }

    public String editWorkerIsShiftManager(int id, boolean newIsShiftManager) {
        if (!workers.containsKey(id)) {
            return "failed, worker with id: " + id + " doesn't exist.";
        }
        Worker worker = workers.get(id);
        worker.setShiftManager(newIsShiftManager);

        try {
            workers_dao.updateIsShiftManager(id, newIsShiftManager);
        } catch (SQLException e) {
            return "failed to edit is shift manager";
        }

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