abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank())
            throw new IllegalArgumentException("Registration number required");
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() { return regNo; }
    public abstract double ratePerHour();
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) { super(regNo); }
    @Override public double ratePerHour() { return 10.0; }
}

class Car extends Vehicle {
    Car(String regNo) { super(regNo); }
    @Override public double ratePerHour() { return 30.0; }
}

class ElectricCar extends Car {
    ElectricCar(String regNo) { super(regNo); }
}

public class VehicleHierarchy {
    public static void main(String[] args) {
        Vehicle bike = new TwoWheeler("TN01A1");
        Vehicle car = new Car("TN02B2");
        Vehicle ev = new ElectricCar("TN03C3");
        System.out.println(bike.getRegNo() + " rate = Rs " + bike.ratePerHour());
        System.out.println(car.getRegNo() + " rate = Rs " + car.ratePerHour());
        System.out.println(ev.getRegNo() + " rate = Rs " + ev.ratePerHour());
    }
}
