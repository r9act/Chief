package ru.resteam.glava.tg.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.resteam.glava.tg.TelegramBot;
import ru.resteam.glava.tg.service.TelegramBotService;

/**
 * @author Artem
 */
@Service
public class TelegramBotServiceImpl implements TelegramBotService {

	public static final Long CHAT_ID = -1002039183219L;

	private final TelegramBot telegramBot;

	@Autowired
	public TelegramBotServiceImpl(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	public TelegramBot getTelegramBot() {
		return telegramBot;
	}

	public void sendMessage(String message) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(CHAT_ID);
		sendMessage.setText(message);

		try {
			telegramBot.execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}