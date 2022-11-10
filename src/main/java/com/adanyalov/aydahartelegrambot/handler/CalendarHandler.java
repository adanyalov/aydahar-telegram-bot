package com.adanyalov.aydahartelegrambot.handler;

import com.adanyalov.aydahartelegrambot.config.BotConfig;
import com.adanyalov.aydahartelegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.adanyalov.aydahartelegrambot.util.TelegramUtil.createMessageTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@PropertySource("application.properties")
public class CalendarHandler implements Handler {

    public static final String HANDLER_NAME = "CalendarHandler";
    public static final String COMMAND_NAME = "/calendar";
    public static final String NO_EVENTS_MESSAGE = "No events were entered into the calendar, ask the staff";
    public static final String CALENDAR_HYPERLINK = "<a href='https://calendar.google.com/calendar"
            + "/u/0/embed?src=m13lb88d3b2qtbktl17lfdmb9c@group.calendar.google.com"
            + "&ctz=Asia/Almaty'>Aydahar's Google Calendar</a>";
    private BotConfig config;

    public CalendarHandler(BotConfig config) {
        this.config = config;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update, User user, String message) {
        SimpleDateFormat ISOFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+06:00", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM EEEE HH:mm");

        SendMessage eventsMessage = createMessageTemplate(user);
        try {
            String urlString = generateURLToGetEventsForTheNext(2*168, ISOFormat);
            String eventsOverview = aggregateEventsAsText(urlString, ISOFormat, outputFormat);

            eventsMessage.enableHtml(true);
            eventsMessage.setText(eventsOverview + "\n" + CALENDAR_HYPERLINK);

            return List.of(eventsMessage);
        } catch (ParseException e) {
            log.error("Error parsing calendar JSON: " + e.getMessage());
            eventsMessage.setText("Issues parsing the calendar \nTry again later");
            return List.of(eventsMessage);
        } catch (MalformedURLException e) {
            log.error("Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO error: " + e.getMessage());
        }

        return Collections.emptyList();
    }

    private String generateURLToGetEventsForTheNext(Integer hours, SimpleDateFormat format) {
        String baseUrl = "https://www.googleapis.com/calendar/v3/calendars/"
                + "m13lb88d3b2qtbktl17lfdmb9c@group.calendar.google.com/events?";
        String key = config.getGoogleAPI();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.HOUR, -12);
        String minTimestamp = format.format(c.getTime());
        c.add(Calendar.HOUR, hours);
        String maxTimestamp = format.format(c.getTime());
        String urlString = baseUrl + "timeMax=" + maxTimestamp.replaceFirst("\\+", "%2B")
                + "&timeMin=" + minTimestamp.replaceFirst("\\+", "%2B")
                + "&orderBy=startTime&singleEvents=true" + "&key=" + key;

        return urlString;
    }

    private String aggregateEventsAsText(String urlString, SimpleDateFormat inputFormat,
                                         SimpleDateFormat outputFormat) throws IOException, ParseException {
        StringBuilder response = new StringBuilder();

        URL url = new URL(urlString);
        JSONObject resp = getJson(url);
        JSONArray items = resp.getJSONArray("items");
        response.append("Upcoming events within the next two weeks:\n\n");
        int countEvents = 0;
        for (Object item: items) {
            JSONObject itemJSON = (JSONObject) item;
            String summary = itemJSON.getString("summary");

            if (!itemJSON.getJSONObject("start").has("dateTime")) {
                continue;
            }

            String dateString = ((JSONObject) item).getJSONObject("start").getString("dateTime");
            Date date = inputFormat.parse(dateString);
            String testString = outputFormat.format(date);
            response.append(testString).append(" - <b>").append(summary).append("</b>\n");
            /*
            String description = ((JSONObject) item).getString("description");
            description = description.replaceAll("<br>", "\n")
                            .replaceAll("</?(?!(?:i|strong|b)\\b)[a-z](?:[^>\"']|\"[^\"]*\"|'[^']*')*>", "")
                    .replaceAll("&nbsp;", " ");

            log.info("EVENT DESCRIPTION: " + description);
            response.append(description);
            */
            countEvents++;
        }

        if (countEvents == 0) {
            return NO_EVENTS_MESSAGE;
        }

        return response.toString();
    }

    public static JSONObject getJson(URL url) throws IOException {
        String json = IOUtils.toString(url, Charset.forName("UTF-8"));
        return new JSONObject(json);
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

