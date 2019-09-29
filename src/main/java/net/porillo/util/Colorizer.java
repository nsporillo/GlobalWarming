package net.porillo.util;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;

import static org.bukkit.ChatColor.*;

public class Colorizer {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static String formatIndex(double index, int score) {
        return String.format("%s%s",
                Colorizer.getScoreColor(score),
                decimalFormat.format(index));
    }

    public static String formatScore(int score) {
        return String.format("%s%d", Colorizer.getScoreColor(score), score);
    }

    public static String formatTemp(double temp) {
        return String.format("%s%s", getTemperatureColor(temp), decimalFormat.format(temp));
    }

    /**
     * Get the color associated with a carbon score
     * - Values are mapped to color-heat from LOW CO2 (cold) to HIGH CO2 (hot)
     * - These ranges are somewhat arbitrary
     */
    public static ChatColor getScoreColor(int score) {
        ChatColor color;
        if (score <= -3500) {
            color = DARK_BLUE;
        } else if (score <= -2500) {
            color = BLUE;
        } else if (score <= -1500) {
            color = DARK_AQUA;
        } else if (score <= -500) {
            color = AQUA;
        } else if (score <= 500) {
            color = GREEN; // (-500, 500]
        } else if (score <= 1500) {
            color = YELLOW;
        } else if (score <= 2500) {
            color = GOLD;
        } else if (score <= 3500) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        return color;
    }

    /**
     * Get the color associated with a temperature
     * - These ranges are somewhat arbitrary
     */
    public static ChatColor getTemperatureColor(double temperature) {
        ChatColor color;
        if (temperature <= 10.5) {
            color = DARK_BLUE;
        } else if (temperature <= 11.5) {
            color = BLUE;
        } else if (temperature <= 12.5) {
            color = DARK_AQUA;
        } else if (temperature <= 13.5) {
            color = AQUA;
        } else if (temperature <= 14.5) {
            color = GREEN; // (13.5, 14.5]
        } else if (temperature <= 15.5) {
            color = YELLOW;
        } else if (temperature <= 16.5) {
            color = GOLD;
        } else if (temperature <= 17.5) {
            color = LIGHT_PURPLE;
        } else if (temperature <= 18.5) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        return color;
    }
}
