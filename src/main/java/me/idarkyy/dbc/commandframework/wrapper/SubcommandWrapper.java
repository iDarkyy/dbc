package me.idarkyy.dbc.commandframework.wrapper;

import me.idarkyy.dbc.commandframework.annotations.Aliases;
import me.idarkyy.dbc.commandframework.annotations.Permissions;
import me.idarkyy.dbc.commandframework.annotations.Subcommand;
import me.idarkyy.dbc.commandframework.command.Command;
import me.idarkyy.dbc.commandframework.event.SubcommandEvent;
import me.idarkyy.dbc.commandframework.exception.SubcommandException;
import net.dv8tion.jda.core.Permission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SubcommandWrapper {
    private Method method;
    private CommandWrapper command;

    public SubcommandWrapper(Method method, CommandWrapper command) {
        this.method = method;
        this.command = command;
    }

    public String getName() {
        if(!validate()) {
            return null;
        }

        return method.getAnnotation(Subcommand.class).value().toLowerCase();
    }

    public String[] getAliases() {
        if(!validate()) {
            return null;
        }

        if(!method.isAnnotationPresent(Aliases.class)) {
            return new String[]{};
        }

        return method.getAnnotation(Aliases.class).value();
    }

    public Permission[] getPermissions() {
        if(!validate()) {
            return null;
        }

        if(!method.isAnnotationPresent(Permissions.class)) {
            return Permission.EMPTY_PERMISSIONS;
        }

        return method.getAnnotation(Permissions.class).value();
    }

    public void invoke(SubcommandEvent event) {
        try {
            method.invoke(command.getCommand(), event);
        } catch(IllegalAccessException e) {
            method.setAccessible(true);
            invoke(event);
        } catch(InvocationTargetException e) {
            throw new SubcommandException("An error occurred while invoking sub-command " + getName() + " of command " + command.getName(), e);
        }
    }

    public boolean validate() {
        if(!method.isAnnotationPresent(Subcommand.class)) {
            return false;
        }

        if(method.getParameterCount() != 1 && method.getParameterTypes()[0] != SubcommandEvent.class) {
            throw new IllegalStateException(
                    "The first (and only) method parameter must be a SubcommandEvent\nMethod: " + method.getName() + "\nClass: " + method.getDeclaringClass().getSimpleName()
            );
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(method == obj || this == obj) {
            return true;
        }

        return false;
    }
}
