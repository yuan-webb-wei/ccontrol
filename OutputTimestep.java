package sim.app.ccontrol;

import sim.engine.*;
import sim.util.Double2D;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

/* 131015AW
   Print functions that are called every timestep.
*/

public class OutputTimestep {
    Ccontrol ccontrol;

    public OutputTimestep(Ccontrol b) {
        this.ccontrol = b;
    }  /* OutputTimestep */

//    public void writePopTrackerData(long timestep, Population tracker) {
//        ccontrol.opfile.popTrackerOp.format(
//                " %5d xy %f %f vel %f %f lastd %f %f\n",
//                timestep,
//                tracker.getLoc().x,
//                tracker.getLoc().y,
//                tracker.velocity.x,
//                tracker.velocity.y,
//                tracker.lastd.x,
//                tracker.lastd.y
//        );
//    }  /* writePopTrackerData */


//   public void writePopDistanceData(long timestep, Population tracker)
//      {
//      ccontrol.opfile.popDistanceOp.format(
//         " %5d dist %f %f disp %f %f av.dist %f %f avg.disp %f %f\n",
//         timestep,
//         tracker.currentDistanceToTarget.x,
//         tracker.currentDistanceToTarget.y,
//         tracker.currentDisplacement.x,
//         tracker.currentDisplacement.y,
//         tracker.sumDistanceX/timestep,
//         tracker.sumDistanceY/timestep,
//         tracker.sumDisplacementX/timestep,
//         tracker.sumDisplacementY/timestep
//         );
//      }  /* writePopDistanceData */


//   public void writeTrackerData(long timestep, Tracker tracker)
//      {
//      ccontrol.opfile.trackerOp.format(
//         " %5d xy %f %f vel %f %f lastd %f %f\n",
//         timestep,
//         tracker.getLoc().x,
//         tracker.getLoc().y,
//         tracker.velocity.x,
//         tracker.velocity.y,
//         tracker.lastd.x,
//         tracker.lastd.y
//         );
//      }  /* writeTrackerData */


//   public void writeDistanceData(long timestep, Tracker tracker)
//      {
//      ccontrol.opfile.distanceOp.format(
//         " %5d dist %f %f disp %f %f av.dist %f %f avg.disp %f %f\n",
//         timestep,
//         tracker.currentDistanceToTarget.x,
//         tracker.currentDistanceToTarget.y,
//         tracker.currentDisplacement.x,
//         tracker.currentDisplacement.y,
//         tracker.sumDistanceX/timestep,
//         tracker.sumDistanceY/timestep,
//         tracker.sumDisplacementX/timestep,
//         tracker.sumDisplacementY/timestep
//         );
//      }  /* writeDistanceData */


    public void writeTargetData(long timestep, Target target) {
        ccontrol.opfile.targetOp.format("%5d xy %f %f vel %f %f\n", timestep, target.getLoc().x, target.getLoc().y, target.velocity.x, target.velocity.y);
    }  /* writeTargetData */

    //@mylist - write data to files
    public void writePopDistanceData(long timestep, Population tracker){
        double[] agentsDistances = new double[tracker.agent.length];
        for (int i = 0; i < tracker.agent.length; i++) {
            agentsDistances[i] = tracker.agent[i].pop.distance;
        }
        double meanDistance = getMean(agentsDistances);
        double standardDeviationDistance = getStandardDeviation(agentsDistances);

        StringBuilder stringValues = new StringBuilder();
        for (int i = 0; i < agentsDistances.length; i++) {
            String stringValue = Integer.toString(i) + " " + Double.toString(agentsDistances[i]) + " ";
            stringValues.append(stringValue);
        }
        String distanceStringValues = stringValues.toString();
        ccontrol.opfile.popDistanceOp.format("%5d %f %f %s\n", timestep, meanDistance, standardDeviationDistance, distanceStringValues);
    }

    public void writeDistanceData(long timestep, Population tracker){
        double xDistance = tracker.displacementEW;
        double yDistance = tracker.displacementNS;
        double actualDistance = tracker.distance;
        ccontrol.opfile.distanceOp.format("%5d %f %f %f\n", timestep, xDistance, yDistance, actualDistance);
    }

    public void writePopTrackerData(long timestep, Population tracker){
        Double2D[] agentsTrackerLocations = new Double2D[tracker.agent.length];
        for (int i = 0; i < tracker.agent.length; i++) {
            agentsTrackerLocations[i] = tracker.agent[i].pop.loc;
        }
        double[] trackerLocationXs = Arrays.stream(agentsTrackerLocations).mapToDouble(t -> t.x).toArray();
        double[] trackerLocationYs = Arrays.stream(agentsTrackerLocations).mapToDouble(t -> t.y).toArray();
        double meanTrackerLocationX = getMean(trackerLocationXs);
        double meanTrackerLocationY = getMean(trackerLocationYs);

        StringBuilder stringValues = new StringBuilder();
        for (int i = 0; i < agentsTrackerLocations.length; i++) {
            String stringValue = Integer.toString(i) + " " + Double.toString(agentsTrackerLocations[i].x) + " " + Double.toString(agentsTrackerLocations[i].y) + " ";
            stringValues.append(stringValue);
        }
        String trackerLocationStringValues = stringValues.toString();
        ccontrol.opfile.popTrackerOp.format("%5d %f %f %s\n", timestep, meanTrackerLocationX, meanTrackerLocationY, trackerLocationStringValues);
    }

    public void writeTrackerData(long timestep, Population tracker){
        double xLocation = tracker.loc.x;
        double yLocation = tracker.loc.y;
        double xVelocity = tracker.velocity.x;
        double yVelocity = tracker.velocity.y;
        ccontrol.opfile.trackerOp.format("%5d xy %f %f vel %f %f\n", timestep, xLocation, yLocation, xVelocity, yVelocity);
    }

    //@mylist - calculate mean
    public double getMean(double[] distances){
        double totalDistances = 0.0;
        for (int i = 0; i < distances.length; i++) {
            totalDistances += distances[i];
        }
        double meanDistance = totalDistances / distances.length;
        return meanDistance;
    }

    //@mylist - calculate standard deviation
    public double getStandardDeviation(double[] distances){
        double meanDistance = getMean(distances);
        double totalDifferenceSquares = 0.0;
        for (int i = 0; i < distances.length; i++) {
            totalDifferenceSquares += Math.pow((distances[i] - meanDistance), 2);
        }
        double varianceDistance = totalDifferenceSquares / distances.length;
        double standardDeviationDistance = Math.sqrt(varianceDistance);
        return standardDeviationDistance;
    }

} /* OutputTimestep */
