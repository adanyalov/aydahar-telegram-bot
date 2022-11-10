package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.User;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;

@Component
public class LongRequestHandler implements Handler {

    public static final String HANDLER_NAME = "LongRequestHandler";
    public static final String COMMAND_NAME = "/longrequesthandler";
    public static final String ERROR_MESSAGE = EmojiParser.parseToUnicode(""
            + "Your message is way too long, handle the bot with care!");

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        SendMessage outMessage = createMessageTemplate(user);
        outMessage.setText(ERROR_MESSAGE);

        return List.of(outMessage);
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
