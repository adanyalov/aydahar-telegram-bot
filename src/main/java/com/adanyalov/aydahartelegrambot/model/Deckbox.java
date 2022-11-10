package com.adanyalov.aydahartelegrambot.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "deckboxes")
@Data
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
}
