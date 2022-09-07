package org.texastorque.torquelib.util;

public final class TorqueUnit {
    public static class Unit {
		public final double coef;

		Unit(final double coef) {
			this.coef = coef;
		}

        public final Unit per(final Unit unit) {
            return new Unit(coef / unit.coef);
        }

		public final Unit to(final Unit unit) {
			return new Unit(coef * unit.coef);
		}

        public final double get() { return coef; }

        public final double calc(final double value) {
            return value * coef;
        }
	}

    public static final Unit 
                METER = new Unit(1.),
				MILIMETER = new Unit(1000.), 
				KILOMETER = new Unit(1./1000.),

				SECOND = new Unit(1.),
				MINUTE = new Unit(1./60.), 
				HOUR = new Unit(1./60./60.),

				RADIAN = new Unit(1.),
				DEGREES = new Unit(Math.PI / 180.),
				ROTATION = new Unit(Math.PI * 2.),

                METER_PER_SECOND = METER.per(SECOND),
                METER_PER_SECOND_PER_SECOND = METER_PER_SECOND.per(SECOND),

                RADIAN_PER_SECOND = RADIAN.per(SECOND),
                RADIAN_PER_SECOND_PER_SECOND = RADIAN_PER_SECOND.per(SECOND),
                
                DEGREES_PER_SECOND = DEGREES.per(SECOND),
                DEGREES_PER_SECOND_PER_SECOND = DEGREES_PER_SECOND.per(SECOND),

                ROTATION_PER_SECOND = ROTATION.per(SECOND),
                ROTATION_PER_SECOND_PER_SECOND = ROTATION_PER_SECOND.per(SECOND);

	public static final void main(final String[] args) {
		System.out.println(METER.per(SECOND).to(KILOMETER.per(MINUTE)).calc(10));
	}

    
}
