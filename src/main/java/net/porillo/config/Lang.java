package net.porillo.config;

import lombok.Getter;
import net.porillo.GlobalWarming;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.text.MessageFormat;

public enum Lang {

    GENERIC_PERMISSION("&cInsufficient Permissions"),

    SCORE_GOAL("&dYour goal is to keep your index above 5"),
    SCORE_INDEX("&dYour carbon footprint index is {0}"),
    SCORE_CARBON("&dYour current carbon footprint is {0}"),
    SCORE_OVERALL("&dYour overall current carbon footprint is {0}")
    ;

    private String def;
    private static LangConfig langConfig;

    Lang(String def) {
        this.def = def;
    }

    public String get() {
        return color(getRaw());
    }

    public String get(Object... args) {
        return color(MessageFormat.format(getRaw(), args));
    }

    public String getRaw() {
        return langConfig.getLangConf().getString(getPath(), getDef());
    }

    public String getPath() {
        return name().replace('_', '.');
    }

    public String getDef() {
        return def;
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }


    private static class LangConfig extends ConfigLoader {

        @Getter private String locale;

        private LangConfig() {
            super("lang.yml");
            super.saveIfNotExist();
            super.load();

            GlobalWarming.getInstance().getLogger().info("Lang Loaded: " + locale);
        }

        private ConfigurationSection getLangConf() {
            return conf.getConfigurationSection("lang");
        }

        @Override
        protected void loadKeys() {
            locale = conf.getString("locale", "en-US");
        }

    }

    public static void init() {
        langConfig = new LangConfig();
    }

}
