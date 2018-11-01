package me.ry00001.memens;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.ChannelType;
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
import java.util.LinkedList;
import java.util.Arrays;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

public class Bot extends ListenerAdapter {
    private Config config;
    public JDA jda;
    public static final Logger logger = LoggerFactory.getLogger(Bot.class);
    public HashMap<String, Command> commands;
    public AudioPlayerManager manager;
    public HashMap<String, AudioPlayer> players; // index = channel id

    public Bot() {
        this.commands = new HashMap<String, Command>(); // welp, I'm stupid
        ConfigReader reader = new ConfigReader(new File("config.json"));
        try {
            this.config = reader.read();
        } catch (IOException e) {
            logger.error("oopsie whoopsie, something died while reading config.");
            logger.error("valve, pls fix.");
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
                .buildBlocking();
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
        cmd.run(event, ll);
    }
}