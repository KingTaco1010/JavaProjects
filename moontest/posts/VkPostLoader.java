package org.moonstudio.moontest.posts;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.moonstudio.moontest.Config;
import org.moonstudio.moontest.Main;
import org.moonstudio.moontest.util.ColorUtil;
import org.moonstudio.moontest.util.HashUtil;
import org.moonstudio.moontest.util.TextUtil;

import java.io.IOException;
import java.util.*;

public class VkPostLoader {
    private String newPostText;
    private String newPostHash;
    private final String oldPostText;
    private final PostParser parser;
    private final boolean isLoaded;

    public VkPostLoader(PostParser parser) {
        this.newPostText = parser.parseFirst();
        if (newPostText != null)
            this.newPostHash = HashUtil.hashText(newPostText);
        this.parser = parser;
        oldPostText = parser.parseLast();

        Objects.requireNonNull(newPostText);
        Objects.requireNonNull(oldPostText);
        Main.instance.runTimerCheck();
        isLoaded = true;
    }
    public boolean saveIfNewPost(String newPostText) {
        String newPostHash = HashUtil.hashText(newPostText);
        if (this.newPostText == null || !this.newPostHash.equals(newPostHash)) {
            this.newPostText = newPostText;
            this.newPostHash = newPostHash;

            return true;
        }

        return false;
    }

    public String getNewPostText() {
        return newPostText;
    }

    public String getOldPostText() {
        return oldPostText;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void openBook(Player player, String text) {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) itemStack.getItemMeta();
        FileConfiguration config = Config.config;

        if (meta != null) {
            meta.setTitle("Ниггер с дцп и его приключения");
            meta.setAuthor("Внебрачный сын Вандама");

            int max_symbols = config.getInt("book_settings.text_max_symbols_in_line", 200);
            String color = config.getString("book_settings.text_color", "&f");

            if (config.getBoolean("book_settings.word_division", true)) {
                divideText(TextUtil.splitTextByWord(text, max_symbols), meta, color);
            } else {
                divideText(TextUtil.splitText(text, max_symbols), meta, color);
            }

            itemStack.setItemMeta(meta);
        }

        player.openBook(itemStack);
    }

    private void divideText(List<String> l1, BookMeta meta, String color) {
        l1.forEach(s -> meta.addPage(ColorUtil.colorAlternate(color + s)));
    }

    public void refreshPosts() throws IOException {
        parser.refresh();
    }

    public String checkNewPost() {
        return parser.parseFirst();
    }
}
