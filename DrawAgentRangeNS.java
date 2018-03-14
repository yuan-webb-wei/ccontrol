package sim.app.ccontrol;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.portrayal.*;
import java.awt.*;

/* show ranges at which agents push, basically, show where there pushNorth
   and pushSouth thresholds are. */

public class DrawAgentRangeNS extends SimplePortrayal2D // implements Steppable
   {
   Agent agent;
   int name;
   int x, y, w, h;
   double rangeWindowWidth;
   double horizBorder = 100;
   double actualMax;
   double actualRadius; // max (and min = -max) values of range axes
                        // range in which agent actually travelled (boxWidth
                        // since agents are moving east and west).
                        // Same as variable in DrawRangeGridNS.java
   double drawRadius;   // max (and min = -max) values on display
                        // assuming center is zero, gives "radius" of max and
                        // min "pixel" to draw in window.
                        // Same as variable in DrawRangeGridNS.java

   public DrawAgentRangeNS(Agent a, double rangeWindowWidth, double horizBorder,
                         double actualMax)
      {
      this.agent = a;
      this.rangeWindowWidth = rangeWindowWidth;
      this.horizBorder = horizBorder;
      this.actualMax = actualMax;

      actualRadius = actualMax/2;
      name = agent.getName();
      x = (int)horizBorder;
      y = 20 + (name + 1) * 5;
      w = (int)(rangeWindowWidth - 2 * horizBorder);
      h = 2;
      drawRadius = rangeWindowWidth/2 - horizBorder;
      }  /* DrawAgentRangeNS */

//   public void step(SimState state)
//      {
//      Ccontrol ccontrol = (Ccontrol) state;
//      actualMax = ccontrol.boxHeight;
//      }  /* step */

   public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
      {
      int xx, ww;

//if (agent.getActive())
//System.out.printf(" in DrawAgentRangeNS(draw) agent %d\n", agent.name);

      if (agent.getActive())  // agent is active
         {
         graphics.setColor(Color.darkGray);
         graphics.fillRect(x, y, w, h);

         // push north range
         ww = (int)(drawRadius + 1 +
              agent.getThreshDistN()/actualRadius * drawRadius);
         if (agent.getPushDirection() == 1)  graphics.setColor(Color.blue);
         else  graphics.setColor(Color.lightGray);
//System.out.printf(" Agent %d threshDistN %.2f, x %d ww %d pushDir %d\n",
//agent.name, agent.getThreshDistN(), x, ww, agent.getPushDirection());
         graphics.fillRect(x, y, ww, h);

         // push south range
         xx = (int)(rangeWindowWidth/2 +
              agent.getThreshDistS()/actualRadius * drawRadius);
         ww = (int)((drawRadius + 1) -
              agent.getThreshDistS()/actualRadius * drawRadius);
         if (agent.getPushDirection() == 2)  graphics.setColor(Color.red);
         else  graphics.setColor(Color.lightGray);
//System.out.printf(" Agent %d threshDistS %.2f, xx %d ww %d pushDir %d\n",
//agent.name, agent.getThreshDistS(), xx, ww, agent.getPushDirection());
         graphics.fillRect(xx, y, ww, h);

         // center point
         graphics.setColor(Color.black);
         graphics.drawLine((int)(rangeWindowWidth/2), y, 
                           (int)(rangeWindowWidth/2), y+h);
         }
      else  // agent is not active
         {
         graphics.setColor(Color.gray);
         graphics.drawRect(x, y, w, h);
         }
      }  /* draw */
   }  /* class DrawAgentRangeNS */
