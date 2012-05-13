package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.IRobotCreateScript;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 * A Ferrari is an implementation of the IRobotCreateInterface.
 *
 * @author Erik
 */
public abstract class Ferrari extends IRobotCreateAdapter implements Runnable
{
    private static final String TAG = "Ferrari";
    private int irCode = 255;
    int none = 255;
    int red = 248;
    int green = 244;
    int reserved = 240;
    int forceField = 242;
    int redAndGreen = 252;
    int redAndForceField = 250;
    int greenAndForceField = 246;
    int RedAndGreenAndForceField = 254;
    int totlalDistance = 0;
    protected final UltraSonicSensors ultraSonicSensors;
    protected final Dashboard dashboard;
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
    private int mode = 0;
    private int forwardDistance = 0;
    private int defaultSpeed = 200;
    private int defaultBackwardDistance = 250;
    private int backwardDistance = defaultBackwardDistance;

    /**
     * Constructs a Ferrari, an amazing machine!
     *
     * @param ioio the IOIO instance that the Ferrari can use to communicate
     * with other peripherals such as sensors
     * @param create an implementation of an iRobot
     * @param dashboard the Dashboard instance that is connected to the Ferrari
     * @throws ConnectionLostException
     */
    public Ferrari(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException
    {
        super(create);
        ultraSonicSensors = new UltraSonicSensors(ioio);
        this.dashboard = dashboard;
    }

    
    /**
     * To run this test, place the Ferrari in a cell surrounded by 4 walls. <p>
     * Note: The sensors draw power from the Create's battery. Make sure it is
     * charged.
     */
    private void testUltraSonicSensors()
    {
        dashboard.log("Starting ultrasonic test.");
        long endTime = System.currentTimeMillis() + 20 * SECOND;
        while (System.currentTimeMillis() < endTime)
        {
            try
            {
                ultraSonicSensors.readUltrasonicSensors();
            } catch (ConnectionLostException ex)
            {
                //TODO
            } catch (InterruptedException ex)
            {
                //TODO
            }
            SystemClock.sleep(500);
        }
        dashboard.log("Ultrasonic test ended.");
    }

    /**
     * Tests the rotation of the Ferrari.
     */
    private void testRotation()
    {
        dashboard.log("Testing rotation");
        try
        {
            turnAndGo(10, 0);
            SystemClock.sleep(500);
            turnAndGo(80, 0);
            SystemClock.sleep(80);
            turnAndGo(-90, 0);
            SystemClock.sleep(80);
            turnAndGo(180, 0);
            SystemClock.sleep(80);
            turnAndGo(-90, 0);
            SystemClock.sleep(80);
            turnAndGo(-180, 0);
            SystemClock.sleep(80);
            turnAndGo(180, 0);
            SystemClock.sleep(80);
        } catch (ConnectionLostException ex)
        {
        } catch (InterruptedException ex)
        {
        }

    }

    private void testStrobe()
    {
        dashboard.log("Starting strobe test.");
        long endTime = System.currentTimeMillis() + 2000 * SECOND;
        while (System.currentTimeMillis() < endTime)
        {
            try
            {
                ultraSonicSensors.testStrobe();
            } catch (ConnectionLostException ex)
            {
                //TODO
            }
            SystemClock.sleep(500);
        }
        dashboard.log("Strobe test ended.");
    }

    /**
     * Turns in place and then goes forward.
     *
     * @param angle the angle in degrees that the Ferrari shall turn. Negative
     * values makes clockwise turns.
     * @param distance the distance in mm that the Ferrari shall run forward.
     * Must be positive.
     */
    private void turnAndGo(int angle, int distance)
            throws ConnectionLostException, InterruptedException
    {
        IRobotCreateScript script = new IRobotCreateScript();
        /*
         * The Create overshoots by approx. 3 degrees depending on the floor
         * surface. Note: This is speed sensitive.
         */
        // TODO: Further tweaks to make the Ferrari make more precise turns.  
        if (angle < 0)
        {
            angle = Math.min(0, angle + 3);
        }
        if (angle > 0)
        {
            angle = Math.max(0, angle - 3);
        }
        if (angle != 0)
        {
            script.turnInPlace(100, angle < 0); // Do not change speed!
            script.waitAngle(angle);
        }
        if (distance > 0)
        {
            script.driveStraight(speed);
            script.waitDistance(distance);
        }
        if (angle != 0 || distance > 0)
        {
            script.stop();
            playScript(script.getBytes(), false);
            // delay return from this method until script has finished executing
        }
    }

    /**
     * Closes down all the connections of the Ferrari, including the connection
     * to the iRobot Create and the connections to all the sensors.
     */
    public void shutDown()
    {
        closeConnection(); // close the connection to the Create
        ultraSonicSensors.closeConnection();
    }

    //// Methods made public for the purpose of the Dashboard ////
    /**
     * Gets the left distance to the wall using the left ultrasonic sensor
     *
     * @return the left distance
     */
    public int getLeftDistance()
    {
        return ultraSonicSensors.getLeftDistance();
    }

    /**
     * Gets the front distance to the wall using the front ultrasonic sensor
     *
     * @return the front distance
     */
    public int getFrontDistance()
    {
        return ultraSonicSensors.getFrontDistance();
    }

    /**
     * Gets the right distance to the wall using the right ultrasonic sensor
     *
     * @return the right distance
     */
    public int getRightDistance()
    {
        return ultraSonicSensors.getRightDistance();
    }

    /**
     * Checks if the Ferrari is running
     *
     * @return true if the Ferrari is running
     */
    public synchronized boolean isRunning()
    {
        return running;
    }

    protected synchronized void setRunning(boolean b)
    {
        running = false;
    }

    public boolean closeToBeacon()
    {
        if (getInfraredByte() == 244 || getInfraredByte() == 248 || getInfraredByte() == 252)//Red, green
        {
            return true;
        } else
        {
            return false;
        }
    }

    

    public void spinLeft(int angle, int speed) throws Exception
    {
        readSensors(SENSORS_GROUP_ID6);
        int currentAngle = getAngle();
        while (currentAngle <= angle)
        {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle = getAngle();
            driveDirect(speed, -speed);
        }
    }

    public void spinRight(int angle, int speed) throws Exception
    {
        readSensors(SENSORS_GROUP_ID6);
        int currentAngle = getAngle();
        while (currentAngle <= angle)
        {
            readSensors(SENSORS_GROUP_ID6);
            currentAngle = getAngle();
            driveDirect(-speed, speed);
        }
    }

    void stop()
    {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    void initialize() throws ConnectionLostException
    {
        dashboard.log("===========Start===========");
        //readSensors(SENSORS_GROUP_ID6);//Resets all counters in the Create to 0.
        //driveDirect(speed, speed);
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    void loop()
    {
       // throw new UnsupportedOperationException("Not yet implemented");
    }

    public abstract void run();

    
}
