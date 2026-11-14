interface Chargeable {
    double chargingFee(int kWh);
    default boolean supportsFastCharging() { return false; }
    static boolean validEnergy(int kWh) { return kWh >= 0; }
}
interface Reservable {
    boolean reserveSlot();
}
abstract class Vehicle {
    private final String regNo;
    protected Vehicle(String regNo) { this.regNo = regNo; }
    public String getRegNo() { return regNo; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
}
class ElectricCar extends Car implements Chargeable, Reservable {
    ElectricCar(String r) { super(r); }
    public double chargingFee(int kWh) {
        if (!Chargeable.validEnergy(kWh)) throw new IllegalArgumentException("Invalid energy");
        return kWh * 12.0;
    }
    public boolean reserveSlot() { return true; }
    @Override public boolean supportsFastCharging() { return true; }
}
public class AbstractionDemo {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03C3");
        System.out.println("Charging fee: Rs " + ev.chargingFee(10));
        System.out.println("Fast charging: " + ev.supportsFastCharging());
        System.out.println("Reservation: " + ev.reserveSlot());
    }
}
