package com.step.bargain.telegram;

import com.step.bargain.model.entity.Item;
import com.step.bargain.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

/**
 * @author Daria Pevets
 **/
@Component
public class BargainBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final ItemService itemService;
    private final TelegramClient telegramClient;
    private final String botToken;

    public BargainBot(@Value("${telegram.bot.token}") String botToken, ItemService itemService) {
        this.botToken = botToken;
        this.itemService = itemService;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() || update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        if (text.equals("/start")) {
            sendText(chatId, "Привет! Отправь мне ссылку на товар, и я начну отслеживать его цену.");
            return;
        }

        if (text.startsWith("/delete ")) {
            handleDelete(chatId, text);
            return;
        }

        if (text.startsWith("http")) {
            handleAddProduct(chatId, text);
        } else {
            sendText(chatId, "Пожалуйста, отправь корректную ссылку на товар.");
        }
    }

    private void handleAddProduct(Long chatId, String url) {

        try {
            Item item = itemService.addItem(url,chatId);
            String response = String.format(
                    "✅ Товар добавлен!\n\n📦 %s\n💰 Цена: %s %s\n🆔 ID для удаления: %d",
                    item.getTitle(),
                    item.getLastPrice(),
                    item.getCurrency(),
                    item.getId()
            );

            sendText(chatId, response);
        } catch (Exception e) {
            sendText(chatId, "❌ Не удалось добавить товар: " + e.getMessage());
        }
    }

    private void handleDelete(Long chatId, String text) {
        try {
            Long id = Long.parseLong(text.replace("/delete", "").trim());
            itemService.removeItem(id,chatId);
            sendText(chatId, "🗑 Товар с ID " + id + " удален из отслеживания.");
        } catch (NumberFormatException exception) {
            sendText(chatId, "Укажите числовой ID товара. Пример: /delete 5");
        } catch (Exception exception) {
            sendText(chatId, "❌ Ошибка: " + exception.getMessage());
        }
    }

    private void sendText(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException exception) {
            exception.printStackTrace();// Todo: убрать иишное нечто и добавить логирование;
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}
