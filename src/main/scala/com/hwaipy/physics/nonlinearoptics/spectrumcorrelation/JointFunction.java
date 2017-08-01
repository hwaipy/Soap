package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

/**
 *
 * @author Hwaipy
 */
public class JointFunction extends CorrelationFunction {

    private final CorrelationFunction[] functions;

    public JointFunction(CorrelationFunction... functions) {
        this.functions = functions;
    }

    @Override
    public double correlationValue(double arg1, double arg2) {
        double result = 1;
        for (CorrelationFunction function : functions) {
            result *= function.value(arg1, arg2);
        }
        return result;
    }
}
