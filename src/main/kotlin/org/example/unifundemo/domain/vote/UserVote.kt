package org.example.unifundemo.domain.vote

import jakarta.persistence.*
import org.example.unifundemo.domain.user.User

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "vote_id"])])
class UserVote(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    val vote: Vote,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_option_id", nullable = false)
    val voteOption: VoteOption
)