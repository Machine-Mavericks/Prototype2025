// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
  
  TalonFX shooterMotor  = new TalonFX(1);
  
  // AdvantageScope logging entries
  private final DoubleLogEntry motorVelocityLog;
  private final DoubleLogEntry motorCommandLog;
  
  /** Basic subsystem template with a sample command and sensor check. */
  public Shooter() {
    // Initialize data logging for AdvantageScope
    DataLog log = DataLogManager.getLog();
    motorVelocityLog = new DoubleLogEntry(log, "/Shooter/MotorVelocity");
    motorCommandLog = new DoubleLogEntry(log, "/Shooter/MotorCommand");
  }

public void shooterSpeed(double speed){
  shooterMotor.set(speed);
  // Log the commanded speed
  motorCommandLog.append(speed);
}

public void stop(){
  shooterMotor.set(0);
  motorCommandLog.append(0.0);
}
public double currentSpeed;
  /**
   * Return a one-time command that would run a short action for this subsystem.
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

  /**
   * Return a fake sensor value for showing how trigger conditions can work.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // Code here would run every robot cycle when this subsystem is alive.
    currentSpeed = shooterMotor.get();
    
    // Log actual motor velocity every cycle to monitor speed dips
    // Get velocity in rotations per second from the TalonFX
    double velocity = shooterMotor.getVelocity().getValueAsDouble();
    motorVelocityLog.append(velocity);
  }

  @Override
  public void simulationPeriodic() {
    // Code here would run each cycle while simulating the robot.
  }
}
