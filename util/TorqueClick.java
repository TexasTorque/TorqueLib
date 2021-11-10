package org.texastorque.torquelib.util;

public class TorqueClick {
	private boolean lastValue;

	public TorqueClick() {
		lastValue = false;
    }

    public boolean calc(boolean current) {
        if(current) {
            if(lastValue != current) {
                lastValue = current;
                return true;
            }
        }
        lastValue = current;
        return false;
    }

}
