package com.shorturl.shorturl.util.randomGenerator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 4:45 오후
 */
@Component
public class ThreadLocalRandomNumberGenerator implements RandomNumberGenerator {
    @Override
    public int nextInt(int min, int bound) {
        return ThreadLocalRandom.current().nextInt(min, bound);
    }

    @Override
    public List<Integer> nextInts(int size, int bound) {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ints.add(ThreadLocalRandom.current().nextInt(bound));
        }
        return ints;
    }
}
