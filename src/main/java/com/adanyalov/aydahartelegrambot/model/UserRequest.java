package com.adanyalov.aydahartelegrambot.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "requests")
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    private long chatId;

    private String name;

    private String text;

    @NotNull
    private Timestamp date;

    public UserRequest() {

    }

    public UserRequest(String text) {
        super();
        this.text = text;
        this.date = new Timestamp(System.currentTimeMillis() + 6*(60 * 60 * 1000));
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", timestamp=" + date +
                '}';
    }
}
