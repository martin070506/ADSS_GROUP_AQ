package Presentation;
import java.time.LocalDate;

import Domain.WorkersFacade;
public class WorkersService{
    private WorkersFacade workers;
    public WorkersService(){
        this.workers = new WorkersFacade();
    }

    public String addWorker(String name, int id, String bank_account, double salary, String salary_condision, LocalDate start_date, boolean is_shift_manager){
        String result = workers.addWorker(name, id,  bank_account, salary, salary_condision, start_date, is_shift_manager);
        return ("the result for adding new worker are: "+ result);
    }

    public String removeWorker(int id){
        String result = workers.removeWorker(id);
        return("the result for removing worker are: "+ result);   
     }

    public String editWorkerName(int id, String new_name){
        String result = workers.editWorkerName(id, new_name);
        return("the result for editing worker name are: "+ result);    
    }
    public String editWorkerId(int old_id, int new_id){
        String result = workers.editWorkerId(old_id, new_id);
        return("the result for editing worker id are: "+ result); 
    }
    public String editWorkerBankAccount(int id, String new_bank_account){
        String result = workers.editWorkerBankAccount(id, new_bank_account);
        return("the result for editing worker bank account information are: "+ result); 
    }
    public String editWorkerSalary(int id, double new_salary){
        String result = workers.editWorkerSalary(id, new_salary);
        return("the result for editing worker salary are: "+ result); 
    }
    public String editWorkerSalaryCondision(int id, String new_salary_condision){
        String result = workers.editWorkerSalaryCondision(id, new_salary_condision);
        return("the result for editing worker salary condision are: "+ result); 
    }
    public String editWorkerStartDate(int id, LocalDate new_start_date){
        String result = workers.editWorkerStartDate(id, new_start_date);
        return("the result for editing worker start date are: "+ result); 
    }
    public String editWorkerIsShiftManager(int id, boolean new_is_shift_manager){
        String result = workers.editWorkerIsShiftManager(id, new_is_shift_manager);
        return("the result for editing worker is shift manager are: "+ result); 
    }
}