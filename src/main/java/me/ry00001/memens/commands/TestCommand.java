package me.ry00001.memens.commands;

import me.ry00001.memens.core.Command;
import me.ry00001.memens.Bot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

public class TestCommand extends Command {
    public TestCommand(Bot bot) {
        this.name = "test";
        this.bot = bot;
    }

    public void run(MessageReceivedEvent event, LinkedList<String> args) {
        
    }
}