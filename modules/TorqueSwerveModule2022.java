package org.texastorque.torquelib.modules;

import org.texastorque.torquelib.modules.base.TorqueSwerveModule;
import org.texastorque.torquelib.motors.TorqueNEO;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

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

    // The CANCoder for wheel angle measurement. (?)
    private final AnalogPotentiometer encoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;
    private final SimpleMotorFeedforward driveFeedForward;

    public TorqueSwerveModule2022(final int driveID, final int turnID, final int encoderID, final TorqueSwerveModuleConfiguration config) {
        super(driveID);
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

        // Configure the encoder
        encoder = new AnalogPotentiometer(encoderID, 2.0 * Math.PI, 0.0);

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
        drive.setPercent(drivePIDOutput + driveFFOutput);

        // Calculate turn output
        final double turnPIDOutput = turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        turn.setPercent(turnPIDOutput);
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }
    
    @Override 
    public Rotation2d getRotation() {
        return new Rotation2d(encoder.get());
    }

    private double getTurnEncoder() {
        return -1.0 * encoder.get();
    }

    public void stop() {
        drive.setPercent(0.0);
        turn.setPercent(0.0);
    }

    public void zero() { 
        final double turnPIDOutput = turnPID.calculate(getTurnEncoder(), 0);
        turn.setPercent(turnPIDOutput);
    }

    /**
     * A structure to define the constants for the swerve module.
     * 
     * Has default values that can be overriden before written to
     * the module.
     */
    public static final class TorqueSwerveModuleConfiguration {
        public int 
                driveMaxCurrent = 35, // amps
                turnMaxCurrent = 25; // amps
        public double 
                voltageCompensation = 12.6, // volts
                maxVelocity = 3.25, // m/s
                maxAcceleration = 3.0, // m/s^2
                maxAngularSpeed = Math.PI, // radians/s
                maxAngularAcceleration = Math.PI, // radians/s
                driveStaticGain = 0.015, 
                driveFeedForward = 0.285, 
                drivePGain = 0.25, 
                driveIGain = 0.0,
                driveDGain = 0.0,
                driveRampRate = 3.0, // %power/s 
                driveGearRatio = 8.333, // Translation motor to wheel
                wheelDiameter = 0.1524, // m
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI), // m/s
                turnPGain = 0.6,
                turnIGain = 0.0,
                turnDGain = 0.0;
    }
}
