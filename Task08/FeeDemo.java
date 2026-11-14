interface Chargeable {
    double chargingFee(int kWh);
}

abstract class Vehicle {
    private final String regNo;
    protected Vehicle(String r) {
        if (r == null || r.isBlank()) throw new IllegalArgumentException("Registration required");
        regNo = r.trim().toUpperCase();
    }
    public String getRegNo() { return regNo; }
    protected abstract double ratePerHour();
    public double calculateFee(long minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Negative duration");
        long hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return hours * ratePerHour();
    }
    @Override public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle)o).regNo);
    }
    @Override public int hashCode() { return regNo.hashCode(); }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) { super(r); }
    protected double ratePerHour() { return 10.0; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    protected double ratePerHour() { return 30.0; }
}
class ElectricCar extends Car implements Chargeable {
    ElectricCar(String r) { super(r); }
    public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("Negative energy");
        return kWh * 12.0;
    }
}
public class FeeDemo {
    public static void main(String[] args) {
        Vehicle bike = new TwoWheeler("TN01A1");
        Car car = new Car("TN02B2");
        ElectricCar ev = new ElectricCar("TN02B2");

        System.out.println("Two-wheeler fee (30 min): Rs " + bike.calculateFee(30));
        System.out.println("Car fee (130 min): Rs " + car.calculateFee(130));
        System.out.println("EV parking fee (130 min): Rs " + ev.calculateFee(130));
        System.out.println("EV charging fee (10 kWh): Rs " + ev.chargingFee(10));
        System.out.println("Car and EV same registration: " + car.equals(ev));
        System.out.println("EV total: Rs " + (ev.calculateFee(130) + ev.chargingFee(10)));
    }
}
