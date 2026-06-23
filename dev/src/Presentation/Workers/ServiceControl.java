package Presentation.Workers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Domain.Transportation.Location;
import Service.Transportation.LocationService;
import Service.Workers.ShiftJobsService;
import Service.Workers.ShiftPlacementService;
import Service.Workers.ShiftWorkersCanidatesService;
import Service.Workers.WorkersService;

public class ServiceControl{
    private  final Scanner scanner = new Scanner(System.in);
    private  final WorkersService workers_service;
    private  final ShiftJobsService jobs_service;
    private  final ShiftPlacementService placement_service;
    private  final ShiftWorkersCanidatesService canidates_service;
    private  final LocationService locationService;

    public ServiceControl(LocationService locationService, WorkersService workers_service, ShiftJobsService jobs_service, ShiftWorkersCanidatesService candidates_service, ShiftPlacementService placement_service){
        this.workers_service= workers_service;
        this.jobs_service = jobs_service;
        this.canidates_service = candidates_service;
        this.placement_service = placement_service;
        this.locationService = locationService;
        this.workers_service.loadAllWorkers();
        this.jobs_service.loadAllJobs();
        this.canidates_service.loadAllCanidates();
        this.placement_service.loadAllPlacement();
    }
//    public   void main(LocationService locationService, WorkersService workers_service, ShiftJobsService jobs_service, ShiftWorkersCanidatesService candidates_service, ShiftPlacementService placement_service) {
//        ServiceControl service = new ServiceControl(locationService, workers_service, jobs_service, candidates_service, placement_service);
//        run();
//    }
    public void run(){
        boolean exit=false;
        System.out.println("system woke up... \nsystem initialize");
//        System.out.println("Enter true to load preset data and false to enter without");
//        boolean data  = scanner.nextBoolean();
//        if(data)
//            load_data();
//        scanner.nextLine();
        while (!exit) {
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
//    public   void load_data(){
//        workers_service.addWorker("Mark", 0, "discount", 33.7, "above avg",  LocalDate.parse("2011-11-11"), true);
//        workers_service.addWorker("Pam", 1, "hapoalim", 31.4, "no extra hours",  LocalDate.parse("2015-05-05"), false);
//        workers_service.addWorker("Elon", 2, "mizrachi", 38.7, "manager terms",  LocalDate.parse("2009-11-11"), true);
//        workers_service.addWorker("Tim", 3, "leomi", 29.9, "trying term",  LocalDate.parse("2021-09-12"), false);
//
//        canidates_service.addCandidate(LocalDate.parse("2027-01-01"),true, new Location(),0);
//        canidates_service.addCandidate(LocalDate.parse("2027-01-01"),true, new Location(),1);
//        canidates_service.addCandidate(LocalDate.parse("2027-01-01"),true, new Location(),2);
//        canidates_service.addCandidate(LocalDate.parse("2027-01-01"),true, new Location(),3);
//
//        canidates_service.addCandidate(LocalDate.parse("2028-01-02"),true, new Location(),0);
//        canidates_service.addCandidate(LocalDate.parse("2028-01-02"),false, new Location(),1);
//
//        jobs_service.addJob(LocalDate.parse("2027-01-01"),true,new Location(),0);
//        jobs_service.addJob(LocalDate.parse("2027-01-01"),true,new Location(),0);
//        jobs_service.addJob(LocalDate.parse("2027-01-01"),true,new Location(),1);
//        jobs_service.addJob(LocalDate.parse("2027-01-01"),true,new Location(),1);
//
//
//        jobs_service.addJob(LocalDate.parse("2028-01-02"),false,new Location(),0);
//
//        placement_service.addPlacement(LocalDate.parse("2027-01-01"), true, new Location(), 0, List.of(0, 1, 2, 3), List.of(0, 1, 0, 1));
//        placement_service.addPlacement(LocalDate.parse("2028-01-02"), false, new Location(), 0, List.of(1), List.of(0));
//
//
//    }
    public void runCanidateService(){
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
    public void runCanidatesServiceRemove(){
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
                        
            Location location = getLocation();

                        
            System.out.println("enter the id for the worker");
            worker = scanner.nextInt();
            scanner.nextLine();

            System.out.println(canidates_service.removeCandidate(date,is_morning,location, worker));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }
    public void runCanidatesServiceAdd(){
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
            
            Location location = getLocation();
            
            System.out.println("enter the id for the worker");
            worker = scanner.nextInt();
            scanner.nextLine();

            System.out.println(canidates_service.addCandidate(date,is_morning,location,worker));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }
    public void runViewCanidatesShiftService(){
        LocalDate date= null;
        boolean is_morning=false;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();

            Location location = getLocation();


            System.out.println(canidates_service.getCandidatesForShift(date,is_morning, location));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }

    }
    public void runJobsService(){
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
                 System.out.println("the system did not understand your input, trying again:");
            }
        }
    }
    public  void runViewShiftJobsService(){
        LocalDate date= null;
        boolean is_morning=false;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();
                        
            Location location = getLocation();


            System.out.println(jobs_service.getShift(date,is_morning, location));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }

    }
    public   void runPlacementService(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to shift placement service \n 1) for adding placement job enter 'add' \n 2) for changing placement enter 'change'\n 3) for viewing shift placement enter 'view' \n 3) for returning to main menu enter 'return' \n");
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
    public   void runChangePlacementService(){
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

            Location location = getLocation();


            System.out.println("enter the id of the worker to remove from shift: ");
            id_change_from = scanner.nextInt();
            scanner.nextLine();

             System.out.println("enter the id of the worker to add from shift: ");
            id_change_to = scanner.nextInt();
            scanner.nextLine();

            System.out.println(placement_service.changePlacment(date,is_morning,location, id_change_from,id_change_to));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }

    }
    public   void runPlacementServiceAdd(){
        boolean exit=false;
        List<Integer> jobs = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        LocalDate date= null;
        boolean is_morning=false;
        int shift_manager = 0;
        Location location = null;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();

            location = getLocation();


            System.out.println("enter shift manager id: ");
            shift_manager = scanner.nextInt();
            scanner.nextLine();
        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }
        while (!exit) {
            System.out.println(" placement adding menu: \n 1) to enter a placement enter 'add' \n 2) to stop entering placement enter 'stop'");
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
                        System.out.println("enter the worker job {0 for casheer, 1 for shop keeper, 2 for driver}:");
                        jobs.addFirst(scanner.nextInt());
                        scanner.nextLine();
                    }
                    catch(Exception e){
                        ids.removeFirst();
                        System.out.println("the system did not understand your input, trying again:");
                    }
                    
                }
                catch(Exception e){
                    System.out.println("the system did not understand your input, trying again:");

                }
                
            }

            else{ 
                 System.out.println("the system did not understand your input, trying again:");
            }
        }
        System.out.println(placement_service.addPlacement(date, is_morning, location, shift_manager, ids, jobs));
    }
    public void runViewPlacementService(){
        LocalDate date= null;
        boolean is_morning=false;
        try{
            System.out.println("enter shift date in this format ('yyyy-mm-dd'): ");
            String text_date = scanner.nextLine();
            date = LocalDate.parse(text_date);

            System.out.println("enter 'true' if this is morning shift else enter 'false': ");
            is_morning = scanner.nextBoolean();
            scanner.nextLine();

            Location location = getLocation();

            System.out.println(placement_service.getShift(date,is_morning, location));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to placement service menu ");

        }

    }
    public void runJobsServiceEdit(){
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

            Location location = getLocation();

            
            System.out.println("enter '0' for changing from casheer job, '1' from shop keeper");
            job_to_be_changed = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter '0' for changing to casheer job, '1' to shop keeper");
            job_to_change_to = scanner.nextInt();
            scanner.nextLine();

            System.out.println(jobs_service.changeJob(date,is_morning,location,job_to_be_changed,job_to_change_to));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }
    public   void runJobsServiceRemove(){
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
            
            Location location = getLocation();


            System.out.println("enter '0' for removing casheer job, '1' for shop keeper, '2' for driver:");
            job = scanner.nextInt();
            scanner.nextLine();

            System.out.println(jobs_service.removeJob(date,is_morning,location,job));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }
    public   void runJobsServiceAdd(){
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
            
            Location location = getLocation();
            
            System.out.println("enter '0' for adding casheer job, '1' for shop keeper, '2' for driver:");
            job = scanner.nextInt();
            scanner.nextLine();

            System.out.println(jobs_service.addJob(date,is_morning,location,job));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }
    public void runWorkersService(){
        boolean exit=false;
        while (!exit) {
            System.out.println("Welcome to workers service \n 1) for adding new worker enter 'add' \n 2) for adding new driver 'add driver' \n 3) for removing worker enter 'remove' \n 4) for editing worker info enter 'edit' \n 5) for viewing all workers 'view' \n 6) for returning to main menu enter 'return' \n");
            String command = scanner.nextLine();

            if (command.equals("return")) {
                exit=true;
            }
            else if (command.equals("add")) {
                runWorkersServiceAdd();
            }
            else if (command.equals("add driver")) {
                runWorkersServiceAddDriver();
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
    public void runWorkersServiceEdit(){
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
    public   void runWorkersServiceEditShiftManager(){
        int id = 0;
        boolean new_is_shift_manager =false;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new info if he can be shift manager: ");
            new_is_shift_manager = scanner.nextBoolean();
            
            System.out.println(workers_service.editWorkerIsShiftManager(id,new_is_shift_manager));

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }  
    public   void runWorkersServiceEditStartDate(){
        int id = 0;
        LocalDate new_date =null;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new start date (in this format ('yyyy-mm-dd')): ");
            String dateInput = scanner.nextLine();
            new_date = LocalDate.parse(dateInput);

            System.out.println(workers_service.editWorkerStartDate(id,new_date));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }  
    public void runWorkersServiceEditSalaryCondision(){
        int id = 0;
        String new_salary_condisions ="";
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new salary condisions: ");
            new_salary_condisions = scanner.nextLine();

            System.out.println(workers_service.editWorkerSalaryCondision(id,new_salary_condisions));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }  
    public void runWorkersServiceEditSalary(){
        int id = 0;
        float new_salary =0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new salary: ");
            new_salary = scanner.nextFloat();
            scanner.nextLine();

            System.out.println(workers_service.editWorkerSalary(id,new_salary));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }  
    public void runWorkersServiceEditBankAccount(){
        String new_bank_account = "";
        int id =0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new bank account info: ");
            new_bank_account = scanner.nextLine();

            System.out.println(workers_service.editWorkerBankAccount(id,new_bank_account));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }  
    public void runWorkersServiceEditId(){
        int new_id = 0;
        int id =0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new id: ");
            new_id = scanner.nextInt();
            scanner.nextLine();

            System.out.println(workers_service.editWorkerId(id,new_id));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }   
    public void runWorkersServiceEditName(){
        String new_name = "";
        int id=0;
        try{
            System.out.println("enter worker id: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter worker new name: ");
            new_name = scanner.nextLine();
            
            System.out.println(workers_service.editWorkerName(id, new_name));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

        runWorkersService();
    }
    public void runWorkersServiceRemove(){
        int id = 0;
        try{

            System.out.println("enter worker id for the worker to be removed: ");
            id = scanner.nextInt();
            scanner.nextLine();

            System.out.println(workers_service.removeWorker(id));


        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

    }
    public void runWorkersServiceAddDriver(){
        String name = "";
        int id = 0;
        String bank_account = "";
        float salary = 0;
        String salary_condision = "";
        LocalDate start_date = null;
        boolean is_shift_manager = false;
        int license = -1;
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

            System.out.println("enter new driver license: ");
            license = scanner.nextInt();
            scanner.nextLine();

            String result =workers_service.addDriver(name, id, bank_account, salary, salary_condision, start_date, is_shift_manager, license);
            System.out.println(result);

        }
        catch(Exception e){
            System.out.println("entered wrong data type, returning to workers service menu ");

        }

        runWorkersService();
    }
    public void runWorkersServiceAdd(){
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

            String result =workers_service.addWorker(name, id, bank_account, salary, salary_condision, start_date, is_shift_manager);
            System.out.println(result);

        }
        catch(Exception e){
           System.out.println("entered wrong data type, returning to workers service menu ");

        }

        runWorkersService();
        
    }

    private Location getLocation(){
        return locationService.getLocation(selectLocation());
    }

    private int selectLocation() {
        List<Integer> locations = locationService.getLocationIds();
        if (locations.isEmpty()) {
            System.out.println("No locations available.");
            return -1;
        }

        System.out.println("\n--- Select Location ---");
        for (Integer location : locations)
            System.out.println(locationService.getLocationDisplay(location));
        while (true) {
            int choice = promptInt("Enter Location ID: ");
            if (locations.contains(choice))
                return choice;
            System.out.println("Invalid Location ID.");
        }
    }

    private int promptInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) { System.out.println("Invalid input. Please enter an integer."); }
        }
    }
}