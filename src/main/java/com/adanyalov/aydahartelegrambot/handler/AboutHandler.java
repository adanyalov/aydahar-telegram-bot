package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.User;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;

import java.io.Serializable;
import java.util.List;

@Component
public class AboutHandler implements Handler {

    public static final String HANDLER_NAME = "AboutHandler";
    public static final String COMMAND_NAME = "/about";
    public static final String ABOUT_MESSAGE = EmojiParser.parseToUnicode(""
            + "Developed using Spring, Hibernate & Postgres by @adanyalov\n\n"
            + "Feel free to message me\n"
            + "P.S. I'm looking for a Java backend developer position :wink:");

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        SendMessage aboutMessage = createMessageTemplate(user);
        aboutMessage.setText(ABOUT_MESSAGE);

        return List.of(aboutMessage);
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

