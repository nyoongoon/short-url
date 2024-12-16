package com.shorturl.shorturl.util.randomGenerator;

import java.util.List;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 4:44 오후
 */
public interface RandomNumberGenerator {
    int nextInt(int min, int bound);

    List<Integer> nextInts(int size, int bound);
}
