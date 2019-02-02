package me.ry00001.memens.commands;

import me.ry00001.memens.core.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import me.ry00001.memens.Bot;

public class QuitCommand extends Command {
    public QuitCommand(Bot b) {
        bot = b;
        name = "quit";
        description = "exits the bot";
    }

    public void run(MessageReceivedEvent evt, LinkedList<String> args) {
        if (evt.getAuthor().getIdLong() != 190544080164487168L) return;
        evt.getChannel().sendMessage("Quitting...").complete();
        bot.shutdown();
    }
}