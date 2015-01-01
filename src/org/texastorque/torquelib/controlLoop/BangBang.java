package org.texastorque.torquelib.controlLoop;

/**
 * A controller that is either on or off depending on if the setpoint is
 * reached.
 *
 * @author TexasTorque
 */
public class BangBang extends ControlLoop {

    /**
     * Create a new BangBang controller.
     */
    public BangBang() {
        super();
    }

    @Override
    public double calculate(double current) {
        currentValue = current;
        if (currentValue < setPoint) {
            return 1.0;
        } else {
            return 0.0;
        }
    }
}
