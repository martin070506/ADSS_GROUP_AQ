Protected Class WorkersService{
    private WorkersFacade workers;
    public WorkersService(WorkersFacade workers){
        this.workers = workers;
    }

    public String addWorker(String name, int id, BankAccount bank_account, float salary, String salary_condision, Date start_date, boolean is_shift_manager){
        String result = workers.addWorker(name, id,  bank_account, salary, salary_condision, start_date, is_shift_manager);
        System.out.println("the result for adding new worker are: "+ result);
    }

    public String removeWorker(int id){
        String result = workers.removeWorker(id);
        System.out.println("the result for removing worker are: "+ result);   
     }

    public String editWorkerName(int id, String new_name){
        String result = workers.editWorkerName(id, new_name);
        System.out.println("the result for editing worker name are: "+ result);    
    }
    public String editWorkerId(int old_id, int new_id){
        String result = workers.editWorkerId(id, new_id);
        System.out.println("the result for editing worker id are: "+ result); 
    }
    public String editWorkerBankAccount(int id, BankAccount new_bank_account){
        String result = workers.editWorkerBankAccount(id, new_bank_account);
        System.out.println("the result for editing worker bank account are: "+ result); 
    }
    public String editWorkerSalary(int id, float new_salary){
        String result = workers.editWorkerSalary(id, new_salary);
        System.out.println("the result for editing worker salary are: "+ result); 
    }
    public String editWorkerSalaryCondision(int id, String new_salary_condision){
        String result = workers.editWorkerSalaryCondision(id, new_salary_condision);
        System.out.println("the result for editing worker salary condision are: "+ result); 
    }
    public String editWorkerStartDate(int id, Date new_start_date){
        String result = workers.editWorkerStartDate(id, new_start_date);
        System.out.println("the result for editing worker start date are: "+ result); 
    }
    public String editWorkerIsShiftManager(int id, boolean new_is_shift_manager){
        String result = workers.editWorkerIsShiftManager(id, new_is_shift_manager);
        System.out.println("the result for editing worker is shift manager are: "+ result); 
    }
}