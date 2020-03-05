package TrainCircuit;

import java.util.concurrent.atomic.*;

public class TrainTrack {
    //19 slots
    private final String[] slots = {"[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]",
            "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]",};

    // declare array to hold the Binary Semaphores for access to track slots (sections)
    private final MageeSemaphore slotSem[] = new MageeSemaphore[19];

    // reference to train activity record
    Activity theTrainActivity;

    // global count of trains on shared track
    AtomicInteger aUsingSharedTrack;
    AtomicInteger bUsingSharedTrack;

    // counting semaphore to limit number of trains on track
    MageeSemaphore CountSem;


    // shared track lock
    MageeSemaphore sharedTrackLock;

    /* Constructor for TrainTrack */
    public TrainTrack() {
        // record the train activity
        theTrainActivity = new Activity(slots);
        // create the array of slotSems and set them all free (empty)
        for (int i = 0; i <= 18; i++) {
            slotSem[i] = new MageeSemaphore(1);
        }

        // create global AtomicInteger count variables
        aUsingSharedTrack = new AtomicInteger(0);
        bUsingSharedTrack = new AtomicInteger(0);
        // create  semaphores for limiting number of trains on track
        CountSem = new MageeSemaphore(8);
        // initially shared track is accessible
        sharedTrackLock = new MageeSemaphore(1);
    }  // constructor

    public void trainA_MoveOnToTrack(String trainName) {
        CDS.idleQuietly((int) (Math.random() * 100));
        CountSem.P(); // limit  number of trains on track to avoid deadlock
        // record the train activity
        slotSem[4].P();
        slotSem[5].P();// wait for slot 5 to be free
        slots[5] = "[" + trainName + "]"; // move train type A on to slot 5
        theTrainActivity.addMovedTo(5); // record the train activity
        slotSem[4].V();
    }// end trainA_movedOnToTrack


    public void trainA_MoveAroundToSharedTrack_First() { //Train A move from slot 5 to slot 8
        CDS.idleQuietly((int) (Math.random() * 100));
        int currentPosition = 5;
        do {
            // wait until the position ahead is empty and then move into it
            slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
            slots[currentPosition + 1] = slots[currentPosition]; // move train forward one position
            slots[currentPosition] = "[..]"; // clear the slot the train vacated
            theTrainActivity.addMovedTo(currentPosition + 1); // record the train activity
            slotSem[currentPosition].V(); // signal slot you are leaving
            currentPosition++;
        } while (currentPosition < 8);
        CDS.idleQuietly((int) (Math.random() * 100));
    } // end trainA_MoveAroundToSharedTrack

    public void trainA_MoveAroundToSharedTrack_Second() { //Train A move from slot 10 to slot 13
        CDS.idleQuietly((int) (Math.random() * 100));
        int currentPosition = 11;
        do {
            // wait until the position ahead is empty and then move into it
            slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
            slots[currentPosition + 1] = slots[currentPosition]; // move train forward one position
            slots[currentPosition] = "[..]"; // clear the slot the train vacated
            theTrainActivity.addMovedTo(currentPosition + 1); // record the train activity
            slotSem[currentPosition].V(); // signal slot you are leaving
            currentPosition++;
        } while (currentPosition < 17);
        CDS.idleQuietly((int) (Math.random() * 100));
    } // end trainA_MoveAroundToSharedTrack



    public void trainA_MoveAlongSharedTrack_First() {  //Train A loop from slot 8
        // wait for the necessary conditions to get access to shared track
        if (aUsingSharedTrack.incrementAndGet() == 1)// if first A train joining shared track
        {
            sharedTrackLock.P();  // grab lock to shared track
        }
        // move on to shared track
        slotSem[9].P();
        slots[9] = slots[8];
        slots[8] = "[..]";
        slotSem[8].V(); //move from slot[8] to slot[9]
        theTrainActivity.addMovedTo(9);  //record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move along shared track
        slotSem[0].P();
        slots[0] = slots[9];
        slots[9] = "[..]";
        slotSem[9].V(); //move from slot[9] to slot[0]
        theTrainActivity.addMovedTo(0); // record the train activity
        slotSem[10].P();
        slots[10] = slots[0];
        slots[0] = "[..]";
        slotSem[0].V(); //move from slot[0] to slot[10]
        theTrainActivity.addMovedTo(10); // record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move off shared track
        slotSem[11].P();
        slots[11] = slots[10];
        slots[10] = "[..]";
        slotSem[10].V(); //move from slot[10] to slot[11]
        theTrainActivity.addMovedTo(11); // record the train activity
        // signal conditions when leaving shared track
        if (aUsingSharedTrack.decrementAndGet() == 0) // if last A train leaving shared track
        {
            sharedTrackLock.V(); // release lock to shared track
        }
        CDS.idleQuietly((int) (Math.random() * 10));
    }// end   trainA_MoveAlongSharedTrack

    public void trainA_MoveAlongSharedTrack_Second() { //Train A loop from slot 11
        // wait for the necessary conditions to get access to shared track
        if (aUsingSharedTrack.incrementAndGet() == 1)// if first A train joining shared track
        {
            sharedTrackLock.P();  // grab lock to shared track
        }
        // move on to shared track
        slotSem[18].P();
        slots[18] = slots[17];
        slots[17] = "[..]";
        slotSem[17].V(); //move from slot[17] to slot[18]
        theTrainActivity.addMovedTo(18);  //record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move along shared track
        slotSem[0].P();
        slots[0] = slots[18];
        slots[18] = "[..]";
        slotSem[18].V(); //move from slot[18] to slot[0]
        theTrainActivity.addMovedTo(0); // record the train activity
        slotSem[1].P();
        slots[1] = slots[0];
        slots[0] = "[..]";
        slotSem[0].V(); //move from slot[0] to slot[1]
        theTrainActivity.addMovedTo(1); // record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move off shared track
        slotSem[2].P();
        slots[2] = slots[1];
        slots[1] = "[..]";
        slotSem[1].V(); //move from slot[1] to slot[2]
        theTrainActivity.addMovedTo(2); // record the train activity
        // signal conditions when leaving shared track
        if (aUsingSharedTrack.decrementAndGet() == 0) // if last A train leaving shared track
        {
            sharedTrackLock.V(); // release lock to shared track
        }
        CDS.idleQuietly((int) (Math.random() * 10));
    }// end   trainA_MoveAlongSharedTrack

    public void trainA_MoveOffTrack(String trainName) {
        CDS.idleQuietly((int) (Math.random() * 10));
        // record the train activity
        theTrainActivity.addMessage("Train " + trainName + " is leaving the loop");
        slots[5] = "[..]"; // move train type A off slot 5
        slotSem[5].V();// signal slot 5 to be free
        CDS.idleQuietly((int) (Math.random() * 10));
        CountSem.V(); // signal space for another A train
    }// end trainA_movedOffTrack


    public void trainA_MoveBackToBeginning () {
        CDS.idleQuietly((int) (Math.random() * 100));
        int currentPosition = 2;
        do {
            /* wait until the position ahead is empty and then move into it*/
            slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
            slots[currentPosition + 1] = slots[currentPosition]; // move train forward
            slots[currentPosition] = "[..]"; //clear the slot the train vacated
            theTrainActivity.addMovedTo(currentPosition + 1); //record the train activity
            slotSem[currentPosition].V(); //signal slot you are leaving
            currentPosition++;
        } while (currentPosition < 5);
        CDS.idleQuietly((int) (Math.random() * 100));
    }
    public void trainB_MoveOnToTrack(String trainName) {
        // record the train activity
        CountSem.P();  // limit  number of trains on track to avoid deadlock
        CDS.idleQuietly((int) (Math.random() * 100));
        slotSem[13].P();
        slotSem[14].P();// wait for slot 14 to be free
        slots[14] = "[" + trainName + "]"; // move train type B on to slot 14
        theTrainActivity.addMovedTo(14); // record the train activity
        slotSem[13].V();
    }// end trainB_movedOnToTrack

    public void trainB_MoveAroundToSharedTrack_First() {
        CDS.idleQuietly((int) (Math.random() * 100));
        int currentPosition = 14;
        do {
            /* wait until the position ahead is empty and then move into it*/
            slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
            slots[currentPosition + 1] = slots[currentPosition]; // move train forward
            slots[currentPosition] = "[..]"; //clear the slot the train vacated
            theTrainActivity.addMovedTo(currentPosition + 1); //record the train activity
            slotSem[currentPosition].V(); //signal slot you are leaving
            currentPosition++;
        } while (currentPosition < 17);
        CDS.idleQuietly((int) (Math.random() * 100));
    } // end trainB_MoveAroundToSharedTrack

    public void trainB_MoveAroundToSharedTrack_Second() {
        CDS.idleQuietly((int) (Math.random() * 100));
        int currentPosition = 2;
        do {
            /* wait until the position ahead is empty and then move into it*/
            slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
            slots[currentPosition + 1] = slots[currentPosition]; // move train forward
            slots[currentPosition] = "[..]"; //clear the slot the train vacated
            theTrainActivity.addMovedTo(currentPosition + 1); //record the train activity
            slotSem[currentPosition].V(); //signal slot you are leaving
            currentPosition++;
        } while (currentPosition < 8);
        CDS.idleQuietly((int) (Math.random() * 100));
    } // end trainB_MoveAroundToSharedTrack

    public void trainB_MoveAlongSharedTrack_First() {
        CDS.idleQuietly((int) (Math.random() * 10));
        // wait for the necessary conditions to get access to shared track
        if (bUsingSharedTrack.incrementAndGet() == 1)// if first B train joining shared track
        {
            sharedTrackLock.P();  // grab lock to shared track
        }
        CDS.idleQuietly((int) (Math.random() * 10));
        // move on to shared track
        slotSem[18].P();
        slots[18] = slots[17];
        slots[17] = "[..]";
        slotSem[17].V(); //move from slot[17] to slot[18]
        theTrainActivity.addMovedTo(18);  //record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move along shared track
        slotSem[0].P();
        slots[0] = slots[18];
        slots[18] = "[..]";
        slotSem[18].V(); //move from slot[18] to slot[0]
        theTrainActivity.addMovedTo(0); // record the train activity
        slotSem[1].P();
        slots[1] = slots[0];
        slots[0] = "[..]";
        slotSem[0].V(); //move from slot[0] to slot[1]
        theTrainActivity.addMovedTo(1); // record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move off shared track
        slotSem[2].P();
        slots[2] = slots[1];
        slots[1] = "[..]";
        slotSem[1].V(); //move from slot[1] to slot[2]
        theTrainActivity.addMovedTo(2); // record the train activity
        // signal conditions when leaving shared track
        if (bUsingSharedTrack.decrementAndGet() == 0) // if last B train leaving shared track
        {
            sharedTrackLock.V(); // release lock to shared track
        }
        CDS.idleQuietly((int) (Math.random() * 10));
    }// end   trainB_MoveAlongSharedTrack

    public void trainB_MoveAlongSharedTrack_Second() {
        CDS.idleQuietly((int) (Math.random() * 10));
        // wait for the necessary conditions to get access to shared track
        if (bUsingSharedTrack.incrementAndGet() == 1)// if first B train joining shared track
        {
            sharedTrackLock.P();  // grab lock to shared track
        }
        CDS.idleQuietly((int) (Math.random() * 10));
        // move on to shared track
        slotSem[9].P();
        slots[9] = slots[8];
        slots[8] = "[..]";
        slotSem[8].V(); //move from slot[8] to slot[9]
        theTrainActivity.addMovedTo(9);  //record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move along shared track
        slotSem[0].P();
        slots[0] = slots[9];
        slots[9] = "[..]";
        slotSem[9].V(); //move from slot[9] to slot[0]
        theTrainActivity.addMovedTo(0); // record the train activity
        slotSem[10].P();
        slots[10] = slots[0];
        slots[0] = "[..]";
        slotSem[0].V(); //move from slot[9] to slot[10]
        theTrainActivity.addMovedTo(10); // record the train activity
        CDS.idleQuietly((int) (Math.random() * 10));
        // move off shared track
        slotSem[11].P();
        slots[11] = slots[10];
        slots[10] = "[..]";
        slotSem[10].V(); //move from slot[10] to slot[11]
        theTrainActivity.addMovedTo(11); // record the train activity
        // signal conditions when leaving shared track
        if (bUsingSharedTrack.decrementAndGet() == 0) // if last B train leaving shared track
        {
            sharedTrackLock.V(); // release lock to shared track
        }
        CDS.idleQuietly((int) (Math.random() * 10));
    }// end   trainB_MoveAlongSharedTrack1

    public void trainB_MoveOffTrack(String trainName) {
        CDS.idleQuietly((int) (Math.random() * 10));
        // record the train activity
        theTrainActivity.addMessage("Train " + trainName + " is leaving the B loop");
        slots[14] = "[..]"; // move train type A off slot zero
        slotSem[14].V();// signal slot 14 to be free
        CDS.idleQuietly((int) (Math.random() * 10));
        CountSem.V(); // signal space for another B train
    }// end trainB_movedOffTrack


    public void trainB_MoveBackToBeginning () {
        CDS.idleQuietly((int) (Math.random() * 100));
        int currentPosition = 11;
        do {
            /* wait until the position ahead is empty and then move into it*/
            slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
            slots[currentPosition + 1] = slots[currentPosition]; // move train forward
            slots[currentPosition] = "[..]"; //clear the slot the train vacated
            theTrainActivity.addMovedTo(currentPosition + 1); //record the train activity
            slotSem[currentPosition].V(); //signal slot you are leaving
            currentPosition++;
        } while (currentPosition < 14);
        CDS.idleQuietly((int) (Math.random() * 100));
    }
} // end Train track
