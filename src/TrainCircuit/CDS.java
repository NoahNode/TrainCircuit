package TrainCircuit;
public class CDS {

    public static void idle(int millisecs) { // with messages
        Thread mainThread = Thread.currentThread();
        System.out.println(mainThread.getName() + ": About to sleep");
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
        System.out.println(mainThread.getName() + ": Woken up");
    } // end idle

    public static void idleQuietly(int millisecs) { // idle with no messages
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
    } // end idleQuietly

} // end CDS