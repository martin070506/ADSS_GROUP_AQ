package Presentation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Domain.ShiftCanidatesWorkersFacade;
import Domain.ShiftJobsFacade;
import Domain.ShiftPlacmentFacade;
import Domain.WorkersFacade;
public class ServiceControl{
    private static Scanner scanner = new Scanner(System.in);
    private static WorkersService workers_service;
    private static ShiftJobsService jobs_service;
    private static ShiftPlacementService placement_service;
    private static ShiftWorkersCanidatesService canidates_service;
    public ServiceControl(){
        WorkersFacade workers_facade = new WorkersFacade();
        workers_service= new WorkersService(workers_facade);
        ShiftJobsFacade jobs_facade = new ShiftJobsFacade();
        jobs_service = new ShiftJobsService(jobs_facade);
        ShiftCanidatesWorkersFacade canidates_facade = new ShiftCanidatesWorkersFacade();
        canidates_service = new ShiftWorkersCanidatesService(canidates_facade);
        ShiftPlacmentFacade placement_facade = new ShiftPlacmentFacade(workers_facade, jobs_facade, canidates_facade);
        placement_service = new ShiftPlacementService(placement_facade);

    }
    public static void main(String[] args) {
        ServiceControl service = new ServiceControl();
        run();
    }
    public static void run(){
        boolean exit=false;
        while (!exit) {
            System.out.println("system woke up... \nsystem initialize");
            System.out.println("Enter command \n 1) 'workers' for workers service \n 2) 'jobs' for jobs service \n 3) 'placement' for placement service \n 4) 'canidate' for shift canidates service \n 5) 'exit' for exit: ");
            String command = scanner.nextLine();

            if (command.equals("exit")) 
                exit=true;
            
            else if (command.equals("workers")) {
                runWorkersService();
            }
            else if (command.equals("jobs")){
                runJobsService();
            }
            else if (command.equals("placement")){
                runPlacementService();
            }
            else if (command.equals("canidate")){
                runCanidateService();
            }
            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
        }
    }
    public static void runCanidateService(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to shifts canidates workers service \n 1) for adding new canidate worker to a shift enter 'add' \n 2) for removing a worker canidate from a shift enter 'remove' \n 3) for viewing all shift canidates workers in a shift 'view' \n 4) for returning to main menu enter 'return' \n");
            String command = scanner.nextLine();
             if (command.equals("return")) {
                exit=true;
            }
            else if (command.equals("add")) {
                runCanidatesServiceAdd();
            }
            else if (command.equals("remove")) {
                runCanidatesServiceRemove();
            }
            else if (command.equals("view")) {
                runViewCanidatesShiftService();
            }
            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
        }
    }
    public static void runCanidatesServiceRemove(){
        LocalDate date= null;
        boolean is_morning=false;
        int worker=-1;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
            
            System.out.println("enter the id for the worker");
            worker = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(canidates_service.removeCandidate(date,is_morning,worker));

    }
    public static void runCanidatesServiceAdd(){
        LocalDate date= null;
        boolean is_morning=false;
        int worker=-1;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
            
            System.out.println("enter the id for the worker");
            worker = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(canidates_service.addCandidate(date,is_morning,worker));

    }
    public static void runViewCanidatesShiftService(){
        LocalDate date= null;
        boolean is_morning=false;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }
        System.out.println(canidates_service.getCandidatesForShift(date,is_morning));

    }
    public static void runJobsService(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to shifts jobs service \n 1) for adding new job enter 'add' \n 2) for removing job enter 'remove' \n 3) for editing job  enter 'edit' \n 4) for viewing all jobs in a shift 'view' \n 5) for returning to main menu enter 'return' \n");
            String command = scanner.nextLine();
             if (command.equals("return")) {
                exit=true;
            }
            else if (command.equals("add")) {
                runJobsServiceAdd();
            }
            else if (command.equals("remove")) {
                runJobsServiceRemove();
            }
            else if (command.equals("edit")) {
                runJobsServiceEdit();
            }
            else if (command.equals("view")) {
                runViewShiftJobsService();
            }
            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
        }
    }
    public static void runViewShiftJobsService(){
        LocalDate date= null;
        boolean is_morning=false;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }
        System.out.println(jobs_service.getShift(date,is_morning));

    }
    public static void runPlacementService(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to shift placement service \n 1) for adding placement job enter 'add' \n 2) for changing placment enter 'change'\n 3) for viewing shift placement enter 'view' \n 3) for returning to main menu enter 'return' \n");
            String command = scanner.nextLine();
             if (command.equals("return")) {
                exit=true;
            }
            else if (command.equals("add")) {
                runPlacementServiceAdd();
            }
            else if (command.equals("view")) {
                runViewPlacementService();
            }
            else if (command.equals("change")) {
                runChangePlacementService();
            }
            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
        }
    }
    public static void runChangePlacementService(){
        LocalDate date= null;
        boolean is_morning=false;
        int id_change_from=0, id_change_to=0;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();

            System.out.println("enter the id of the worker to remove from shift: ");
            id_change_from = scanner.nextInt();
            scanner.nextLine();

             System.out.println("enter the id of the worker to add from shift: ");
            id_change_to = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }
        System.out.println(placement_service.changePlacment(date,is_morning, id_change_from,id_change_to));

    }
    public static void runPlacementServiceAdd(){
        boolean exit=false;
        List<Integer> jobs = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        LocalDate date= null;
        boolean is_morning=false;
        int shift_manager = 0;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();

            System.out.println("enter shift manager id: ");
            shift_manager = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }
        while (!exit) {
            System.out.println(" placement adding menu: \n 1) to enter a placement enter 'add' \n 2) to stop enterin placement enter 'stop'");
            String command = scanner.nextLine();
             if (command.equals("stop")) {
                exit=true;
            }
            else if (command.equals("add")) {
                try{
                    System.out.println("enter the worker id:");
                    ids.addFirst(scanner.nextInt());
                    scanner.nextLine();
                    try{
                        System.out.println("enter the worker job {0 for casheer, 1 for shop keeper}:");
                        jobs.addFirst(scanner.nextInt());
                        scanner.nextLine();
                    }
                    catch(Exception e){
                        ids.removeFirst();
                        System.out.println("the system did not understand your input, tryng again:");
                    }
                    
                }
                catch(Exception e){
                    System.out.println("the system did not understand your input, tryng again:");

                }
                
            }

            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
        }
        System.out.println(placement_service.addPlacement(date, is_morning,shift_manager, ids, jobs));
    }
    public static void runViewPlacementService(){
        LocalDate date= null;
        boolean is_morning=false;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }
        System.out.println(placement_service.getShift(date,is_morning));

    }
    public static void runJobsServiceEdit(){
        LocalDate date= null;
        boolean is_morning=false;
        int job_to_be_changed=-1, job_to_change_to=-1;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
            
            System.out.println("enter '0' for changing from casheer job, '1' from shop keeper");
            job_to_be_changed = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter '0' for changing to casheer job, '1' to shop keeper");
            job_to_change_to = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(jobs_service.changeJob(date,is_morning,job_to_be_changed,job_to_change_to));

    }
    public static void runJobsServiceRemove(){
        LocalDate date= null;
        boolean is_morning=false;
        int job=-1;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
            
            System.out.println("enter '0' for removing casheer job, '1' for shop keeper");
            job = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(jobs_service.removeJob(date,is_morning,job));

    }
    public static void runJobsServiceAdd(){
        LocalDate date= null;
        boolean is_morning=false;
        int job=-1;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
            
            System.out.println("enter '0' for adding casheer job, '1' for shop keeper");
            job = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(jobs_service.addJob(date,is_morning,job));

    }
    public static void runWorkersService(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to workers service \n 1) for adding new worker enter 'add' \n 2) for removing worker enter 'remove' \n 3) for editing worker info enter 'edit' \n 4) for viewing all workers 'view' \n 5) for returning to main menu enter 'return' \n");
            String command = scanner.nextLine();

            if (command.equals("return")) {
                exit=true;
            }
            else if (command.equals("add")) {
                runWorkersServiceAdd();
            }
            else if (command.equals("remove")) {
                runWorkersServiceRemove();
            }
            else if (command.equals("edit")) {
                runWorkersServiceEdit();
            }
             else if (command.equals("view")) {
                System.out.println(workers_service.getAllWorkers());
            }
            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
        }
    }
    public static void runWorkersServiceEdit(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to workers edit service \n 1) for editing worker name 'name' \n 2) for editing worker id 'id' \n 3) for editing worker bank account info enter 'bank account' \n 4) for editing worker salary enter 'salary' \n 5) for editing worker salary condision enter 'salary condision' \n 6) for editing worker start date enter 'date' \n 7) for editing if worker can be shift manager enter 'shift manager' \n 8) for returning to workers service menu enter 'return' \n");
            String command = scanner.nextLine();
            if (command.equals("return")) {
                    exit=true;
            }
            else if (command.equals("name")) {
                    runWorkersServiceEditName();
            }
            else if (command.equals("id")) {
                    runWorkersServiceEditId();
            }
            else if (command.equals("bank account")) {
                    runWorkersServiceEditBankAccount();
            }
            else if (command.equals("salary")) {
                    runWorkersServiceEditSalary();
            }
            else if (command.equals("salary condision")) {
                    runWorkersServiceEditSalaryCondision();
            }
             else if (command.equals("date")) {
                    runWorkersServiceEditStartDate();
            }
             else if (command.equals("shift manager")) {
                    runWorkersServiceEditShiftManager();
            }
            else{ 
                 System.out.println("the system did not understand your input, tryng again:");
            }
            
        }
    }
    public static void runWorkersServiceEditShiftManager(){
        int id = 0;
        boolean new_is_shift_manager =false;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new info if he can be shift manager: ");
            new_is_shift_manager = scanner.nextBoolean();
            

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerIsShiftManager(id,new_is_shift_manager));

    }  
    public static void runWorkersServiceEditStartDate(){
        int id = 0;
        LocalDate new_date =null;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new start date (in this format ('yyyy-mm-dd')): ");
            String dateInput = scanner.nextLine();
            new_date = LocalDate.parse(dateInput);

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerStartDate(id,new_date));

    }  
    public static void runWorkersServiceEditSalaryCondision(){
        int id = 0;
        String new_salary_condisions ="";
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new salary condisions: ");
            new_salary_condisions = scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerSalaryCondision(id,new_salary_condisions));

    }  
    public static void runWorkersServiceEditSalary(){
        int id = 0;
        float new_salary =0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new salary: ");
            new_salary = scanner.nextFloat();
            scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerSalary(id,new_salary));

    }  
    public static void runWorkersServiceEditBankAccount(){
        String new_bank_account = "";
        int id =0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new bank account info: ");
            new_bank_account = scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerBankAccount(id,new_bank_account));

    }  
    public static void runWorkersServiceEditId(){
        int new_id = 0;
        int id =0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new id: ");
            new_id = scanner.nextInt();
            scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerId(id,new_id));

    }   
    public static void runWorkersServiceEditName(){
        String new_name = "";
        int id=0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new name: ");
            new_name = scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.editWorkerName(id, new_name));

        runWorkersService();
    }
    public static void runWorkersServiceRemove(){
        int id = 0;
        try{

            System.out.println("enter worker id for the worker to be removed: ");
            id = scanner.nextInt();
            scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        System.out.println(workers_service.removeWorker(id));

    }
    public static void runWorkersServiceAdd(){
        String name = "";
        int id = 0;
        String bank_account = "";
        float salary = 0;
        String salary_condision = "";
        LocalDate start_date = null;
        boolean is_shift_manager = false;
        try{

            System.out.println("enter new worker name: ");
            name = scanner.nextLine();

            System.out.println("enter new worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter new worker bank account info: ");
            bank_account = scanner.nextLine();

            System.out.println("enter new worker salary: ");
            salary = scanner.nextFloat();
            scanner.nextLine();

            System.out.println("enter new worker salary condisions: ");
            salary_condision = scanner.nextLine();

            System.out.print("Enter new worker start date in this format (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();
            start_date = LocalDate.parse(dateInput);

            System.out.println("enter if new worker can be shift manager: (false/true) ");
            is_shift_manager = scanner.nextBoolean();
            scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }
        String result =workers_service.addWorker(name, id, bank_account, salary, salary_condision, start_date, is_shift_manager);
        System.out.println(result);

        runWorkersService();
        
    }


}