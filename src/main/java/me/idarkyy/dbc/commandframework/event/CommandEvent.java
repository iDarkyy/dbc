package me.idarkyy.dbc.commandframework.event;

import me.idarkyy.dbc.commandframework.CommandManager;
import me.idarkyy.dbc.commandframework.wrapper.CommandWrapper;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandEvent {
    private CommandManager commandManager;

    private String prefix;
    private CommandWrapper command;
    private String[] args;

    private User user;
    private Member member;

    private Message message;
    private TextChannel textChannel;

    public CommandEvent(CommandManager commandManager, String prefix, CommandWrapper command, String[] args, User user, Member member, Message message, TextChannel textChannel) {
        this.commandManager = commandManager;
        this.prefix = prefix;
        this.command = command;
        this.args = args;
        this.user = user;
        this.member = member;
        this.message = message;
        this.textChannel = textChannel;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public CommandWrapper getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    public User getUser() {
        return user;
    }

    public Member getMember() {
        return member;
    }

    public Message getMessage() {
        return message;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }
}
