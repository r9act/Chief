package ru.resteam.glava.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class BeanLogger implements BeanPostProcessor {
	private static final Logger log = LoggerFactory.getLogger(BeanLogger.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		log.info("🔄 Инициализация бина: {} ({})", beanName, bean.getClass().getSimpleName());
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		log.info("✅ Бин готов к работе: {} ({})", beanName, bean.getClass().getSimpleName());
		return bean;
	}
}
