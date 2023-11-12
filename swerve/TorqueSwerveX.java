/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve;

import org.texastorque.torquelib.motors.TorqueNEO;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * West Coast Products Swerve X Module.
 *
 * @author Justus Languell - Omar Afzal
 */
public final class TorqueSwerveX extends TorqueSwerveModule {
    public int driveMaxCurrent = 35, turnMaxCurrent = 25;

    public double voltageCompensation = 12.6, drivePGain = .2, driveIGain = 0.0, driveDGain = 0.0,
            driveStaticGain = 0.015, driveFF = 0.212, driveGearRatio = 6.75, wheelDiameter = 0.1016,
            driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI),
            drivePosFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI), turnPGain = 0.1,
            turnIGain = 0.0, turnDGain = 0.0, turnGearRatio = 13.71;


    private final TorqueNEO drive, turn;

    private final CANCoder cancoder;

    private final PIDController drivePID, turnPID;

    private final SimpleMotorFeedforward driveFeedForward;

    public final double staticOffset;

    public final String name;

    public TorqueSwerveX(final String name, final SwervePorts ports, final double staticOffset,
            final double driveP, final double turnP) {
        super(ports.drive);
        this.name = name.replaceAll(" ", "_").toLowerCase();
        this.staticOffset = staticOffset;

        drive = new TorqueNEO(ports.drive);
        drive.setCurrentLimit(driveMaxCurrent);
        drive.setVoltageCompensation(voltageCompensation);
        drive.setBreakMode(true);
        drive.invertMotor(false);
        drive.setConversionFactors(drivePosFactor, driveVelocityFactor);
        drive.burnFlash();

        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(turnMaxCurrent);
        turn.setVoltageCompensation(voltageCompensation);
        turn.setBreakMode(false);
        turn.burnFlash();

        cancoder = new CANCoder(ports.encoder);
        final CANCoderConfiguration cancoderConfig = new CANCoderConfiguration();
        cancoderConfig.sensorCoefficient = 2 * Math.PI / 4096.0;
        cancoderConfig.unitString = "rad";
        cancoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;
        cancoderConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        cancoder.configAllSettings(cancoderConfig);

        drivePID = new PIDController(driveP, driveIGain, driveDGain);
        turnPID = new PIDController(turnP, turnIGain, turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        driveFeedForward = new SimpleMotorFeedforward(driveStaticGain, driveFF);
    }

    public void setDrivePID(final double p) {
        drivePID.setP(p);
    }


    public void setDesiredState(final SwerveModuleState state) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        final double drivePIDOutput =drivePID.calculate(drive.getVelocity(), optimized.speedMetersPerSecond);
        final double driveFFOutput = driveFeedForward.calculate(optimized.speedMetersPerSecond);

        SmartDashboard.putNumber(name + " Req Speed", optimized.speedMetersPerSecond);
        SmartDashboard.putNumber(name + " Drive Speed", drive.getVelocity());

        SmartDashboard.putNumber(name + " Drive PID Volts", drivePIDOutput);

        drive.setVolts(drivePIDOutput);


        final double turnPIDOutput =
                turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        turn.setVolts(-turnPIDOutput);
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(drive.getPosition(), getRotation());
    }

    @Override
    public Rotation2d getRotation() {
        return Rotation2d.fromRadians(getTurnEncoder());
    }

    private double getTurnEncoder() {
        return MathUtil
                .angleModulus(new Rotation2d(cancoder.getPosition() - staticOffset).getRadians());
    }

}
