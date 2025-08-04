package com.example.adservice.application.in.dto;

import java.util.UUID;

public record BidAdvertisementRequest(UUID bidNoticeForAdZoneId, UUID advertiserId, Long amount) {
}
