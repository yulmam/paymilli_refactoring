package com.paymilli.paymilli.global.util;

import java.util.Random;

public class RandomNumberGeneratorImpl implements RandomNumberGenerator{


    @Override
    public String generateRandomNumber() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }
}
