package com.example.adservice.domain.model;

public enum BidReviewStatus {
    PENDING,     // 대기
    REVIEWING,   // 리뷰 중
    REJECTED,    // 반려 (보완 요청)
    PASSED,  // 통과
}
