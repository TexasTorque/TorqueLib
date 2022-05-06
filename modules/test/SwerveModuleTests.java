package org.texastorque.torquelib.modules.test;

import org.texastorque.torquelib.modules.TorqueSwerveModule2021;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public final class SwerveModuleTests {

    private static void printSMS(final SwerveModuleState[] states) {
        for (final SwerveModuleState state : states)
            System.out.println("> " + state.speedMetersPerSecond);
    }

    public static void main(final String[] args) {
        Rotation2d z = new Rotation2d(0);

        SwerveModuleState[] states = {
            new SwerveModuleState(4.2, z),
            new SwerveModuleState(3.9, z),
            new SwerveModuleState(3.7, z),
            new SwerveModuleState(3.8, z),
        };

        printSMS(states);

        TorqueSwerveModule2021.equalizedDriveRatio(states, 4);

        printSMS(states);
    } 
}
