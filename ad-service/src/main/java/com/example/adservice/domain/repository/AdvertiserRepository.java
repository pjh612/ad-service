package com.example.adservice.domain.repository;

import com.example.adservice.domain.model.Advertiser;

import java.util.Optional;
import java.util.UUID;

public interface AdvertiserRepository {
    Optional<Advertiser> findById(UUID id);

    Optional<Advertiser> findByUsername(String username);

    Advertiser save(Advertiser advertiser);
}
