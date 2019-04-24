import me.idarkyy.dbc.commandframework.annotations.Aliases;
import me.idarkyy.dbc.commandframework.annotations.Name;
import me.idarkyy.dbc.commandframework.annotations.Subcommand;
import me.idarkyy.dbc.commandframework.command.Command;
import me.idarkyy.dbc.commandframework.event.CommandEvent;
import me.idarkyy.dbc.commandframework.event.SubcommandEvent;

import java.util.Arrays;

@Name("hello")
@Aliases("h")
public class TestCommand implements Command {
    @Override
    public void execute(CommandEvent event) {
        event.getTextChannel().sendMessage("Works!").queue();

        if(event.getArgs().length > 0) {
            event.getTextChannel().sendMessage("Args: " + Arrays.toString(event.getArgs())).queue();
        }
    }


    @Subcommand("uwu")
    @Aliases("owo")
    public void uwuCommand(SubcommandEvent event) {
        event.getTextChannel().sendMessage("owo uwu").queue();

        if(event.getArgs().length > 0) {
            event.getTextChannel().sendMessage("Args: " + Arrays.toString(event.getArgs())).queue();
        }
    }
}
