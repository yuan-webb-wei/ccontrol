package sim.app.ccontrol;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.portrayal.*;
import java.awt.*;

/* show ranges at which agents push, basically, show where there pushNorth
   and pushSouth thresholds are. */

public class DrawAgentRangeEW extends SimplePortrayal2D // implements Steppable
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
                        // Same as variable in DrawAgentRangeEW.java
   double drawRadius;   // max (and min = -max) values on display
                        // assuming center is zero, gives "radius" of max and
                        // min "pixel" to draw in window.
                        // Same as variable in DrawAgentRangeEW.java

   public DrawAgentRangeEW(Agent a, double rangeWindowWidth, double horizBorder,
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
      }  /* DrawAgentRangeEW */

//   public void step(SimState state)
//      {
//      Ccontrol ccontrol = (Ccontrol) state;
//      actualMax = ccontrol.boxHeight;
//      }  /* step */

   public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
      {
      int xx, ww;

//if (agent.getActive())
//System.out.printf(" in DrawAgentRangeEW(draw) agent %d\n", agent.name);

      if (agent.getActive())  // agent is active
         {
         graphics.setColor(Color.darkGray);
         graphics.fillRect(x, y, w, h);

         // push west range
         ww = (int)(drawRadius + 1 +
              agent.getThreshDistW()/actualRadius * drawRadius);
         if (agent.getPushDirection() == 4)  graphics.setColor(Color.green);
         else  graphics.setColor(Color.lightGray);
//System.out.printf(" Agent %d threshDistW %.2f, x %d ww %d pushDir %d\n",
//agent.name, agent.getThreshDistW(), x, ww, agent.getPushDirection());
         graphics.fillRect(x, y, ww, h);

         // push east range
         xx = (int)(rangeWindowWidth/2 +
              agent.getThreshDistE()/actualRadius * drawRadius);
         ww = (int)((drawRadius + 1) -
              agent.getThreshDistE()/actualRadius * drawRadius);
         if (agent.getPushDirection() == 3)  graphics.setColor(Color.yellow);
         else  graphics.setColor(Color.lightGray);
//System.out.printf(" Agent %d threshDistE %.2f, xx %d ww %d pushDir %d\n",
//agent.name, agent.getThreshDistE(), xx, ww, agent.getPushDirection());
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
   }  /* class DrawAgentRangeEW */
