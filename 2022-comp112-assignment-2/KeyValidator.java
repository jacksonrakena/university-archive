// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import ecs100.UI;

/**
 * Key:
 * Core: Method must report whether the key is valid, or
 * report that it is invalid and give one reason why it is invalid.
 * To be valid, the key must
 * - be at least 8 characters and at most 16 characters long,
 * - not end with the special characters '#' or '$',
 * - not have a hyphen ('-') character anywhere
 * 
 * Completion: Method should either report that the key is valid, or
 * report that it is invalid and list ALL the reasons that it is invalid.
 * To be valid, the key must
 * - satisfy all of the conditions above AND
 * - have at least one Upper case character and at least one Lower case
 * character,
 * - not start with the same character as the first character of the user's name
 * - contain either a '#' or a '$', but not both.
 * Challenge: Same as completion, except that to be valid, the key must
 * - satisfy all of the conditions above AND
 * - have a mix of numbers and letters
 * - not contain the user's name spelled backwards, case insensitive.
 * (eg if name is Peter, it does not contain "ReTEp", or "RETEP" or "retep",
 * or...)
 *
 * Hint. Look at the documentation in the String class.
 * You will definitely find the length(), endsWith(...), and contains(...)
 * methods to be helpful
 */

class RuleCheckingContext {
    String input;
    String userName;

    public RuleCheckingContext(String input, String userName) {
        this.input = input;
        this.userName = userName;
    }
}

class KeyRule {
    public String description;
    public Predicate<RuleCheckingContext> tester;

    public KeyRule(String description, Predicate<RuleCheckingContext> tester) {
        this.description = description;
        this.tester = tester;
    }

    public boolean test(String value, String userName) {
        return tester.test(new RuleCheckingContext(value, userName));
    }
}

public class KeyValidator {

    public static final ArrayList<KeyRule> CORE_RULES = new ArrayList<>();

    public static final ArrayList<KeyRule> COMPLETION_RULES = new ArrayList<>();

    public static final ArrayList<KeyRule> CHALLENGE_RULES = new ArrayList<>();

    public void setupRules() {
        // Core rules
        CORE_RULES.add(new KeyRule(
                "Key must be at least 8 characters long", (e) -> e.input.length() >= 8));

        CORE_RULES.add(new KeyRule("Key must not be longer than 16 characters", (e) -> e.input.length() <= 16));

        CORE_RULES.add(new KeyRule("The key must not end with special characters '#' or '$'",
                (e) -> !e.input.endsWith("#") && !e.input.endsWith("$")));

        CORE_RULES.add(new KeyRule("The key must not contain a hyphen '-' character.", (e) -> !e.input.contains("-")));

        // Completion rules
        COMPLETION_RULES.addAll(CORE_RULES);
        COMPLETION_RULES.add(
                new KeyRule("The key must have at least one upper-case character and at least one lower-case character",
                        (e) -> Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).+$").matcher(e.input).matches()));

        COMPLETION_RULES.add(new KeyRule(
                "The key must not start with the same character as the first character of your name.",
                (e) -> !e.input.startsWith(e.userName.substring(0, 1))));
        COMPLETION_RULES.add(
                new KeyRule("The key must contain at least one '#' or at least one '$', but can not contain both.",
                        (e) -> {
                            if (e.input.contains("$") && e.input.contains("#"))
                                return false;
                            if (!e.input.contains("$") && !e.input.contains("#"))
                                return false;
                            return true;
                        }));
        // Challenge rules
        CHALLENGE_RULES.addAll(COMPLETION_RULES);

        CHALLENGE_RULES.add(new KeyRule(
                "The key must contain at least one number and at least one letter.",
                (e) -> Pattern.compile("^(?=.*[a-z]|[A-Z])(?=.*\\d).+$").matcher(e.input).matches()));

        CHALLENGE_RULES.add(new KeyRule(
                "The key must not contain your name spelled backwards (case-sensitive)",
                (e) -> {
                    return !e.input.toLowerCase()
                            .contains(new StringBuilder(e.userName.toLowerCase()).reverse().toString());
                }));
    }

    public void runAllRuleChecksAndPrintErrors(String input, String userName, ArrayList<KeyRule> rules) {
        ArrayList<String> errors = new ArrayList<>();
        for (KeyRule rule : rules) {
            if (!rule.test(input, userName)) {
                errors.add(rule.description);
            }
        }
        if (errors.size() > 0) {
            UI.println("Your key failed " + errors.size() + " rules:");
            for (String error : errors) {
                UI.println("- Failed: " + error);
            }
            UI.println("Tested against " + rules.size() + " rules on " + new Date().toString());
        } else {
            UI.println("Your key passed all " + rules.size() + " rules.");
        }
    }

    public String promptString(String prompt) {
        String value = "";
        boolean valid = false;
        while (!valid) {
            value = UI.askString(prompt);
            if (value.length() > 0)
                valid = true;
        }
        return value;
    }

    /**
     * Asks user for key word and then checks if it is a valid key word.
     */
    public void doCore() {
        UI.clearText();
        String key = promptString("Key:   ");
        UI.println();
        this.validateKeyCore(key);
    }

    /**
     * CORE
     * Report "Valid" or "Invalid: ...reason...."
     */
    public void validateKeyCore(String key) {
        for (KeyRule rule : CORE_RULES) {
            if (!rule.test(key, "")) {
                UI.println("Invalid: " + rule.description);
                return;
            }
        }
        UI.println("Your key passed all " + CORE_RULES.size() + " rules.");
    }

    /**
     * Asks user for key word and the name and then checks if it is a valid key
     * word.
     */
    public void doCompletion() {
        UI.clearText();
        String key = promptString("Key:   ");
        String name = promptString("Your name:   ");
        UI.println();
        this.validateKeyCompletion(key, name);
    }

    public void doChallenge() {
        UI.clearText();
        String key = promptString("Key:   ");
        String name = promptString("Your name:   ");
        UI.println();
        this.validateKeyChallenge(key, name);
    }

    /**
     * COMPLETION
     * Report that the key is valid or report ALL the rules that the key failed.
     */
    public void validateKeyCompletion(String key, String name) {
        runAllRuleChecksAndPrintErrors(key, name, COMPLETION_RULES);
    }

    /**
     * CHALLENGE
     */
    public void validateKeyChallenge(String key, String name) {
        runAllRuleChecksAndPrintErrors(key, name, CHALLENGE_RULES);
    }

    public void setupGUI() {
        UI.initialise();
        setupRules();
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Validate Key (Core)", this::doCore);
        UI.addButton("Validate Key (Completion)", this::doCompletion);
        UI.addButton("Validate Key (Challenge)", this::doChallenge);

        UI.addButton("Quit", UI::quit);
        UI.setDivider(1); // Expand the text area
    }

    public static void main(String[] args) {
        KeyValidator kv = new KeyValidator();
        kv.setupGUI();
    }
}
