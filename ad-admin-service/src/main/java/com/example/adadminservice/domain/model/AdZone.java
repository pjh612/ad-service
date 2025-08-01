package com.example.adadminservice.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Getter
@Setter
public class AdZone {
    private UUID id;
    private String name;
    private String description;
    private int width;
    private int height;
    private AdZoneState adZoneState;

    public AdZone(UUID id, String name, String description, int width, int height, AdZoneState adZoneState) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("구좌 이름을 입력하세요");
        }

        if (!StringUtils.hasText(description)) {
            throw new IllegalArgumentException("구좌 설명을 입력하세요");
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("구좌 크기는 0 이상이어야 합니다.");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.adZoneState = adZoneState;
    }

    public static AdZone create(String name, String description, int width, int height) {
        return new AdZone(null, name, description, width, height, AdZoneState.EMPTY);
    }
}
