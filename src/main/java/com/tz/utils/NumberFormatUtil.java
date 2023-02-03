package com.tz.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author lxn
 * @date 2022年12月12日 10:32
 */
public class NumberFormatUtil {

    public static String update(Double value) {
        BigDecimal bg = new BigDecimal(value + "");
        return value != null ? bg.setScale(2, RoundingMode.UP).toString().replace(".00", "") : "0";
    }

    static double div(double a1, double b1, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("error");

        }
        BigDecimal a2 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(b1));
        return a2.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    static String test(Double value) {
        if (value > 1) {
            DecimalFormat format2 = new DecimalFormat("#.00");
            return format2.format(value);
        } else {
            BigDecimal bg = new BigDecimal(value + "");
            return bg.setScale(2, RoundingMode.UP).toString();
        }
    }

}
