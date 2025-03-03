package ru.resteam.glava.tg.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.resteam.glava.app.service.MessagingService;
import ru.resteam.glava.app.service.impl.MessagingServiceImpl;

/**
 * @author Artem
 */
@Component
public class UpdateHandler {

	public static final Long CHAT_ID = 1L;
	public static final String botName = "@r9act_py_bot";

	@Autowired
	@Lazy
	private MessagingService messagingService;

	public SendMessage handleTGUpdate(Update update) {
		Message message = update.getMessage();
		var chatId = message.getChatId();
		var messageText = message.getText();
		if (messageText.startsWith("/discord@r9act_py_bot") || messageText.contains("@r9act_py_bot")) {
			String author = update.getMessage().getFrom().getFirstName();
			String payload = messageText.replace(botName, author + " прислал из телеги: ");
			messagingService.forwardToDiscord(payload);
			return new SendMessage(chatId.toString(), "Отправил в дискорд");
			}
		messagingService.forwardToOpenAi();
		return new SendMessage(chatId.toString(), "Не знаю такую команду");
	}
}
