package sim.app.ccontrol;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.portrayal.*;
import java.awt.*;

public class DrawRangeGridEW extends SimplePortrayal2D //implements Steppable
   {
   Population pop;
   int x, y1, y2;
   double rangeWindowWidth;
   double rangeWindowHeight;
   double horizBorder = 100;
   double actualMax;
   double actualRadius; // max (and min = -max) values of range axes
                        // range in which agent actually travelled (boxHeight,
                        // not width, since agents are moving north and south).
                        // Same as variable in DrawAgentRange.java
   double drawRadius;   // max (and min = -max) values on display
                        // assuming center is zero, gives "radius" of max and
                        // min "pixel" to draw in window.
                        // Same as variable in DrawAgentRange.java

   public DrawRangeGridEW(Population pop, double rangeWindowWidth, 
                        double rangeWindowHeight, double horizBorder,
                        double actualMax)
      {
      this.pop = pop;
      this.rangeWindowWidth = rangeWindowWidth;
      this.rangeWindowHeight = rangeWindowHeight;
      this.horizBorder = horizBorder;
      this.actualMax = actualMax;

      actualRadius = actualMax/2.0;
      y1 = 0;
      y2 = (int)(rangeWindowHeight);
      drawRadius = rangeWindowWidth/2 - horizBorder;
      }  /* DrawRangeGridEW */

//   public void step(SimState state)
//      {
//      Ccontrol ccontrol = (Ccontrol) state;
//      actualMax = ccontrol.boxHeight;
//      }  /* step */

   public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
      {
      double increment;
      int numTics = 12;
      int i;
      double displacementVal;
      int displacementDraw;
      Double ticLabel;

      increment = (drawRadius * 2)/numTics;
      for (i=0; i<=numTics; i++)
         {
         graphics.setColor(Color.lightGray);
         x = (int)(horizBorder + (i * increment) );
         graphics.drawLine(x, y1, x, y2);

         graphics.setColor(Color.gray);
         ticLabel = (i*increment)/(2*drawRadius)*actualMax - actualRadius;
         // next line is because int to double cast truncates the decimal
         // rather than rounding.
         if (ticLabel > 0)  ticLabel += 0.5;
         if (ticLabel.intValue() % 100 == 0)
            graphics.drawString( String.valueOf(ticLabel.intValue()), x, y2);
         }  /* for */

      displacementVal = pop.getDisplacementEW();
//System.out.printf(" in DrawRangeGridEW(draw) displacementVal %.2f\n",
//displacementVal);
      if (displacementVal < -actualRadius)
         {
         graphics.setColor(Color.orange);
         displacementDraw = (int)(rangeWindowWidth/2 - drawRadius - 1);
         graphics.drawLine(displacementDraw, y1, displacementDraw, y2);
         graphics.drawString(String.valueOf((int)displacementVal),
                             displacementDraw, y2);
         }
      else if (displacementVal > actualRadius)
         {
         graphics.setColor(Color.orange);
         displacementDraw = (int)(rangeWindowWidth/2 + drawRadius + 1);
         graphics.drawLine(displacementDraw, y1, displacementDraw, y2);
         graphics.drawString(String.valueOf((int)displacementVal),
                             displacementDraw, y2);
         }
      else 
         {
         graphics.setColor(Color.magenta);
         displacementDraw = (int)(rangeWindowWidth/2 + 
                            displacementVal/(actualMax/2) * drawRadius);
         graphics.drawLine(displacementDraw, y1, displacementDraw, y2);
         graphics.drawString(String.valueOf((int)displacementVal),
                             displacementDraw, y2);
         }

      }  /* draw */
   }  /* class DrawRangeGridEW */
