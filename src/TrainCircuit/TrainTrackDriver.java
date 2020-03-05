package TrainCircuit;
class TrainTrackDriver {

    static final int NUM_OF_A_TRAINS = 20;
    static final int NUM_OF_B_TRAINS = 20;
    static TrainTrack theTrainTrack;

    public static void main(String[] args) {

        // create a train track
        theTrainTrack = new TrainTrack();
        System.out.println("STARTED");

        // create arrays to hold the trains
        TrainAProcess[] trainAProcess = new TrainAProcess[NUM_OF_A_TRAINS];
        TrainBProcess[] trainBProcess = new TrainBProcess[NUM_OF_B_TRAINS];

        // create trains to enter the track
        for (int i = 0; i < NUM_OF_A_TRAINS; i++) {
            CDS.idleQuietly((int) (Math.random() * 100));
            trainAProcess[i] = new TrainAProcess("A" + i, theTrainTrack);
        }
        for (int i = 0; i < NUM_OF_B_TRAINS; i++) {
            CDS.idleQuietly((int) (Math.random() * 100));
            trainBProcess[i] = new TrainBProcess("B" + i, theTrainTrack);
        }

        // set the train processes running
        for (int i = 0; i < NUM_OF_A_TRAINS; i++) {
            trainAProcess[i].start();
        } // end for
        for (int i = 0; i < NUM_OF_B_TRAINS; i++) {
            trainBProcess[i].start();
        } // end for

        // trains now travelling
        //  wait for all the train threads to finish before printing out final message.
        for (int i = 0; i < NUM_OF_A_TRAINS; i++) {
            try {
                trainAProcess[i].join();
            } catch (InterruptedException ex) {
            }
        } // end for

        for (int i = 0; i < NUM_OF_B_TRAINS; i++) {
            try {
                trainBProcess[i].join();
            } catch (InterruptedException ex) {
            }
        } // end for

        // Display all the train activity that took place
        theTrainTrack.theTrainActivity.printActivities();

        // Final message
        System.out.println("All trains have successfully travelled the loop of the track.");
    } // end main
}
