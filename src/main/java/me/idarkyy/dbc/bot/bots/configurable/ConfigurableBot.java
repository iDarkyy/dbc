package me.idarkyy.dbc.bot.bots.configurable;

import me.idarkyy.dbc.bot.bots.StaticBot;
import me.idarkyy.yaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigurableBot extends StaticBot {
    private YamlConfiguration config;

    private ConfigurableBot(YamlConfiguration config, Object... listeners) {
        super(config.getString("token"), listeners);

        this.config = config;
    }

    private ConfigurableBot(File file, String token, Object... listeners) {
        this(YamlConfiguration.loadConfiguration(file), listeners);
    }

    public static ConfigurableBot create(File file, Object... listeners) throws IOException {
        YamlConfiguration config = null;

        if (!file.exists()) {
            file.createNewFile();

            config = YamlConfiguration.loadConfiguration(file);

            config.set("token", "token here");
            config.save(file);
            file.exists(); // just to remove the duplicate warning
        }

        if(config == null) {
            config = YamlConfiguration.loadConfiguration(file);
        }

        return new ConfigurableBot(config, listeners);
    }

    public static ConfigurableBot of(YamlConfiguration config, Object... listeners) {
        return new ConfigurableBot(config, listeners);
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
