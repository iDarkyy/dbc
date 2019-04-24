package me.idarkyy.dbc.commandframework.command;

import me.idarkyy.dbc.commandframework.event.CommandEvent;

public interface Command {
    void execute(CommandEvent event);
}
