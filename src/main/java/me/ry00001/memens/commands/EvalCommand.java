package me.ry00001.memens.commands;

import me.ry00001.memens.core.Command;
import me.ry00001.memens.Bot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.apache.commons.lang3.StringUtils;

public class EvalCommand extends Command {
    public EvalCommand(Bot bot) {
        this.bot = bot;
        this.name = "eval";
        this.description = "Runs arbitrary javascript";
    }

    @Override
    public void run(MessageReceivedEvent event, LinkedList<String> args) {
        if (event.getAuthor().getIdLong() != 190544080164487168L) return;
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine nashorn = manager.getEngineByName("nashorn");
        nashorn.put("event", event);
        nashorn.put("jda", event.getJDA());
        nashorn.put("args", args);
        nashorn.put("bot", this.bot);
        nashorn.put("logger", logger);
        try {
            Object a = nashorn.eval(StringUtils.join(args, " "));
            if (a == null) a = "No output";
            event.getChannel().sendMessage("```"+a.toString()+"```").queue();
        } catch(ScriptException e) {
            event.getChannel().sendMessage("error: ```"+e.toString()+"```").queue();
        }
    }
}