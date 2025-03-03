package ru.resteam.glava.app.service;

import ru.resteam.glava.app.config.MessageEvent;

public interface MessagingService {
	void handleMessageEvent(MessageEvent event);
}
