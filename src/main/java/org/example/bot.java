package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class bot {
    private final TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));
    private static final String THINKING = "Thinking...";
    private static final List<String> firstwinner = new ArrayList<String>(){
        {
            add("02");
            add("10");
            add("21");
        }};

    public void serve(){

        bot.setUpdatesListener(updates -> {
            updates.forEach(this::gocode);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;

        });

    }
    private void gocode(Update updt){
        Message mssg = updt.message();
        CallbackQuery cllbck = updt.callbackQuery();
        InlineQuery inline = updt.inlineQuery();
        BaseRequest answer = null;

        if (mssg != null && mssg.viaBot() != null && mssg.viaBot().username().equals("KamenNozhnicyBumaga_bot")) {
            InlineKeyboardMarkup rplmrkp = mssg.replyMarkup();
            if (rplmrkp == null) {
                return;
            }
            InlineKeyboardButton[][] buttons = rplmrkp.inlineKeyboard();
            if (buttons == null) {
                return;
            }
            InlineKeyboardButton button = buttons[0][0];
            String bttnLbl = button.text();
            if (!bttnLbl.equals(THINKING)) {
                return;
            }
            Long chatId = mssg.chat().id();
            String sender = mssg.from().firstName();
            String choise = button.callbackData();
            Integer messageId = mssg.messageId();

            answer = new EditMessageText(chatId, messageId, mssg.text())
                    .replyMarkup(
                            new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("\uD83D\uDDFF")
                                            .callbackData(String.format("%d %s %s %s %d", chatId, sender, choise, "0", messageId)),
                                    new InlineKeyboardButton("\uD83D\uDDD2")
                                            .callbackData(String.format("%d %s %s %s %d", chatId, sender, choise, "1", messageId)),
                                    new InlineKeyboardButton("✂")
                                            .callbackData(String.format("%d %s %s %s %d", chatId, sender, choise, "2", messageId))
                            )
                    );
        } else if (inline != null) {
            InlineQueryResultArticle pipka = getButton("rock", "\uD83D\uDDFF Rock", "0");
            InlineQueryResultArticle scissors = getButton("paper", "\uD83D\uDDD2 Paper", "1");
            InlineQueryResultArticle ruler = getButton("scissors", "✂  Scissors", "2");

            answer = new AnswerInlineQuery(inline.id(), pipka, scissors, ruler).cacheTime(1);
        } else if (cllbck != null) {
            String[] s = cllbck.data().split(" ");
            Long chatID = Long.parseLong(s[0]);
            String sender = s[1];
            String choise = s[2];
            String secondChoise = s[3];
            String secondName = cllbck.from().firstName();
            if (choise.equals(secondChoise)) {
                answer = new SendMessage(chatID, "Draw");
            } else if (firstwinner.contains(choise+secondChoise)) {
                answer = new SendMessage(chatID,
                        String.format("%s (%s) won %s (%s)",
                                sender, choise, secondName, secondChoise));
            } else {
                    answer = new SendMessage(chatID,
                            String.format("%s (%s) won %s (%s)",
                                    secondName, secondChoise, sender, choise));
            }
            System.out.println("");
        }
        /*else if (mssg != null) {
           long chatId = mssg.chat().id();
            answer = new SendMessage(chatId, "Hello!");
        }*/
        if (answer != null) {
            bot.execute(answer);
        }
    }

    private InlineQueryResultArticle getButton(String id, String title, String callbackData) {
        return new InlineQueryResultArticle(id, title, "Go to fight!")
                .replyMarkup(
                        new InlineKeyboardMarkup(
                                new InlineKeyboardButton(THINKING).callbackData(callbackData)
                        )
                );
    }
}
