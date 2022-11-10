package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;

import java.io.Serializable;
import java.util.List;

@Component
public class UnsubscribeHandler implements Handler {

    public static final String HANDLER_NAME = "UnsubscribeHandler";
    public static final String COMMAND_NAME = "/unsubscribe";
    public static final String UNSUBSCRIBE_TEXT = "You are unsubscribed.\n"
            + "If you change your mind you can use /subscribe command!";

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        user.setSubscribed(false);

        SendMessage unsubscribeMessage = createMessageTemplate(user);
        unsubscribeMessage.setText(UNSUBSCRIBE_TEXT);

        return List.of(unsubscribeMessage);
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

