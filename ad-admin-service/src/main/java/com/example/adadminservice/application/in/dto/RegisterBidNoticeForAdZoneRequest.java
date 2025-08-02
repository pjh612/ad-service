package com.example.adadminservice.application.in.dto;

import java.time.Instant;
import java.util.UUID;

public record RegisterBidNoticeForAdZoneRequest(UUID zoneId, String title, String content, UUID authorId,
                                                String authorName, Instant adStartAt, Instant adEndAt,
                                                Instant bidStartAt, Instant bidEndAt,
                                                Long minPrice) {
}
