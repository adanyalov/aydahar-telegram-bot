package com.adanyalov.aydahartelegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Chat;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity(name = "usersDataTable")
@Getter
@Setter
@AllArgsConstructor
public class User {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    private boolean isSubscribed;

    private boolean isAdmin;

    private Timestamp registeredAt;

    private Timestamp lastActivityAt;

    public User() {

    }

    public User(Long chatId, Chat chat) {
        this.chatId = chatId;
        firstName = chat.getFirstName();
        lastName = chat.getLastName();
        userName = chat.getUserName();
        registeredAt = new Timestamp(System.currentTimeMillis() + 6*(60 * 60 * 1000));
        lastActivityAt = registeredAt;
        isSubscribed = true;
        isAdmin = false;
    }

    public void updateActivityDate() {
        lastActivityAt = new Timestamp(System.currentTimeMillis() + 6*(60 * 60 * 1000));
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}