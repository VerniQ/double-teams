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

    public static class Database {
        public DatabaseType type = DatabaseType.SQLITE;
        public String host = "localhost";
        public String password = "password";
        public String username = "username";
        public String database = "teams";
        public int port = 3306;
        public boolean useSSL = false;
    }
}
