package com.adanyalov.aydahartelegrambot.model;

import lombok.Data;

@Data
public class Card {
    private String name;

    private String count;

    private String cardPage;

    private String edition;

    private String price;

    private String language;

    private String foil;

    public String toString() {
        return count + " " + name + foil + " " + price + " (" + edition + " " + language + ")\n";
    }
}
