package me.idarkyy.dbc.commandframework.event;

import me.idarkyy.dbc.commandframework.CommandManager;
import me.idarkyy.dbc.commandframework.wrapper.CommandWrapper;
import me.idarkyy.dbc.commandframework.wrapper.SubcommandWrapper;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SubcommandEvent {
    private CommandManager commandManager;

    private String prefix;
    private CommandWrapper command;
    private SubcommandWrapper subcommand;
    private String[] args;

    private User user;
    private Member member;

    private Message message;
    private TextChannel textChannel;

    public SubcommandEvent(CommandManager commandManager, String prefix, CommandWrapper command, SubcommandWrapper subcommand, String[] args, User user, Member member, Message message, TextChannel textChannel) {
        this.commandManager = commandManager;
        this.prefix = prefix;
        this.command = command;
        this.subcommand = subcommand;
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

    public SubcommandWrapper getSubcommand() {
        return subcommand;
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
