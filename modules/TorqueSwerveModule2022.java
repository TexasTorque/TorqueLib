package org.texastorque.torquelib.modules;

import org.opencv.core.RotatedRect;
import org.texastorque.torquelib.modules.base.TorqueSwerveModule;
import org.texastorque.torquelib.motors.TorqueNEO;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorTimeBase;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Super cool flipped swerve module built in 2023 by Abishek.
 * 
 * https://drive.google.com/file/d/1KftGS1G6L668PEpUwcPeUHwzFnFnMYsh/view?usp=sharing
 * 
 * @author Justus Languell
 */
public final class TorqueSwerveModule2022 extends TorqueSwerveModule {

    private final TorqueSwerveModuleConfiguration config;

    // The NEO motors for turn and drive.
    private final TorqueNEO drive, turn;

    // The CANCoder for wheel angle measurement.
    // FRC 1706 used AnalogPotentiometer, but everyone else uses CANCoder.
    // I have both implemented, lets see which one works.
    private final AnalogPotentiometer potentiometer;
    private final CANCoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;
    private final SimpleMotorFeedforward driveFeedForward;

    // Rotation offset for tearing
    public final double staticOffset;

    public final String name;

    public TorqueSwerveModule2022(final String name, final int driveID, final int turnID, final int encoderID, 
            final double staticOffset, final TorqueSwerveModuleConfiguration config) {
        super(driveID);
        this.name = name.replaceAll(" ", "_").toLowerCase();
        this.staticOffset = staticOffset;
        this.config = config;

        // Configure the drive motor.
        drive = new TorqueNEO(driveID);
        drive.setCurrentLimit(config.driveMaxCurrent);
        drive.setVoltageCompensation(config.voltageCompensation);
        drive.setBreakMode(true);
        drive.setConversionFactors(1.0, config.driveVelocityFactor);
        drive.burnFlash();

        // Configure the turn motor.
        turn = new TorqueNEO(turnID);
        turn.setCurrentLimit(config.turnMaxCurrent);
        turn.setVoltageCompensation(config.voltageCompensation);
        turn.setBreakMode(true);
        turn.burnFlash();

        // Configure the cancoder
        potentiometer = new AnalogPotentiometer(encoderID, 2.0 * Math.PI, 0.0);

        // Cancoder docs: https://api.ctr-electronics.com/phoenix/release/java/com/ctre/phoenix/sensors/CANCoder.html
        cancoder = new CANCoder(encoderID);
        final CANCoderConfiguration cancoderConfig = new CANCoderConfiguration();
        // set units of the CANCoder to radians, with velocity being radians per second
        cancoderConfig.sensorCoefficient = 2 * Math.PI / 4096.0;
        cancoderConfig.unitString = "rad";
        cancoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;
        cancoder.configAllSettings(cancoderConfig);
        

        // Configure the controllers
        drivePID = new PIDController(config.drivePGain, config.driveIGain, config.driveDGain);
        turnPID = new PIDController(config.turnPGain, config.turnIGain, config.turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        driveFeedForward = new SimpleMotorFeedforward(config.driveStaticGain, config.driveFeedForward);
    }

    @Override
    public void setDesiredState(final SwerveModuleState state) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        // Calculate drive output
        final double drivePIDOutput = drivePID.calculate(drive.getVelocity(), optimized.speedMetersPerSecond);
        final double driveFFOutput = driveFeedForward.calculate(optimized.speedMetersPerSecond);
        log("Drive PID Output", drivePIDOutput + driveFFOutput);
        // drive.setPercent(drivePIDOutput + driveFFOutput);

        // Calculate turn output
        final double turnPIDOutput = turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        log("Turn PID Output", turnPIDOutput);
        // turn.setPercent(turnPIDOutput);
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }
    
    @Override 
    public Rotation2d getRotation() {
        return new Rotation2d(getTurnEncoder());
    }

    private double getTurnEncoder() {
        getTurnCancoder();
        getTurnPotentiometer();
        getTurnNEOEncoder();
        return getTurnCancoder();
    }

    private double getTurnCancoder() {
        final double value = cancoder.getPosition();
        return log("cancoder", value);
    }

    private double getTurnPotentiometer() {
        final double value = potentiometer.get();
        return log("potentiometer", value);
    }

    private double getTurnNEOEncoder() {
        final double value = coterminal(((turn.getPosition()) / config.turnGearRatio) * 2 * Math.PI); // with Neo encoder
        return log("neo encoder", value);
    }

    public void stop() {
        drive.setPercent(0.0);
        turn.setPercent(0.0);
    }

    public void zero() { 
        final double turnPIDOutput = turnPID.calculate(getTurnEncoder(), 0);
        turn.setPercent(turnPIDOutput);
    }

    private static double coterminal(final double rotation) {
        double coterminal = rotation;
        final double full = Math.signum(rotation) * 2 * Math.PI;
        while (coterminal > Math.PI || coterminal < -Math.PI)
            coterminal -= full;
        return coterminal; 
    }

    private double log(final String item, final double value) {
        final String key = name + "." + item.replaceAll(" ", "_").toLowerCase();
        SmartDashboard.putNumber(String.format("%s::%s", name, key), value);
        return value;
    }


    /**
     * A structure to define the constants for the swerve module.
     * 
     * Has default values that can be overriden before written to
     * the module.
     */
    public static final class TorqueSwerveModuleConfiguration {
        public static final TorqueSwerveModuleConfiguration defaultConfig = new TorqueSwerveModuleConfiguration();

        public double magic = 6.57 / (8.0 + 1.0 / 3.0);

        public int 
                driveMaxCurrent = 35, // amps
                turnMaxCurrent = 25; // amps
        public double 
                voltageCompensation = 12.6, // volts
                maxVelocity = 3.25, // m/s
                maxAcceleration = 3.0, // m/s^2
                maxAngularVelocity = Math.PI, // radians/s
                maxAngularAcceleration = Math.PI, // radians/s

                // The following will most likely need to be overriden
                // depending on the weight of each robot
                driveStaticGain = 0.015, 
                driveFeedForward = 0.212, 
                drivePGain = 0.2, 
                driveIGain = 0.0,
                driveDGain = 0.0,

                driveRampRate = 3.0, // %power/s 
                driveGearRatio = 6.57, // Translation motor to wheel
                wheelDiameter = 4.0 * 0.0254, // m
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI), // m/s
                turnPGain = 0.6,
                turnIGain = 0.0,
                turnDGain = 0.0,
                turnGearRatio = 12.41; // Rotation motor to wheel
    }

}
