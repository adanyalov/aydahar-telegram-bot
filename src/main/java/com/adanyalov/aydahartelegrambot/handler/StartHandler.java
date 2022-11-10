package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;
import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createInlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class StartHandler implements Handler {

    public static final String HANDLER_NAME = "StartHandler";
    public static final String COMMAND_NAME = "/start";
    public static final String START_MESSAGE = "Hello, this is Aydahar's telegram bot!\n\n"
            + "Use /calendar to see upcoming events.\n"
            + "Send \"bolt\" to search for cards that contain \"bolt\" in their name.\n"
            + "You can search by using a part of a name: \"angel\" to search all cards that incorporate \"angel\" in their name.\n"
            + "/unsubscribe to stop receiving event announcements from the store.";

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        SendMessage startMessage = createMessageTemplate(user);
        startMessage.setText(START_MESSAGE);

        startMessage.setReplyMarkup(getStartInlineKeyboard());
        return List.of(startMessage);
    }

    public InlineKeyboardMarkup getStartInlineKeyboard() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createInlineKeyboardButton("Calendar", "/calendar"));
        row.add(createInlineKeyboardButton("Search \"angel\"", "angel"));
        rowsInLine.add(row);
        markupInLine.setKeyboard(rowsInLine);

        return markupInLine;
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
