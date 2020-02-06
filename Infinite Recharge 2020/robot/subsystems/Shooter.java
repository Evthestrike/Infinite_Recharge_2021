/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;;

public class Shooter extends SubsystemBase {
  private final TalonFX shootMotorTop = new TalonFX(RobotMap.SHOOT_TOP_TalonSRX_CAN_ID);
  private final TalonFX shootMotorBottom = new TalonFX(RobotMap.SHOOT_BOTTOM_TalonSRX_CAN_ID);
  private int TIMEOUT = RobotMap.TalonFX_TIMEOUT;

  /**
   * Creates a new Shooter.
   */
  public Shooter() {

  }

  // Sets the motor velocities for each shooter motor independently
  public void shootBall(double topVelocity, double bottomVelocity){
    shootMotorTop.set(ControlMode.Velocity, topVelocity);
    shootMotorBottom.set(ControlMode.Velocity, bottomVelocity);
  }

  // Stops the motors
  public void stopMotors() {
    shootMotorTop.set(ControlMode.Velocity, 0.);
    shootMotorBottom.set(ControlMode.Velocity, 0.);
  }

  public void initShootMotors() {
    shootMotorTop.configFactoryDefault();
    
    shootMotorTop.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, TIMEOUT); 
    shootMotorTop.selectProfileSlot(0, 0); // slot #, PID #
    
    shootMotorTop.setInverted(false);
    
    shootMotorTop.setSelectedSensorPosition(0);
    shootMotorTop.configClearPositionOnQuadIdx(false, TIMEOUT);
    
    shootMotorTop.configMotionAcceleration(40960, TIMEOUT); // 400 Optical Encoder accel and velocity targets
    shootMotorTop.configMotionCruiseVelocity(20480, TIMEOUT);
    
    shootMotorTop.configPeakOutputForward(1., TIMEOUT);
    shootMotorTop.configPeakOutputReverse(-1., TIMEOUT);
    
    shootMotorTop.configNominalOutputForward(0, TIMEOUT);
    shootMotorTop.configNominalOutputReverse(0, TIMEOUT);
    
    shootMotorTop.configAllowableClosedloopError(0, 5, TIMEOUT); // Error for2048 CTRE Encoder
    
    shootMotorTop.config_IntegralZone(0, 100, TIMEOUT); // I-zone limits
    
    shootMotorTop.config_kP(0, .05, TIMEOUT);
    shootMotorTop.config_kI(0, 0.0001, TIMEOUT);
    shootMotorTop.config_kD(0, 2., TIMEOUT);
    shootMotorTop.config_kF(0, .05, TIMEOUT);
    

    System.out.println("  - Top Shooter Motor Initialized");

    shootMotorBottom.configFactoryDefault();
    
    shootMotorBottom.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, TIMEOUT); 
    shootMotorBottom.selectProfileSlot(0, 0); // slot #, PID #
    
    shootMotorBottom.setInverted(false);
    
    shootMotorBottom.setSelectedSensorPosition(0);
    shootMotorBottom.configClearPositionOnQuadIdx(false, TIMEOUT);
    
    shootMotorBottom.configMotionAcceleration(40960, TIMEOUT); // 400 Optical Encoder accel and velocity targets
    shootMotorBottom.configMotionCruiseVelocity(20480, TIMEOUT);
    
    shootMotorBottom.configPeakOutputForward(1., TIMEOUT);
    shootMotorBottom.configPeakOutputReverse(-1., TIMEOUT);
    
    shootMotorBottom.configNominalOutputForward(0, TIMEOUT);
    shootMotorBottom.configNominalOutputReverse(0, TIMEOUT);
    
    shootMotorBottom.configAllowableClosedloopError(0, 5, TIMEOUT); // Error for2048 CTRE Encoder
    
    shootMotorBottom.config_IntegralZone(0, 100, TIMEOUT); // I-zone limits
    
    shootMotorBottom.config_kP(0, .05, TIMEOUT);
    shootMotorBottom.config_kI(0, 0.0001, TIMEOUT);
    shootMotorBottom.config_kD(0, 2., TIMEOUT);
    shootMotorBottom.config_kF(0, .05, TIMEOUT);
    

    System.out.println("  - Bottom Shooter Motor Initialized");
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
