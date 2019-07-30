package net.porillo.util;

import lombok.Setter;
import net.porillo.GlobalWarming;
import net.porillo.config.Lang;
import net.porillo.objects.GPlayer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create a table where the columns align correctly
 * - Assumes the default Minecraft font which is not monospaced
 * - Customizable options:
 * - Title
 * - Headers: color and width in pixels
 * - Default text colors
 * - Row and column styles
 */
public class ChatTable {
    public enum Section {
        HEADER(0),
        BODY(1);

        public int get() {
            return value;
        }

        private final int value;

        Section(int value) {
            this.value = value;
        }
    }

    private enum Alignment {LEFT, CENTER, RIGHT}

    public static final int CHAT_WIDTH = GlobalWarming.getInstance().getConf().getChatTableWidth();
    private final Map<Character, Integer> CHAR_WIDTH;
    @Setter private String title;
    private List<String> headers;
    private List<Integer> headerWidth;
    private List<List<String>> body;
    @Setter private ChatColor gridColor;
    private ChatColor[] textColor;
    private Character[] decoration;
    private Character[] delimiter;
    private Character[] onePixelPad;
    private Character[] twoPixelPad;

    public ChatTable(String title) {
        this.title = title;

        headers = new ArrayList<>();
        headerWidth = new ArrayList<>();
        body = new ArrayList<>();

        gridColor = ChatColor.WHITE;

        textColor = new ChatColor[2];
        textColor[Section.HEADER.get()] = ChatColor.WHITE;
        textColor[Section.BODY.get()] = ChatColor.WHITE;

        decoration = new Character[2];
        decoration[Section.HEADER.get()] = '-';
        decoration[Section.BODY.get()] = ' ';

        delimiter = new Character[2];
        delimiter[Section.HEADER.get()] = '+';
        delimiter[Section.BODY.get()] = '|';

        twoPixelPad = new Character[2];
        twoPixelPad[Section.HEADER.get()] = '\u00B7';
        twoPixelPad[Section.BODY.get()] = '\u00B7';

        onePixelPad = new Character[2];
        onePixelPad[Section.HEADER.get()] = '\u17f2';
        onePixelPad[Section.BODY.get()] = '\u17f2';

        //ASCII character sizes:
        CHAR_WIDTH = new HashMap<>();
        CHAR_WIDTH.put(' ', 4);
        CHAR_WIDTH.put('!', 2);
        CHAR_WIDTH.put('"', 4);
        CHAR_WIDTH.put('#', 6);
        CHAR_WIDTH.put('$', 6);
        CHAR_WIDTH.put('%', 6);
        CHAR_WIDTH.put('&', 6);
        CHAR_WIDTH.put('\'', 2);
        CHAR_WIDTH.put('(', 4);
        CHAR_WIDTH.put(')', 4);
        CHAR_WIDTH.put('*', 4);
        CHAR_WIDTH.put('+', 6);
        CHAR_WIDTH.put(',', 2);
        CHAR_WIDTH.put('-', 6);
        CHAR_WIDTH.put('.', 2);
        CHAR_WIDTH.put('/', 6);
        CHAR_WIDTH.put('0', 6);
        CHAR_WIDTH.put('1', 6);
        CHAR_WIDTH.put('2', 6);
        CHAR_WIDTH.put('3', 6);
        CHAR_WIDTH.put('4', 6);
        CHAR_WIDTH.put('5', 6);
        CHAR_WIDTH.put('6', 6);
        CHAR_WIDTH.put('7', 6);
        CHAR_WIDTH.put('8', 6);
        CHAR_WIDTH.put('9', 6);
        CHAR_WIDTH.put(':', 2);
        CHAR_WIDTH.put(';', 2);
        CHAR_WIDTH.put('<', 5);
        CHAR_WIDTH.put('=', 6);
        CHAR_WIDTH.put('>', 5);
        CHAR_WIDTH.put('?', 6);
        CHAR_WIDTH.put('@', 8);
        CHAR_WIDTH.put('A', 6);
        CHAR_WIDTH.put('B', 6);
        CHAR_WIDTH.put('C', 6);
        CHAR_WIDTH.put('D', 6);
        CHAR_WIDTH.put('E', 6);
        CHAR_WIDTH.put('F', 6);
        CHAR_WIDTH.put('G', 6);
        CHAR_WIDTH.put('H', 6);
        CHAR_WIDTH.put('I', 4);
        CHAR_WIDTH.put('J', 6);
        CHAR_WIDTH.put('K', 6);
        CHAR_WIDTH.put('L', 6);
        CHAR_WIDTH.put('M', 6);
        CHAR_WIDTH.put('N', 6);
        CHAR_WIDTH.put('O', 6);
        CHAR_WIDTH.put('P', 6);
        CHAR_WIDTH.put('Q', 6);
        CHAR_WIDTH.put('R', 6);
        CHAR_WIDTH.put('S', 6);
        CHAR_WIDTH.put('T', 6);
        CHAR_WIDTH.put('U', 6);
        CHAR_WIDTH.put('V', 6);
        CHAR_WIDTH.put('W', 6);
        CHAR_WIDTH.put('X', 6);
        CHAR_WIDTH.put('Y', 6);
        CHAR_WIDTH.put('Z', 6);
        CHAR_WIDTH.put('[', 4);
        CHAR_WIDTH.put('\\', 6);
        CHAR_WIDTH.put(']', 4);
        CHAR_WIDTH.put('^', 6);
        CHAR_WIDTH.put('_', 6);
        CHAR_WIDTH.put('`', 3);
        CHAR_WIDTH.put('a', 6);
        CHAR_WIDTH.put('b', 6);
        CHAR_WIDTH.put('c', 6);
        CHAR_WIDTH.put('d', 6);
        CHAR_WIDTH.put('e', 6);
        CHAR_WIDTH.put('f', 5);
        CHAR_WIDTH.put('g', 6);
        CHAR_WIDTH.put('h', 6);
        CHAR_WIDTH.put('i', 2);
        CHAR_WIDTH.put('j', 6);
        CHAR_WIDTH.put('k', 5);
        CHAR_WIDTH.put('l', 3);
        CHAR_WIDTH.put('m', 6);
        CHAR_WIDTH.put('n', 6);
        CHAR_WIDTH.put('o', 6);
        CHAR_WIDTH.put('p', 6);
        CHAR_WIDTH.put('q', 6);
        CHAR_WIDTH.put('r', 6);
        CHAR_WIDTH.put('s', 6);
        CHAR_WIDTH.put('t', 4);
        CHAR_WIDTH.put('u', 6);
        CHAR_WIDTH.put('v', 6);
        CHAR_WIDTH.put('w', 6);
        CHAR_WIDTH.put('x', 6);
        CHAR_WIDTH.put('y', 6);
        CHAR_WIDTH.put('z', 6);
        CHAR_WIDTH.put('{', 4);
        CHAR_WIDTH.put('|', 2);
        CHAR_WIDTH.put('}', 4);
        CHAR_WIDTH.put('~', 7);
        CHAR_WIDTH.put('°', 4);
        CHAR_WIDTH.put(twoPixelPad[Section.HEADER.get()], 2);
        CHAR_WIDTH.put(twoPixelPad[Section.BODY.get()], 2);
        CHAR_WIDTH.put(onePixelPad[Section.HEADER.get()], 1);
        CHAR_WIDTH.put(onePixelPad[Section.BODY.get()], 1);
    }

    public void setTextColor(Section section, ChatColor color) {
        textColor[section.get()] = color;
    }

    public void setOnePixedPad(Section section, Character pad) {
        onePixelPad[section.get()] = pad;
    }

    public void setTwoPixedPad(Section section, Character pad) {
        twoPixelPad[section.get()] = pad;
    }

    public void setDecoration(Section section, Character decoration) {
        this.decoration[section.get()] = decoration;
    }

    public void setDelimiter(Section section, Character delimiter) {
        this.delimiter[section.get()] = delimiter;
    }

    public void addHeader(String header, int headerPixels) {
        headers.add(format(header, headerPixels, Section.HEADER, Alignment.CENTER));
        headerWidth.add(headerPixels);
    }

    public void addRow(List<String> values) {
        List<String> row = new ArrayList<>();
        for (int i = 0; i < values.size() && i < headers.size() && i < headerWidth.size(); i++) {
            String value = format(String.format(" %s", values.get(i)), headerWidth.get(i), Section.BODY, Alignment.LEFT);
            row.add(value);
        }

        body.add(row);
    }

    private int getPixelWidth(String value) {
        int width = 0;
        String cleanValue = value.replaceAll("§\\w", "");
        for (Character chr : cleanValue.toCharArray()) {
            Integer size = CHAR_WIDTH.get(chr);
            width += size == null ? 0 : size;
        }

        return width;
    }

    private String getPadding(int pixelWidth, Section section) {
        //Pad:
        StringBuilder padding = new StringBuilder();
        Integer padPixels = CHAR_WIDTH.get(decoration[section.get()]);
        if (padPixels != null && padPixels > 0) {
            int count = pixelWidth / padPixels;
            for (int i = 0; i < count; i++) {
                padding.append(decoration[section.get()]);
            }
        }

        //Alignment issues:
        int remainingPixels = pixelWidth - getPixelWidth(padding.toString());
        if (remainingPixels > 0) {
            //2-pixel character padding:
            while (remainingPixels > 1) {
                padding.append(twoPixelPad[section.get()]);
                remainingPixels -= 2;
            }

            //Single-pixel character padding:
            if (remainingPixels > 0) {
                padding.append(onePixelPad[section.get()]);
            }
        }

        return padding.toString();
    }

    private String pad(String value, int pixelWidth, Section section, Alignment align) {
        StringBuilder paddedValue = new StringBuilder();
        int remainingPixels = pixelWidth - getPixelWidth(value);
        switch (align) {
            case LEFT:
                paddedValue.append(value);
                paddedValue.append(getPadding(remainingPixels, section));
                break;
            case CENTER:
                int leftHalf = remainingPixels / 2;
                int rightHalf = remainingPixels - leftHalf;
                paddedValue.append(getPadding(leftHalf, section));
                paddedValue.append(value);
                paddedValue.append(getPadding(rightHalf, section));
                break;
            case RIGHT:
                paddedValue.append(getPadding(remainingPixels, section));
                paddedValue.append(value);
                paddedValue.append(value);
                break;
        }

        return paddedValue.toString();
    }

    private String format(String value, int columnPixels, Section section, Alignment align) {
        String formattedValue = value;
        while (formattedValue.length() > 3 && getPixelWidth(formattedValue) >= columnPixels) {
            formattedValue = String.format(
                    "%s...",
                    formattedValue.substring(0, formattedValue.length() - 4));
        }

        formattedValue = String.format(
                "%s%s%s",
                textColor[section.get()],
                formattedValue,
                gridColor);

        return pad(formattedValue, columnPixels, section, align);
    }

    @Override
    public String toString() {
        //Delimiters:
        int delimiterWidth;
        int headerDelimiterWidth = CHAR_WIDTH.get(delimiter[Section.HEADER.get()]);
        int bodyDelimiterWidth = CHAR_WIDTH.get(delimiter[Section.BODY.get()]);
        String[] delimiters = new String[2];
        if (headerDelimiterWidth > bodyDelimiterWidth) {
            delimiterWidth = headerDelimiterWidth;
            delimiters[Section.HEADER.get()] = delimiter[Section.HEADER.get()].toString();
            delimiters[Section.BODY.get()] = pad(
                    delimiter[Section.BODY.get()].toString(),
                    headerDelimiterWidth,
                    Section.BODY,
                    Alignment.CENTER);
        } else {
            delimiterWidth = bodyDelimiterWidth;
            delimiters[Section.HEADER.get()] = pad(
                    delimiter[Section.HEADER.get()].toString(),
                    bodyDelimiterWidth,
                    Section.HEADER,
                    Alignment.CENTER);

            delimiters[Section.BODY.get()] = delimiter[Section.BODY.get()].toString();
        }

        //Header:
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append(gridColor);
        tableBuilder.append(delimiters[Section.HEADER.get()]);
        tableBuilder.append(String.join(delimiters[Section.HEADER.get()], headers));
        tableBuilder.append(delimiters[Section.HEADER.get()]);

        //Title (uses full header width to center):
        int tableWidth = getPixelWidth(tableBuilder.toString());
        if (title.length() > 0) {
            String formattedValue = String.format(
                    "%s%s%s",
                    textColor[Section.HEADER.get()],
                    title,
                    gridColor);

            tableBuilder.insert(0, String.format(
                    "\n%s%s\n",
                    gridColor,
                    pad(formattedValue, tableWidth, Section.HEADER, Alignment.CENTER)));
        }

        //Body:
        if (body.size() == 0) {
            //No data:
            String formattedValue = String.format(
                    "%s%s%s",
                    textColor[Section.BODY.get()],
                    Lang.TABLE_EMPTY.get(),
                    gridColor);

            tableBuilder.append("\n");
            tableBuilder.append(gridColor);
            tableBuilder.append(delimiters[Section.BODY.get()]);
            tableBuilder.append(pad(
                    formattedValue,
                    tableWidth - delimiterWidth * 2,
                    Section.BODY,
                    Alignment.CENTER));

            tableBuilder.append(delimiters[Section.BODY.get()]);
        } else {
            //Row data:
            for (List<String> row : body) {
                tableBuilder.append("\n");
                tableBuilder.append(gridColor);
                tableBuilder.append(delimiters[Section.BODY.get()]);
                tableBuilder.append(String.join(delimiters[Section.BODY.get()], row));
                tableBuilder.append(delimiters[Section.BODY.get()]);
            }
        }

        //Footer:
        tableBuilder.append("\n");
        tableBuilder.append(gridColor);
        tableBuilder.append(delimiters[Section.HEADER.get()]);
        List<String> footers = new ArrayList<>();
        for (int width : headerWidth) {
            footers.add(pad("", width, Section.HEADER, Alignment.LEFT));
        }

        tableBuilder.append(String.join(delimiters[Section.HEADER.get()], footers));
        tableBuilder.append(delimiters[Section.HEADER.get()]);
        tableBuilder.append("\n");

        return tableBuilder.toString();
    }

    /**
     * Replaces each token with a clickable-event, while converting the
     * surrounding text to JSON (for use as a tellraw command)
     *
     * @param gPlayer    message recipient
     * @param clickToken generic text to click (e.g., CLICK HERE)
     * @param command    the command to execute when clicked
     * @param tooltip    text to display on hover
     * @param uniqueIds  values to send with each token's command
     * @return a tellraw command with clickable links
     */
    public String toJson(GPlayer gPlayer, String clickToken, String command, String tooltip, List<Integer> uniqueIds) {
        String table = toString();

        //Generic tooltip for each link:
        String hoverEvent = "";
        if (!tooltip.isEmpty()) {
            hoverEvent = String.format(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"%s\"}", tooltip);
        }

        //Split the table where we will insert the links:
        String[] textBlocks = table.split(String.format("%s\\b", clickToken));

        //Add clickable links:
        StringBuilder jsonBuilder = new StringBuilder();
        for (String textBlock : textBlocks) {
            //JSON separator:
            if (jsonBuilder.length() > 0) {
                jsonBuilder.append(",");
            }

            //Text block:
            jsonBuilder.append(String.format(
                    "{\"text\":\"%s\"}",
                    textBlock));

            //Click block:
            if (uniqueIds.size() > 0) {
                int id = uniqueIds.remove(0);
                jsonBuilder.append(String.format(
                        ", {\"text\":\"%s\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"%s %d\"}%s}",
                        clickToken,
                        command,
                        id,
                        hoverEvent));
            }
        }

        //Tellraw command:
        // - Note: escaping newlines prior to execution
        return String.format(
                "tellraw %s [%s]",
                gPlayer.getOfflinePlayer().getName(),
                jsonBuilder.toString().replace("\n", "\\n"));
    }
}
