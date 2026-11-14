import java.time.*;
import java.util.*;

interface Chargeable { double chargingFee(int kWh); }

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
class ParkingTicket {
    private static long sequence = 1000;
    private final long id;
    private final Vehicle vehicle;
    private final int floor, slot;
    private final LocalDateTime entry;
    private LocalDateTime exit;
    private double fee;

    ParkingTicket(Vehicle v, int f, int s, LocalDateTime in) {
        id = sequence++; vehicle = v; floor = f; slot = s; entry = in;
    }
    long getId() { return id; }
    Vehicle getVehicle() { return vehicle; }
    int getFloor() { return floor; }
    int getSlot() { return slot; }
    LocalDateTime getEntry() { return entry; }
    void close(LocalDateTime out, double amount) {
        if (exit != null || out == null || out.isBefore(entry))
            throw new IllegalStateException("Invalid close");
        exit = out; fee = amount;
    }
    double getFee() { return fee; }
}
class ParkingFacility {
    private final Vehicle[][] occupied;
    ParkingFacility(int[] capacity) {
        if (capacity == null) throw new IllegalArgumentException("Capacity required");
        occupied = new Vehicle[capacity.length][];
        for (int f = 0; f < capacity.length; f++) {
            if (capacity[f] < 0) throw new IllegalArgumentException("Negative capacity");
            occupied[f] = new Vehicle[capacity[f]];
        }
    }
    int[] allocate(Vehicle v) {
        for (int f = 0; f < occupied.length; f++)
            for (int s = 0; s < occupied[f].length; s++)
                if (occupied[f][s] == null) {
                    occupied[f][s] = v;
                    return new int[]{f, s};
                }
        return null;
    }
    void release(int f, int s) {
        if (f < 0 || f >= occupied.length || s < 0 ||
            s >= occupied[f].length || occupied[f][s] == null)
            throw new IllegalArgumentException("Invalid slot");
        occupied[f][s] = null;
    }
}
class ParkingService {
    private final ParkingFacility facility;
    private final Map<String, ParkingTicket> active = new HashMap<>();
    ParkingService(ParkingFacility f) { facility = f; }

    ParkingTicket enter(Vehicle v, LocalDateTime now) {
        if (v == null || now == null) throw new IllegalArgumentException("Vehicle/time required");
        if (active.containsKey(v.getRegNo()))
            throw new IllegalStateException("Duplicate active registration");
        int[] p = facility.allocate(v);
        if (p == null) throw new IllegalStateException("Parking full");
        ParkingTicket t = new ParkingTicket(v, p[0], p[1], now);
        active.put(v.getRegNo(), t);
        return t;
    }

    double exit(String reg, LocalDateTime now, int kWh) {
        if (reg == null || now == null) throw new IllegalArgumentException("Registration/time required");
        String key = reg.trim().toUpperCase();
        ParkingTicket t = active.get(key);
        if (t == null) throw new IllegalArgumentException("No active ticket");
        long mins = Duration.between(t.getEntry(), now).toMinutes();
        double fee = t.getVehicle().calculateFee(mins);
        if (t.getVehicle() instanceof Chargeable c) fee += c.chargingFee(kWh);
        t.close(now, fee);
        facility.release(t.getFloor(), t.getSlot());
        active.remove(key);
        return fee;
    }
}
public class SmartParkingDemo {
    public static void main(String[] args) {
        ParkingService service = new ParkingService(new ParkingFacility(new int[]{2, 1}));
        LocalDateTime start = LocalDateTime.of(2026, 11, 14, 9, 0);

        ParkingTicket a = service.enter(new TwoWheeler("TN01A1"), start);
        ParkingTicket b = service.enter(new Car("TN02B2"), start);
        ParkingTicket c = service.enter(new ElectricCar("TN03C3"), start);

        System.out.println("Tickets: " + a.getId() + ", " + b.getId() + ", " + c.getId());
        System.out.println("Two-wheeler fee: Rs " + service.exit("TN01A1", start.plusMinutes(30), 0));
        System.out.println("Car fee: Rs " + service.exit("TN02B2", start.plusMinutes(130), 0));
        System.out.println("EV total: Rs " + service.exit("TN03C3", start.plusMinutes(130), 10));
    }
}
