package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import java.util.Random;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.IRobotCreateScript;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 * A Ferrari is an implementation of the IRobotCreateInterface.
 *
 * @author Erik
 */
public class JackMagic extends Ferrari 
{
    private static final String TAG = "Ferrari";
    private static final int RED_BUOY_CODE = 248;
    private static final int GREEN_BUOY_CODE = 244;
    private static final int FORCE_FIELD_CODE = 242;
    private static final int BOTH_BUOY_CODE = 252;
    private static final int RED_BUOY_FORCE_FIELD_CODE = 250;
    private static final int GREEN_BUOY_FORCE_FIELD_CODE = 246;
    private static final int BOTH_BUOY_FORCE_FIELD_CODE = 254;
    /*
     * The maze can be thought of as a grid of quadratic cells, separated by
     * zero-width walls. The cell width includes half a pipe diameter on each
     * side, i.e the cell edges pass through the center of surrounding pipes.
     * <p> Row numbers increase northward, and column numbers increase eastward.
     * <p> Positions and direction use a reference system that has its origin at
     * the west-most, south-most corner of the maze. The x-axis is oriented
     * eastward; the y-axis is oriented northward. The unit is 1 mm. <p> What
     * the Ferrari knows about the maze is:
     */
    private final static int NUM_ROWS = 12;
    private final static int NUM_COLUMNS = 4;
    private final static int CELL_WIDTH = 712;
    /*
     * State variables:
     */
    private int speed = 300; // The normal speed of the Ferrari when going straight
    // The row and column number of the current cell. 
    private int row;
    private int column;
    private boolean running = true;
    private final static int SECOND = 1000; // number of millis in a second
    
    private int presentState = 0;
    private int statePointer = 0;
    private int[] c =
    {
        60, 200
    };
    private int[] e =
    {
        64, 200
    };
    private int[] g =
    {
        67, 200
    };
    private final int[][] stateTable =
    {
        {
            0, 1, 2, 3
        },
        {
            1, 1, 2, 3
        },
        {
            2, 1, 2, 3
        },
        {
            3, 1, 2, 3
        }
    };

    /**
     * Constructs a Ferrari, an amazing machine!
     *
     * @param ioio the IOIO instance that the Ferrari can use to communicate
     * with other peripherals such as sensors
     * @param create an implementation of an iRobot
     * @param dashboard the Dashboard instance that is connected to the Ferrari
     * @throws ConnectionLostException
     */
    public JackMagic(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException
    {
        super(ioio, create, dashboard);
    }

    /**
     * Main method that gets the Ferrari running.
     *
     */
    public void run()
    {
        try
        {
            stateController();
        } catch (Exception ex)
        {
            dashboard.log("problem: " + ex.getMessage());
        }
        dashboard.log("Run completed.");
        setRunning(false);
        shutDown();
        setRunning(false);
    }


    private void backingUp(String direction) throws Exception
    {
        
        dashboard.log("backingup");
        driveDirect(-200,-200);
        SystemClock.sleep(1000);
        if (direction.equals("right"))
        {
            driveDirect(100, -100);
            SystemClock.sleep(1000);
            /*
             * spins 45 degrees to the right
             */
        }
        if (direction.equals("left"))
        {
            driveDirect(-100, 100);
            SystemClock.sleep(1000);
            /*
             * spins45 degrees to the left
             */
        }
        if (direction.equals("straight"))
        {
            int r = (int) (Math.random() * 2);

            if (r == 1)
            {
                driveDirect(100, -100);
                dashboard.log("~right /" + r);
            }
            if (r == 0)
            {
                driveDirect(-100, 100);
                dashboard.log("~left /" + r);
            }
            SystemClock.sleep(1000);
        }
//        SystemClock.sleep(2000);
        driveDirect(100, 100);//drive direct needs to go forward more
        statePointer = 0;
        presentState = 0;
        // dashboard.log("hi");
    }

    public void stateController() throws Exception
    {
        driveDirect(100, 100);
        // dashboard.log("in state contol");
        while (true)
        {
            setStatePointer();
            switch (stateTable[presentState][statePointer])
            {
                case 0:
                    presentState = 0;
                    dashboard.log("0");
                    break;
                case 1:
                    presentState = 1;
                    backingUp("right");
                    dashboard.log("bumpright");
                    break;
                case 2:
                    presentState = 2;
                    backingUp("left");
                    break;
                case 3:
                    presentState = 3;
                    backingUp("straight");
                    break;
            }
        }
    }

    public void setStatePointer() throws ConnectionLostException
    {
        readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
//        SystemClock.sleep(00);
        // dashboard.log("statepointer");
        // dashboard.log("is bump right" + isBumpRight());
        // dashboard.log("is bump left" + isBumpLeft());

        if (isBumpRight() && !isBumpLeft())//Right
        {
            dashboard.log("rightbump det");
            statePointer = 1;
        }
        if (isBumpLeft() && !isBumpRight())//left
        {
            dashboard.log("left bump detected");
            statePointer = 2;
        }
        if (isBumpRight() && isBumpLeft())//straight
        {
            dashboard.log("front bump detected");
            statePointer = 3;
        }
        if (!isBumpLeft() && !isBumpRight())//none
        {
            // dashboard.log("no bump detected");
            statePointer = 0;
        }
    }
}
