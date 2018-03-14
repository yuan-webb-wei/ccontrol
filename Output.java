package sim.app.ccontrol;

import sim.engine.*;

import java.util.*;
import java.io.*;

/* 131014AW
   This class sets up the output files for a run and calls the print
   functions that are only called once per run.
   Print functions that are called every timestep are in the OutputTimestep
   class.
*/

public class Output {
    Ccontrol ccontrol;
    Formatter runNumOp;         // run number
    Formatter targetOp;         // target position, velocity
    Formatter trackerOp;        // tracker position, velocity
    Formatter distanceOp;       // distance: per time , average, cumulative
    Formatter popTrackerOp;     // population based tracker position, velocity
    Formatter popDistanceOp;    // distance: per time , average, cumulative

    public Output(Ccontrol b) {
        this.ccontrol = b;

        makeRunDirectory();
        openFiles();
    }  /* Output */

    // Write to file at the start of a run
    // such as runNum
    public void writeStartOfRun() {
        runNumOp.format("%d\n", ccontrol.runNum);
    }  /* writeStartOfRun */

    // Write to file at the end of a run
    // such at statistical data like averages and sums
    public void writeEndOfRun() {
    }  /* writeEndOfRun */

    public void makeRunDirectory() {
        String command;

        //command = new String("mkdir " + ccontrol.outputDirectory +
        //                       "/run." + ccontrol.runNum);

        command = new String("mkdir " + ccontrol.outputDirectory + "\\run." + ccontrol.runNum);
        System.out.println(command);

        try {
            //@mylist - call command line

            //unix
            //Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});

            //windows
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});

            if (p.waitFor() == 0)
                System.out.printf(" --Directory created: %s\n", command);
        } catch (Exception exception) {
            System.err.printf(" --Error(Output): Unable to execute: %s\n",
                    command);
            System.exit(1);
        }
    }  /* makeRunDirectory */

    public void openFiles() {
        String filename;

        // run.*.runnum
        filename = new String(ccontrol.outputDirectory +
                "/run." + ccontrol.runNum +
                "/run." + ccontrol.runNum + ".runnum");
        try {
            runNumOp = new Formatter(filename);
        }  /* try */ catch (FileNotFoundException fileNotFoundException) {
            System.err.printf(" --Error(Output.openFiles):");
            System.err.printf(" Error creating file: %s\n", filename);
            System.exit(1);
        }  /* catch */
        System.out.printf(" --Open file: %s\n", filename);

        // run.*.target
        filename = new String(ccontrol.outputDirectory +
                "/run." + ccontrol.runNum +
                "/run." + ccontrol.runNum + ".target");
        try {
            targetOp = new Formatter(filename);
        }  /* try */ catch (FileNotFoundException fileNotFoundException) {
            System.err.printf(" --Error(Output.openFiles):");
            System.err.printf(" Error creating file: %s\n", filename);
            System.exit(1);
        }  /* catch */
        System.out.printf(" --Open file: %s\n", filename);

        // run.*.tracker
        filename = new String(ccontrol.outputDirectory +
                "/run." + ccontrol.runNum +
                "/run." + ccontrol.runNum + ".tracker");
        try {
            trackerOp = new Formatter(filename);
        }  /* try */ catch (FileNotFoundException fileNotFoundException) {
            System.err.printf(" --Error(Output.openFiles):");
            System.err.printf(" Error creating file: %s\n", filename);
            System.exit(1);
        }  /* catch */
        System.out.printf(" --Open file: %s\n", filename);

        // run.*.distance
        filename = new String(ccontrol.outputDirectory +
                "/run." + ccontrol.runNum +
                "/run." + ccontrol.runNum + ".distance");
        try {
            distanceOp = new Formatter(filename);
        }  /* try */ catch (FileNotFoundException fileNotFoundException) {
            System.err.printf(" --Error(Output.openFiles):");
            System.err.printf(" Error creating file: %s\n", filename);
            System.exit(1);
        }  /* catch */
        System.out.printf(" --Open file: %s\n", filename);

        // run.*.popTracker
        filename = new String(ccontrol.outputDirectory +
                "/run." + ccontrol.runNum +
                "/run." + ccontrol.runNum + ".popTracker");
        try {
            popTrackerOp = new Formatter(filename);
        }  /* try */ catch (FileNotFoundException fileNotFoundException) {
            System.err.printf(" --Error(Output.openFiles):");
            System.err.printf(" Error creating file: %s\n", filename);
            System.exit(1);
        }  /* catch */
        System.out.printf(" --Open file: %s\n", filename);

        // run.*.popDistance
        filename = new String(ccontrol.outputDirectory +
                "/run." + ccontrol.runNum +
                "/run." + ccontrol.runNum + ".popDistance");
        try {
            popDistanceOp = new Formatter(filename);
        }  /* try */ catch (FileNotFoundException fileNotFoundException) {
            System.err.printf(" --Error(Output.openFiles):");
            System.err.printf(" Error creating file: %s\n", filename);
            System.exit(1);
        }  /* catch */
        System.out.printf(" --Open file: %s\n", filename);

    }  /* open files */

    public void closeFiles() {
        runNumOp.close();
        targetOp.close();
        trackerOp.close();
        distanceOp.close();
        popTrackerOp.close();
        popDistanceOp.close();
    }  /* close files */

}  /* class Output */
