package TrainCircuit;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class Activity {

    private final CopyOnWriteArrayList<String> theActivities;

    private final String[] trainTrack;

    // Constructor for objects of class Activity
    // A reference to the track is passed as a parameter
    public Activity(String[] trainTrack) {
        theActivities = new CopyOnWriteArrayList<>();
        this.trainTrack = trainTrack;
    }

    public void addMovedTo(int section) {
        // add an activity message to the activity history
        String tempString1 = "Train " + trainTrack[section] + " moving/moved to [" + section + "]";
        theActivities.add(tempString1);
        // add the current state of the track to the activity history
        theActivities.add(trackString());
    }// end addMovedTo

    public void addMessage(String message) {
        // add an activity message to the activity history
        String tempString1 = message;
        theActivities.add(tempString1);
    }// end addMessage

    public void printActivities() {
        // print all the train activity history
        System.out.println("TRAIN TRACK ACTIVITY(Tracks [0..16])");
        Iterator<String> iterator = theActivities.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }// end printActivities

    // Utility method to represent the track as a string for printing/display
    public String trackString() {
        String trackStateAsString = trainTrack[2] + trainTrack[3] + trainTrack[4] + "\n"
                + trainTrack[1] + "    " + trainTrack[5] + "\n"
                + trainTrack[0] + "    " + trainTrack[6] + "\n"
                + trainTrack[9] + trainTrack[8] + trainTrack[7] + "\n"
                + trainTrack[10] + "    " + trainTrack[16] + "\n"
                + trainTrack[11] + "    " + trainTrack[15] + "\n"
                + trainTrack[12] + trainTrack[13] + trainTrack[14] + "\n";
        return (trackStateAsString);
    }// end trackString

}// end Activity




