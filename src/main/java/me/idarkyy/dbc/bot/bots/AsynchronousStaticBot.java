package me.idarkyy.dbc.bot.bots;

import me.idarkyy.dbc.bot.Bot;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class AsynchronousStaticBot implements Bot {
    private JDABuilder builder;
    private JDA jda;

    public AsynchronousStaticBot(String token, Object... listeners) {
        builder = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(listeners);
    }

    public void addListener(Object... listeners) {
        builder.addEventListener(listeners);
    }

    @Override
    public void launch() throws LoginException {
        if (jda != null) {
            throw new IllegalStateException("The bot is already started");
        }

        jda = builder.buildAsync();
    }

    @Override
    @SuppressWarnings("all")
    public void shutdown(boolean force) {
        if(jda == null) {
            throw new IllegalStateException("The bot is not started");
        }

        if(force) {
            jda.shutdownNow();
        } else {
            jda.shutdown();
        }

        jda = null;
    }

    @Override
    public JDA getJda() {
        return jda;
    }
}
