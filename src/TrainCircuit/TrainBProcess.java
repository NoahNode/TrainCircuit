package TrainCircuit;
class TrainBProcess extends Thread {
    // Note This process is used to emulate a train as it proceeds around the track
    String trainName;
    TrainTrack theTrack;
    //initialise (constructor)
    public TrainBProcess(String trainName, TrainTrack theTrack) {
        this.trainName = trainName;
        this.theTrack = theTrack;
    }

    @Override
    public void run() {   // start train Process
        // wait for clearance before moving on to the track
        theTrack.trainB_MoveOnToTrack(trainName); // move on to track B
        int circuitCount = 0;
        while (circuitCount < 1) { // keep cycling the B track loop
            theTrack.trainB_MoveAroundToSharedTrack_First(); // move around B loop
            theTrack.trainB_MoveAlongSharedTrack_First(); // move along shared track
            theTrack.trainB_MoveAroundToSharedTrack_Second(); // move around A loop
            theTrack.trainB_MoveAlongSharedTrack_Second(); // move along shared track
            theTrack.trainB_MoveBackToBeginning(); //move train back to beginning
            circuitCount++;
        }
        theTrack.trainB_MoveOffTrack(trainName); // move off the track
    } // end run
} // end trainBProcess
