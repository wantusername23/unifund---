package org.example.unifundemo.domain.vote

import jakarta.persistence.*
import org.example.unifundemo.domain.user.User
import org.example.unifundemo.domain.worldview.WorldView
import java.time.LocalDateTime

@Entity
class Vote(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_view_id", nullable = false)
    val worldView: WorldView,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: User,

    @Column(nullable = false)
    val topic: String, // 투표 주제 (e.g., "다음 에피소드에서 등장할 악당은?")

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var endsAt: LocalDateTime, // 투표 마감 시간

    @OneToMany(mappedBy = "vote", cascade = [CascadeType.ALL], orphanRemoval = true)
    val options: MutableList<VoteOption> = mutableListOf()
)