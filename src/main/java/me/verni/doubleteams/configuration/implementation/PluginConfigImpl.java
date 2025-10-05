package me.verni.doubleteams.configuration.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import me.verni.doubleteams.database.DatabaseType;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Header("# DoubleTeams Configuration File")
public class PluginConfigImpl extends OkaeriConfig {

    public Database database = new Database();
    public RatingSettings rating = new RatingSettings();

    public static class Database extends OkaeriConfig {
        public DatabaseType type = DatabaseType.SQLITE;
        public String host = "localhost";
        public String password = "password";
        public String username = "username";
        public String database = "teams";
        public int port = 3306;
        public boolean useSSL = false;
    }

    public static class RatingSettings extends OkaeriConfig {
        public boolean enabled = true;
        public int cooldownSeconds = 300; // 5 minut
        public String killerMessage = "&aZabiłeś gracza {victim}! &7(Rating: &b{new_rating} &a+{points_change}&7)";
        public String victimMessage = "&cZostałeś zabity przez {killer}! &7(Rating: &b{new_rating} &c-{points_change}&7)";
        public String cooldownMessage = "&cZabiłeś tego gracza niedawno, punkty nie zostały przyznane.";
    }
}