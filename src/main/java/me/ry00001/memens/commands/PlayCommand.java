package me.ry00001.memens.commands;

import me.ry00001.memens.Bot;
import me.ry00001.memens.core.Command;
import java.util.LinkedList;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

public class PlayCommand extends Command {
    public final Bot bot;

    public PlayCommand(Bot b) {
        this.name = "play";
        this.description = "Plays music, I think.";
        bot = b;
    }

    public void run(MessageReceivedEvent evt, LinkedList<String> args) {
        String trk = StringUtils.join(args, " ");
        if (trk == "") {
            evt.getChannel().sendMessage("specify a track you bamana").queue();
            return;
        }
        bot.loadAndPlay(evt.getTextChannel(), trk);
    }
}