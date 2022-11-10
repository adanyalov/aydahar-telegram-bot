package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.model.Card;
import com.adanyalov.aydahartelegrambot.model.Deckbox;
import com.adanyalov.aydahartelegrambot.model.User;
import com.adanyalov.aydahartelegrambot.repository.DeckboxRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class CardSearchHandler implements Handler {

    private final DeckboxRepository deckboxRepository;
    private final List<Deckbox> collectionsList;
    public static final String HANDLER_NAME = "CardSearchHandler";
    public static final String COMMAND_NAME = "/search";
    public static final String TOO_MANY_ERROR = "You are trying to search for too many cards at a time.\n"
            + "Be more considerate to the bot!";
    private static final int CARDS_LIMIT = 40;
    private int counter = 0;

    public CardSearchHandler(DeckboxRepository deckboxRepository) {
        this.deckboxRepository = deckboxRepository;
        collectionsList = deckboxRepository.findAllByOrderByIdAsc();
    }

    public String searchInCollection(Deckbox deckbox, String searchInput) {
        if (counter > CARDS_LIMIT) {
            return "";
        }
        String encodedRequest = Base64.getEncoder()
                .encodeToString(searchInput.getBytes()).replaceAll("=", "*");
        String url = deckbox.getUrl() + encodedRequest;
        StringBuilder outputBuilder = new StringBuilder();
        try {
            Document doc = Jsoup.connect(url).get();
            Element table = doc.getElementById("set_cards_table_details");
            Elements cardNames = table.select("tr[class]");
            List<Card> cardsList = new ArrayList<>();
            for (Element cardElement : cardNames) {
                cardsList.add(getCardFromJSON(cardElement));
            }
            outputBuilder.append("In <b>" + deckbox.getOwner() + "</b> inventory:\n");
            if (cardsList.size() == 0) {
                return "";
            } else {
                for (Card card : cardsList) {
                    if (counter > CARDS_LIMIT) {
                        return outputBuilder.toString();
                    }
                    outputBuilder.append("  ");
                    outputBuilder.append(card.toString());
                    counter++;
                }
                if (cardsList.size() == 30) {
                    outputBuilder.append("^ first 30 matches\n\n");
                } else {
                    outputBuilder.append("\n");
                }
            }

            return outputBuilder.toString();

        } catch (IOException e) {
            log.error("Failed to connect to deckbox: " + deckbox.getUrl() + e.getMessage());
            return "Failed to connect to " + deckbox.getOwner() + "'s deckbox, try again later\n";
        }
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        List<PartialBotApiMethod<? extends Serializable>> searchResultMessages = new ArrayList<>();

        String[] cardNames = message.split("\n");
        if (cardNames.length > 75) {
            SendMessage errorMessage = createMessageTemplate(user);
            errorMessage.setText(TOO_MANY_ERROR);
            return List.of(errorMessage);
        }

        for (String cardName: message.split("\n")) {
            searchResultMessages.add(handleOneCardSearch(update, user, cardName));
        }

        return searchResultMessages;
    }

    public SendMessage handleOneCardSearch(Update update, User user, String message) {
        SendMessage cardsMessage = createMessageTemplate(user);
        counter = 0;
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("Searching for cards containing \"<b>" + message + "</b>\":\n\n");
        for (Deckbox deckbox: collectionsList) {
            outputBuilder.append(searchInCollection(deckbox, message));
        }
        if (counter == 0) {
            outputBuilder.setLength(0);
            outputBuilder.append(EmojiParser.parseToUnicode("Cards matching the search input \"<b>"
                    + message + "</b>\" were not found :pensive:"));
        } else {
            if (counter > CARDS_LIMIT) {
                outputBuilder.append("\n<b>... output is too long, be more specific with your search</b>\n");
            }
            outputBuilder.append("The prices are approximate, double check with TCG Mid");
        }

        cardsMessage.setText(outputBuilder.toString());
        cardsMessage.enableHtml(true);

        return cardsMessage;
    }


    public Card getCardFromJSON(Element element) {
        Card newCard = new Card();

        newCard.setName(getCardName(element));
        newCard.setCount(getCardCount(element));
        newCard.setCardPage(getCardPageLink(element));
        newCard.setEdition(getCardEdition(element));
        newCard.setPrice(getCardPrice(element));
        newCard.setLanguage(getCardLanguage(element));
        newCard.setFoil(getCardFoil(element));
        return newCard;
    }

    private String getCardName(Element jsonElement) {
        return jsonElement.select("a[href]").text();
    }

    private String getCardCount(Element jsonElement) {
        return jsonElement.select(".inventory_count").text();
    }

    private String getCardPageLink(Element jsonElement) {
        return jsonElement.select("a[href]").attr("href");
    }

    private String getCardEdition(Element jsonElement) {
        return jsonElement.select("div[data-title]").attr("class")
                .replaceFirst("^.*?_", "")
                .replaceFirst("_.*$", "")
                .replaceFirst("ptc", "PRE");
    }

    private String getCardPrice(Element jsonElement) {
        return jsonElement.select("td.center").text();
    }

    private String getCardLanguage(Element jsonElement) {
        return jsonElement.select("img.flag").attr("data-title")
                .replaceFirst("Russian", "RUS")
                .replaceFirst("English", "ENG");
    }

    private String getCardFoil(Element jsonElement) {
        Elements selection = jsonElement.select("img[data-title=Foil]");
        return (selection.isEmpty()) ? "" : " Foil";
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
