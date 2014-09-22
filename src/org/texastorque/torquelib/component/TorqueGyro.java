package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AccumulatorResult;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Timer;

public class TorqueGyro {

    static final int kOversampleBits = 10;
    static final int kAverageBits = 0;
    static final double kSamplesPerSecond = 50.0;
    static final double kCalibrationSampleTime = 5.0;
    static final double kDefaultVoltsPerDegreePerSecond = 0.007;

    double m_voltsPerDegreePerSecond;

    double m1_offset;
    int m1_center;
    boolean m1_channelAllocated = false;
    private AnalogInput m1_analog;
    AccumulatorResult result1;

    double m2_offset;
    int m2_center;
    boolean m2_channelAllocated = false;
    private AnalogInput m2_analog;
    AccumulatorResult result2;

    public TorqueGyro(int port1, int port2) {
        m1_analog = new AnalogInput(port1);
        m1_channelAllocated = true;

        m2_analog = new AnalogInput(port2);
        m2_channelAllocated = true;
    }

    private void initGyro() {
        m_voltsPerDegreePerSecond = kDefaultVoltsPerDegreePerSecond;

        result1 = new AccumulatorResult();

        m1_analog.setAverageBits(kAverageBits);
        m1_analog.setOversampleBits(kOversampleBits);
        double sampleRate = kSamplesPerSecond
                * (1 << (kAverageBits + kOversampleBits));
        AnalogInput.setGlobalSampleRate(sampleRate);
        Timer.delay(1.0);

        m1_analog.initAccumulator();
        m1_analog.resetAccumulator();

        Timer.delay(kCalibrationSampleTime);

        m1_analog.getAccumulatorOutput(result1);

        m1_center = (int) ((double) result1.value / (double) result1.count + .5);

        m1_offset = ((double) result1.value / (double) result1.count)
                - m1_center;

        m1_analog.setAccumulatorCenter(m1_center);
        m1_analog.resetAccumulator();

        setDeadband(0.005);
    }

    public void free() {
        if (m1_analog != null && m1_channelAllocated) {
            m1_analog.free();
        }
        m1_analog = null;

        if (m2_analog != null && m2_channelAllocated) {
            m2_analog.free();
        }
        m2_analog = null;
    }

    public void reset() {
        if (m1_analog != null) {
            m1_analog.resetAccumulator();
        }
        if (m2_analog != null) {
            m2_analog.resetAccumulator();
        }
    }

    public void setDeadband(double volts) {
        int deadband = (int) (volts * 1e9 / m1_analog.getLSBWeight() * (1 << m1_analog.getOversampleBits()));
        m1_analog.setAccumulatorDeadband(deadband);

        deadband = (int) (volts * 1e9 / m2_analog.getLSBWeight() * (1 << m2_analog.getOversampleBits()));
        m2_analog.setAccumulatorDeadband(deadband);
    }

    public void setSensitivity(double voltsPerDegreePerSecond) {
        m_voltsPerDegreePerSecond = voltsPerDegreePerSecond;
    }

    public double getAngle() {
        if (m1_analog == null || m2_analog == null) {
            return 0.0;
        } else {
            m1_analog.getAccumulatorOutput(result1);

            long value1 = result1.value - (long) (result1.count * m1_offset);

            double scaledValue1 = value1
                    * 1e-9
                    * m1_analog.getLSBWeight()
                    * (1 << m1_analog.getAverageBits())
                    / (AnalogInput.getGlobalSampleRate() * m_voltsPerDegreePerSecond);

            long value2 = result2.value - (long) (result2.count * m2_offset);

            double scaledValue2 = - 1 * value2
                    * 1e-9
                    * m2_analog.getLSBWeight()
                    * (1 << m2_analog.getAverageBits())
                    / (AnalogInput.getGlobalSampleRate() * m_voltsPerDegreePerSecond);

            return (scaledValue1 + scaledValue2) / 2;
        }
    }
    
    public double getRate() {
        if (m1_analog == null || m2_analog == null) {
            return 0.0;
        } else {
            double rate1 = (m1_analog.getAverageValue() - (m1_center + m1_offset))
                    * 1e-9
                    * m1_analog.getLSBWeight()
                    / ((1 << m1_analog.getOversampleBits()) * m_voltsPerDegreePerSecond);
            
            double rate2 = (m1_analog.getAverageValue() - (m1_center + m1_offset))
                    * 1e-9
                    * m1_analog.getLSBWeight()
                    / ((1 << m1_analog.getOversampleBits()) * m_voltsPerDegreePerSecond);
            
            return (rate1 + rate2) / 2;
        }
    }
}
