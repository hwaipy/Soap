package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.lab;

/**
 *
 * @author Hwaipy
 */
public class Luokan {

  public static void main(String[] args) {
    double length = 2e-3;//why length is 2mm?
//FWHM=2.*pi.*3e+8.*2.0e-9./405e-9.^2;
//thgma=FWHM./(2.*sqrt(2.*log(2)));%泵浦光的高斯分布误差
    double w1 = 405e-9;
    double w2 = 810e-9;
    double w3 = 810e-9;
//%wavelength
    double nx_390 = 3.29100 + 0.04140 / (w1 * w1 * 1e12 - 0.03978) + 9.35522 / (w1 * w1 * 1e12 - 31.45571);
    double ny_390 = 3.45018 + 0.04341 / (w1 * w1 * 1e12 - 0.04597) + 16.98825 / (w1 * w1 * 1e12 - 39.43799);
    double nz_390 = 4.59423 + 0.06206 / (w1 * w1 * 1e12 - 0.04763) + 110.80672 / (w1 * w1 * 1e12 - 86.12171);
//%折射率平方——泵浦光
    double nx_780f = 3.29100 + 0.04140 / (w2 * w2 * 1e12 - 0.03978) + 9.35522 / (w2 * w2 * 1e12 - 31.45571);
    double ny_780f = 3.45018 + 0.04341 / (w2 * w2 * 1e12 - 0.04597) + 16.98825 / (w2 * w2 * 1e12 - 39.43799);
    double nz_780f = 4.59423 + 0.06206 / (w2 * w2 * 1e12 - 0.04763) + 110.80672 / (w2 * w2 * 1e12 - 86.12171);
//%折射率平方——纠缠e光
    double nx_780s = 3.29100 + 0.04140 / (w3 * w3 * 1e12 - 0.03978) + 9.35522 / (w3 * w3 * 1e12 - 31.45571);
    double ny_780s = 3.45018 + 0.04341 / (w3 * w3 * 1e12 - 0.04597) + 16.98825 / (w3 * w3 * 1e12 - 39.43799);
    double nz_780s = 4.59423 + 0.06206 / (w3 * w3 * 1e12 - 0.04763) + 110.80672 / (w3 * w3 * 1e12 - 86.12171);
//%折射率平方——纠缠o光
    double nf1 = Math.sqrt(ny_780f);
    double nl1 = Math.sqrt(nz_780s);
    double nf2 = Math.sqrt(ny_390);
//%nf1为780快光，nf2为390快光，nl1为780慢光
    double kf1 = 2 * Math.PI * nf1 / w2;
    double kl1 = 2 * Math.PI * nl1 / w3;
    double kf2 = 2 * Math.PI * nf2 / w1;
//% kf2data=subs(kf2,w1,w2+w3);
//    double dk = kf2 - kf1 - kl1 + 2 * Math.PI / 1.0113e-5;
    double W = -2 * Math.PI / (kf2 - kf1 - kl1);

    System.out.println(W);
//dk=kf2data-kf1data-kl1data-2.*pi./1.0113e-5;%相位失配量810
//%波矢(wi为真空中的波长)
//wf1=2.*pi.*3e+8./w2;
//wl1=2.*pi.*3e+8./w3;
//wf2=2.*pi.*3e+8./w1;
//wf2data=subs(wf2,w1,405e-9);
//%w
//% dnf1=3e+8./(nf1-diff(nf1,w2).*w2);
//% dnl1=3e+8./(nl1-diff(nl1,w3).*w3);
//% dnf2=3e+8./(nf2-diff(nf2,w1).*w1);
//% %求群速度
//% dnf1data=subs(dnf1,w2,780e-9);
//% dnl1data=subs(dnl1,w3,780e-9);
//% dnf2data=subs(dnf2,w1,390e-9);
//% %赋予平均值
//%%%%%%%%Sinc函数%%%%%%%%%%%%%%%
//keth1=kf2-kf1-kl1;
//% keth2=2.*pi./(4.499e-5);%1560
//keth2=2.*pi./(1.0113e-5);%810
//keth=(keth1-keth2).*length./2./pi;
//% aphla=(exp(-(wf1+wl1-wf2data).^2./(2.*thgma.^2))).^2;%未归一化
//aphla=(exp(-(wf1+wl1-wf2data).^2./(2.*thgma.^2))./(thgma.*sqrt(2.*pi))).^2;%归一化
//% .*4.*log(2)./FWHM.^2.*pi;
//beta=(sinc(keth)).^2;
//final=beta.*aphla;
//w2=805e-9:0.2e-9:815e-9;
//w3=805e-9:0.2e-9:815e-9;
//% w3=1./(1./390e-9-1./w2);
// for i=1:51
//     fprintf('%d\n',i) ;
//     for j=1:51
//         xvar=[w2(i),w3(j)];
//         zfinal(i,j)=pzuotu(xvar);
//         
//     end
// end
//imagesc(zfinal)
  }
}
