package com.github.camelion.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Camelion
 * @since 21.03.17
 */
@Component
public class DutyService {
    private static final List<String> DUTIES = Arrays.asList("@ymqabot", "@neiwick");

    public String getDutyOnDate(LocalDateTime dateTime) {
        Collections.shuffle(DUTIES);

        return DUTIES.get(0);
    }
}
