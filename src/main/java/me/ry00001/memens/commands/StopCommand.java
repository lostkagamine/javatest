package me.ry00001.memens.commands;

import java.util.LinkedList;
import me.ry00001.memens.Bot;
import me.ry00001.memens.core.Command;
import me.ry00001.memens.music.GuildAudioHandler;
import me.ry00001.memens.music.TrackScheduler;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StopCommand extends Command {
    public StopCommand(Bot b) {
        bot = b;
        name = "stop";
        description = "stop playing music";
    }

    public void run(MessageReceivedEvent event, LinkedList<String> args) {
        GuildAudioHandler h = bot.getGuildAudioPlayer(event.getGuild());
        TrackScheduler sched = h.getScheduler();
        event.getChannel().sendMessage("Stopping...").queue();
        sched.destroy();
    }
}