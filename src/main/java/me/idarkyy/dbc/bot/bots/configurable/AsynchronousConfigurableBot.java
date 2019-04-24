package me.idarkyy.dbc.bot.bots.configurable;

import me.idarkyy.dbc.bot.bots.AsynchronousStaticBot;
import me.idarkyy.yaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;

public class AsynchronousConfigurableBot extends AsynchronousStaticBot {
    private YamlFile config;

    private AsynchronousConfigurableBot(YamlFile config, Object... listeners) {
        super(config.getString("token"), listeners);

        this.config = config;
    }

    private AsynchronousConfigurableBot(File file, String token, Object... listeners) {
        this(new YamlFile(file), listeners);
    }

    public static AsynchronousConfigurableBot create(File file, Object... listeners) throws IOException {
        YamlFile config = new YamlFile(file);

        if (!config.exists()) {
            config.createNewFile(true);

            config.set("token", "token here");
        }

        return new AsynchronousConfigurableBot(config, listeners);
    }

    public static AsynchronousConfigurableBot of(YamlFile config, Object... listeners) {
        return new AsynchronousConfigurableBot(config, listeners);
    }

    public YamlFile getConfig() {
        return config;
    }
}
