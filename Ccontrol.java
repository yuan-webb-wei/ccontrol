package sim.app.ccontrol;

import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Ccontrol extends SimState {
    //   public Continuous2D trackerSpace;
    public Continuous2D targetSpace;
    public Continuous2D popSpace;
    public Continuous2D popLinesSpace;
    public double boxWidth = 600;
    public double boxHeight = 600;
    public double boxDiagonal;

    public String s1 = "Target parameters";
    public String s2 = "Population parameters";

/*
   // window that shows each agent as a circle with a line indicating the
   // direction in which each agent is pushing
   public Continuous2D agentInfo;
   public double agentInfoWidth = 310;
   public double agentInfoHeight = 310;

   // window that shows an axis on which agents are marked by a gray line at
   // their threshold values (separate axis for north and south threshold).
   // Indicate whether or not agents are pushing with colored lines.
   public Continuous2D axisView;
   public double axisWidth = 200;
   public double axisHeight = 100;
   public double axisWindowWidth = 600;
   public double axisWindowHeight = 300;
*/

    // window that shows a line for each agent and indicates regions at which
    // agents will push north and south
    public Continuous2D rangeViewEW;
    public Continuous2D rangeViewNS;
    public double rangeWidth = 200;
    public double rangeHeight = 100;
    public double rangeWindowWidth = 600;
    public double rangeWindowHeight = 550;
    public double horizBorder = 50;

    public int setRunNum = -1;     // if = -1, read from run.num and add one
    // if >= 0, use given number, don't increment
    // and don't change run.num
    public int runNum = -1;
    public String outputDirectory = "Output";
    public Output opfile;          // output files
    public OutputTimestep optimestep;      // output fxns called every timestep

    // target parameters
    public int numTargets = 1;
    public int numTargetScenarios = 5;
    public int targetScenario = 1;
    public double targetStepLen = 1;

    // population parameters
    public Population pop;
    public int popSize = 100; //@mylist - value used to be 1; set to 100 temporarily
    public int numPopScenarios = 3;
    public int popScenario = 0; //@mylist - value used to be 1; set to 0 temporarily
    public int maxPopSize = 100;
    public double threshMaxPercentX = 0.1;  // range of variation as a percent
    public double threshMaxPercentY = 0.1;  // of work space (boxHeight&Width)
    public boolean trackNS = true;
    public boolean trackEW = true;
    public boolean startOnTarget = true;  //tracker starts at same loc as target
    public boolean preferencesOn = false;  //agents have preferences for tasks
    public boolean mutateThresholdsOn = false; //@mylist - value used to be true; set to false temporarily

    public double gaussianPctRange = 0.1; //% boxheight to multiply Gaussian with
/*
   public int agentWidth = 30;
*/

    public final static double collisionDistance = 5.0;

    // initial starting locations of each element.  If set to -1, then init
    // location is randomly generated.  If set >= 0, then that value is the
    // starting location.
    public double targetStartX = -1;
    public double targetStartY = -1;
    public double popStartX = -1;
    public double popStartY = -1;

//  public double trackerStartY = 10;

    // @mylist
    public int distributionType = 0;
    public int sortOption = 0;
    public ArrayList<Integer> indices_of_picked_agents;

    // parameters in Model pane

    public int getSetRunNum() {
        return setRunNum;
    }

    public void setSetRunNum(int val) {
        setRunNum = val;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String name) {
        outputDirectory = name;
    }

    // target parameters
    public String getS1() {
        return s1;
    }

    public double getTargetStepLen() {
        return targetStepLen;
    }

    public void setTargetStepLen(double val) {
        targetStepLen = val;
    }

    public double getTargetStartX() {
        return targetStartX;
    }

    public double getTargetStartY() {
        return targetStartY;
    }

    public void setTargetStartX(double val) {
        targetStartX = val;
    }

    public void setTargetStartY(double val) {
        targetStartY = val;
    }

    public int getTargetScenario() {
        return targetScenario;
    }

    public void setTargetScenario(int val) {
        if (val >= 0 && val < numTargetScenarios) targetScenario = val;
    }

    public Object domTargetScenario() {
        return new String[]{"Straight line, non-toroidal",
                "Random motion, non-toroidal",
                "Straight line, toroidal",
                "Random motion, toroidal",
                "Constant motion south",
                "This should not work"};
    }

    // population based tracker parameters
    public String getS2() {
        return s2;
    }

    public int getMaxPopSize() {
        return maxPopSize;
    }

    public int getPopSize() {
        return popSize;
    }

    public void setPopSize(int val) {
        if (val > 0 && val <= maxPopSize) popSize = val;

        //int i;
        //for (i = 0; i < popSize; i++) pop.agent[i].setActive(true);
        //for (i = popSize; i < maxPopSize; i++) pop.agent[i].setActive(false);

        //@mylist - select agents randomly
        indices_of_picked_agents = new ArrayList<Integer>(this.popSize);
        indices_of_picked_agents = getRandomIndices(this.popSize, this.maxPopSize);

        for (int i = 0; i < maxPopSize; i++){
            if (this.indices_of_picked_agents.contains(i)){
                pop.agent[i].setActive(true);
            }
            else {
                pop.agent[i].setActive(false);
            }
        }
    }

    public double getPopStartX() {
        return popStartX;
    }

    public double getPopStartY() {
        return popStartY;
    }

    public void setPopStartX(double val) {
        popStartX = val;
    }

    public void setPopStartY(double val) {
        popStartY = val;
    }

    public boolean getStartOnTarget() {
        return startOnTarget;
    }

    public void setStartOnTarget(boolean val) {
        startOnTarget = val;
    }

    public double getThreshMaxPercentX() {
        return threshMaxPercentX;
    }

    public double getThreshMaxPercentY() {
        return threshMaxPercentY;
    }

    public void setThreshMaxPercentX(double val) {
        if (val >= 0.0 && val <= 1.0) threshMaxPercentX = val;
    }

    public void setThreshMaxPercentY(double val) {
        if (val >= 0.0 && val <= 1.0) threshMaxPercentY = val;
    }

    public boolean getPreferencesOn() {
        return preferencesOn;
    }

    public void setPreferencesOn(boolean val) {
        preferencesOn = val;
    }

    public boolean getTrackNS() {
        return trackNS;
    }

    public void setTrackNS(boolean val) {
        trackNS = val;
    }

    public boolean getTrackEW() {
        return trackEW;
    }

    public void setTrackEW(boolean val) {
        trackEW = val;
    }

    public boolean getMutateThresholdsOn() {
        return mutateThresholdsOn;
    }

    public void setMutateThresholdsOn(boolean val) {
        mutateThresholdsOn = val;
    }

    public int getPopScenario() {
        return popScenario;
    }

    public void setPopScenario(int val) {
        if (val >= 0 && val < numPopScenarios) popScenario = val;
    }

    public Object domPopScenario() {
        return new String[]{"Act on distance to target",
                "Act on change in distance to target (under development)"};
    }
//                             "Act on change in distance to target (TBD)",
//                             "This should not work"}; }
//   public double getGaussianPctRange()  { return gaussianPctRange; }
//   public void setGaussianPctRange(double val)  
//      {
//      if (val >= 0.0 && val <= 1.0)  gaussianPctRange = val;
//      }

/*
   public double getAxisWindowWidth()  { return axisWindowWidth; }
   public double getAxisWindowHeight()  { return axisWindowHeight; }
*/

    //@mylist - add distribution type
    public int getDistributionType() {
        return this.distributionType;
    }

    public void setDistributionType(int value) {
        this.distributionType = value;
    }

    public Object domDistributionType() {
        return new String[]{ "Gaussian Distribution", "Inverted Gaussian Distribution", "Uniform Distribution", "Random Distribution" };
    }

    //@mylist - add sorting option
    public int getSortOption() {
        return this.sortOption;
    }

    public void setSortOption(int value) {
        this.sortOption = value;
    }

    public Object domSortOption() {
        return new String[]{ "Unsorted", "Sorted" };
    }

    // functions for parameters not in model pane

    public Ccontrol(long seed) {
        super(seed);
    }  /* Ccontrol */

    public void start() {
        super.start();

        targetSpace = new Continuous2D(collisionDistance, boxWidth, boxHeight);
        popSpace = new Continuous2D(collisionDistance, boxWidth, boxHeight);
        popLinesSpace = new Continuous2D(collisionDistance, boxWidth, boxHeight);
/*
      agentInfo = new Continuous2D(collisionDistance, 
                  agentInfoWidth, agentInfoHeight);
      axisView = new Continuous2D(collisionDistance, axisWidth, axisHeight);
*/
        rangeViewEW = new Continuous2D(collisionDistance, rangeWidth, rangeHeight);
        rangeViewNS = new Continuous2D(collisionDistance, rangeWidth, rangeHeight);

        runNum = getCurrentRunNum();
        System.out.printf(" --runNum: %d\n", runNum);
        opfile = new Output(this);
        optimestep = new OutputTimestep(this);

        boxDiagonal = Math.sqrt(boxHeight * boxHeight + boxWidth * boxWidth);

        initTarget();
        initPopulation();

        opfile.writeStartOfRun();
    }  /* start */

    public void finish() {
        opfile.closeFiles();
    }  /* finish */

//   public double BoxDiagonalLength()  { return boxDiagonal; }

    public void initTarget() {
        int i;
        Double2D loc;
        double x, y;

        for (i = 0; i < numTargets; i++) {
            loc = setStartLocation(getTargetStartX(), getTargetStartY());

            Target target = new Target(i, loc);

            //start with random orientation
            target.setOrientation2D(random.nextDouble() * 2 * Math.PI);
            targetSpace.setObjectLocation(target, loc);
            schedule.scheduleRepeating(target);
        }
    }  /* initTarget */

    public void initPopulation() {
        Double2D loc;
        double x, y;
        int whichTarget = 0;
        Target t;
        double r1, r2;   // used to initialize agent thresholds
        int i, j;

        if (startOnTarget) {
            t = (Target) targetSpace.allObjects.objs[whichTarget];
            loc = t.getLoc();
        } else
            loc = setStartLocation(getPopStartX(), getPopStartY());

        pop = new Population(maxPopSize, loc);
        pop.setOrientation2D(Math.PI / 2); //start out pointed north
        popSpace.setObjectLocation(pop, loc);
        schedule.scheduleRepeating(pop);

        // set up to draw lines at population position
        DrawPopLines drawPopLines = new DrawPopLines(pop, loc, boxHeight, boxWidth);
        popLinesSpace.setObjectLocation(drawPopLines, new Double2D(0, 0));
        schedule.scheduleRepeating(drawPopLines);

/*
      // set up window to draw axis
      DrawAxis drawAxis = new DrawAxis(axisWindowWidth, axisWindowHeight, 500);
      axisView.setObjectLocation(drawAxis, new Double2D(0, 0) );
      schedule.scheduleRepeating(drawAxis);
*/

        // draw grid in agent range window (EW)
        DrawRangeGridEW drawRangeGridEW = new DrawRangeGridEW(pop,
                rangeWindowWidth, rangeWindowHeight, horizBorder, boxWidth);
        rangeViewEW.setObjectLocation(drawRangeGridEW, new Double2D(0, 0));

        // draw grid in agent range window (NS)
        DrawRangeGridNS drawRangeGridNS = new DrawRangeGridNS(pop,
                rangeWindowWidth, rangeWindowHeight, horizBorder, boxHeight);
        rangeViewNS.setObjectLocation(drawRangeGridNS, new Double2D(0, 0));

        //@mylist - initialize distributed values
        double thresholdX = threshMaxPercentY * boxWidth / 3;
        double thresholdY = threshMaxPercentY * boxHeight / 3;
        double thresholdXMean = 0.0;
        double thresholdYMean = 0.0;
        double thresholdXStandardDeviation = (thresholdX);
        double thresholdYStandardDeviation = (thresholdY);
        int size = getMaxPopSize();

        //generate Gaussian (normal), inverted Gaussian (inverted normal), and uniform Distributed threshold values for both X and Y directions
        double[] normalXValues = getGaussianDistributedValues(size, thresholdX, thresholdXMean, thresholdXStandardDeviation);
        double[] invertedNormalXValues = getInvertedGaussianDistributedValues(size, thresholdX, thresholdXMean, thresholdXStandardDeviation);
        double[] uniformXValues = getUniformDistributedValues(size, thresholdX);
        double[] normalYValues = getGaussianDistributedValues(size, thresholdY, thresholdYMean, thresholdYStandardDeviation);
        double[] invertedNormalYValues = getInvertedGaussianDistributedValues(size, thresholdY, thresholdYMean, thresholdYStandardDeviation);
        double[] uniformYValues = getUniformDistributedValues(size, thresholdY);

        // initialize agents in population
        for (i = 0; i < getMaxPopSize(); i++) {
            //@mylist - set thresholds
            if (this.distributionType == 0) {
                //Gaussian Distribution (East/West direction)
                double thresholdX_value = (normalXValues[i] >= 0 ? normalXValues[i] : normalXValues[i] * (-1));
                pop.agent[i].setThreshDistE(thresholdX_value);
                pop.agent[i].setThreshDistW(thresholdX_value * (-1));
                //Gaussian Distribution (South/North direction)
                double thresholdY_value = (normalYValues[i] >= 0 ? normalYValues[i] : normalYValues[i] * (-1));
                pop.agent[i].setThreshDistS(thresholdY_value);
                pop.agent[i].setThreshDistN(thresholdY_value * (-1));
            }
            else if (this.distributionType == 1) {
                //Inverted Gaussian Distribution (East/West direction)
                double thresholdX_value = (invertedNormalXValues[i] >= 0 ? invertedNormalXValues[i] : invertedNormalXValues[i] * (-1));
                pop.agent[i].setThreshDistE(thresholdX_value);
                pop.agent[i].setThreshDistW(thresholdX_value * (-1));
                //Inverted Gaussian Distribution (South/North direction)
                double thresholdY_value = (invertedNormalYValues[i] >= 0 ? invertedNormalYValues[i] : invertedNormalYValues[i] * (-1));
                pop.agent[i].setThreshDistS(thresholdY_value);
                pop.agent[i].setThreshDistN(thresholdY_value * (-1));
            }
            else if (this.distributionType == 2) {
                //Uniform Distribution (East/West direction)
                double thresholdX_value = (uniformXValues[i] >= 0 ? uniformXValues[i] : uniformXValues[i] * (-1));
                pop.agent[i].setThreshDistE(thresholdX_value);
                pop.agent[i].setThreshDistW(thresholdX_value * (-1));
                //Uniform Distribution (South/North direction)
                double thresholdY_value = (uniformYValues[i] >= 0 ? uniformYValues[i] : uniformYValues[i] * (-1));
                pop.agent[i].setThreshDistS(thresholdY_value);
                pop.agent[i].setThreshDistN(thresholdY_value * (-1));
            }
            else {
                //Random Distribution

                // initialize thresholds
                r1 = random.nextGaussian() * threshMaxPercentY * boxHeight / 3;
                r2 = random.nextGaussian() * threshMaxPercentY * boxHeight / 3;
                if (r1 > r2) {
                    pop.agent[i].setThreshDistS(r1);
                    pop.agent[i].setThreshDistN(r2);
                } else {
                    pop.agent[i].setThreshDistS(r2);
                    pop.agent[i].setThreshDistN(r1);
                }

                r1 = random.nextGaussian() * threshMaxPercentY * boxWidth / 3;
                r2 = random.nextGaussian() * threshMaxPercentY * boxWidth / 3;
                if (r1 > r2) {
                    pop.agent[i].setThreshDistE(r1);
                    pop.agent[i].setThreshDistW(r2);
                } else {
                    pop.agent[i].setThreshDistE(r2);
                    pop.agent[i].setThreshDistW(r1);
                }
                pop.agent[i].printThresholds();
            }

            // initialize preferences
            if (preferencesOn)
                for (j = 0; j < 5; j++)
                    pop.agent[i].setPreference(random.nextDouble(), j);
            else
                for (j = 0; j < 5; j++)
                    pop.agent[i].setPreference(0, j);
            pop.agent[i].printPreferences();

            // initialize base threshold mutation
            pop.agent[i].setThreshMutation(
                    random.nextDouble() *
                            (pop.agent[i].threshMutationMax - pop.agent[i].threshMutationMin) +
                            pop.agent[i].threshMutationMin);
            System.out.printf(" Agent %d threshMutation %f\n", pop.agent[i].name,
                    pop.agent[i].getThreshMutation());
/*
         schedule.scheduleRepeating(pop.agent[i]);

         DrawAgentCircle drawAgentCircle = new DrawAgentCircle(agent, 
                     10 + (i/10) * (agentWidth+10),
                     10 + (i%10) * (agentWidth+10),
                     agentWidth);
         agentInfo.setObjectLocation(drawAgentCircle,
                  new Double2D(agentInfoWidth/2.0, agentInfoHeight/2.0) );
         schedule.scheduleRepeating(drawAgentCircle);

         DrawAgentLine drawAgentLine = new DrawAgentLine(agent, drawAxis);
         axisView.setObjectLocation(drawAgentLine, new Double2D(0, 0));
         schedule.scheduleRepeating(drawAgentLine);
*/

            DrawAgentRangeEW drawAgentRangeEW = new DrawAgentRangeEW(pop.agent[i], rangeWindowWidth, horizBorder, boxWidth);
            rangeViewEW.setObjectLocation(drawAgentRangeEW, new Double2D(0, 0));

            DrawAgentRangeNS drawAgentRangeNS = new DrawAgentRangeNS(pop.agent[i], rangeWindowWidth, horizBorder, boxHeight);
            rangeViewNS.setObjectLocation(drawAgentRangeNS, new Double2D(0, 0));

        }

        setPopSize(getPopSize());

    }  /* initPopulation */

    public Double2D setStartLocation(double paramX, double paramY) {
        Double2D loc;
        double x, y;

        x = paramX;
        y = paramY;

        if (x < 0) x = random.nextDouble() * boxWidth;
        else if (x >= boxWidth) x = boxWidth - 1;

        if (y < 0) y = random.nextDouble() * boxHeight;
        else if (y >= boxHeight) y = boxHeight - 1;

        loc = new Double2D(x, y);

        return (loc);
    }  /* setStartLocation */

    int getNumTargets() {
        return numTargets;
    }

    int getCurrentRunNum() {
        Scanner ipFile;
        Formatter opFile;

        if (setRunNum < 0)
        // if setRunNum == -1, read from file run.num and increment by one
        // if setRunNum >= 0, use that number as runNum
        {
            try {
                // read in the previous run number from file run.num and add one
                ipFile = new Scanner(new File("run.num"));
                try {
                    runNum = 1 + ipFile.nextInt();
                }  /* try */ catch (NoSuchElementException elementException) {
                    System.err.println
                            (" --Error(Ccontrol.readInRunNum): File read error: run.num");
                    ipFile.close();
                    System.exit(1);
                }  /* catch */
                ipFile.close();

                // write the new run number for the current run into file run.num
                try {
                    opFile = new Formatter("run.num");
                    opFile.format("%d\n", runNum);
                    opFile.close();
                }  /* try */ catch (FileNotFoundException fileNotFoundException) {
                    System.err.print(" --Error(Ccontrol.readInRunNum):");
                    System.err.println(" Error opening or creating file: run.num");
                    System.exit(1);
                }  /* catch */
            }  /* try */ catch (FileNotFoundException fileNotFoundException) {
                System.err.print(" --Error(Ccontrol.readInRunNum):");
                System.err.println(" File not found: run.num");
                System.exit(1);
            }  /* catch */
        }  /* if */ else {
            runNum = setRunNum;
        }  /* else */
        return runNum;
    }  /* readInRunNum */


    public static void main(String[] args) {
        doLoop(Ccontrol.class, args);
        System.exit(0);
    }  /* main */

    static final long serialVersionUID = -7164072518609011190L;

    //@mylist - get Gaussian distributed origin values
    public double[] getGaussianDistributedOriginValues(int size){
        //nextGaussian() - Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0 from this random number generator's sequence.
        Random randomGenerator = new Random();
        double[] normalValues_origin = new double[size];
        for (int k = 0; k < normalValues_origin.length; k++) {
            normalValues_origin[k] = randomGenerator.nextGaussian();
        }
        //Arrays.sort(normalValues_origin);
        //printToFile("./normalDistributionOrigin.out", normalValues_origin);
        return normalValues_origin;
    }

    //@mylist - get uniform distributed origin values
    public double[] getUniformDistributedOriginValues(int size){
        //nextDouble() - Returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's sequence.
        Random randomGenerator = new Random();
        double[] uniformValues_origin = new double[size];
        for (int k = 0; k < uniformValues_origin.length; k++) {
            uniformValues_origin[k] = randomGenerator.nextDouble();
        }
        //Arrays.sort(uniformValues_origin);
        //printToFile("./uniformDistributionOrigin.out", uniformValues_origin);
        return uniformValues_origin;
    }

    //@mylist - get Gaussian distributed values
    public double[] getGaussianDistributedValues(int size, double threshold, double thresholdMean, double thresholdStandardDeviation){
        double[] normalValues_origin = getGaussianDistributedOriginValues(size);
        double[] normalValues = new double[size];
        for (int k = 0; k < normalValues.length; k++) {
            normalValues[k] = normalValues_origin[k] * thresholdStandardDeviation + thresholdMean;
        }
        normalValues = normalizeData(normalValues, 0, threshold);
        if (this.sortOption == 1){
            Arrays.sort(normalValues);
        }
        //printToFile("./normalDistribution.out", normalValues);
        return normalValues;
    }

    //@mylist - get inverted Gaussian distributed values
    public double[] getInvertedGaussianDistributedValues(int size, double threshold, double thresholdMean, double thresholdStandardDeviation){
        double[] normalValues = getGaussianDistributedValues(size, threshold, thresholdMean, thresholdStandardDeviation);
        double min_negative = Arrays.stream(normalValues).min().getAsDouble();
        double max_positive = Arrays.stream(normalValues).max().getAsDouble();
        double[] invertedNormalValues = new double[size];
        for (int k = 0; k < normalValues.length; k++) {
            if (normalValues[k] > 0) {
                invertedNormalValues[k] = max_positive - normalValues[k];
            }
            if (normalValues[k] < 0) {
                invertedNormalValues[k] = min_negative - normalValues[k];
            }
        }
        invertedNormalValues = normalizeData(invertedNormalValues, 0, threshold);
        if (this.sortOption == 1) {
            Arrays.sort(invertedNormalValues);
        }
        //printToFile("./invertedNormalDistribution.out", invertedNormalValues);
        return invertedNormalValues;
    }

    //@mylist - get uniform distributed values
    public double[] getUniformDistributedValues(int size, double threshold){
        double[] uniformValues_origin = getUniformDistributedOriginValues(size);
        uniformValues_origin = normalizeData(uniformValues_origin, -1, 1);
        double[] uniformValues = new double[size];
        for (int k = 0; k < uniformValues.length; k++) {
            uniformValues[k] = uniformValues_origin[k] * threshold;
        }
        uniformValues = normalizeData(uniformValues, 0, threshold);
        if (this.sortOption == 1) {
            Arrays.sort(uniformValues);
        }
        //printToFile("./uniformDistribution.out", uniformValues);
        return uniformValues;
    }

    //@mylist - normalize Data
    public double[] normalizeData(double[] data_in, double lower_bound, double upper_bound) {
        //normalize given data's range into [lower_bound, upper_bound]
        //Arrays.sort(data_in);
        double data_in_max = Arrays.stream(data_in).max().getAsDouble();
        double data_in_min = Arrays.stream(data_in).min().getAsDouble();
        double[] data_out = new double[data_in.length];
        for (int i = 0; i < data_in.length; i++) {
            data_out[i] = (upper_bound - lower_bound) * (data_in[i] - data_in_min) / (data_in_max - data_in_min) + lower_bound;
        }
        return data_out;
    }

    //@mylist - print to file
    public void printToFile(String fileNameOut, double[] data) {
        try {
            File outputFile = new File(fileNameOut);
            PrintWriter outputWriter = new PrintWriter(outputFile);
            for (int i = 0; i < data.length; i++) {
                outputWriter.print(data[i]);
                if (i < data.length - 1) {
                    outputWriter.print(", ");
                }
            }
            outputWriter.println();
            outputWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //@mylist - get random indices
    public ArrayList<Integer> getRandomIndices(int size, int maximum_size) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        Random r = new Random();
        for (int i = 0; i < size; i++){
            int index = r.nextInt(maximum_size);
            if (!indices.contains(index)) {
                indices.add(index);
            }
            else {
                i--;
            }
        }
        return indices;
    }

}  /* class Ccontrol */
