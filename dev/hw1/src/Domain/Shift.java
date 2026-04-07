package Domain;
import java.time.LocalDate; 


public class Shift {
    private LocalDate date;
    private boolean is_morning;
    public Shift(LocalDate date, boolean is_morning){
        this.date=date;
        this.is_morning=is_morning;
    }
    
    public LocalDate getDate(){return date;}
    public boolean getIsMorning(){return is_morning;}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other == null || getClass() != other.getClass()) return false;

        Shift o = (Shift) other;

        return o.getDate().equals(this.date)
                && o.is_morning == this.is_morning;
    }

    
    public String toString(){
        return "-shift date: "+ date+"\n -is this morning shift? "+ is_morning;
    }
}
