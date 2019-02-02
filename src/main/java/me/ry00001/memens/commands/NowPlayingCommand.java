package me.ry00001.memens.commands;

import java.util.LinkedList;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.ry00001.memens.Bot;
import me.ry00001.memens.core.Command;
import me.ry00001.memens.music.GuildAudioHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class NowPlayingCommand extends Command {
    public final Bot bot;

    public NowPlayingCommand(Bot b) {
        this.name = "np";
        this.description = "Shows what's playing!";
        bot = b;
    }

    public void run(MessageReceivedEvent evt, LinkedList<String> args) {
        GuildAudioHandler h = bot.getGuildAudioPlayer(evt.getGuild());
        AudioPlayer ply = h.getPlayer();
        AudioTrack trk = ply.getPlayingTrack();
        if (trk == null) {
            evt.getChannel().sendMessage("Nothing is playing.").queue();
            return;
        }
        String title = trk.getInfo().title;
        String author = trk.getInfo().author;
        MessageEmbed embed = new EmbedBuilder()
            .setTitle("Now playing")
            .addField(title, String.format("(by %s)", author), false)
            .build();
        evt.getChannel().sendMessage(embed).queue();
    }
}