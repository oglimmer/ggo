package de.oglimmer.ggo.random;

import java.util.Random;

public class RandomName {

    private static final String[] ADJECTIVES = {
            "Agile", "Bold", "Bright", "Calm", "Clever", "Courageous", "Daring",
            "Delightful", "Energetic", "Fanciful", "Friendly", "Gentle", "Humble",
            "Ingenious", "Jolly", "Keen", "Lively", "Mighty", "Noble", "Playful",
            "Quick", "Radiant", "Steady", "Sunny", "Tenacious", "Vibrant", "Wise", "Zealous"
    };

    private static final String[] NOUNS = {
            "Lion", "Tiger", "Falcon", "Wolf", "Fox", "Eagle", "Bear", "Otter",
            "Panda", "Hawk", "Panther", "Dolphin", "Rhino", "Stallion", "Phoenix",
            "Sparrow", "Rabbit", "Owl", "Shark", "Cobra"
    };

    private static final Random RANDOM = new Random();

    /**
     * Returns a “good” random name by picking a random adjective + noun.
     * If you pass in i ≥ 0, it will seed the RNG for reproducible names;
     * if you pass in i < 0, it will use true randomness.
     */
    public static String getName() {
        Random rng = RANDOM;
        String adjective = ADJECTIVES[rng.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[rng.nextInt(NOUNS.length)];
        // If you want to suffix the number, uncomment the next line:
        // return adjective + noun + i;
        return adjective + noun;
    }
}
