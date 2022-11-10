package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;

@Component
public class SubscribeHandler implements Handler {

    public static final String HANDLER_NAME = "SubscribeHandler";
    public static final String COMMAND_NAME = "/subscribe";
    public static final String SUBSCRIBE_TEXT = "You are subscribed, you will receive event announcements, yay!";

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        user.setSubscribed(true);

        SendMessage subscribeMessage = createMessageTemplate(user);
        subscribeMessage.setText(SUBSCRIBE_TEXT);

        return List.of(subscribeMessage);
    }

    @Override
    public List<String> operatedCallbackQuery() {
        return List.of(COMMAND_NAME);
    }

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }
}

