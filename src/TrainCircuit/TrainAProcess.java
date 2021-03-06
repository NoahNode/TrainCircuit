package TrainCircuit;
/* Train type A Process class*/
class TrainAProcess extends Thread {
    // Note This process is used to emulate a train as it proceeds around the track

    String trainName;
    TrainTrack theTrack;

    //initialise (constructor)
    public TrainAProcess(String trainName, TrainTrack theTrack) {
        this.trainName = trainName;
        this.theTrack = theTrack;
    }

    @Override
    public void run() {   // start train Process
        // wait for clearance before moving on to the track
        theTrack.trainA_MoveOnToTrack(trainName); // move on to track A
        int circuitCount = 0;
        while (circuitCount < 1) { // keep cycling the A track loop
            theTrack.trainA_MoveAroundToSharedTrack_First(); // move around A loop
            theTrack.trainA_MoveAlongSharedTrack_First(); // move along shared track
            theTrack.trainA_MoveAroundToSharedTrack_Second(); // move around B loop
            theTrack.trainA_MoveAlongSharedTrack_Second(); // move along shared track
            theTrack.trainA_MoveBackToBeginning(); //move train back to beginning
            circuitCount++;
        }
        theTrack.trainA_MoveOffTrack(trainName); // move off the track
    }
}

