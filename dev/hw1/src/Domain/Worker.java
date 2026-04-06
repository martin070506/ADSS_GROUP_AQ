package domain;
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

    public String getBankInfo() {
        return bank_info;
    }

    public double getSalary() {
        return salary;
    }

    public String getSalaryCondition() {
        return salary_condition;
    }

    public LocalDate getStartJobDate() {
        return start_job_date;
    }

    public boolean isShiftManager() {
        return is_shift_manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBankInfo(String bank_info) {
        this.bank_info = bank_info;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setSalaryCondition(String salary_condition) {
        this.salary_condition = salary_condition;
    }

    public void setStartJobDate(LocalDate start_job_date) {
        this.start_job_date = start_job_date;
    }

    public void setShiftManager(boolean is_shift_manager) {
        this.is_shift_manager = is_shift_manager;
    }
}