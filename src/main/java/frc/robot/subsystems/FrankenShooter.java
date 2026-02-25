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
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// AdvantageScope logging entries
// private final DoubleLogEntry motorVelocityLog;
// private final DoubleLogEntry motorCommandLog;

@Logged
public class FrankenShooter extends SubsystemBase {

    @Logged
    TalonFX shooterMotor = new TalonFX(1);

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

    // constants
    private final double TICKSPStoRPM = (1 / 4096.0) * 60.0;

    // target speed
    public static double TargetSpeed; // When disabling dashboard/panels turn back to privet. Make static when not
                                      // using pannels
    public static double CurrentSpeed; // When disabling dashboard/panels turn back to privet. Make static when not
                                       // using pannels
    // PIF Controller Gains
    private final double FsGain = 0.0;
    private final double FvGain = 0.00021; // was 0.0002 // unit=power/rpm initial value=1.0/6000rpm=0.00016667
    public final double PGain = 0.0012;// was 0.0003
    public final double IGain = 0.0002;

    // integrated error
    private double IError;
    private Timer timer;

    /** Place code here to initialize subsystem */
    public FrankenShooter() {

        TalonFXConfiguration config = new TalonFXConfiguration()
                .withFeedback(
                        new FeedbackConfigs()
                                // CTRE Needs reduction ration (N:1) instead of actual ratio
                                .withSensorToMechanismRatio(1 / MECHANISM_RATIO))
                .withMotorOutput(
                        new MotorOutputConfigs()
                                .withInverted(InvertedValue.Clockwise_Positive))
                .withSlot0(new Slot0Configs()
                        .withKP(1.25)
                        .withKI(0)
                        .withKD(0)
                        .withKV(1 / FEEDFORWARD));

        // important! - set motor to coast mode - only works for 0 power
        //shooterMotor.setNeutralMode(NeutralModeValue.Coast);

        shooterMotor.getConfigurator().apply(config);

        // motor is initially off
        shooterMotor.set(0.0);

        // reset integrated error
        timer = new Timer();
        timer.reset();
        IError = 0.0;

        // reset target speed (rpm)
        TargetSpeed = 0.0;
    }

    public void shooterSpeed(double speed) {
        System.out.println(speed);
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

    /**
     * Method called periodically by the scheduler
     * Place any code here you wish to have run periodically
     */
    @Override
    public void periodic() {

        // our current speed
        CurrentSpeed = shooterMotor.get() * TICKSPStoRPM;

        // our current speed error
        double SpeedError = TargetSpeed - CurrentSpeed;

        // integrated error
        // determine time since last iteration
        timer.reset();
        // integrate speed error
        IError += IGain * SpeedError * 0.02;
        // anti-windup to prevent overshoots
        if (SpeedError < -50.0 && IError > 0.0)
            IError *= 0.90;
        if (SpeedError > 50.0 && IError < 0.0)
            IError *= 0.90;
        // integrated error limiter
        if (IError > 0.15)
            IError = 0.15;
        if (IError < -0.1)
            IError = -0.1;

        // PIF controller
        double NewPower = FsGain + // static feedforward
                FvGain * TargetSpeed + // speed feedforward
                PGain * SpeedError + // proportional gain
                IError; // integrated error
        // only drive motor in positive direction, otherwise let it coast
        System.out.println(NewPower);
        // if (SpeedError >= -50.0)
        //     shooterMotor.set(NewPower);
        //else
           // shooterMotor.set(0.0);
    }

    // Place special subsystem methods here

    /**
     * Sets shooter flywheel speed in rpm
     * 
     * @param RPM a double representing the desired flywheel speed in rpm. Negative
     *            values will be treated as 0.0.
     */
    public void SetFlywheelSpeed(double RPM) {
        // Setting velocity using the RPMToVelocity methode
        TargetSpeed = RPM;

    }

    /**
     * gets current flywheel speed in rpm
     * 
     * @return current flywheel speed in rpm
     */
    public double GetFlyWheelSpeed() {
        return CurrentSpeed;
    }

    /**
     * gets target flywheel speed in rpm
     * 
     * @return target flywheel speed in rpm
     */
    public double GetFlyWheelTargetSpeed() {
        return TargetSpeed;
    }

}