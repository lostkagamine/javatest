package me.ry00001.memens.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildAudioHandler {
    public final AudioPlayer player;
    public final TrackScheduler sched;
    public final AudioPlayerManager manager;

    public GuildAudioHandler(AudioPlayerManager m) {
        manager = m;
        player = m.createPlayer();
        sched = new TrackScheduler(player);
        player.addListener(sched);
    }

    public TrackScheduler getScheduler() {
        return sched;
    }

    public AudioPlayerManager getManager() {
        return manager;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioPlayerSendHandler getHandler() {
        return new AudioPlayerSendHandler(player);
    }
}