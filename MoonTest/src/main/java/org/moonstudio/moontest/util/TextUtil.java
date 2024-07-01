package org.moonstudio.moontest.util;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
    public static List<String> splitTextByWord(String text, int maxLength) {
        String[] words = text.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() == 0) {
                currentLine.append(word);
            } else {
                if (currentLine.length() + 1 + word.length() <= maxLength) {
                    currentLine.append(" ").append(word);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }


    public static List<String> splitText(String text, int maxLength) {
        List<String> stringList = new ArrayList<>();
        int length = text.length();

        for (int i = 0; i < length; i += maxLength) {
            if (i + maxLength <= length) {
                stringList.add(text.substring(i, i + maxLength));
            } else {
                stringList.add(text.substring(i));
            }
        }

        return stringList;
    }


}
