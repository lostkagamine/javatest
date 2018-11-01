package me.ry00001.memens.commands;

import me.ry00001.memens.Bot;
import me.ry00001.memens.core.Command;
import java.util.LinkedList;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.Map;
import java.lang.StringBuilder;

public class HelpCommand extends Command {
    public HelpCommand(Bot bot) {
        this.bot = bot;
        this.name = "help";
        this.description = "I guess that's where you are";
    }

    public void run(MessageReceivedEvent event, LinkedList<String> args) {
        StringBuilder output = new StringBuilder("```");
        for (Map.Entry<String, Command> entry: this.bot.commands.entrySet()) {
            String name = entry.getKey();
            Command cmd = entry.getValue();
            output.append(name + " - " + cmd.getDescription() + "\n");
        }
        output.append("```");
        event.getChannel().sendMessage(output.toString()).queue();
    }
}