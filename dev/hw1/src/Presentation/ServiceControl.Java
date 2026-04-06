package presentation;
public class ServiceControl{
    private WorkersService workers_service;
    private ShiftJobsService jobs_service;
    private ShiftPlacementService placement_service;
    private ShiftWorkersCanidatesService canidates_service;
    public ServiceControl(){
        workers_service = new WorkersService();
        jobs_service = new ShiftJobsService();
        placement_service = new ShiftPlacementService();
        canidates_service = new ShiftWorkersCanidatesService();
    }
    public static void main(String[] args) {
        ServiceControl service = new ServiceControl();
        run();
    }
    public void run(){
        boolean exit=false;
        Scanner scanner = new Scanner(System.in);
        while (!exit) {
            System.out.println("Enter command (\n 'workers' for workers service \n 'exit' for exit):");
            String command = scanner.nextLine();

            if (command.equals("exit")) 
                exit=true;
            
            else if (command.equals("workers")) {
                runWorkersService();
            }
        }
    }
    public void runWorkersService(){
        boolean exit=false;
        Scanner scanner = new Scanner(System.in);
        while (!exit) {
            System.out.println("Welcome to workers service \n 1) for adding new worker enter 'add' \n 2) for removing worker enter 'remove' \n 3) for editing worker info enter 'edit' \n 4) for returning to main menu enter 'return' \n");
            String command = scanner.nextLine();

            if (command.equals("return")) 
                exit=true;
            
            else if (command.equals("add")) {
                runWorkersServiceAdd();
            }
            else if (command.equals("remove")) {
                runWorkersServiceRemove();
            }
            else if (command.equals("edit")) {
                runWorkersServiceEdit();
            }
        }
        run();
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
            Scanner scanner = new Scanner(System.in);

            System.out.println("enter new worker name: ");
            String name = scanner.nextLine();

            System.out.println("enter new worker id: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.println("enter new worker bank account info: ");
            String bank_account = scanner.nextLine();

            System.out.println("enter new worker salary: ");
            float salary = scanner.nextFloat();
            scanner.nextLine();

            System.out.println("enter new worker salary condisions: ");
            String salary_condision = scanner.nextLine();

            System.out.print("Enter new worker start date in this format (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();
            LocalDate start_date = LocalDate.parse(dateInput);

            System.out.println("enter if new worker can be shift manager: ");
            boolean is_shift_manager = scanner.nextBoolean();
            scanner.nextLine();

        }
        catch(Exception e){
           System.out.println("entered wring data type, returning to workers service menu ");

        }
        System.out.println(service.workers_service.add(name, id, bank_account, salary, salary_condisio, start_date, is_shift_manager));

        runWorkersService();
        
    }


}