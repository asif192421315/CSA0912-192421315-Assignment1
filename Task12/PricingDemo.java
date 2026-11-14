import java.math.BigDecimal;
import java.time.*;

interface PricingRule {
    BigDecimal calculate(long minutes, LocalDateTime entry,
                         LocalDateTime exit, BigDecimal baseRate);
}
class HourlyPricingRule implements PricingRule {
    public BigDecimal calculate(long minutes, LocalDateTime in,
                                LocalDateTime out, BigDecimal rate) {
        if (minutes < 0) throw new IllegalArgumentException("Negative duration");
        long hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        return rate.multiply(BigDecimal.valueOf(hours));
    }
}
class FlatFeePricingRule implements PricingRule {
    private final BigDecimal amount;
    FlatFeePricingRule(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) throw new IllegalArgumentException("Invalid flat fee");
        this.amount = amount;
    }
    public BigDecimal calculate(long minutes, LocalDateTime in,
                                LocalDateTime out, BigDecimal rate) {
        return amount;
    }
}
class TimeWindowPricingRule implements PricingRule {
    private final BigDecimal multiplier;
    TimeWindowPricingRule(BigDecimal multiplier) {
        if (multiplier == null || multiplier.signum() < 0)
            throw new IllegalArgumentException("Invalid multiplier");
        this.multiplier = multiplier;
    }
    // Assignment example: double the entire visit if entry begins from 09:00 up to but not including 17:00.
    public BigDecimal calculate(long minutes, LocalDateTime in,
                                LocalDateTime out, BigDecimal rate) {
        if (minutes < 0 || in == null || out == null || out.isBefore(in))
            throw new IllegalArgumentException("Invalid time interval");
        long hours = minutes == 0 ? 0 : (minutes + 59) / 60;
        BigDecimal normal = rate.multiply(BigDecimal.valueOf(hours));
        boolean startsInWindow =
            !in.toLocalTime().isBefore(LocalTime.of(9, 0)) &&
             in.toLocalTime().isBefore(LocalTime.of(17, 0));
        return startsInWindow ? normal.multiply(multiplier) : normal;
    }
}
interface ParkingPass {
    boolean isValidFor(LocalDate date);
}
class OneDayPass implements ParkingPass {
    private final LocalDate validDate;
    OneDayPass(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date required");
        validDate = date;
    }
    public boolean isValidFor(LocalDate date) {
        return date != null && validDate.equals(date);
    }
}
public class PricingDemo {
    public static void main(String[] args) {
        LocalDateTime in = LocalDateTime.of(2026, 11, 14, 10, 0);
        LocalDateTime out = in.plusMinutes(60);
        BigDecimal carRate = new BigDecimal("30.00");

        PricingRule hourly = new HourlyPricingRule();
        PricingRule eBikeFlat = new FlatFeePricingRule(new BigDecimal("25.00"));
        PricingRule vanRule = new TimeWindowPricingRule(new BigDecimal("2.0"));

        System.out.println("Hourly car fee: Rs " + hourly.calculate(61, in, out, carRate));
        System.out.println("E-bike flat fee: Rs " + eBikeFlat.calculate(600, in, out, carRate));
        System.out.println("Delivery van fee: Rs " + vanRule.calculate(60, in, out, carRate));

        ParkingPass pass = new OneDayPass(LocalDate.of(2026, 11, 14));
        System.out.println("Pass valid today: " + pass.isValidFor(LocalDate.of(2026, 11, 14)));
        System.out.println("Pass valid tomorrow: " + pass.isValidFor(LocalDate.of(2026, 11, 15)));

        int[] slotsPerFloor = {40, 55, 35, 70, 48};
        VehicleSlotLayout layout = new VehicleSlotLayout(slotsPerFloor);
        System.out.println("Number of floors: " + layout.floorCount());
    }
}
class VehicleSlotLayout {
    private final Object[][] floors;
    VehicleSlotLayout(int[] slotsPerFloor) {
        floors = new Object[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] < 0) throw new IllegalArgumentException("Negative capacity");
            floors[f] = new Object[slotsPerFloor[f]];
        }
    }
    int floorCount() { return floors.length; }
}
