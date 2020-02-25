        package TrainCircuit;

        import java.util.concurrent.atomic.*;

        public class TrainTrack {

            private final String[] slots = {"[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]",
                    "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]", "[..]",};

            // declare array to hold the Binary Semaphores for access to track slots (sections)
            private final MageeSemaphore slotSem[] = new MageeSemaphore[17];

            // reference to train activity record
            Activity theTrainActivity;

            // global count of trains on shared track
            AtomicInteger aUsingSharedTrack;
            AtomicInteger bUsingSharedTrack;

            // counting semaphore to limit number of trains on track
            MageeSemaphore aCountSem;
            MageeSemaphore bCountSem;

            // declare  Semaphores for mutually exclusive access to aUsingSharedTrack
            private final MageeSemaphore aMutexSem;
            // declare  Semaphores for mutually exclusive access to bUsingSharedTrack
            private final MageeSemaphore bMutexSem;

            // shared track lock
            MageeSemaphore sharedTrackLock;

            /* Constructor for TrainTrack */
            public TrainTrack() {
                // record the train activity
                theTrainActivity = new Activity(slots);
                // create the array of slotSems and set them all free (empty)
                for (int i = 0; i <= 16; i++) {
                    slotSem[i] = new MageeSemaphore(1);
                }
                // create  semaphores for mutually exclusive access to global count
                aMutexSem = new MageeSemaphore(1);
                bMutexSem = new MageeSemaphore(1);
                // create global AtomicInteger count variables
                aUsingSharedTrack = new AtomicInteger(0);
                bUsingSharedTrack = new AtomicInteger(0);
                // create  semaphores for limiting number of trains on track
                aCountSem = new MageeSemaphore(6);
                bCountSem = new MageeSemaphore(6);
                // initially shared track is accessible
                sharedTrackLock = new MageeSemaphore(1);
            }  // constructor

            public void trainA_MoveOnToTrack(String trainName) {
                CDS.idleQuietly((int) (Math.random() * 100));
                aCountSem.P(); // limit  number of trains on track to avoid deadlock
                // record the train activity
                slotSem[0].P();// wait for slot 0 to be free
                slots[0] = "[" + trainName + "]"; // move train type A on to slot zero
                theTrainActivity.addMovedTo(0); // record the train activity
            }// end trainA_movedOnToTrack

            public void trainB_MoveOnToTrack(String trainName) {
                // record the train activity
                bCountSem.P();  // limit  number of trains on track to avoid deadlock
                CDS.idleQuietly((int) (Math.random() * 100));
                slotSem[16].P();// wait for slot 16 to be free
                slots[16] = "[" + trainName + "]"; // move train type B on to slot sixteen
                theTrainActivity.addMovedTo(16); // record the train activity
            }// end trainB_movedOnToTrack

            public void trainA_MoveAroundToSharedTrack(String trainName) {
                CDS.idleQuietly((int) (Math.random() * 100));
                int currentPosition = 0;
                do {
                    // wait until the position ahead is empty and then move into it
                    slotSem[currentPosition + 1].P(); // wait for the slot ahead to be free
                    slots[currentPosition + 1] = slots[currentPosition]; // move train forward one position
                    slots[currentPosition] = "[..]"; // clear the slot the train vacated
                    theTrainActivity.addMovedTo(currentPosition + 1); // record the train activity
                    slotSem[currentPosition].V(); // signal slot you are leaving
                    currentPosition++;
                } while (currentPosition < 6);
                CDS.idleQuietly((int) (Math.random() * 100));
            } // end trainA_MoveAroundToSharedTrack

            public void trainB_MoveAroundToSharedTrack(String trainName) {
                CDS.idleQuietly((int) (Math.random() * 100));
                int currentPosition = 16;
                do {
                    /* wait until the position ahead is empty and then move into it*/
                    slotSem[currentPosition - 1].P(); // wait for the slot ahead to be free
                    slots[currentPosition - 1] = slots[currentPosition]; // move train forward
                    slots[currentPosition] = "[..]"; //clear the slot the train vacated
                    theTrainActivity.addMovedTo(currentPosition - 1); //record the train activity
                    slotSem[currentPosition].V(); //signal slot you are leaving
                    currentPosition--;
                } while (currentPosition > 10);
                CDS.idleQuietly((int) (Math.random() * 100));
            } // end trainB_MoveAroundToSharedTrack

            public void trainA_MoveAlongSharedTrack(String trainName) {
                // wait for the necessary conditions to get access to shared track
                aMutexSem.P(); // obtain mutually exclusive access to global variable aUsingSharedTrack
                if (aUsingSharedTrack.incrementAndGet() == 1)// if first A train joining shared track
                {
                    sharedTrackLock.P();  // grab lock to shared track
                }
                aMutexSem.V(); // release mutually exclusive access to global variable aUsingSharedTrack
                // move on to shared track
                slotSem[7].P();
                slots[7] = slots[6];
                slots[6] = "[..]";
                slotSem[6].V(); //move from slot[6] to slot[7]
                theTrainActivity.addMovedTo(7);  //record the train activity
                CDS.idleQuietly((int) (Math.random() * 10));
                // move along shared track
                slotSem[8].P();
                slots[8] = slots[7];
                slots[7] = "[..]";
                slotSem[7].V(); //move from slot[7] to slot[8]
                theTrainActivity.addMovedTo(8); // record the train activity
                slotSem[9].P();
                slots[9] = slots[8];
                slots[8] = "[..]";
                slotSem[8].V(); //move from slot[8] to slot[9]
                theTrainActivity.addMovedTo(9); // record the train activity
                CDS.idleQuietly((int) (Math.random() * 10));
                // move off shared track
                slotSem[0].P();
                slots[0] = slots[9];
                slots[9] = "[..]";
                slotSem[9].V(); //move from slot[9] to slot[0]
                theTrainActivity.addMovedTo(0); // record the train activity
                // signal conditions when leaving shared track
                aMutexSem.P(); // obtain mutually exclusive access to global variable aUsingSharedTracK
                if (aUsingSharedTrack.decrementAndGet() == 0) // if last A train leaving shared track
                {
                    sharedTrackLock.V(); // release lock to shared track
                }
                aMutexSem.V(); // release mutually exclusive access to global variable aUsingSharedTrack
                CDS.idleQuietly((int) (Math.random() * 10));
            }// end   trainA_MoveAlongSharedTrack

            public void trainB_MoveAlongSharedTrack(String trainName) {
                CDS.idleQuietly((int) (Math.random() * 10));
                // wait for the necessary conditions to get access to shared track
                bMutexSem.P(); // obtain mutually exclusive access to global variable bUsingSharedTrack
                if (bUsingSharedTrack.incrementAndGet() == 1)// if first B train joining shared track
                {
                    sharedTrackLock.P();  // grab lock to shared track
                }
                bMutexSem.V(); // release mutually exclusive access to global variable bUsingSharedTrack
                CDS.idleQuietly((int) (Math.random() * 10));
                // move on to shared track
                slotSem[9].P();
                slots[9] = slots[10];
                slots[10] = "[..]";
                slotSem[10].V(); //move from slot[10] to slot[9]
                theTrainActivity.addMovedTo(9);  //record the train activity
                CDS.idleQuietly((int) (Math.random() * 10));
                // move along shared track
                slotSem[8].P();
                slots[8] = slots[9];
                slots[9] = "[..]";
                slotSem[9].V(); //move from slot[9] to slot[8]
                theTrainActivity.addMovedTo(8); // record the train activity
                slotSem[7].P();
                slots[7] = slots[8];
                slots[8] = "[..]";
                slotSem[8].V(); //move from slot[8] to slot[7]
                theTrainActivity.addMovedTo(7); // record the train activity
                CDS.idleQuietly((int) (Math.random() * 10));
                // move off shared track
                slotSem[16].P();
                slots[16] = slots[7];
                slots[7] = "[..]";
                slotSem[7].V(); //move from slot[7] to slot[16]
                theTrainActivity.addMovedTo(16); // record the train activity
                // signal conditions when leaving shared track
                bMutexSem.P(); // obtain mutually exclusive access to global variable aUsingSharedTracK
                if (bUsingSharedTrack.decrementAndGet() == 0) // if last B train leaving shared track
                {
                    sharedTrackLock.V(); // release lock to shared track
                }
                bMutexSem.V(); // release mutually exclusive access to global variable aUsingSharedTrack
                CDS.idleQuietly((int) (Math.random() * 10));
            }// end   trainB_MoveAlongSharedTrack

            public void trainA_MoveOffTrack(String trainName) {
                CDS.idleQuietly((int) (Math.random() * 10));
                // record the train activity
                theTrainActivity.addMessage("Train " + trainName + " is leaving the A loop at section 0");
                slots[0] = "[..]"; // move train type A off slot zero
                slotSem[0].V();// signal slot 0 to be free
                CDS.idleQuietly((int) (Math.random() * 10));
                aCountSem.V(); // signal space for another A train
            }// end trainA_movedOffTrack

            public void trainB_MoveOffTrack(String trainName) {
                CDS.idleQuietly((int) (Math.random() * 10));
                // record the train activity
                theTrainActivity.addMessage("Train " + trainName + " is leaving the B loop at section 16");
                slots[16] = "[..]"; // move train type A off slot zero
                slotSem[16].V();// signal slot 0 to be free
                CDS.idleQuietly((int) (Math.random() * 10));
                bCountSem.V(); // signal space for another B train
            }// end trainB_movedOffTrack

        } // end Train track

