package me.ry00001.memens.core;

import me.ry00001.memens.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.lang.Throwable;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
    public String name;
    public Bot bot;
    public String description;
    public static final Logger logger = LoggerFactory.getLogger(Command.class);

    public String getDescription() {
        if (this.description != null) return this.description;
        return "No description.";
    }

    public abstract void run(MessageReceivedEvent event, LinkedList<String> args) throws Throwable;
}