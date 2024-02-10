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
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

/**
 * West Coast Products Swerve X Module.
 * 
 * @deprecated This doesnt work and we dont know why -Jus
 *
 * @author Omar Afzal
 * @author Davis Jenney
 */
@Deprecated
public final class TorqueSwerveModuleX extends TorqueSwerveModule {

    public static record PIDConfig(double p, double i, double d, double ff) {
    }

    /**
     * A structure to define the constants for the swerve module.
     *
     * Has default values that can be overriden before written to
     * the module.
     */
    public static final class SwerveConfig {
        public static final SwerveConfig defaultConfig = new SwerveConfig();

        public int maxDriveCurrent = 35, maxTurnCurrent = 25;

        public PIDConfig drivePID = new PIDConfig(.1, 0, .01, 0.2);
        public PIDConfig turnPID = new PIDConfig(1.5, 0, 0, 0);

        public double wheelDiameter = 4.0 * 0.0254;

        public double maxDriveSpeed = 4.6;

        public double driveGearRatio = 6.75, turnGearRatio = 13.71;

        private final double neoFreeSpeedRPS = 5676 * 0.9 / 60;

        private double getVeloFactor() {
            return (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI);
        }

        private double getPosFactor() {
            return (1.0 / driveGearRatio) * (wheelDiameter * Math.PI);
        }

        private double getWheelFreeSpeed() {
            return (neoFreeSpeedRPS * wheelDiameter * Math.PI) / driveGearRatio;
        }
    }

    private final TorqueNEO drive, turn;

    private final CANcoder cancoder;

    private final PIDController turnPID;

    public final String name;

    private final SwerveConfig config;

    public TorqueSwerveModuleX(final String name, final SwervePorts ports, final SwerveConfig config) {
        super(ports.drive);

        this.config = config;

        this.name = name.replaceAll(" ", "_").toLowerCase();

        drive = new TorqueNEO(ports.drive);
        drive.setCurrentLimit(config.maxDriveCurrent);
        drive.setVoltageCompensation(12.6);
        drive.setBreakMode(true);
        drive.invertMotor(false);
        drive.setConversionFactors(config.getPosFactor(), config.getVeloFactor());

        drive.setPIDFeedbackDevice(drive.encoder);
        drive.configurePIDF(config.drivePID.p, config.drivePID.i, config.drivePID.d, 1 / config.getWheelFreeSpeed());
        drive.burnFlash();

        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(config.turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(config.maxTurnCurrent);
        turn.setVoltageCompensation(12.6);
        turn.setBreakMode(true);
        turn.burnFlash();

        cancoder = new CANcoder(ports.encoder);

        turnPID = new PIDController(config.turnPID.p, config.turnPID.i, config.turnPID.d);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
    }

    public void setDesiredState(final SwerveModuleState state) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        // Debug.log(name + " req speed", optimized.speedMetersPerSecond);
        // Debug.log(name + " actual speed", drive.getVelocity());

        if (DriverStation.isAutonomous())
            drive.setPIDReference(optimized.speedMetersPerSecond, CANSparkBase.ControlType.kVelocity);
        else
            drive.setPercent(optimized.speedMetersPerSecond / config.maxDriveSpeed);

        final double turnPIDOutput = -turnPID.calculate(getRotation().getRadians(), optimized.angle.getRadians());

        turn.setVolts(turnPIDOutput);

        prevState = optimized;
    }

    // THIS IS SIMULATION STUFF
    private SwerveModuleState prevState = new SwerveModuleState();
    private double prevTime = -1, sum = 0;

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }

    public SwerveModulePosition getPosition() {
        if (!RobotBase.isReal()) {
            final double time = Timer.getFPGATimestamp();
            sum += prevTime == -1 ? prevState.speedMetersPerSecond * (time - prevTime) : 0;
            prevTime = time;
            return new SwerveModulePosition(sum, prevState.angle);
        }
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
