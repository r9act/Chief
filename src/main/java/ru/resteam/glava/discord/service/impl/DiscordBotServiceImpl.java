package ru.resteam.glava.discord.service.impl;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Service;
import ru.resteam.glava.discord.DiscordBot;
import ru.resteam.glava.discord.service.DiscordBotService;

/**
 * @author Artem
 */
@Service
public class DiscordBotServiceImpl implements DiscordBotService {
	private static final String CHANNEL_NAME = "\uD83D\uDCCCосновной";
	private final DiscordBot discordBot;

	public DiscordBotServiceImpl(DiscordBot discordBot) {
		this.discordBot = discordBot;
	}

	public DiscordBot getDiscordBot() {
		return discordBot;
	}

	public void sendMessage(String message) {
		TextChannel textChannel = discordBot.getJda().getTextChannelsByName(CHANNEL_NAME,true).get(0);
		textChannel.sendMessage(message).queue();
	}
}
