public class ParkingFacility {
    private final String[][] slots;

    public ParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor == null) throw new IllegalArgumentException("Capacity is required");
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] < 0) throw new IllegalArgumentException("Negative capacity");
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    // Returns {floor, slot}; null means the facility is full.
    public int[] allocate(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank())
            throw new IllegalArgumentException("Vehicle number required");
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicleNo.trim().toUpperCase();
                    return new int[]{f, s};
                }
            }
        }
        return null;
    }

    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        return true;
    }

    public static void main(String[] args) {
        ParkingFacility p = new ParkingFacility(new int[]{2, 1});
        System.out.println(java.util.Arrays.toString(p.allocate("TN01A1")));
        System.out.println(java.util.Arrays.toString(p.allocate("TN02B2")));
        System.out.println(java.util.Arrays.toString(p.allocate("TN03C3")));
        System.out.println(p.allocate("TN04D4"));
        System.out.println(p.release(0, 1) + " " + p.release(0, 1) + " " + p.release(5, 0));
        System.out.println(java.util.Arrays.toString(p.allocate("TN04D4")));
    }
}
