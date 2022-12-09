package com.lu.magic.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.ColorUtils;

import java.util.Locale;

public class ColorUtil {


    public static String toHTMLColor(@ColorInt int color) {
        return String.format("#%06X", 0xFFFFFF & color).toUpperCase(Locale.ROOT);
    }

    public static String toHtmlColor(@ColorInt int color) {
        return String.format("#%06X", 0xFFFFFF & color).toLowerCase(Locale.ROOT);
    }

    public static String toHex8Color(@ColorInt int color) {
        return String.format("#%08X", 0xFFFFFF & color).toLowerCase(Locale.ROOT);
    }

    public static String toHex6Color(@ColorInt int color) {
        return String.format("#%06X", 0xFFFFFF & color).toLowerCase(Locale.ROOT);
    }


    // ---------------------------------------------
    // Delegate: androidx.core.graphics.ColorUtils;
    // ---------------------------------------------


    public static int compositeColors(int foreground, int background) {
        return ColorUtils.compositeColors(foreground, background);
    }

    @NonNull
    @RequiresApi(26)
    public static Color compositeColors(@NonNull Color foreground, @NonNull Color background) {
        return ColorUtils.compositeColors(foreground, background);
    }

    @FloatRange(from = 0.0, to = 1.0)
    public static double calculateLuminance(int color) {
        return ColorUtils.calculateLuminance(color);
    }

    public static double calculateContrast(int foreground, int background) {
        return ColorUtils.calculateContrast(foreground, background);
    }

    public static int calculateMinimumAlpha(int foreground, int background, float minContrastRatio) {
        return ColorUtils.calculateMinimumAlpha(foreground, background, minContrastRatio);
    }

    public static void RGBToHSL(int r, int g, int b, @NonNull float[] outHsl) {
        ColorUtils.RGBToHSL(r, g, b, outHsl);
    }

    public static void colorToHSL(int color, @NonNull float[] outHsl) {
        ColorUtils.colorToHSL(color, outHsl);
    }

    @ColorInt
    public static int HSLToColor(@NonNull float[] hsl) {
        return ColorUtils.HSLToColor(hsl);
    }

    @ColorInt
    public static int setAlphaComponent(int color, int alpha) {
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    public static void colorToLAB(int color, @NonNull double[] outLab) {
        ColorUtils.colorToLAB(color, outLab);
    }

    public static void RGBToLAB(int r, int g, int b, @NonNull double[] outLab) {
        ColorUtils.RGBToLAB(r, g, b, outLab);
    }

    public static void colorToXYZ(int color, @NonNull double[] outXyz) {
        ColorUtils.colorToXYZ(color, outXyz);
    }

    public static void RGBToXYZ(int r, int g, int b, @NonNull double[] outXyz) {
        ColorUtils.RGBToXYZ(r, g, b, outXyz);
    }

    public static void XYZToLAB(double x, double y, double z, @NonNull double[] outLab) {
        ColorUtils.XYZToLAB(x, y, z, outLab);
    }

    public static void LABToXYZ(double l, double a, double b, @NonNull double[] outXyz) {
        ColorUtils.LABToXYZ(l, a, b, outXyz);
    }

    @ColorInt
    public static int XYZToColor(double x, double y, double z) {
        return ColorUtils.XYZToColor(x, y, z);
    }

    @ColorInt
    public static int LABToColor(double l, double a, double b) {
        return ColorUtils.LABToColor(l, a, b);
    }

    public static double distanceEuclidean(@NonNull double[] labX, @NonNull double[] labY) {
        return ColorUtils.distanceEuclidean(labX, labY);
    }

    @ColorInt
    public static int blendARGB(int color1, int color2, float ratio) {
        return ColorUtils.blendARGB(color1, color2, ratio);
    }

    public static void blendHSL(@NonNull float[] hsl1, @NonNull float[] hsl2, float ratio, @NonNull float[] outResult) {
        ColorUtils.blendHSL(hsl1, hsl2, ratio, outResult);
    }

    public static void blendLAB(@NonNull double[] lab1, @NonNull double[] lab2, double ratio, @NonNull double[] outResult) {
        ColorUtils.blendLAB(lab1, lab2, ratio, outResult);
    }
}
