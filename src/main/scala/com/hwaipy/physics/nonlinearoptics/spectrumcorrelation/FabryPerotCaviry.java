package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

/**
 *
 * @author Hwaipy
 */
public class FabryPerotCaviry implements Filter {

    private final double R;
    private final double l;
    private final double F;
    private final double finesse;

    public FabryPerotCaviry(double R, double l) {
        this.R = R;
        this.l = l;
        F = 4 * R / Math.pow(1 - R, 2);
        finesse = Math.PI / 2 / Math.asin(1 / Math.sqrt(F));
    }

    public double getFSR(double lamda0) {
        return Math.pow(lamda0, 2) / 1e9 / 2 / l;
    }

    @Override
    public double transmittance(double lamda) {
        double delta = 4 * Math.PI * l / (lamda / 1e9);
        double T = 1 / (1 + F * Math.pow(Math.sin(delta / 2), 2));
        return T;
    }
//    public static void main(String[] args) {
//        FabryPerotCaviry fpc = new FabryPerotCaviry(0.855, 0.2535 / 1000, true);
//        System.out.println(fpc.F);
//        System.out.println(fpc.finesse);
//        System.out.println(fpc.getFSR(390));
//        for (double lamda = 390; lamda < 390.3; lamda += 0.0001) {
//            System.out.println(lamda + "\t" + fpc.transmittance(lamda));
//        }
//    }
}
