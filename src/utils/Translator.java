package utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.getDefault());

    public static String translate(String key) {
        return bundle.getString(key);
    }

    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n.messages", locale);
    }
}
