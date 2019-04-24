package me.idarkyy.dbc.bot.bots.configurable;

import me.idarkyy.dbc.bot.bots.AsynchronousStaticBot;
import me.idarkyy.yaml.configuration.file.YamlConfiguration;
import me.idarkyy.yaml.configuration.file.YamlConfigurationOptions;
import me.idarkyy.yaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

public class AsynchronousConfigurableBot extends AsynchronousStaticBot {
    private YamlConfiguration config;

    private AsynchronousConfigurableBot(YamlConfiguration config, Object... listeners) {
        super(config.getString("token"), listeners);

        this.config = config;
    }

    private AsynchronousConfigurableBot(File file, String token, Object... listeners) {
        this(YamlConfiguration.loadConfiguration(file), listeners);
    }

    public static AsynchronousConfigurableBot create(File file, Object... listeners) throws IOException {
        YamlConfiguration config = null;

        if (!file.exists()) {
            file.createNewFile();

            config = YamlConfiguration.loadConfiguration(file);

            config.set("token", "token here");
            config.save(file);
        }

        if(config == null) {
            config = YamlConfiguration.loadConfiguration(file);
        }

        return new AsynchronousConfigurableBot(config, listeners);
    }

    public static AsynchronousConfigurableBot of(YamlFile config, Object... listeners) {
        return new AsynchronousConfigurableBot(config, listeners);
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
