package sim.app.ccontrol;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;

public class Target implements Steppable, sim.portrayal.Orientable2D {
    public int name;
    //   public double targetLength = 6;
    Double2D loc = new Double2D(0, 0);
    Double2D lastd = new Double2D(0, 0);

    Double2D velocity = new Double2D(0, 1);

    double myNewOrientation;

    public Target(int name, Double2D loc) {
        this.name = name;
        this.loc = loc;

        System.out.printf(" Target %d created at (%.2f, %.2f)\n",
                this.name, loc.x, loc.y);
    }  /* Target */

    // set the orientation of this agent.  val is in radians.
    public void setOrientation2D(double val) {
        lastd = new Double2D(Math.cos(val), Math.sin(val));
    }  /* setOrientation2D */

    public double orientation2D() {
        if (lastd.x == 0 && lastd.y == 0) return 0;
        return Math.atan2(lastd.y, lastd.x);
    }  /* orientation2D */

    public int getName() {
        return name;
    }

    public double getOrientation() {
        return orientation2D();
    }

    public Double2D getLoc() {
        return loc;
    }

    public void step(SimState state) {
        int i;
        Double2D myPos;
        Double2D myNewPos;
        double newx, newy, tempx, tempy;
        double dx, dy;
        double adjustOrientation;
        double r;

        Ccontrol ccontrol = (Ccontrol) state;

        myPos = ccontrol.targetSpace.getObjectLocation(this);

        switch (ccontrol.getTargetScenario()) {
            case 0:  /* straight line */ {
                dx = ccontrol.getTargetStepLen() * Math.cos(orientation2D());
                dy = ccontrol.getTargetStepLen() * Math.sin(orientation2D());

                if ((myPos.x + dx < 0) || (myPos.x + dx >= ccontrol.boxWidth))
                    dx = -dx;
                if ((myPos.y + dy < 0) || (myPos.y + dy >= ccontrol.boxHeight))
                    dy = -dy;

                newx = myPos.x + dx;
                newy = myPos.y + dy;

                velocity = new Double2D(dx, dy);
                // 131118ASW we may not need loc, seems to be same as myPos
                // but I did set the box size and resolution of the box to be same
                loc = new Double2D(this.loc.x + dx, this.loc.y + dy);
                myNewPos = new Double2D(newx, newy);
                ccontrol.targetSpace.setObjectLocation(this, myNewPos);
                lastd = new Double2D(dx, dy);

                System.out.printf(
                        " t%d Target %d: myNewPos (%.2f,%.2f) loc (%.2f,%.2f)\n",
                        ccontrol.schedule.getSteps(),
                        getName(), myNewPos.x, myNewPos.y, loc.x, loc.y);

                ccontrol.optimestep.writeTargetData(
                        ccontrol.schedule.getSteps(), this);

                return;
            }  /* case 0 */
            case 1:  /* random motion */ {
                // 50% chance that orientation will change in each timestep
                // this is meant to try and smooth out motion

                r = ccontrol.random.nextDouble();
                if (r < 0.4)
                    r = 0;
                else
                    //assume Gaussian will generate floats ranging at most from -4 to 4
                    r = ccontrol.random.nextGaussian() / 4.0 * Math.PI / 6.0;

                dx = ccontrol.getTargetStepLen() * Math.cos(orientation2D() + r);
                dy = ccontrol.getTargetStepLen() * Math.sin(orientation2D() + r);

                if ((myPos.x + dx < 0) || (myPos.x + dx >= ccontrol.boxWidth))
                    dx = -dx;
                if ((myPos.y + dy < 0) || (myPos.y + dy >= ccontrol.boxHeight))
                    dy = -dy;

                newx = myPos.x + dx;
                newy = myPos.y + dy;

                velocity = new Double2D(dx, dy);
                // 131118ASW we may not need loc, seems to be same as myPos
                // but I did set the box size and resolution of the box to be same
                loc = new Double2D(this.loc.x + dx, this.loc.y + dy);
                myNewPos = new Double2D(newx, newy);
                ccontrol.targetSpace.setObjectLocation(this, myNewPos);
                lastd = new Double2D(dx, dy);

                System.out.printf(
                        " t%d Target %d: myNewPos (%.2f,%.2f) loc (%.2f,%.2f)\n",
                        ccontrol.schedule.getSteps(),
                        getName(), myNewPos.x, myNewPos.y, loc.x, loc.y);

                ccontrol.optimestep.writeTargetData(
                        ccontrol.schedule.getSteps(), this);

                return;
            }  /* case 1 */
            case 2:  /* straight line toroidal */ {
                dx = ccontrol.getTargetStepLen() * Math.cos(orientation2D());
                dy = ccontrol.getTargetStepLen() * Math.sin(orientation2D());

                newx = (myPos.x + dx + ccontrol.boxWidth) % ccontrol.boxWidth;
                newy = (myPos.y + dy + ccontrol.boxHeight) % ccontrol.boxHeight;

                velocity = new Double2D(dx, dy);
                // 131118ASW we may not need loc, seems to be same as myPos
                // but I did set the box size and resolution of the box to be same
                loc = new Double2D(
                        (this.loc.x + dx + ccontrol.boxWidth) % ccontrol.boxWidth,
                        (this.loc.y + dy + ccontrol.boxHeight) % ccontrol.boxHeight);
                myNewPos = new Double2D(newx, newy);
                ccontrol.targetSpace.setObjectLocation(this, myNewPos);
                lastd = new Double2D(dx, dy);

                System.out.printf(
                        " t%d Target %d: myNewPos (%.2f,%.2f) loc (%.2f,%.2f)\n",
                        ccontrol.schedule.getSteps(),
                        getName(), myNewPos.x, myNewPos.y, loc.x, loc.y);

                ccontrol.optimestep.writeTargetData(
                        ccontrol.schedule.getSteps(), this);

                return;
            }  /* case 2 */
            case 3:  /* random motion toroidal */ {
                // 50% chance that orientation will change in each timestep
                // this is meant to try and smooth out motion

                r = ccontrol.random.nextDouble();
                if (r < 0.4)
                    r = 0;
                else
                    //assume Gaussian will generate floats ranging at most from -4 to 4
                    r = ccontrol.random.nextGaussian() / 4.0 * Math.PI / 6.0;

                dx = ccontrol.getTargetStepLen() * Math.cos(orientation2D() + r);
                dy = ccontrol.getTargetStepLen() * Math.sin(orientation2D() + r);

                newx = (myPos.x + dx + ccontrol.boxWidth) % ccontrol.boxWidth;
                newy = (myPos.y + dy + ccontrol.boxHeight) % ccontrol.boxHeight;

                velocity = new Double2D(dx, dy);
                // 131118ASW we may not need loc, seems to be same as myPos
                // but I did set the box size and resolution of the box to be same
                loc = new Double2D(
                        (this.loc.x + dx + ccontrol.boxWidth) % ccontrol.boxWidth,
                        (this.loc.y + dy + ccontrol.boxHeight) % ccontrol.boxHeight);
                myNewPos = new Double2D(newx, newy);
                ccontrol.targetSpace.setObjectLocation(this, myNewPos);
                lastd = new Double2D(dx, dy);

                System.out.printf(
                        " t%d Target %d: myNewPos (%.2f,%.2f) loc (%.2f,%.2f)\n",
                        ccontrol.schedule.getSteps(),
                        getName(), myNewPos.x, myNewPos.y, loc.x, loc.y);

                ccontrol.optimestep.writeTargetData(
                        ccontrol.schedule.getSteps(), this);

                return;
            }  /* case 3 */
            case 4:  /* constant motion south */ {
                dx = 0;
                dy = ccontrol.getTargetStepLen();

                newx = (myPos.x + dx) % ccontrol.boxWidth;
                newy = (myPos.y + dy) % ccontrol.boxHeight;

                velocity = new Double2D(dx, dy);
                loc = new Double2D(this.loc.x + dx, this.loc.y + dy);

                if (newx < 0) newx = ccontrol.boxWidth;
                if (newy < 0) newy = ccontrol.boxWidth;
                myNewPos = new Double2D(newx, newy);
                ccontrol.targetSpace.setObjectLocation(this, myNewPos);
                lastd = new Double2D(dx, dy);

                System.out.printf(" %d Target %d: myNewPos is (%.2f,%.2f)\n",
                        ccontrol.schedule.getSteps(),
                        getName(), myNewPos.x, myNewPos.y);

                ccontrol.optimestep.writeTargetData(
                        ccontrol.schedule.getSteps(), this);

                return;
            }  /* case 4 */
            case 5:  /* no motion */ {
                return;
            }  /* case 5 */
        }  /* switch */

    }  /* step */

}  /* class Target */

