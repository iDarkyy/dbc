package me.idarkyy.dbc.commandframework;

import me.idarkyy.common.utils.ListHashMap;
import me.idarkyy.dbc.bot.Bot;
import me.idarkyy.dbc.commandframework.command.Command;
import me.idarkyy.dbc.commandframework.listener.CommandListener;
import me.idarkyy.dbc.commandframework.wrapper.CommandWrapper;
import me.idarkyy.dbc.commandframework.wrapper.SubcommandWrapper;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {
    private JDA jda;
    private CommandListener listener;

    private String defaultPrefix;
    private HashMap<Guild, String> prefixes = new HashMap<>();

    private HashMap<String, CommandWrapper> commands = new HashMap<>();
    private HashMap<String, CommandWrapper> aliases = new HashMap<>();

    private ListHashMap<CommandWrapper, SubcommandWrapper> subcommands = new ListHashMap<>();

    public CommandManager() {
        this("!");
    }

    public CommandManager(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
        listener = new CommandListener(this);
    }

    public void register(Command command) {
        CommandWrapper wrapper = new CommandWrapper(command);

        if (wrapper.getName() == null) {
            throw new IllegalArgumentException("Command not annotated with @Name(String)");
        }

        commands.put(wrapper.getName(), wrapper);

        for (String alias : wrapper.getAliases()) {
            aliases.put(alias, wrapper);
        }

        List<SubcommandWrapper> sc = wrapper.getSubcommands();
        subcommands.put(wrapper, sc);
    }

    public CommandWrapper findCommand(String name) {
        name = name.toLowerCase();

        if (commands.containsKey(name)) {
            return commands.get(name);
        }

        if (aliases.containsKey(name)) {
            return aliases.get(name);
        }

        return null;
    }

    public SubcommandWrapper findSubcommand(CommandWrapper command, String subcommand) {
        for (SubcommandWrapper sc : subcommands.getOrDefault(command, Collections.emptyList())) {
            if (sc.getName().toLowerCase().equalsIgnoreCase(subcommand)
                    || !Arrays.stream(sc.getAliases()).filter(s -> s.toLowerCase().equalsIgnoreCase(subcommand.toLowerCase())).collect(Collectors.toList()).isEmpty()) {

                return sc;
            }
        }

        return null;
    }

    public void registerListener(JDA jda) {
        jda.addEventListener(listener);
    }

    public void registerListener(JDABuilder jdaBuilder) {
        jdaBuilder.addEventListener(listener);
    }

    public CommandListener getListener() {
        return listener;
    }

    public void setCommandsEnabled(boolean enabled) {
        listener.enabled = enabled;
    }

    public boolean areCommandsEnabled() {
        return listener.enabled;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }

    public String getPrefixForGuild(Guild guild) {
        return prefixes.getOrDefault(guild, getDefaultPrefix());
    }

    public void setPrefixForGuild(Guild guild, String prefix) {
        prefixes.put(guild, prefix);
    }

    public HashMap<Guild, String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(HashMap<Guild, String> prefixes) {
        this.prefixes = prefixes;
    }

    public HashMap<String, CommandWrapper> getCommands() {
        return commands;
    }

    public HashMap<String, CommandWrapper> getAliases() {
        return aliases;
    }

    public ListHashMap<CommandWrapper, SubcommandWrapper> getSubcommands() {
        return subcommands;
    }
}
