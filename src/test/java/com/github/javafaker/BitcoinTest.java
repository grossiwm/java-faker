package com.github.javafaker;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitcoinTest extends AbstractFakerTest {

    @Test
    public void testMainnetAddressGeneration() {
        String address = Bitcoin.address();
        assertNotNull("Address should not be null.", address);
        assertFalse("Address should not be empty.", address.isEmpty());
        assertTrue("Address should start with '1' or '3'.", address.startsWith("1") || address.startsWith("3"));
        assertTrue("Address length should be between 26 and 35 characters.",
                address.length() >= 26 && address.length() <= 35);
    }

    @Test
    public void testTestnetAddressGeneration() {
        String address = Bitcoin.testnetAddress();
        assertNotNull("Address should not be null.", address);
        assertFalse("Address should not be empty.", address.isEmpty());
        assertTrue("Address should start with 'm' or 'n'.", address.startsWith("m") || address.startsWith("n"));
        assertTrue("Address length should be between 26 and 35 characters.",
                address.length() >= 26 && address.length() <= 35);
    }

    @Test
    public void testGenerateTransactionHash() {
        String transactionHash = Bitcoin.generateTransactionHash();
        assertNotNull("Transaction hash should not be null.", transactionHash);
        assertEquals("Transaction hash should be 64 characters long.", 64, transactionHash.length());
        assertTrue("Transaction hash should contain only hexadecimal characters.", transactionHash.matches("^[0-9a-fA-F]+$"));
    }

}
