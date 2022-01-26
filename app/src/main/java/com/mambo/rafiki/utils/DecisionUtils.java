package com.mambo.rafiki.utils;

public class DecisionUtils {

    //decision message
    public static final String DO = "Go for it!";
    public static final String WAIT = "Probably wait ...";
    public static final String THINK = "Think more about it ...";
    public static final String DO_N0T = "Don't do it!";

    public static final String START = "Good idea";

    //decision
    public static final int YES = 2000;
    public static final int NO = 2001;
    public static final int UNCERTAIN = 2002;

    public static String getDecisionByCode(int code) {
        switch (code) {
            case YES:
                return "Decided to do it!";

            case NO:
                return "Decided not to do it!";

            case UNCERTAIN:
            default:
                return "Uncertain";
        }
    }

    public static String getSuggestionMessage(int probability) {
        switch (probability) {

            case 5:
            case 4:
            case 3:
                return DO;

            case 2:
                return WAIT;

            case 1:
            case 0:
            case -1:
                return THINK;

            case -2:
                return WAIT;

            case -3:
            case -4:
            case -5:
                return DO_N0T;

            default:
                return START;
        }
    }

    public static int getSuggestion(int probability) {
        switch (probability) {

            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                return YES;

            case 0:
                return UNCERTAIN;

            case -1:
            case -2:
            case -3:
            case -4:
            case -5:
                return NO;

            default:
                return UNCERTAIN;
        }
    }

    public static String getEmojiWithDifference(int difference) {
        switch (difference) {

            case 5:
            case 4:
                return getEmojiByUnicode(0x1F601);

            case 3:
            case 2:
                return getEmojiByUnicode(0x1F928);

            case 1:
            case 0:
            case -1:
                return getEmojiByUnicode(0x1F914);

            case -2:
            case -3:
                return getEmojiByUnicode(0x1F928);

            case -4:
            case -5:
                return getEmojiByUnicode(0x1F61E);

            default:
                return getEmojiByUnicode(0x1F62F);
        }
    }

    public static String getEmojiPositiveIntensity(int intensity) {
        switch (intensity) {

            case 2:
                return getEmojiByUnicode(0x1F642);

            case 3:
                return getEmojiByUnicode(0x1F60A);

            case 4:
                return getEmojiByUnicode(0x1F600);

            case 5:
                return getEmojiByUnicode(0x1F60D);

            case 1:
            default:
                return getEmojiByUnicode(0x1F610);
        }
    }

    public static String getEmojiNegativeIntensity(int intensity) {
        switch (intensity) {

            case 2:
                return getEmojiByUnicode(0x1F641);

            case 3:
                return getEmojiByUnicode(0x2639);

            case 4:
                return getEmojiByUnicode(0x1F61E);

            case 5:
                return getEmojiByUnicode(0x1F61F);

//            return getEmojiByUnicode(0x1F62D);

            case 1:
            default:
                return getEmojiByUnicode(0x1F610);
        }
    }

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
