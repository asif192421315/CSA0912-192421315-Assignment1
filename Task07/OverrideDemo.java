abstract class Vehicle {
    private final String regNo;
    Vehicle(String regNo) { this.regNo = regNo; }
    public String getRegNo() { return regNo; }
    public abstract double ratePerHour();
    public double calculateFee(long minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Minutes cannot be negative");
        long hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * ratePerHour();
    }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) { super(r); }
    @Override public double ratePerHour() { return 10.0; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    @Override public double ratePerHour() { return 30.0; }
}
public class OverrideDemo {
    public static void main(String[] args) {
        Vehicle[] vehicles = { new TwoWheeler("TN01A1"), new Car("TN02B2") };
        for (Vehicle v : vehicles)
            System.out.println(v.getRegNo() + " for 61 minutes = Rs " + v.calculateFee(61));
    }
}
