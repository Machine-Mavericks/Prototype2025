// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

@Logged
public class Shooter extends SubsystemBase {

  @Logged
  TalonFX shooterMotor  = new TalonFX(1);


  // AdvantageScope logging entries
  // private final DoubleLogEntry motorVelocityLog;
  // private final DoubleLogEntry motorCommandLog;
  
  @Logged
  public double velocity;
  @Logged
  public double wheelRPM;
  @Logged
  public double commanded;

  /**
   * Total ratio from motor to flywheel <br/>
   * Represents how many wheel rotations occur for one motor rotation <br/>
   * 12T pulley to 24T pulley, then 30T gear to 26T gear
   */
  private static final double MECHANISM_RATIO = (12 / 24d) * (30 / 26d);

  /**
   * Feedforward value, in RPS per Volt
   */
  private static final double FEEDFORWARD = 4.93884;

  /** Basic subsystem template with a sample command and sensor check. */
  public Shooter() {
    // Initialize data logging for AdvantageScope
    DataLog log = DataLogManager.getLog();
    // motorVelocityLog = new DoubleLogEntry(log, "/Shooter/MotorVelocity");
    // motorCommandLog = new DoubleLogEntry(log, "/Shooter/MotorCommand");

    TalonFXConfiguration config = new TalonFXConfiguration()
      .withFeedback(
        new FeedbackConfigs()
          // CTRE Needs reduction ration (N:1) instead of actual ratio
          .withSensorToMechanismRatio(1 / MECHANISM_RATIO)
      ).withMotorOutput(
        new MotorOutputConfigs()
        .withInverted(InvertedValue.Clockwise_Positive)
      ).withSlot0(new Slot0Configs()
        .withKP(1.25)
        .withKI(0)
        .withKD(0)
        .withKV(1 / FEEDFORWARD)
      );

    shooterMotor.getConfigurator().apply(config);
  }

public void shooterSpeed(double speed){
  if (speed < 15) {
    shooterMotor.set(0);
  } else {
    // shooterMotor.set(speed);
    shooterMotor.setControl(new VelocityVoltage(speed));
  }
  // Log the commanded speed
  // motorCommandLog.append(speed);
  commanded = speed;
}

public void stop(){
  shooterMotor.set(0);
  // motorCommandLog.append(0.0);
  commanded = 0;
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
    double wheelRPM = velocity*60;
    // motorVelocityLog.append(velocity);
    this.velocity = velocity;
    this.wheelRPM = wheelRPM;

  }

  @Override
  public void simulationPeriodic() {
    // Code here would run each cycle while simulating the robot.
  }

  @Logged(name = "Shooter Volts")
  public double getVolts() {
    return shooterMotor.getMotorVoltage().getValueAsDouble();
  }

  @Logged(name = "Bus Volts")
  public double getBusVolts() {
    return shooterMotor.getSupplyVoltage().getValueAsDouble();
  }
  
  @Logged(name = "Shooter Amps")
  public double getCurrent() {
    return shooterMotor.getStatorCurrent().getValueAsDouble();
  }
}
