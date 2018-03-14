package sim.app.ccontrol;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.portrayal.*;
import java.awt.*;

/* Draw a horizontal and vertical line indicating the position of the
tracker that is being controlled by the population */

public class DrawPopLines extends SimplePortrayal2D implements Steppable
   {
   double boxHeight, boxWidth;
   Population pop;
   Double2D loc;

   public DrawPopLines(Population pop, Double2D loc,
                       double boxHeight, double boxWidth)
      {
      this.pop = pop;
      this.loc = loc;

      this.boxHeight = boxHeight;
      this.boxWidth = boxWidth;
      }  /* DrawPopLines */


   public void step(SimState state)
      {
      Ccontrol ccontrol = (Ccontrol) state;

      loc = pop.getLoc();
      }  /* step */

   public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
      {
      graphics.setColor(Color.cyan);
      // vertical line
      graphics.drawLine(0, (int)loc.y, (int)boxHeight, (int)loc.y);
      // horizontal line
      graphics.drawLine((int)loc.x, 0, (int)loc.x, (int)boxWidth);
      }  /* draw */
   }  /* class DrawPopLines */
