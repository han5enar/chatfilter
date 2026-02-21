package com.chatfilter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.regex.Pattern;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatFilterMod implements ClientModInitializer {

    private static final LinkedHashMap<Pattern, String> RULES = new LinkedHashMap<>();

    static {
        r("\\boh[\\s_\\-]*my[\\s_\\-]*god\\b",      "oh my gosh");
        r("\\bjerk[\\s_\\-]*off\\b",                  "break the Law of Chastity");
        r("\\bgod[\\s_\\-]*dammit\\b",                "dangit");
        r("\\bim[\\s_\\-]*wet\\b",                    "");
        r("\\bcocksucker\\b",    "suckup");
        r("\\bdumbass\\b",       "idiot");
        r("\\bjackass\\b",       "jerk");
        r("\\bbadass\\b",        "cool");
        r("\\basshole\\b",       "jerk");
        r("\\bdammit\\b",        "dangit");
        r("\\basses\\b",         "butts");
        r("\\bass\\b",           "butt");
        r("\\bbastard\\b",       "idiot");
        r("\\bbitch\\b",         "bench");
        r("\\bcunt\\b",          "expletive");
        r("\\bdamn\\b",          "dang");
        r("\\bfaggot\\b",        "gay");
        r("\\bfags\\b",          "gays");
        r("\\bfag\\b",           "gay");
        r("\\bf+u+c+k+\\w*\\b",  "freak");
        r("\\bph+u+c+k+\\w*\\b", "freak");
        r("\\bgod\\b",           "gosh");
        r("\\bhell\\b",          "heck");
        r("\\bniggers\\b",       "people");
        r("\\bnigger\\b",        "man");
        r("\\bnigga\\b",         "bruh");
        r("\\bomg\\b",           "omgosh");
        r("\\bpissed\\b",        "ticked");
        r("\\bpiss\\b",          "pee");
        r("\\bpussies\\b",       "softies");
        r("\\bpussy\\b",         "softie");
        r("\\bsh!t\\b",          "");
        r("\\bshit+\\b",         "crap");
        r("\\bslut\\b",          "tramp");
        r("\\bsybau\\b",         "shut up");
        r("\\btits\\b",          "chest");
        r("\\btwats\\b",         "dumbos");
        r("\\btwat\\b",          "dumbo");
        r("\\bwhore\\b",         "tramp");
        r("\\bwtf\\b",           "wtfrick");
        r("\\be621\\b",          "porn");
        r("\\bdick\\b",          "");
    }

    private static void r(String regex, String replacement) {
        RULES.put(Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), replacement);
    }

    public static String filterText(String input) {
        String result = input;
        for (Map.Entry<Pattern, String> entry : RULES.entrySet()) {
            result = entry.getKey().matcher(result).replaceAll(entry.getValue());
        }
        return result.replaceAll("  +", " ").trim();
    }

    @Override
    public void onInitializeClient() {
        // For player chat: block the original, re-add filtered version to HUD
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            String original = message.getString();
            String filtered = filterText(original);
            if (!filtered.equals(original)) {
                // Schedule on main thread so ChatHud is ready
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.inGameHud != null) {
                        mc.inGameHud.getChatHud().addMessage(Text.literal(filtered));
                    }
                });
                return false; // block original
            }
            return true; // no changes needed, show as-is
        });

        // For server/system messages: modify directly
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
            String original = message.getString();
            String filtered = filterText(original);
            if (!filtered.equals(original)) {
                return Text.literal(filtered).setStyle(message.getStyle());
            }
            return message;
        });

        System.out.println("[ChatFilter] Loaded " + RULES.size() + " substitution rules.");
    }
}
