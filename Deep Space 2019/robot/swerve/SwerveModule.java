/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/**
 * This class provides methods for setting, getting and init of various motor
 * parameters for the drive and steering motors within a Swerve Module object.
 */

package frc.robot.swerve;

import frc.robot.RobotMap;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class SwerveModule {

    private String name;
    private WPI_TalonSRX driveMotor;
    private WPI_TalonSRX steerMotor;
    private Faults driveFaults = new Faults();
    private Faults steerFaults = new Faults();

    private int TIMEOUT = RobotMap.TalonSRX_TIMEOUT;

    public SwerveModule(String wheelName, WPI_TalonSRX wheelDriveMotor, WPI_TalonSRX wheelSteerMotor){
        name = wheelName;
        driveMotor = wheelDriveMotor;
        steerMotor = wheelSteerMotor;
    }

    //Open-Loop Percentage Output Setting -1 to 1
    public void setDriveSpeed(double driveSpeed){
        driveMotor.set(ControlMode.PercentOutput, driveSpeed);
    }

    //Open-Loop Percentage Output Setting -1 to 1
    public void setSteerSpeed(double steerSpeed){
        steerMotor.set(ControlMode.PercentOutput, steerSpeed);
    }

    //Closed-Loop Velocity Target Setting (+/- speed in pulses per 100 msec)
    public void setVelocity(double wheelVelocity){
        driveMotor.set(ControlMode.Velocity, wheelVelocity);
    }

    //Closed-Loop Position Target Setting (+/- pulses per +/-180 degrees)
    public void setPosition(double wheelPosition){
        steerMotor.set(ControlMode.Position, wheelPosition);
    }

    public int getVelocity(){
        return driveMotor.getSelectedSensorVelocity();
    }

    public int getPosition(){
        return steerMotor.getSelectedSensorPosition();
    }
    
    public void setBrake(){
        driveMotor.setNeutralMode(NeutralMode.Brake);
    }

    public void setCoast(){
        driveMotor.setNeutralMode(NeutralMode.Coast);
    }

    public void stop(){
        driveMotor.stopMotor();
        steerMotor.stopMotor();
	}
    
    public WPI_TalonSRX getDriveMotor(){
		return driveMotor;
	}
	
	public WPI_TalonSRX getSteerMotor(){
		return steerMotor;
    }

    public String getName(){
        return name;
    }

    public boolean detectDriveMotorPhase(){
        driveMotor.getFaults(driveFaults);
        return driveFaults.SensorOutOfPhase;
    }

    public boolean detectSteerMotorPhase(){
        steerMotor.getFaults(steerFaults);
        return steerFaults.SensorOutOfPhase;
    }

    private void delay(int msec){
        try{
            Thread.sleep(msec);
        }
        catch (Exception e){
            System.out.println("Error in Waitloop");
        }
    }

    // Steering Motor Calibration Routine to find the Index Sensor and set the wheel straight forward
    public void rotateSteerForCal(){
        //Init local variables
        boolean clear = false;
        int currentPos = 0;
        int newPos = 409600;

        System.out.println("  --Calibrating - "+name);

        //Enable encoder clearing so when the index sensor goes active the reset executes.
        steerMotor.configClearPositionOnQuadIdx(true, TIMEOUT);

        //Set current position to a known value and start the motor open-loop, but slow
        steerMotor.setSelectedSensorPosition(currentPos);
        steerMotor.set(ControlMode.PercentOutput, .1);
        
        //
        delay(40);

        //While the motor is running check to see when the encoder has been reset
        while (!clear) {
            if (newPos < currentPos){
                steerMotor.set(ControlMode.PercentOutput, 0.);
                clear = true;
            }
            else {
                currentPos = newPos;
            }

            delay(40);
            newPos = steerMotor.getSelectedSensorPosition();
        }
    
        //Give the motor extra time to stop
        delay(60);

        //Disabled index clearing and get the encoder position which will always be positive after stopping the open loop run
        steerMotor.configClearPositionOnQuadIdx(false, TIMEOUT);
        double error = (double) steerMotor.getSelectedSensorPosition();

        //Find the index offset in pulses for the wheel being calibrated
        double offset = 0.;

        switch (name){
            case "FrontRightWheel":
            offset = RobotMap.FRONT_RIGHT_STEER_INDEX_OFFSET_PULSES;
            break;
            case "FrontLeftWheel":
            offset = RobotMap.FRONT_LEFT_STEER_INDEX_OFFSET_PULSES;
            break;
            case "RearLeftWheel":
            offset = RobotMap.REAR_LEFT_STEER_INDEX_OFFSET_PULSES;
            break;
            case "RearRightWheel":
            offset = RobotMap.REAR_RIGHT_STEER_INDEX_OFFSET_PULSES;
            break;
        }

        //Negative offset means rotate CCW so error is subtracted to rotate more
        //Positive offset means rotate CW so error is subtracted to ratote less
        offset -= error;
        System.out.println("    -Index Dectection Error = "+(int) error);

        //Set the position off the wheel for the calibrated index correcting for index dection error
        steerMotor.set(ControlMode.Position, offset);

        //Delay for the time it will take the wheel to move to the set position, this assumes ~ 2 sec per wheel revolution
        double positionDelay = offset / RobotMap.STEER_PPR * 2.;
        delay((int) positionDelay);
      
        //Check for the error in driving to the position and reset the encoder to the new 0 reference
        error = (double) steerMotor.getSelectedSensorPosition();
        error = Math.abs(error) - Math.abs(offset);

        steerMotor.setSelectedSensorPosition(0);

        //Delay to make sure it had time to set the reference to 0 before the next command is called
        delay(40);

        System.out.println("    -Calibration Error = "+(int) error);
        System.out.println("    -Calibration Completed");
    }

    //Talon configuration for the Steer Motor
    public void initSteerMotor(){
        steerMotor.configFactoryDefault();

        steerMotor.setInverted(false);
    	steerMotor.setNeutralMode(NeutralMode.Brake);

        steerMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TIMEOUT);
    	steerMotor.selectProfileSlot(0, 0); //slot #, PID #

        steerMotor.setSensorPhase(false);
        steerMotor.setSelectedSensorPosition(0);
        steerMotor.configClearPositionOnQuadIdx(false, TIMEOUT);

        steerMotor.configPeakOutputForward(.3, TIMEOUT);
    	steerMotor.configPeakOutputReverse(-.3, TIMEOUT);
    	
    	steerMotor.configNominalOutputForward(0, TIMEOUT);
    	steerMotor.configNominalOutputReverse(0, TIMEOUT);
    	
        steerMotor.configAllowableClosedloopError(0, 100, TIMEOUT);
    	
    	steerMotor.config_kP(0, .15, TIMEOUT);
    	steerMotor.config_kI(0, 0, TIMEOUT);
    	steerMotor.config_kD(0, 1, TIMEOUT);
        steerMotor.config_kF(0, 0, TIMEOUT);

        System.out.println("Steer Motor Initialized - "+name);
    }

    //Talon configuration for the Drive Motor
    public void initDriveMotor(){
        driveMotor.configFactoryDefault();

        //Invert the drive motors on one side because the calibration routine will rotate 
        //them after indexing an extra 180 degrees so the pullies face inward on the chassis
        switch (name){
            case "FrontRightWheel":
            driveMotor.setInverted(RobotMap.FRONT_RIGHT_DRIVE_TalonSRX_Invert);
            break;
            case "FrontLeftWheel":
            driveMotor.setInverted(RobotMap.FRONT_LEFT_DRIVE_TalonSRX_Invert);
            break;
            case "RearLeftWheel":
            driveMotor.setInverted(RobotMap.REAR_LEFT_DRIVE_TalonSRX_Invert);
            break;
            case "RearRightWheel":
            driveMotor.setInverted(RobotMap.REAR_RIGHT_DRIVE_TalonSRX_Invert);
            break;
        }

        driveMotor.setNeutralMode(NeutralMode.Coast);

        driveMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, TIMEOUT);
    	driveMotor.selectProfileSlot(0, 0); //slot #, PID #

        driveMotor.setSensorPhase(true);
        driveMotor.setSelectedSensorPosition(0);

        driveMotor.configPeakOutputForward(1, TIMEOUT);
        driveMotor.configPeakOutputReverse(-1, TIMEOUT);
        
    	driveMotor.configNominalOutputForward(0, TIMEOUT);
        driveMotor.configNominalOutputReverse(0, TIMEOUT);
             
        driveMotor.configAllowableClosedloopError(0, 4, TIMEOUT);
        
    	driveMotor.config_kP(0, .5, TIMEOUT);
    	driveMotor.config_kI(0, 0.0001, TIMEOUT);
    	driveMotor.config_kD(0, 1, TIMEOUT);
        driveMotor.config_kF(0, 1.624, TIMEOUT);

        System.out.println("Drive Motor Initialized - "+name);
    }
}
