package ru.resteam.glava.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.context.event.EventListener;

/**
 * @author Artem
 */
public class DiscordBot {

	private final JDA jda;

	public DiscordBot(JDA jda) {
		this.jda = jda;
	}

	public JDA getJda() {
		return jda;
	}
}
