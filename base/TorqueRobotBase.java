/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

// If you do not have oblog make sure your build.gradle is as follows:
// https://raw.githubusercontent.com/TexasTorque/Swerve-2023/9df7698cb69a6655d90583ae314c6a44a94c2045/build.gradle
import java.util.ArrayList;

import org.texastorque.torquelib.auto.TorqueAutoManager;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import io.github.oblarg.oblog.Logger;

/**
 * A replacment for TorqueIterative.
 *
 * TorqueIterative was a modified version of the WPILIBJ IterativeRobot template.
 * It was created to improve upon the performance by utalizing two threads on the RoboRio.
 * However, it has neither been maintained nor updated since it's inception, and has
 * fallen out of date with the TimedRobot, IterativeRobotBase, and RobotBase standard.
 *
 * Because of this, I (Justus) am working on a replacement class called TorqueRobotBase.
 *
 * Although I may start working on drafts, I cannot start serious work unitl after our
 * next competition, the Texas Robotics Invitational, which we have but only 8 meetings
 * at 3 hours a piece to do way too much.
 *
 * @author Justus Languell
 */
public class TorqueRobotBase extends TimedRobot {
    private static final double HERTZ = 50;
    public static final double PERIOD = 1. / HERTZ;

    private final TorqueInput input;
    private final TorqueAutoManager autoManager;

    private final ArrayList<TorqueSubsystem> subsystems = new ArrayList<TorqueSubsystem>();

    public TorqueRobotBase(final TorqueInput input, final TorqueAutoManager autoManager) {
        this(input, autoManager, PERIOD);
    }

    public TorqueRobotBase(final TorqueInput input, final TorqueAutoManager autoManager, final double period) {
        super(PERIOD);
        this.input = input;
        this.autoManager = autoManager;
    }

    public final void addSubsystem(final TorqueSubsystem subsystem) { subsystems.add(subsystem); }

    @Override
    public final void robotInit() {
        Logger.setCycleWarningsEnabled(true);
        for (final TorqueSubsystem subsystem : subsystems) Logger.configureLoggingAndConfig(subsystem, false);
    }

    @Override
    public final void robotPeriodic() {
        Shuffleboard.update();
        Logger.updateEntries();
    }

    @Override
    public final void disabledInit() {
        // This makes no sense
        // subsystems.forEach(subsystem -> subsystem.initialize(TorqueMode.DISABLED));
    }

    @Override
    public final void disabledPeriodic() {
        // This makes no sense
        // subsystems.forEach(subsystem -> subsystem.update(TorqueMode.DISABLED));
    }

    @Override
    public final void teleopInit() {
        subsystems.forEach(subsystem -> subsystem.initialize(TorqueMode.TELEOP));
    }

    @Override
    public final void teleopPeriodic() {
        input.update();
        subsystems.forEach(subsystem -> subsystem.update(TorqueMode.TELEOP));
    }

    @Override
    public final void autonomousInit() {
        autoManager.chooseCurrentSequence();
        subsystems.forEach(subsystem -> subsystem.initialize(TorqueMode.AUTO));
    }

    @Override
    public final void autonomousPeriodic() {
        autoManager.runCurrentSequence();
        subsystems.forEach(subsystem -> subsystem.update(TorqueMode.AUTO));
    }

    @Override
    public final void testInit() {
        subsystems.forEach(subsystem -> subsystem.initialize(TorqueMode.TEST));
    }

    @Override
    public final void testPeriodic() {
        input.update();
        subsystems.forEach(subsystem -> subsystem.update(TorqueMode.TEST));
    }

    @Override
    public final void endCompetition() {}
}
