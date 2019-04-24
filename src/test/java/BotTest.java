import me.idarkyy.dbc.bot.bots.configurable.AsynchronousConfigurableBot;
import me.idarkyy.dbc.commandframework.CommandManager;
import me.idarkyy.dbc.commandframework.annotations.Name;
import me.idarkyy.dbc.commandframework.annotations.Subcommand;
import me.idarkyy.dbc.commandframework.command.Command;
import me.idarkyy.dbc.commandframework.event.CommandEvent;

import java.io.File;

public class BotTest {
    public static void main(String[] args) throws Exception {
        File dir = new File(BotTest.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " "));

        File conf = new File(dir, "config.yml");

        System.out.println("PATH: " + conf.getPath());

        if(!conf.exists()) {
            conf.createNewFile();
        }

        CommandManager commandManager = new CommandManager();

        commandManager.register(new TestCommand());


        AsynchronousConfigurableBot bot = AsynchronousConfigurableBot.create(conf, commandManager.getListener());

        bot.launch();
    }
}
