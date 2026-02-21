package com.chatfilter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatFilterMod implements ClientModInitializer {

    // Each entry: regex pattern -> replacement string
    // Using word-boundary (\b) matching where appropriate, case-insensitive
    // Ordered from most specific to least specific to avoid partial conflicts
    private static final LinkedHashMap<Pattern, String> RULES = new LinkedHashMap<>();

    static {
        // --- Phrases first (most specific) ---
        r("\\boh[\\s_\\-]*my[\\s_\\-]*god\\b",     "oh my gosh");
        r("\\bjerk[\\s_\\-]*off\\b",                 "break the Law of Chastity");
        r("\\bgod[\\s_\\-]*dammit\\b",               "dangit");
        r("\\bim[\\s_\\-]*wet\\b",                   "");  // remove

        // --- Multi-word / compound words ---
        r("\\bcocksucker\\b",   "suckup");
        r("\\bdumbass\\b",      "idiot");
        r("\\bjackass\\b",      "jerk");
        r("\\bbadass\\b",       "cool");
        r("\\basshole\\b",      "jerk");
        r("\\bdammit\\b",       "dangit");

        // --- Single words ---
        r("\\basses\\b",        "butts");
        r("\\bass\\b",          "butt");
        r("\\bbastard\\b",      "idiot");
        r("\\bbitch\\b",        "bench");
        r("\\bcunt\\b",         "expletive");
        r("\\bdamn\\b",         "dang");
        r("\\bfaggot\\b",       "gay");
        r("\\bfags\\b",         "gays");
        r("\\bfag\\b",          "gay");
        r("\\bf+u+c+k+\\w*\\b", "freak");  // fuck, fuking, phucking etc.
        r("\\bph+u+c+k+\\w*\\b","freak");
        r("\\bgod\\b",          "gosh");
        r("\\bhell\\b",         "heck");
        r("\\bniggers\\b",      "people");
        r("\\bnigger\\b",       "man");
        r("\\bnigga\\b",        "bruh");
        r("\\bomg\\b",          "omgosh");
        r("\\bpissed\\b",       "ticked");
        r("\\bpiss\\b",         "pee");
        r("\\bpussies\\b",      "softies");
        r("\\bpussy\\b",        "softie");
        r("\\bsh!t\\b",         "");        // remove leet-speak variant
        r("\\bshit+\\b",        "crap");
        r("\\bslut\\b",         "tramp");
        r("\\bsybau\\b",        "shut up");
        r("\\btits\\b",         "chest");
        r("\\btwats\\b",        "dumbos");
        r("\\btwat\\b",         "dumbo");
        r("\\bwhore\\b",        "tramp");
        r("\\bwtf\\b",          "wtfrick");
        r("\\be621\\b",         "porn");
        r("\\bdick\\b",         "");        // remove (no sub defined in APF)
    }

    private static void r(String regex, String replacement) {
        RULES.put(Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), replacement);
    }

    public static String filterText(String input) {
        String result = input;
        for (Map.Entry<Pattern, String> entry : RULES.entrySet()) {
            result = entry.getKey().matcher(result).replaceAll(entry.getValue());
        }
        // Clean up any double spaces left by removed words
        result = result.replaceAll("  +", " ").trim();
        return result;
    }

    @Override
    public void onInitializeClient() {
        // Filter incoming chat messages from other players
        ClientReceiveMessageEvents.MODIFY_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            String original = message.getString();
            String filtered = filterText(original);
            if (filtered.equals(original)) return message;

            // Preserve the original style (color, formatting) from the message
            Style style = message.getStyle();
            return Text.literal(filtered).setStyle(style);
        });

        // Also filter game/system messages (e.g. from server plugins using /say or death messages)
        ClientReceiveMessageEvents.MODIFY_GAME_MESSAGE.register((message, overlay) -> {
            String original = message.getString();
            String filtered = filterText(original);
            if (filtered.equals(original)) return message;

            Style style = message.getStyle();
            return Text.literal(filtered).setStyle(style);
        });

        System.out.println("[ChatFilter] Loaded with " + RULES.size() + " substitution rules.");
    }
}
