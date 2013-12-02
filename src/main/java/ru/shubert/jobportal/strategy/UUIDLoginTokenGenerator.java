package ru.shubert.jobportal.strategy;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation that uses UUID for token generation
 *
 */

@Service
public class UUIDLoginTokenGenerator implements ILoginTokenGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}

