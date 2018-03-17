package sim.app.ccontrol;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import java.util.*;

/* Code for single agent.  Multiple agents make up population. */

public class Population implements Steppable, sim.portrayal.Orientable2D {
    Agent[] agent;
    int maxPopSize;
    int whichTarget = 0;  // which target is this agent tracking
    int[] pushCount = new int[5];  // 0 counts agents that do nothing
    // 1 counts agents that push north
    // 2 counts agents that push south
    // 3 counts agents that push east
    // 4 counts agents that push west
    double[] pushPercent = new double[5];  // output of mapping function
    // input is pushCount mapping function
    // pushPercent[0] is not used, just included
    // so that puschPercent and pushCount indices
    // are consistent
    // aggregated agent impact in each dimension
    double netY;  // south - north, so + means push south and - means push north
    // Note that this is opposite most calculations in code
    // usually I put north first, but this was done this way
    // because y=0 is at the top of the window, and this should
    // allow me to add netY to velocity to change it correctly.
    double netX;  // east - west, so + means push east and - means push west

    // basic variables to show a target moving on screen
    Double2D loc = new Double2D(0, 0);
    Double2D lastd = new Double2D(0, 0);  // appears to be last vel: dx and dy

    double previousDistanceToTarget = 0;
    double distance = 0;    // current distance to target
    double displacementNS;  // targetY - trackerY
    // positive means target south of tracker
    // negative means target north of tracker
    double displacementEW;  // targetX - trackerX
    // positive means target east of tracker
    // negative means target west of tracker

    // not sure if needed yet
    int northSum = 0;
    int southSum = 0;

    Double2D velocity = new Double2D(0.0, 0.0);
    Double2D acceleration = new Double2D(0.1, 0.1);  // change in velocity
    // newVelocity = oldVelocity + acceleration * time
    Double2D newVelocity;
// not sure if needed yet

    // variables needed by OutputTimestep
    double sumDistanceX = 0;
    double sumDistanceY = 0;
    double sumDisplacementX = 0;
    double sumDisplacementY = 0;

    public Population(int maxPopSize, Double2D loc) {
        int i;

        this.maxPopSize = maxPopSize;
        this.loc = loc;

        agent = new Agent[maxPopSize];
        for (i = 0; i < maxPopSize; i++) {
            agent[i] = new Agent(i, this);
        }

        System.out.printf(" Population created: tracker start at (%.2f, %.2f)\n", loc.x, loc.y);
    }  /* Population */

    /* Need to include the following three to compile */
    public void setOrientation2D(double val) {
        lastd = new Double2D(Math.cos(val), Math.sin(val));
    }  /* setOrientation2D */

    public double orientation2D() {
        if (lastd.x == 0 && lastd.y == 0) return 0;
        return Math.atan2(lastd.y, lastd.x);
    }  /* orientation2D */

    public double getOrientation() {
        return orientation2D();
    }
    /* Need to include the above three to compile */

    public Double2D getLoc() {
        return loc;
    }

    public double getDisplacementNS() {
        return displacementNS;
    }

    public double getDisplacementEW() {
        return displacementEW;
    }

    public double getDistance() {
        return distance;
    }

    public void initPushCount() {
        int i;
        for (i = 0; i < 5; i++) pushCount[i] = 0;
    }  /* initPushCount */

    // Function that scales the impact of each agent.
    // Done separately for each direction and each dimension.
    // Function that maps the count of how many agents are pushing in each
    // directions into values that can be used to determine push direction
    // or change in velocity (dx and dy) for this timestep.
    public void aggregateDirections(Ccontrol ccontrol) {
        int i;

        for (i = 0; i < 5; i++) {
            pushPercent[i] = (double) pushCount[i] / (double) ccontrol.getPopSize();
        }
        netY = pushPercent[2] - pushPercent[1];
        netX = pushPercent[3] - pushPercent[4];

        System.out.printf(" t%d Pop(aggregateDirections): pushCount: ",
                ccontrol.schedule.getSteps());
        for (i = 0; i < 5; i++)
            System.out.printf(" %d ", pushCount[i]);
        System.out.printf("\n");
        System.out.printf(" t%d Pop(aggregateDirections): pushPercent: ",
                ccontrol.schedule.getSteps());
        for (i = 0; i < 5; i++)
            System.out.printf(" %.2f ", pushPercent[i]);
        System.out.printf("\n");

    }  /* aggregateDirections */

    public void step(SimState state) {
        int i;
        int pushDirection;
        //Double2D myPos;
        //Double2D myNewPos;
        double dx, dy;

        Ccontrol ccontrol = (Ccontrol) state;

        displacementNS = calcDisplacementNS(whichTarget, ccontrol);
        displacementEW = calcDisplacementEW(whichTarget, ccontrol);
        distance = calcDistance(displacementEW, displacementNS);

        //@mylist - only update active agents
        // mutate agent thresholds based on current distance/diagonal length
        for (i = 0; i < ccontrol.maxPopSize; i++) {
            if (agent[i].active) {
                if (ccontrol.getMutateThresholdsOn())
                    agent[i].mutateThresholds(ccontrol, displacementNS, displacementEW);
            }
        }  // for

        initPushCount();

        //@mylist - only update active agents
        for (i = 0; i < ccontrol.maxPopSize; i++) {
            if (agent[i].active) {
                pushDirection = agent[i].getDecision(ccontrol);
                pushCount[pushDirection]++;
                System.out.printf(" t%d Pop(step): pushDirection %d\n", ccontrol.schedule.getSteps(), pushDirection);
            }
        }  // for
        System.out.printf(" t%d Pop(step): pushCount: ", ccontrol.schedule.getSteps());

        for (i = 0; i < 5; i++)
            System.out.printf(" %d ", pushCount[i]);
        System.out.printf("\n");

        aggregateDirections(ccontrol);
        System.out.printf(" t%d Pop(step): netX %.2f netY %.2f\n", ccontrol.schedule.getSteps(), netX, netY);

        if (ccontrol.getTrackNS() && ccontrol.getTrackEW())
        // if tracking in two dimensions
        {
            newVelocity = new Double2D(velocity.x + netX * acceleration.x, velocity.y + netY * acceleration.y);
        }  // if
        else if (ccontrol.getTrackNS())   // if tracking NS, vertically
        {
            newVelocity = new Double2D(0, velocity.y + netY * acceleration.y);
        }  // else if
        else if (ccontrol.getTrackEW())   // if tracking EW, horizontally
        {
            newVelocity = new Double2D(velocity.x + netX * acceleration.x, 0);
        }  // else if
        else {
            newVelocity = new Double2D(0, 0);
        }  // else
        System.out.printf(" t%d Pop(step):", ccontrol.schedule.getSteps());
        System.out.printf(" velocity %.2f,%.2f newVelocity %.2f,%.2f\n", velocity.x, velocity.y, newVelocity.x, newVelocity.y);

        velocity = newVelocity;
        dx = velocity.x;
        dy = velocity.y;
        lastd = getNewPos(ccontrol, dx, dy);
        loc = new Double2D(loc.x + dx, loc.y + dy);

        // set previous distance = current distance in prep for next step
        //previousDistanceToTarget = distance;
        //sumDistanceX += distance;
        //sumDistanceY += distance;
        //sumDisplacementX += currentDisplacement.x;
        //sumDisplacementY += currentDisplacement.y;

        //ccontrol.optimestep.writePopTrackerData(ccontrol.schedule.getSteps(), this);
        //ccontrol.optimestep.writePopDistanceData(ccontrol.schedule.getSteps(), this);

        //@mylist - write data to files
        ccontrol.optimestep.writePopDistanceData(ccontrol.schedule.getSteps(), this);
        ccontrol.optimestep.writeDistanceData(ccontrol.schedule.getSteps(), this);
        ccontrol.optimestep.writePopTrackerData(ccontrol.schedule.getSteps(), this);
        ccontrol.optimestep.writeTrackerData(ccontrol.schedule.getSteps(), this);

    }  /* step */

    public Double2D getNewPos(Ccontrol ccontrol, double dx, double dy) {
        Double2D myPos, myNewPos, lastd;
        double newx, newy;

        myPos = ccontrol.popSpace.getObjectLocation(this);

        newx = (myPos.x + dx) % ccontrol.boxWidth;
        newy = (myPos.y + dy) % ccontrol.boxHeight;

        if (newx < 0) newx = ccontrol.boxWidth;
        if (newy < 0) newy = ccontrol.boxWidth;
        myNewPos = new Double2D(newx, newy);
        ccontrol.popSpace.setObjectLocation(this, myNewPos);
        lastd = new Double2D(dx, dy);

        System.out.printf(" t%d Pop(getNewPos): myNewPos is %.2f,%.2f\n",
                ccontrol.schedule.getSteps(),
                myNewPos.x, myNewPos.y);

        return lastd;
    }  /* getNewPos */

    // Calculates the distance between the target and the tracker.
    // Always a positive value.
    public double calcDistance(double xdisplacement, double ydisplacement) {
        double dist;

        dist = Math.sqrt(xdisplacement * xdisplacement +
                ydisplacement * ydisplacement);

//      System.out.printf("          in calcDistance()");
//      System.out.printf(" xdisp %.2f ydisp %.2f distance %.2f\n", 
//                          xdisplacement, ydisplacement, dist);

        return (dist);
    }  /* calcDistance */

    // Calculates the NS displacement from the tracker to the target.
    // displacementNS = targetY - trackerY
    //     if positive, target south of tracker
    //     if negative, target north of tracker
    public double calcDisplacementNS(int whichTarget, Ccontrol ccontrol) {
        Double2D targetLoc;
        Target target;
        double ydist;

        target = (Target) ccontrol.targetSpace.allObjects.objs[whichTarget];
        targetLoc = target.getLoc();
        ydist = targetLoc.y - this.getLoc().y;

//      System.out.printf("          in getDisplacementNS()\n");
//      System.out.printf(
//         "          distance from Target %d A(%.2f,%.2f) D(%.2f,%.2f)",
//         whichTarget, targetLoc.x, targetLoc.y,
//         targetDisplayLoc.x, targetDisplayLoc.y);
//      System.out.printf(
//         " to Tracker %d A(%.2f,%.2f) D(%.2f,%.2f) is (x,%.2f)\n",
//         getName(), this.getLoc().x, this.getLoc().y,
//         ccontrol.trackerSpace.getObjectLocation(this).x,
//         ccontrol.trackerSpace.getObjectLocation(this).y,
//         ydist);

        return ydist;
    }  /* calcDisplacementNS */

    // Calculates the EW displacement from the tracker to the target.
    // displacementEW = targetY - trackerY
    //     if positive, target east of tracker
    //     if negative, target west of tracker
    public double calcDisplacementEW(int whichTarget, Ccontrol ccontrol) {
        Double2D targetLoc;
        Target target;
        double xdist;

        target = (Target) ccontrol.targetSpace.allObjects.objs[whichTarget];
        targetLoc = target.getLoc();
        xdist = targetLoc.x - this.getLoc().x;

//      System.out.printf("          in getDisplacementEW()\n");
//      System.out.printf(
//         "          distance from Target %d A(%.2f,%.2f) D(%.2f,%.2f)",
//         whichTarget, targetLoc.x, targetLoc.y,
//         targetDisplayLoc.x, targetDisplayLoc.y);
//      System.out.printf(
//         " to Tracker %d A(%.2f,%.2f) D(%.2f,%.2f) is (%.2f,y)\n",
//         getName(), this.getLoc().x, this.getLoc().y,
//         ccontrol.trackerSpace.getObjectLocation(this).x,
//         ccontrol.trackerSpace.getObjectLocation(this).y,
//         dist);

        return xdist;
    }  /* calcDisplacementEW */

}  /* class Population */
