public final class TicketSequence {
    private static long next = 1000;

    private TicketSequence() { }

    public static synchronized long nextId() {
        return next++;
    }

    public static void main(String[] args) {
        System.out.println(TicketSequence.nextId());
        System.out.println(TicketSequence.nextId());
        System.out.println(TicketSequence.nextId());
    }
}
