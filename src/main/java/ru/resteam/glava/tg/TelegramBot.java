package ru.resteam.glava.tg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.resteam.glava.tg.handlers.UpdateHandler;

import static ru.resteam.glava.tg.handlers.UpdateHandler.CHAT_ID;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	@Autowired
	private UpdateHandler updateHandler;

	public TelegramBot(String botToken) {
		super(botToken);
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			try {
				execute(updateHandler.handleTGUpdate(update));
			} catch (TelegramApiException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void sendMessageToTelegram(String text) {
		SendMessage message = new SendMessage();
		message.enableMarkdown(true);
		message.setChatId(CHAT_ID);
		message.setText(text);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

		@Override public String getBotUsername() {
		return "Glava";
	}
}
