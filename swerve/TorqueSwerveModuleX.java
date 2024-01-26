/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve;

import org.texastorque.Debug;
import org.texastorque.torquelib.motors.TorqueNEO;
import com.ctre.phoenix6.hardware.CANcoder;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;
import com.revrobotics.CANSparkBase;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * West Coast Products Swerve X Module.
 *
 * @author Omar Afzal
 */
public final class TorqueSwerveModuleX extends TorqueSwerveModule {
     /**
     * A structure to define the constants for the swerve module.
     *
     * Has default values that can be overriden before written to
     * the module.
     */
    public static final class SwerveConfig {
        public static final SwerveConfig defaultConfig = new SwerveConfig();

        public int driveMaxCurrent = 35, turnMaxCurrent = 25;

        public final double drivePGain = .1, driveIGain = 0, driveDGain = .01, driveFF = 0.2,
                driveGearRatio = 6.75, wheelDiameter = 0.1016,
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI),
                drivePosFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI),
                NEOFreeSpeedRPS = 5676 * 0.9 / 60,
                driveWheelFreeSpeedRps = (NEOFreeSpeedRPS * wheelDiameter * Math.PI) / driveGearRatio,
                driveMaxSpeed = 4.6, turnPGain = 1.5, turnIGain = 0, turnDGain = 0,
                turnGearRatio = 13.71;
    }

    private final TorqueNEO drive, turn;

    private final CANcoder cancoder;

    private final PIDController turnPID;

    public final String name;

    public TorqueSwerveModuleX(final String name, final SwervePorts ports, final SwerveConfig config) {
        super(ports.drive);
        this.name = name.replaceAll(" ", "_").toLowerCase();

        drive = new TorqueNEO(ports.drive);
        drive.setCurrentLimit(driveMaxCurrent);
        drive.setVoltageCompensation(12.6);
        drive.setBreakMode(true);
        drive.invertMotor(true);
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

        cancoder = new CANcoder(ports.encoder);

        turnPID = new PIDController(turnPGain, turnIGain, turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
    }

    public void setDesiredState(final SwerveModuleState state) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        if (DriverStation.isAutonomous())
            drive.setPIDReference(optimized.speedMetersPerSecond, CANSparkBase.ControlType.kVelocity);
        else
            drive.setPercent(optimized.speedMetersPerSecond / driveMaxSpeed);
            
        final double turnPIDOutput = -turnPID.calculate(getRotation().getRadians(), optimized.angle.getRadians());

        turn.setVolts(turnPIDOutput);
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(-drive.getPosition(), getRotation());
    }

    @Override
    public Rotation2d getRotation() {
        double absAngle = Math.toRadians(cancoder.getAbsolutePosition().getValue() * 360);
        absAngle %= 2.0 * Math.PI;
        if (absAngle < 0.0) {
            absAngle += 2.0 * Math.PI;
        }

        return Rotation2d.fromRadians(MathUtil.angleModulus(absAngle));
    }
}
