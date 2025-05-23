package umg.edu.gt;

import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = "CristopherDamain";
        byte[] decoded = encoder.encode(encoded.getBytes());

        String decodedString = encoder.encodeToString(decoded);
        System.out.println(decodedString);
    }
}