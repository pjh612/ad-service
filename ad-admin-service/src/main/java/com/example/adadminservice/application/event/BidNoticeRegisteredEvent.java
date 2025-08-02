package com.example.adadminservice.application.event;

import com.example.adadminservice.common.outbox.OutboxEvent;

import java.time.Instant;
import java.util.UUID;

public record BidNoticeRegisteredEvent(
        UUID bidNoticeId,
        String title,
        UUID adZoneId,
        Instant bidStartAt,
        Instant bidEndAt,
        Long minPrice,
        UUID authorId,
        String authorName,
        Instant eventTimestamp
) implements OutboxEvent<UUID, BidNoticeRegisteredEvent> {

    @Override
    public UUID aggregateId() {
        return bidNoticeId;
    }

    @Override
    public String aggregateType() {
        return "BidNotice";
    }

    @Override
    public String type() {
        return "BidNoticeRegistered";
    }

    @Override
    public Instant timestamp() {
        return eventTimestamp;
    }

    @Override
    public BidNoticeRegisteredEvent payload() {
        return this;
    }
}
