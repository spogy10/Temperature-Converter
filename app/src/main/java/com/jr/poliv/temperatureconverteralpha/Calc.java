package com.jr.poliv.temperatureconverteralpha;

/**
 * Created by poliv on 5/8/2016.
 */
public class Calc {

    static double F_to_C(double var)
    {
        double result;
        result = (var-32) / 1.8;
        return result;
    }
    static double C_to_F(double var)
    {
        double result;
        result = (var * 1.8) + 32;
        return result;
    }
    static double K_to_C(double var)
    {
        double result;
        result = var - 273.15;
        return result;
    }
    static double C_to_K(double var)
    {
        double result;
        result = var + 273.15;
        return result;
    }
    static double F_to_K(double var)
    {
        double result;
        result = ((var - 32) / 1.8) + 273.15;
        return result;
    }
    static double K_to_F(double var)
    {
        double result;
        result = ((var - 273.15) * 1.8) + 32;
        return result;
    }
}
