package ru.resteam.glava.app.config;

import org.springframework.context.ApplicationEvent;

/**
 * Для устранения циклической зависимости между сервисами использован паттерн наблюдатель @Observes ~ @EventListener
 * @author a.mishkin
 */
public class MessageEvent extends ApplicationEvent {
	private final String message;
	private final MessageTarget target;

	public MessageEvent(Object source, String message, MessageTarget target) {
		super(source);
		this.message = message;
		this.target = target;
	}

	public String getMessage() {
		return message;
	}

	public MessageTarget getTarget() {
		return target;
	}

	public enum MessageTarget {
		TELEGRAM, DISCORD, OPENAI
	}
}