package me.idarkyy.dbc.bot.bots.configurable;

import me.idarkyy.dbc.bot.bots.StaticBot;
import me.idarkyy.yaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

public class ConfigurableBot extends StaticBot {
    private YamlFile config;

    private ConfigurableBot(YamlFile config, Object... listeners) {
        super(config.getString("token"), listeners);

        this.config = config;
    }

    private ConfigurableBot(File file, String token, Object... listeners) {
        this(new YamlFile(file), listeners);
    }

    public static ConfigurableBot create(File file, Object... listeners) throws IOException {
        YamlFile config = new YamlFile(file);

        if(!config.exists()) {
            config.createNewFile(true);

            config.set("token", "token here");
        }

        return new ConfigurableBot(config, listeners);
    }

    public static ConfigurableBot of(YamlFile config, Object... listeners) {
        return new ConfigurableBot(config, listeners);
    }

    public YamlFile getConfig() {
        return config;
    }
}
