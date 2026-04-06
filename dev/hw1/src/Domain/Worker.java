package Domain;
import java.time.LocalDate;



public class Worker {
    private String name;
    private int id;
    private String bank_info;
    private double salary;
    private String salary_condition;
    private LocalDate start_job_date;
    private boolean is_shift_manager;

    public Worker(String name, int id, String bank_info, double salary, String salary_condition, LocalDate start_job_date, boolean is_shift_manager) {
        this.name = name;
        this.id = id;
        this.bank_info = bank_info;
        this.salary = salary;
        this.salary_condition = salary_condition;
        this.start_job_date = start_job_date;
        this.is_shift_manager = is_shift_manager;
    }


    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getBank_info() {
        return bank_info;
    }

    public double getSalary() {
        return salary;
    }

    public String getSalary_condition() {
        return salary_condition;
    }

    public LocalDate getStart_job_date() {
        return start_job_date;
    }

    
    public boolean isIs_shift_manager() {
        return is_shift_manager;
    }

    

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBank_info(String bank_info) {
        this.bank_info = bank_info;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setSalary_condition(String salary_condition) {
        this.salary_condition = salary_condition;
    }

    public void setStart_job_date(LocalDate start_job_date) {
        this.start_job_date = start_job_date;
    }

    public void setIs_shift_manager(boolean is_shift_manager) {
        this.is_shift_manager = is_shift_manager;
    }
}
