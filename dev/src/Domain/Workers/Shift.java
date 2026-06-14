package Domain.Workers;
import java.time.LocalDate;
import Domain.Transportation.Location;


public class Shift {
    private LocalDate date;
    private boolean is_morning;
    private Location location;
    public Shift(LocalDate date, boolean is_morning, Location location){
        this.date=date;
        this.is_morning=is_morning;
        this.location=location;
    }
    
    public LocalDate getDate(){return date;}
    public boolean getIsMorning(){return is_morning;}
    public Location geLocation(){return location;}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other == null || getClass() != other.getClass()) return false;

        Shift o = (Shift) other;

        return o.getDate().equals(this.date)
                && o.is_morning == this.is_morning && (o.geLocation()).equals(this.location);
    }

    
    public String toString(){
        return "-shift date: "+ date+"\n -is this morning shift? "+ is_morning +"\n -shift location: "+location;
    }
}
