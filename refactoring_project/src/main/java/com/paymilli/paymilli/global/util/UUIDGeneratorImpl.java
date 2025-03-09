package com.paymilli.paymilli.global.util;

import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class UUIDGeneratorImpl implements UUIDGenerator{

    @Override
    public UUID generateUUID() {
        return UlidCreator.getMonotonicUlid().toUuid();
    }
}
