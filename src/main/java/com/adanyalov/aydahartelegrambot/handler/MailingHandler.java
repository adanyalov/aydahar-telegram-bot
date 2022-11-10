package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.User;
import com.adanyalov.aydahartelegrambot.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MailingHandler implements Handler {

    public static final String HANDLER_NAME = "MailingHandler";

    UserRepository userRepository;

    public MailingHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        List<PartialBotApiMethod<? extends Serializable>> output = new ArrayList<>();
        long chatId = user.getChatId();
        if (chatId != 829954644 && !user.isAdmin()) {
            return Collections.emptyList();
        }

        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photo = photos.get(photos.size() - 1);
        String photoId = photo.getFileId();
        String caption = update.getMessage().getCaption();
        GetFile getFile = new GetFile();
        getFile.setFileId(photoId);

        var botUsers = userRepository.findAll();
        InputFile photoFile = new InputFile(photoId);
        for (User botUser : botUsers) {
            if (!botUser.isSubscribed()) {
                continue;
            }
            SendPhoto mail = new SendPhoto();
            mail.setChatId(String.valueOf(botUser.getChatId()));
            mail.setPhoto(photoFile);
            mail.setCaption(caption);
            output.add(mail);
        }

        return output;
    }

    @Override
    public List<String> operatedCallbackQuery() {
        return Collections.emptyList();
    }

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }


}

