package sim.app.ccontrol;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;

import javax.swing.*;
import java.awt.*;

import sim.portrayal.simple.*;
import sim.portrayal.SimplePortrayal2D;

public class CcontrolWithUI extends GUIState {
    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D targetPortrayal = new ContinuousPortrayal2D();
    ContinuousPortrayal2D trackerPortrayal = new ContinuousPortrayal2D();
    ContinuousPortrayal2D popPortrayal = new ContinuousPortrayal2D();
    ContinuousPortrayal2D popLinesPortrayal = new ContinuousPortrayal2D();

//   public Display2D agentDisplay;
//   public JFrame agentDisplayFrame;
//   ContinuousPortrayal2D agentInfoPortrayal = new ContinuousPortrayal2D();

//   public Display2D axisViewDisplay;
//   public JFrame axisViewDisplayFrame;
//   ContinuousPortrayal2D axisViewPortrayal = new ContinuousPortrayal2D();

    public Display2D rangeViewDisplayEW;
    public JFrame rangeViewDisplayFrameEW;
    ContinuousPortrayal2D rangeViewPortrayalEW = new ContinuousPortrayal2D();

    public Display2D rangeViewDisplayNS;
    public JFrame rangeViewDisplayFrameNS;
    ContinuousPortrayal2D rangeViewPortrayalNS = new ContinuousPortrayal2D();

    public static void main(String[] args) {
        new CcontrolWithUI().createController();
    }  /* main */

    public CcontrolWithUI() {
        super(new Ccontrol(System.currentTimeMillis()));
    }  /* CcontrolWithUI */

    public CcontrolWithUI(SimState state) {
        super(state);
    }  /* CcontrolWithUI */

    public static String getName() {
        return "Ccontrol Formation Simulation";
    }

    public Object getSimulationInspectedObject() {
        return state;
    }

    public void start() {
        super.start();
        setupPortrayals();
    }  /* start */

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }  /* load */

    public void setupPortrayals() {
        int i;
        Ccontrol ccontrol = (Ccontrol) state;

        // tell portrayals what to portray and how to portray them
        targetPortrayal.setField(ccontrol.targetSpace);
        popPortrayal.setField(ccontrol.popSpace);
        popLinesPortrayal.setField(ccontrol.popLinesSpace);
/*
      trackerPortrayal.setField(ccontrol.trackerSpace);
*/

        // targets
        for (i = 0; i < ccontrol.getNumTargets(); i++) {
            SimplePortrayal2D p = new AdjustablePortrayal2D(
                    new MovablePortrayal2D(
                            new OrientedPortrayal2D(
                                    new SimplePortrayal2D(),
                                    0,
                                    12.0,
                                    new Color(255, 0, 0),
                                    OrientedPortrayal2D.SHAPE_COMPASS)
                    )
            );
            targetPortrayal.setPortrayalForObject(
                    ccontrol.targetSpace.allObjects.objs[i], p);
        }  /* for i */

        // single tracker

        // population based tracker
        popPortrayal.setPortrayalForObject(
                ccontrol.popSpace.allObjects.objs[0],
                new CircledPortrayal2D(
                        new CircledPortrayal2D(
                                new OrientedPortrayal2D(
                                        new OvalPortrayal2D(1),
                                        0, 20),
                                0, 30.0, Color.blue, false),
                        0, 60.0, Color.blue, false));

/*
      SimplePortrayal2D p = new AdjustablePortrayal2D(
                new MovablePortrayal2D(
                        new OrientedPortrayal2D(
                                new SimplePortrayal2D(),
                                0,
                                12.0,
                                new Color(0, 0, 255),
                                OrientedPortrayal2D.SHAPE_COMPASS)
                        )
                );
      popPortrayal.setPortrayalForObject(
                      ccontrol.popSpace.allObjects.objs[0], p);
*/

        // set up portrayal for the agent info window (show agents and point dir)
//      agentInfoPortrayal.setField(ccontrol.agentInfo);

        // set up portrayal for axis view of variation values
//      axisViewPortrayal.setField(ccontrol.axisView);

        // set up portrayal for range view agents' action thresholds
        rangeViewPortrayalEW.setField(ccontrol.rangeViewEW);
        rangeViewPortrayalNS.setField(ccontrol.rangeViewNS);

        // reschedule the displayer
        display.reset();
//      agentDisplay.reset();
//      axisViewDisplay.reset();
        rangeViewDisplayEW.reset();
        rangeViewDisplayNS.reset();

        // redraw the display
        display.repaint();
//      agentDisplay.repaint();
//      axisViewDisplay.repaint();
        rangeViewDisplayEW.repaint();
        rangeViewDisplayNS.repaint();
    }  /* setup Portrayals */

    public void init(Controller c) {
        super.init(c);

        // make the displayer
        display = new Display2D(600, 600, this, 1);
        display.setBackdrop(Color.white);
        // turn off clipping
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Group Tracking Simulation");
        //register the frame so it appears in the "Display" list
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(targetPortrayal, "Target");
        display.attach(popPortrayal, "Population Tracker");
        display.attach(popLinesPortrayal, "Population Tracker location");
//      display.attach(trackerPortrayal, "Single Tracker");

/*
      // make the displayer for the population of agents
      agentDisplay = new Display2D(420, 420, this, 1);
      agentDisplay.setBackdrop(Color.white);
      agentDisplay.setClipping(false);

      agentDisplayFrame = agentDisplay.createFrame();
      agentDisplayFrame.setTitle("Agent Information");
      agentDisplayFrame.setLocation(new Point(0,400));
      c.registerFrame(agentDisplayFrame);  
      agentDisplayFrame.setVisible(true);
      agentDisplay.attach(agentInfoPortrayal, "agentInfo");

      // make the display showing an axis of the variation values and 
      // when agents act
      axisViewDisplay = new Display2D(600, 300, this, 1);
      axisViewDisplay.setBackdrop(Color.white);
      axisViewDisplay.setClipping(false);

      axisViewDisplayFrame = axisViewDisplay.createFrame();
      axisViewDisplayFrame.setTitle("Variation values");
      axisViewDisplayFrame.setLocation(new Point(100,400));
      c.registerFrame(axisViewDisplayFrame);
      axisViewDisplayFrame.setVisible(true);
      axisViewDisplay.attach(axisViewPortrayal, "axisView");
*/

        // make the display showing the action range for each agent (EW)
        rangeViewDisplayEW = new Display2D(600, 550, this, 1);
        rangeViewDisplayEW.setBackdrop(Color.white);
        rangeViewDisplayEW.setClipping(false);

        rangeViewDisplayFrameEW = rangeViewDisplayEW.createFrame();
        rangeViewDisplayFrameEW.setTitle("Agent thresholds, EW");
        rangeViewDisplayFrameEW.setLocation(new Point(200, 0));
        c.registerFrame(rangeViewDisplayFrameEW);
        rangeViewDisplayFrameEW.setVisible(true);
        rangeViewDisplayEW.attach(rangeViewPortrayalEW, "rangeViewEW");

        // make the display showing the action range for each agent (NS)
        rangeViewDisplayNS = new Display2D(600, 550, this, 1);
        rangeViewDisplayNS.setBackdrop(Color.white);
        rangeViewDisplayNS.setClipping(false);

        rangeViewDisplayFrameNS = rangeViewDisplayNS.createFrame();
        rangeViewDisplayFrameNS.setTitle("Agent thresholds, NS");
        rangeViewDisplayFrameNS.setLocation(new Point(300, 0));
        c.registerFrame(rangeViewDisplayFrameNS);
        rangeViewDisplayFrameNS.setVisible(true);
        rangeViewDisplayNS.attach(rangeViewPortrayalNS, "rangeViewNS");

    }  /* init */

    public void quit() {
        super.quit();

        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display = null;

        if (rangeViewDisplayFrameEW != null) rangeViewDisplayFrameEW.dispose();
        rangeViewDisplayFrameEW = null;
        rangeViewDisplayEW = null;

        if (rangeViewDisplayFrameNS != null) rangeViewDisplayFrameNS.dispose();
        rangeViewDisplayFrameNS = null;
        rangeViewDisplayNS = null;

    }  /* quit */

}  /* class CcontrolWithUI */
