package ru.resteam.glava.discord;

import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;

/**
 * @author Artem
 */
@Component
public class DiscordBot {

	private final JDA jda;

	public DiscordBot(JDA jda) {
		this.jda = jda;
	}

	public JDA getJda() {
		return jda;
	}
}
