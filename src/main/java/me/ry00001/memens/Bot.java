package me.ry00001.memens;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import me.ry00001.memens.utilities.Config;
import me.ry00001.memens.utilities.ConfigReader;
import java.io.File;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.reflections.Reflections; // command loader
import me.ry00001.memens.core.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.LinkedList;
import java.util.Arrays;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import me.ry00001.memens.music.*;

public class Bot extends ListenerAdapter {
    private Config config;
    public JDA jda;
    public static final Logger logger = LoggerFactory.getLogger(Bot.class);
    public HashMap<String, Command> commands;


    private final AudioPlayerManager manager;
    public final Map<Long, GuildAudioHandler> guildManagers;

    public Bot() {
        this.guildManagers = new HashMap<>();
        this.commands = new HashMap<>(); // welp, I'm stupid
        ConfigReader reader = new ConfigReader(new File("config.json"));
        try {
            this.config = reader.read();
        } catch (IOException e) {
            logger.error("oopsie whoopsie, something died while reading config.");
            logger.error("valve, pls fix."); // test
            logger.error(e.toString());
            System.exit(1);
        }

        logger.info("Initialising Lavaplayer!");
        this.manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.manager);

        // BEGIN HACKY COMMAND LOADER WEIRDNESS
        logger.info("Attempting to load commands...!");
        Reflections reflections = new Reflections("me.ry00001.memens.commands");
        Set<Class<? extends Command>> cmds = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> cmd: cmds) {
            try {
                Command acmd = cmd.getDeclaredConstructor(Bot.class).newInstance(this);
                commands.put(acmd.name, acmd);
            } catch (Exception e) {
                logger.error("Error while loading a command!");
                e.printStackTrace();
                System.exit(1);
            }
        }
        logger.info("Done loading commands! I loaded "+commands.size()+" of them!");
        // END HACKY COMMAND LOADER WEIRDNESS
    }

    public void buildJdaAndStart() {
        try {
            this.jda = new JDABuilder(AccountType.BOT)
                .setToken(this.config.token)
                .setAudioSendFactory(new NativeAudioSendFactory()) // JDA-NAS
                .setAudioEnabled(true)
                .addEventListener(this)
                .build().awaitReady();
        } catch (InterruptedException|LoginException e) {
            logger.error("can't log in!");
            logger.error("valve, pls fix.");
            logger.error(e.toString());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Bot().buildJdaAndStart();
    }

    public void shutdown() {
        logger.info("Shutting down.");
        this.jda.shutdown();
        System.exit(0);
    }

    public synchronized GuildAudioHandler getGuildAudioPlayer(Guild guild) {
        long gid = guild.getIdLong();
        GuildAudioHandler handler = guildManagers.get(gid);
        if (handler == null) {
            handler = new GuildAudioHandler(manager);
            guildManagers.put(gid, handler);
        }
        guild.getAudioManager().setSendingHandler(handler.getHandler());
        return handler;
    }

    public void loadAndPlay(final TextChannel ch, final String url) {
        GuildAudioHandler h = getGuildAudioPlayer(ch.getGuild());
        manager.loadItemOrdered(h, url, new AudioLoadResultHandler(){
        
            @Override
            public void trackLoaded(AudioTrack track) {
                AudioTrackInfo info = track.getInfo();
                ch.sendMessage(String.format("Queueing `%s`...", info.title)).queue();
                play(ch.getGuild(), h, track);
            }
        
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack fst = playlist.getSelectedTrack();
                if (fst == null) {
                    fst = playlist.getTracks().get(0);
                }

                ch.sendMessage(String.format("Queueing `%s` (first track of playlist `%s`)...", fst.getInfo().title, playlist.getName())).queue();
                play(ch.getGuild(), h, fst);
            }
        
            @Override
            public void noMatches() {
                ch.sendMessage(String.format("`%s` didn't match anything.", url)).queue();
            }
        
            @Override
            public void loadFailed(FriendlyException exception) {
                ch.sendMessage(String.format("Error occurred while trying to play: %s", exception.getMessage())).queue();
            }
        });
    }

    public void play(Guild g, GuildAudioHandler gah, AudioTrack trk) {
        connectToFirstVoiceChannel(g.getAudioManager());
        gah.getScheduler().queue(trk);
    }

    public void skipTrack(TextChannel ch) {
        GuildAudioHandler h = getGuildAudioPlayer(ch.getGuild());
        h.getScheduler().nextTrack();
    }

    private static void connectToFirstVoiceChannel(AudioManager mgr) {
        if (!mgr.isConnected() && !mgr.isAttemptingToConnect()) {
            for (VoiceChannel ch : mgr.getGuild().getVoiceChannels()) {
                mgr.openAudioConnection(ch);
                break;
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("ok]");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        Message msg = event.getMessage();
        String raw = msg.getContentRaw();
        if (!raw.startsWith(this.config.prefix)) return;
        String prefixless = raw.substring(this.config.prefix.length());
        String[] split = prefixless.split(" ");
        LinkedList<String> ll = new LinkedList<String>(Arrays.asList(split));
        String cmdname = ll.removeFirst();
        Command cmd = this.commands.get(cmdname);
        if (cmd == null) return;
        try {
            cmd.run(event, ll);
        } catch(Throwable e) {
            logger.error("ERROR in command "+cmdname);
            logger.error(e.toString());
        }
    }
}