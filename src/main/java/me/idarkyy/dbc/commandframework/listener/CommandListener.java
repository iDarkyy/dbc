package me.idarkyy.dbc.commandframework.listener;

import me.idarkyy.dbc.commandframework.CommandManager;
import me.idarkyy.dbc.commandframework.event.CommandEvent;
import me.idarkyy.dbc.commandframework.event.SubcommandEvent;
import me.idarkyy.dbc.commandframework.wrapper.CommandWrapper;
import me.idarkyy.dbc.commandframework.wrapper.SubcommandWrapper;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;

public class CommandListener extends ListenerAdapter {
    public boolean enabled = true;

    private CommandManager commandManager;

    public CommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!enabled) {
            return;
        }

        String prefix = commandManager.getPrefixForGuild(event.getGuild());

        String stripped = event.getMessage().getContentStripped();

        if (!stripped.startsWith(prefix)) {
            return;
        }

        String[] split = stripped.substring(prefix.length()).split(" ");

        CommandWrapper command = commandManager.findCommand(split[0]);

        if(command == null) {
            return;
        }

        SubcommandWrapper subcommand;

        if(split.length > 1 && (subcommand = commandManager.findSubcommand(command, split[1])) != null) {
            if(!event.getMember().hasPermission(subcommand.getPermissions())) {
                event.getChannel().sendMessage(buildPermissionMessage(subcommand.getPermissions())).queue();
                return;
            }

            SubcommandEvent evt = new SubcommandEvent(commandManager,
                    prefix,
                    command,
                    subcommand,
                    Arrays.copyOfRange(split, 2, split.length),
                    event.getAuthor(),
                    event.getMember(),
                    event.getMessage(),
                    event.getChannel());

            subcommand.invoke(evt);
        } else {
            CommandEvent evt = new CommandEvent(commandManager,
                    prefix,
                    command,
                    Arrays.copyOfRange(split, 1, split.length),
                    event.getAuthor(),
                    event.getMember(),
                    event.getMessage(),
                    event.getChannel());

            command.getCommand().execute(evt);
        }
    }

    private MessageEmbed buildPermissionMessage(Permission... permissions) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setDescription("You are missing the following permissions: " + String.join(", ", toStringArray(permissions)));

        return embed.build();
    }

    private String[] toStringArray(Permission[] permissions) {
        String[] arr = new String[permissions.length - 1];

        for (int i = 0; i < permissions.length; i++) {
            arr[i] = permissions[i].getName();
        }

        return arr;
    }
}
