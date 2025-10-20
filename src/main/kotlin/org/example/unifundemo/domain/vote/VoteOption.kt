package org.example.unifundemo.domain.vote

import jakarta.persistence.*

@Entity
class VoteOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    val vote: Vote,

    @Column(nullable = false)
    val text: String, // 투표 항목 내용

    var voteCount: Int = 0
)