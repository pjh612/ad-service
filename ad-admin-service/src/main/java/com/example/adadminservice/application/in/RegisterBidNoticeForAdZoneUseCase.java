package com.example.adadminservice.application.in;


import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneRequest;
import com.example.adadminservice.application.in.dto.RegisterBidNoticeForAdZoneResponse;

public interface RegisterBidNoticeForAdZoneUseCase {
    RegisterBidNoticeForAdZoneResponse register(RegisterBidNoticeForAdZoneRequest request);
}
