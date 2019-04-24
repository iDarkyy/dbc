package me.idarkyy.dbc.commandframework.wrapper;

import me.idarkyy.dbc.commandframework.annotations.*;
import me.idarkyy.dbc.commandframework.command.Command;
import net.dv8tion.jda.core.Permission;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandWrapper {
    private Command command;

    private List<SubcommandWrapper> subcommands;

    public CommandWrapper(Command command) {
        this.command = command;
    }

    public String getName() {
        if(!command.getClass().isAnnotationPresent(Name.class)) {
            return null;
        }

        return command.getClass().getAnnotation(Name.class).value().toLowerCase();
    }

    public String[] getAliases() {
        if(!command.getClass().isAnnotationPresent(Aliases.class)) {
            return new String[]{};
        }

        return command.getClass().getAnnotation(Aliases.class).value();
    }

    public String getDescription() {
        if(!command.getClass().isAnnotationPresent(Description.class)) {
            return null;
        }

        return command.getClass().getAnnotation(Description.class).value();
    }

    public Permission[] getPermissions() {
        if(!command.getClass().isAnnotationPresent(Permissions.class)) {
            return Permission.EMPTY_PERMISSIONS;
        }

        return command.getClass().getAnnotation(Permissions.class).value();
    }

    public List<SubcommandWrapper> getSubcommands() {
        if(subcommands == null) {
            subcommands = new ArrayList<>();

            for(Method method : Arrays.stream(command.getClass().getMethods())
                    .filter(m -> m.isAnnotationPresent(Subcommand.class))
                    .collect(Collectors.toList())) {

                SubcommandWrapper wrapper = new SubcommandWrapper(method, this);

                if(wrapper.validate()) {
                    subcommands.add(wrapper);
                }
            }
        }

        return subcommands;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object obj) {
        if(command == obj || this == obj) {
            return true;
        }

        return false;
    }
}
