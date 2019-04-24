package me.idarkyy.dbc.bot;

import net.dv8tion.jda.core.JDA;

import javax.security.auth.login.LoginException;

public interface Bot {
    void launch() throws LoginException;

    void shutdown(boolean force);

    JDA getJda();

    // TODO
    //  void includeMusicManager(MusicManager musicManager);
}
