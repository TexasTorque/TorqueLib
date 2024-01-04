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
import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * West Coast Products Swerve X Module.
 *
 * @author Omar Afzal
 */
public final class TorqueSwerveX extends TorqueSwerveModule {
    public int driveMaxCurrent = 35, turnMaxCurrent = 25;

    public final double drivePGain = .1, driveIGain = 0, driveDGain = .01, driveFF = 0.2,
            driveGearRatio = 6.75, wheelDiameter = 0.1016,
            driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI),
            drivePosFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI),
            NEOFreeSpeedRPS = 5676 * 0.9 / 60,
            driveWheelFreeSpeedRps = (NEOFreeSpeedRPS * wheelDiameter * Math.PI) / driveGearRatio,
            driveMaxSpeed = 4.6, turnPGain = 4, turnIGain = 0.0, turnDGain = 0.0,
            turnGearRatio = 13.71;

    private final TorqueNEO drive, turn;

    private final CANCoder cancoder;

    private final PIDController turnPID;

    private final CANCoderConfiguration cancoderConfig;

    public final String name;

    public final double staticOffset;


    public TorqueSwerveX(final String name, final SwervePorts ports, final double staticOffset) {

        super(ports.drive);
        this.name = name.replaceAll(" ", "_").toLowerCase();
        this.staticOffset = staticOffset;

        drive = new TorqueNEO(ports.drive);
        drive.setCurrentLimit(driveMaxCurrent);
        drive.setVoltageCompensation(12.6);
        drive.setBreakMode(true);
        drive.invertMotor(false);
        drive.setConversionFactors(drivePosFactor, driveVelocityFactor);

        drive.setPIDFeedbackDevice(drive.encoder);
        drive.configurePIDF(drivePGain, driveIGain, driveDGain, 1 / driveWheelFreeSpeedRps);
        drive.burnFlash();

        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(turnMaxCurrent);
        turn.setVoltageCompensation(12.6);
        turn.setBreakMode(true);
        turn.burnFlash();

        cancoder = new CANCoder(ports.encoder);
        cancoderConfig = new CANCoderConfiguration();
        cancoderConfig.sensorCoefficient = 2 * Math.PI / 4096.0;
        cancoderConfig.unitString = "rad";
        cancoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;
        cancoderConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        cancoder.configAllSettings(cancoderConfig);

        turnPID = new PIDController(turnPGain, turnIGain, turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
    }

    public void setDesiredState(final SwerveModuleState state) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        if (DriverStation.isAutonomous())
            drive.setPIDReference(optimized.speedMetersPerSecond,
                    CANSparkMax.ControlType.kVelocity);
        else
            drive.setPercent(optimized.speedMetersPerSecond / driveMaxSpeed);

        final double turnPIDOutput =
                -turnPID.calculate(getRotation().getRadians(), optimized.angle.getRadians());
        SmartDashboard.putNumber(name + " Angle", getRotation().getRadians());
        turn.setVolts(turnPIDOutput);
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
        return Rotation2d.fromRadians(MathUtil
                .angleModulus(new Rotation2d(cancoder.getPosition() - staticOffset).getRadians()));
    }
}
