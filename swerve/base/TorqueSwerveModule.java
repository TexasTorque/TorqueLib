/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve.base;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve module base class
 */
public abstract class TorqueSwerveModule {
    protected final String name;
    protected final SwerveConfig config;

    protected TorqueSwerveModule(final String name, final SwerveConfig config) {
        this.name = name.replaceAll(" ", "_").toLowerCase();
        this.config = config;
    }

    public String getPort() {
        return name;
    }

    public abstract void setDesiredState(final SwerveModuleState state);

    public abstract SwerveModuleState getState();

    public abstract SwerveModulePosition getPosition();

    public abstract Rotation2d getRotation();

    public static final class SwervePorts {
        public final int drive, turn, encoder;

        public SwervePorts(final int drive, final int turn, final int encoder) {
            this.drive = drive;
            this.turn = turn;
            this.encoder = encoder;
        }
    }

    /**
     * A structure to define the constants for the swerve module.
     *
     * Has default values that can be overriden before written to
     * the module.
     */
    public static final class SwerveConfig {
        public static final SwerveConfig defaultConfig = new SwerveConfig();
        public static final SwerveConfig abishek = new SwerveConfig();
        public static final SwerveConfig swervexNeo = new SwerveConfig();
        public static final SwerveConfig swervexKraken = new SwerveConfig();

        static {
            // Neo Swerve X
            swervexNeo.driveGearRatio = 6.75;
            swervexNeo.turnGearRatio = 13.71;
            swervexNeo.drivePGain = .1;
            swervexNeo.driveFeedForward = (1. / swervexNeo.maxVelocity);

            // Kraken Swerve X
            swervexKraken.driveGearRatio = 6.75;
            swervexKraken.turnGearRatio = 13.71;
            swervexKraken.drivePGain = .1;
            swervexKraken.maxVelocity = 4.6;
            swervexKraken.driveFeedForward = (1. / swervexKraken.maxVelocity);
            // swervexKraken.driveFeedForward = 1;

            swervexKraken.driveMaxCurrentStator = 35;
            swervexKraken.driveMaxCurrentSupply = 50;

            swervexKraken.driveVelocityFactor = (1.0 / swervexKraken.driveGearRatio / 60.0)
                    * (swervexKraken.wheelDiameter * Math.PI); // m/s
            swervexKraken.drivePoseFactor = (1.0 / swervexKraken.driveGearRatio)
                    * (swervexKraken.wheelDiameter * Math.PI); // m
        }

        public double magic = 6.57 / (8.0 + 1.0 / 3.0);

        public int driveMaxCurrentSupply = 35, // amps
                driveMaxCurrentStator = 35, // amps
                turnMaxCurrent = 25; // amps
        public double voltageCompensation = 12.6, // volts
                maxVelocity = 4.6, // m/s
                maxAcceleration = 3.0, // m/s^2
                maxAngularVelocity = Math.PI, // radians/s
                maxAngularAcceleration = Math.PI, // radians/s

                // The following will most likely need to be overriden
                // depending on the weight of each robot
                driveStaticGain = 0.015, driveFeedForward = 0.2485, drivePGain = 0.1, driveIGain = 0.0,
                driveDGain = 0.0,

                driveRampRate = 3.0, // %power/s
                driveGearRatio = 6.75, // Translation motor to wheel
                wheelDiameter = 4.0 * 0.0254, // m
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI), // m/s
                drivePoseFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI), // m
                turnPGain = 0.5, turnIGain = 0.0, turnDGain = 0.0,
                turnGearRatio = 12.41; // Rotation motor to wheel
    }

    protected double log(final String item, final double value) {
        final String key = name + "." + item.replaceAll(" ", "_").toLowerCase();
        SmartDashboard.putNumber(String.format("%s::%s", name, key), value);
        return value;
    }
}
