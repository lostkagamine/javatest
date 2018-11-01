package me.ry00001.memens.commands;

import me.ry00001.memens.core.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import me.ry00001.memens.Bot;

/*
    NOTE ON LOGGERS:
    the "logger" object is an instance of org.slf4j.Logger
    available to every command, via a static field
    in the parent Command class. Pls don't touch.
    Like, I'm serious. Do not touch it. Or bot dies.
*/

public class PingCommand extends Command {
    public PingCommand(Bot bot) {
        this.name = "ping";
        this.bot = bot;
        this.description = "what do you think this is";
    }

    public void run(MessageReceivedEvent event, LinkedList<String> args) {
        event.getChannel().sendMessage("pong").queue();
    }
}