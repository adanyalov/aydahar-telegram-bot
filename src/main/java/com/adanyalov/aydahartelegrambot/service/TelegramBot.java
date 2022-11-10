package com.adanyalov.aydahartelegrambot.service;

import com.adanyalov.aydahartelegrambot.UpdateReceiver;
import com.adanyalov.aydahartelegrambot.config.BotConfig;
import com.adanyalov.aydahartelegrambot.model.User;
import com.adanyalov.aydahartelegrambot.repository.UserRepository;
import com.adanyalov.aydahartelegrambot.repository.UserRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRequestRepository userRequestRepository;
    @Autowired
    private UpdateReceiver updateReceiver;

    private BotConfig config;
    static final String HELP_TEXT = "No help for you";

    public TelegramBot(BotConfig botConfig) {
        config = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Get started"));
        listOfCommands.add(new BotCommand("/calendar", "Get upcoming events"));
        listOfCommands.add(new BotCommand("/unsubscribe", "Unsubscribe from announcements"));
        listOfCommands.add(new BotCommand("/subscribe", "Subscribe to announcements"));
        listOfCommands.add(new BotCommand("/about", "About the bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            registerUser(update.getMessage());
        }
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = updateReceiver.handle(update);

        if (messagesToSend != null && !messagesToSend.isEmpty()) {
            for (Object response: messagesToSend) {
                if (response instanceof SendMessage) {
                    executeWithExceptionCheck((SendMessage) response);
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        log.debug("ISSUE WITH A THREAD SLEEP: " + e.getMessage());
                    }
                } else if (response instanceof SendPhoto) {
                    executeWithExceptionCheck((SendPhoto) response);
                }
            }
        }
    }

    public void executeWithExceptionCheck(PartialBotApiMethod<? extends Serializable> sendMethod) {
        try {
            if (sendMethod instanceof SendMessage) {
                execute((SendMessage) sendMethod);
            } else if (sendMethod instanceof SendPhoto) {
                execute((SendPhoto) sendMethod);
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (TelegramApiException e) {
            System.out.println("issues sending a message");
            log.error("Issue sending a message: " + e.getMessage());
        }
    }


    private void registerUser(Message msg) {
        if (!userRepository.existsById(msg.getChatId())) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User(chatId, chat);

            userRepository.save(user);
            log.info("User saved: " + user);
        }
    }
}
