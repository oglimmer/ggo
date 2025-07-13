package de.oglimmer.ggo.random;

public class RandomString {
    public static String getRandomStringHex(int i) {
        return Integer.toHexString((int) (i * Math.random() * 100000));
    }
}
