package org.texastorque.torquelib.modules.test;

public final class SwerveModuleTests {
    public static void main(final String[] args) {
        final double CLICKS_PER_ROTATION = 4096;

        final double set = 90;
        final double pos = 100;

        final double r = Math.IEEEremainder((set * CLICKS_PER_ROTATION / 360.) 
                - pos, CLICKS_PER_ROTATION / 2.) + pos;
        
        System.out.println(r);
    } 
}
