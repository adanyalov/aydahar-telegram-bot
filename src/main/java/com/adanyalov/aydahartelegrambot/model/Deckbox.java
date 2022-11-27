package com.adanyalov.aydahartelegrambot.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "deckboxes")
@Getter
@Setter
@AllArgsConstructor
public class Deckbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @NotNull
    private String url;

    @NotNull
    private String owner;

    public Deckbox() {

    }

    @Override
    public String toString() {
        return "Deckbox{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
