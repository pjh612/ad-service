package com.example.adservice.domain.model;

public enum BidNoticeState {
    READY, // 공고 개시
    BIDDING, // 입찰 중
    CLOSED, //확정 대기
    ASSIGNED, // 확정
    CANCELED // 취소
}
