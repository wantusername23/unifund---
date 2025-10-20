package org.example.unifundemo.domain.report

enum class ReportStatus {
    PENDING,  // 처리 대기 중
    RESOLVED, // 처리 완료 (표절 인정)
    DISMISSED // 기각
}