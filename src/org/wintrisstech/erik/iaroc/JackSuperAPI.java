/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wintrisstech.erik.iaroc;

import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;

/**
 *
 * @author droid3
 */
public class JackSuperAPI
{
    private int howFarWeHaveGone;
    private boolean weHaveBeenBumped = false;
    private final IRobotCreateAdapter createAdapter;
    private final Dashboard dashboard;

    public JackSuperAPI(IRobotCreateAdapter createAdapter, Dashboard dashboard)
        
    {
            this.createAdapter = createAdapter;
            this.dashboard = dashboard;
    }
    
    
    
    /**
     * *************************************************************************
     * Jack Super API
     * *************************************************************************
     */
    private void goForward(int leftWheelSpeed, int rightWheelSpeed)
    {
        try
        {
            createAdapter.driveDirect(leftWheelSpeed, rightWheelSpeed);
        } catch (ConnectionLostException ex)
        {
        }
    }

    private void goBackward(int leftWheelSpeed, int rightWheelSpeed)
    {
        try
        {
            createAdapter.driveDirect(leftWheelSpeed, rightWheelSpeed);
        } catch (ConnectionLostException ex)
        {
        }
    }

    private void goForward(int leftWheelSpeed, int rightWheelSpeed, int howFarWeWantToGo)
    {
        howFarWeHaveGone = howFarWeHaveGone + createAdapter.getDistance();
        dashboard.log("how far we have gone " + howFarWeHaveGone);
        if (howFarWeHaveGone >= howFarWeWantToGo)
        {
            howFarWeHaveGone = 0;
            weHaveBeenBumped = false;
            stop();
        }
    }
    
    
    private void stop()
    {
        try
        {
            createAdapter.driveDirect(0, 0);
        } catch (ConnectionLostException ex)
        {
        }
    }

    private void goBackward(int leftWheelSpeed, int rightWheelSpeed, int howFarWeWantToGo)
    {
        howFarWeHaveGone = howFarWeHaveGone + createAdapter.getDistance();
        dashboard.log("how far we have gone " + howFarWeHaveGone);
        if (howFarWeHaveGone >= howFarWeWantToGo)
        {
            howFarWeHaveGone = 0;
            weHaveBeenBumped = false;
            goForward(leftWheelSpeed, rightWheelSpeed);
        }
    }


    
}
