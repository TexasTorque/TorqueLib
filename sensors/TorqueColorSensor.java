/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.sensors;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import org.texastorque.torquelib.util.TorqueUtil;

/**
 * @author Omar Afzal
 * @author Justus Languell
 */
public final class TorqueColorSensor extends ColorSensorV3 {

    private final ColorMatch matcher;

    public TorqueColorSensor() {
        super(I2C.Port.kOnboard);
        matcher = new ColorMatch();
    }

    public TorqueColorSensor(final Color... colors) {
        this();
        for (final Color color : colors) addDetectableColor(color);
    }

    public final void addDetectableColor(final Color color) { matcher.addColorMatch(color); }

    public final Color getClosestColor() { return matcher.matchClosestColor(getColor()).color; }
}
