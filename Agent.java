package sim.app.ccontrol;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;

/* Code for single agent.  Multiple agents make up population. */

public class Agent // implements Steppable //, sim.portrayal.Orientable2D
{
    public int name;
    Population pop;
    boolean active = false;

    int whichTarget = 0;
    Target target;
    Double2D targetLoc;
    Double2D trackerLoc;  // refers to the population based tracker

    int pushDirection = 0;  // 0=inactive, 1=pushNorth, 2=pushSouth
    // 3 = pushEast, 4 = pushWest

    /**** variables for when agent acts based on target location ****/

    // next 5 variables are sources of variation
    // initialize within class if needed, instead of by Population or Ccontrol
    double threshDistN = 0;
    double threshDistS = 0;
    double threshDistE = 0;
    double threshDistW = 0;
//   double distanceErrorPercentage;  //percent error on distance to target
//                                    //adjust actual distance by this percent

    // values range between 0 and 1.  Indicate agent's relative preferences
    // of direction in which to push.
    // May allow this to be mutated later or affected by agent actions.
    double preference[] = new double[5];  // 0=inactive, 1=pushNorth,
    // 2=pushSouth 3 = pushEast, 4 = pushWest

    // base mutation rate for thresholds.  Ranges from 0.01 to 0.1.
    // if mutation occurs, causes threshold to change by 10% up or down
    // (multiple threshold by 0.9 or 1.1.
    // This is set by Ccontrol because of needing to use its random function.
    double threshMutationMin = 0.01;
    double threshMutationMax = 0.1;
    double threshMutation = 0.0;
//   double adjThreshMut = 0.0;   // adjusted threshold mutation
    // = threshMutation * distance as % of diagonal

    /**** variables for when agent acts based on change in distance to target ****/

    // will still use threshDistN and accompanying variables for this scenario.
    // threshDistN and threshDistS bound the NS region during which the agent
    // does not act.  Above and below, the agent will speed up or slow down.

    double currentDistanceToTarget = 0;
    double previousDistanceToTarget = 0;

//   Double2D currentDistanceToTarget = new Double2D(0, 0);
//   Double2D previousDistanceToTarget = new Double2D(0, 0);

    public Agent(int name, Population pop) {
        this.name = name;
        this.pop = pop;

        //this.distanceErrorPercentage = 0;

//      System.out.printf(" Agent %d created: ", name);
//      System.out.printf(
//      "threshDistN %.2f threshDistS %.2f threshDistE %.2f threshDistW %.2f\n",
//      threshDistN, threshDistS, threshDistE, threshDistW);

    }  /* Agent */

    public int getName() {
        return name;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean val) {
        active = val;
    }

    public int getPushDirection() {
        return pushDirection;
    }

    public double getThreshDistN() {
        return threshDistN;
    }

    public double getThreshDistS() {
        return threshDistS;
    }

    public double getThreshDistE() {
        return threshDistE;
    }

    public double getThreshDistW() {
        return threshDistW;
    }

    public void setThreshDistN(double val) {
        threshDistN = val;
    }

    public void setThreshDistS(double val) {
        threshDistS = val;
    }

    public void setThreshDistE(double val) {
        threshDistE = val;
    }

    public void setThreshDistW(double val) {
        threshDistW = val;
    }

    public double getPreference(int direction) {
        return preference[direction];
    }

    public void setPreference(double val, int direction) {
        preference[direction] = val;
        if (preference[direction] < 0) preference[direction] = 0;
        if (preference[direction] > 1) preference[direction] = 1;
    }

    public double getThreshMutation() {
        return threshMutation;
    }

    public void setThreshMutation(double val) {
        if (val >= threshMutationMin && val <= threshMutationMax)
            threshMutation = val;
    }
//   public double getAdjThreshMut()  { return adjThreshMut; }
//   public void setAdjThreshMut(double val)  { adjThreshMut = val; }

    public void printThresholds() {
        System.out.printf(" Agent %d: threshDistN %6.2f", name, threshDistN);
        System.out.printf(" threshDistS %6.2f", threshDistS);
        System.out.printf(" threshDistE %6.2f", threshDistE);
        System.out.printf(" threshDistW %6.2f\n", threshDistW);
    }  /* printThresholds */

    public void printPreferences() {
        int i;

        System.out.printf(" Agent %d: ", name);
        for (i = 0; i < 5; i++)
            System.out.printf(" pref[%d] %5.2f", i, preference[i]);
        System.out.printf("\n");
    }  /* printThresholds */

/*
   public void step(SimState state)
      {
      System.out.printf(" Agent %d: in step()\n", name);
      }  /* step */

    public int getDecision(SimState state) {
        double displacementNS;
        double displacementEW;
        double distance;

// below three variables seem to only be used for printing.
// try to eliminate
        Target target;
        Double2D targetLoc;
        Double2D trackerLoc;
        int pushDirNS, pushDirEW;

        Ccontrol ccontrol = (Ccontrol) state;
        pushDirection = 0;

// below three variables seem to only be used for printing.
// try to eliminate
        target = (Target) ccontrol.targetSpace.allObjects.objs[whichTarget];
        targetLoc = target.getLoc();
        trackerLoc = pop.getLoc();

        ////@mylist - calculate distance between tracker location and target location for an agent?
        //this.currentDistanceToTarget = pop.distance;

        ////@mylist - caluculate tracker location for an agent?
        //this.trackerLoc = pop.loc;

        switch (ccontrol.getPopScenario()) {
            case 0:  // react to target position relative to tracker
            {
                displacementNS = pop.getDisplacementNS();
                displacementEW = pop.getDisplacementEW();

                if (ccontrol.getTrackNS() && ccontrol.getTrackEW())
                // if tracking in two dimensions
                {
                    pushDirNS = pushNorthOrSouth(displacementNS);
                    pushDirEW = pushEastOrWest(displacementEW);
 
/*
               if (pushDirNS == 0)  pushDirection = pushDirEW;
               else if (pushDirEW == 0)  pushDirection = pushDirNS;
               else if (preference[pushDirNS] > preference[pushDirEW])
*/
                    if (preference[pushDirNS] > preference[pushDirEW])
                        if (preference[0] > preference[pushDirNS]) pushDirection = 0;
                        else pushDirection = pushDirNS;
                    else if (preference[pushDirEW] > preference[pushDirNS])
                        if (preference[0] > preference[pushDirEW]) pushDirection = 0;
                        else pushDirection = pushDirEW;
                    else  // equal difference, choose randomly
                    {
                        if (ccontrol.random.nextInt(2) == 1)
                            if (preference[0] > preference[pushDirNS]) pushDirection = 0;
                            else pushDirection = pushDirNS;
                        else if (preference[0] > preference[pushDirEW]) pushDirection = 0;
                        else pushDirection = pushDirEW;
                    }
                    System.out.printf(" t%d Agent %d", ccontrol.schedule.getSteps(), name);
                    System.out.printf(" pushDirNS %d pushDirEW %d", pushDirNS, pushDirEW);
                    System.out.printf(" prefX %.2f prefN %.2f prefS %.2f prefE %.2f prefW %.2f",
                            preference[0], preference[1], preference[2], preference[3], preference[4]);
                    System.out.printf(" chosen direction: %d\n", pushDirection);
                }  // if
                else if (ccontrol.getTrackNS())   // if tracking NS, vertically
                {
                    pushDirection = pushNorthOrSouth(displacementNS);
                    if (preference[0] > preference[pushDirection]) pushDirection = 0;
                }  // else if
                else if (ccontrol.getTrackEW())   // if tracking EW, horizontally
                {
                    pushDirection = pushEastOrWest(displacementEW);
                    if (preference[0] > preference[pushDirection]) pushDirection = 0;
                }  // else if
                else {
                    pushDirection = 0;
//               System.out.printf(" Agent %d: do not push\n", name);
                }  // else

                System.out.printf(" t%d Agent %d: dNS %.2f dEW %.2f pushDir %d\n",
                        ccontrol.schedule.getSteps(),
                        name, displacementNS, displacementEW, pushDirection);
                return pushDirection;
            }  /* case 0 */
            case 1:  // react to target's acceleration in last timestep
            {
                displacementNS = pop.getDisplacementNS();
                displacementEW = pop.getDisplacementEW();
                previousDistanceToTarget = currentDistanceToTarget;

                if (ccontrol.getTrackNS() && ccontrol.getTrackEW())
                // if tracking in two dimensions
                {
                }
                else if (ccontrol.getTrackNS())  // only tracking NS
                {
                    currentDistanceToTarget = displacementNS;
                    System.out.printf("       t%d Agent %d", ccontrol.schedule.getSteps(), name);
                    System.out.printf("       track NS current %f previous %f |c|-|p| %f\n",
                            currentDistanceToTarget, previousDistanceToTarget,
                            Math.abs(currentDistanceToTarget) - Math.abs(previousDistanceToTarget));
                    if (Math.abs(currentDistanceToTarget) -
                            Math.abs(previousDistanceToTarget) > 0)  //dist increasing
                    {
                        System.out.printf("       Distance increasing, move toward target");
                        if (displacementNS >= 0)  //target south of tracker
                        {
                            System.out.printf(", push south\n");
                            pushDirection = 2;  // push south
                        } else  // target north of tracker
                        {
                            System.out.printf(", push north\n");
                            pushDirection = 1;  // push north
                        }
                    } else {
                        System.out.printf("       Distance not increasing, do nothing\n");
                    }
                }
                else if (ccontrol.getTrackEW())  // only tracking EW
                {
                    currentDistanceToTarget = displacementEW;
                    System.out.printf("       t%d Agent %d", ccontrol.schedule.getSteps(), name);
                    System.out.printf("       track EW current %f previous %f |c|-|p| %f\n",
                            currentDistanceToTarget, previousDistanceToTarget,
                            Math.abs(currentDistanceToTarget) - Math.abs(previousDistanceToTarget));
                    if (Math.abs(currentDistanceToTarget) -
                            Math.abs(previousDistanceToTarget) > 0)  //dist increasing
                    {
                        System.out.printf("       Distance increasing, move toward target");
                        if (displacementEW >= 0)  //target east of tracker
                        {
                            System.out.printf(", push east\n");
                            pushDirection = 3;  // push east
                        } else  // target west of tracker
                        {
                            System.out.printf(", push west\n");
                            pushDirection = 4;  // push west
                        }
                    } else {
                        System.out.printf("       Distance not increasing, do nothing\n");
                    }
                }
                else  // not tracking at all
                {
                    pushDirection = 0;
                    System.out.printf(" Agent %d: not tracking\n", name);
                }

                System.out.printf(" t%d Agent %d", ccontrol.schedule.getSteps(), name);
                System.out.printf(" pushDirection %d\n", pushDirection);
                return pushDirection;
/*
            currentDistanceToTarget = pop.getCurrentDistanceToTarget();
            currentY = currentDistanceToTarget.y -
                  (distanceErrorPercentage * currentDistanceToTarget.y);

            System.out.printf(
                   " Current dist %.2f Previous dist %.2f currentY %.2f\n",
                   currentDistanceToTarget.y, previousDistanceToTarget.y,
                   currentY);

            if (currentDistanceToTarget.y > previousDistanceToTarget.y)
               // distance increasing
               {
               if (targetInFront == 1)  //speed up
                  {
                  pushDirection = 2;
                  }
               else  // slow down
                  {
                  pushDirection = 1;
                  }
               }  // if 
            else  // distance decreasing
               {
               pushDirection = 0;
               System.out.printf(" Agent %d do nothing\n", name);
               }  // else 

            previousDistanceToTarget = 
               new Double2D(currentDistanceToTarget.x, currentY);
*/
            }  /* case 1 */
        }  /* switch */

        return pushDirection;

    }  /* getDecision */

    public int pushNorthOrSouth(double displacementNS) {
        int pushDirection;

        if (displacementNS > threshDistS)   // target south of tracker
            pushDirection = 2;  // push south
        else if (displacementNS < threshDistN)  // target north of tracker
            pushDirection = 1;  // push north
        else   // target vertically even with tracker
            pushDirection = 0;  // don't push at all

        return pushDirection;
    }  /* pushNorthOrSouth */

    public int pushEastOrWest(double displacementEW) {
        int pushDirection;

        if (displacementEW > threshDistE)   // target east of tracker
            pushDirection = 3;  // push east
        else if (displacementEW < threshDistW)  // target west of tracker
            pushDirection = 4;  // push west
        else   // target horizontally even with tracker
            pushDirection = 0;  // don't push at all

        return pushDirection;
    }  /* pushEastOrWest */

    public void mutateThresholds(Ccontrol ccontrol,
                                 double displacementNS, double displacementEW) {
        double absDisplacementNS, absDisplacementEW;

        absDisplacementNS = Math.abs(displacementNS);
        absDisplacementEW = Math.abs(displacementEW);

        if (ccontrol.random.nextDouble() <
                threshMutation * absDisplacementNS / ccontrol.boxHeight) {
            if (ccontrol.random.nextDouble() < 0.5)
                setThreshDistN(threshDistN * 0.9);
            else
                setThreshDistN(threshDistN * 1.1);
        }  // if

        if (ccontrol.random.nextDouble() <
                threshMutation * absDisplacementNS / ccontrol.boxHeight) {
            if (ccontrol.random.nextDouble() < 0.5)
                setThreshDistS(threshDistS * 0.9);
            else
                setThreshDistS(threshDistS * 1.1);
        }  // if

        if (ccontrol.random.nextDouble() <
                threshMutation * absDisplacementEW / ccontrol.boxWidth) {
            if (ccontrol.random.nextDouble() < 0.5)
                setThreshDistE(threshDistE * 0.9);
            else
                setThreshDistE(threshDistE * 1.1);
        }  // if

        if (ccontrol.random.nextDouble() <
                threshMutation * absDisplacementEW / ccontrol.boxWidth) {
            if (ccontrol.random.nextDouble() < 0.5)
                setThreshDistW(threshDistW * 0.9);
            else
                setThreshDistW(threshDistW * 1.1);
        }  // if
    }  /* mutateThresholds */

}  /* class Agent */
