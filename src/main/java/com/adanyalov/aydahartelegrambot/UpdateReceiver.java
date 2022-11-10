package com.adanyalov.aydahartelegrambot;

import com.adanyalov.aydahartelegrambot.handler.Handler;
import com.adanyalov.aydahartelegrambot.model.User;
import com.adanyalov.aydahartelegrambot.model.UserRequest;
import com.adanyalov.aydahartelegrambot.repository.UserRepository;
import com.adanyalov.aydahartelegrambot.repository.UserRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class UpdateReceiver {
    private final List<Handler> handlers;
    private final UserRepository userRepository;
    private final UserRequestRepository requestRepository;

    public UpdateReceiver(List<Handler> handlers, UserRepository userRepository,
                          UserRequestRepository requestRepository) {
        this.handlers = handlers;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        try {
            if (isMessageWithText(update) || isMessageWithPhoto(update)) {
                final Message message = update.getMessage();
                final String messageText = message.getText();
                final long chatId = message.getChatId();
                final Chat chat = message.getChat();
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId, chat)));
                user.updateActivityDate();
                if (messageText.length() > 1500) {
                    return getHandlerByName("LongRequestHandler").handle(update, user, messageText);
                }
                UserRequest newRequest = new UserRequest("/mailing");
                newRequest.setName(user.getFirstName());
                newRequest.setChatId(chatId);
                if (isMessageWithPhoto(update)) {
                    requestRepository.save(newRequest);
                    return getHandlerByName("MailingHandler").handle(update, user, messageText);
                }
                newRequest.setText(message.getText());
                requestRepository.save(newRequest);
                log.info("QUERY: " + messageText);
                log.info("HANDLER SEARCH: " + getHandlerByCallbackQuery(messageText).getHandlerName());
                return getHandlerByCallbackQuery(message.getText()).handle(update, user, message.getText());
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final long chatId = callbackQuery.getMessage().getChatId();
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId, callbackQuery.getMessage().getChat())));

                return getHandlerByCallbackQuery(callbackQuery.getData()).handle(update, user, callbackQuery.getData());
            }
        } catch (Exception e) {
            log.error("Issue handling an update: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private Handler getHandlerByCallbackQuery(String query) {
        return handlers.stream()
                .filter(h -> h.operatedCallbackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElse(getHandlerByName("CardSearchHandler"));
    }

    private Handler getHandlerByName(String handlerName) {
        return handlers.stream()
                .filter(h -> h.getHandlerName().equals(handlerName))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isMessageWithPhoto(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasPhoto();
    }
}
