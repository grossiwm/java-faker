package com.github.javafaker;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bitcoin {
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    private static final Map<String, Integer> PROTOCOL_VERSIONS = new HashMap<String, Integer>() {{
        put("main", 0);
        put("testnet", 111);
    }};

    public static String address() {
        return addressFor("main");
    }

    public static String testnetAddress() {
        return addressFor("testnet");
    }

    public static String generateTransactionHash() {
        try {
            byte[] randomData = new byte[32];
            SecureRandom random = new SecureRandom();
            random.nextBytes(randomData);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(randomData);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static String addressFor(String network) {
        Integer version = PROTOCOL_VERSIONS.get(network);
        if (version == null) {
            throw new IllegalArgumentException("Invalid network specified");
        }

        byte[] addressBytes = new byte[21];
        addressBytes[0] = version.byteValue();

        Random random = new Random();
        byte[] randomBytes = new byte[20];
        random.nextBytes(randomBytes);
        System.arraycopy(randomBytes, 0, addressBytes, 1, 20);

        byte[] checksum = calculateChecksum(addressBytes);

        byte[] fullAddress = new byte[25];
        System.arraycopy(addressBytes, 0, fullAddress, 0, 21);
        System.arraycopy(checksum, 0, fullAddress, 21, 4);

        return encodeBase58(fullAddress);
    }

    private static byte[] calculateChecksum(byte[] data) {

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash1 = sha256.digest(data);
            byte[] hash2 = sha256.digest(hash1);
            byte[] checksum = new byte[4];
            System.arraycopy(hash2, 0, checksum, 0, 4);
            return checksum;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private static String encodeBase58(byte[] input) {
        java.math.BigInteger lv = java.math.BigInteger.ZERO;
        for (int i = 0; i < input.length; i++) {
            int value = input[input.length - 1 - i] & 0xFF;
            lv = lv.add(java.math.BigInteger.valueOf(value).multiply(java.math.BigInteger.valueOf(256).pow(i)));
        }

        StringBuilder ret = new StringBuilder();
        while (lv.compareTo(java.math.BigInteger.ZERO) > 0) {
            java.math.BigInteger[] divmod = lv.divideAndRemainder(java.math.BigInteger.valueOf(BASE));
            lv = divmod[0];
            int mod = divmod[1].intValue();
            ret.append(ALPHABET.charAt(mod));
        }

        for (int i = 0; i < input.length && input[i] == 0; i++) {
            ret.append(ALPHABET.charAt(0));
        }

        return ret.reverse().toString();
    }
}
