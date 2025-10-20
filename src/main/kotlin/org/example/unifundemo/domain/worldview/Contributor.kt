package org.example.unifundemo.domain.worldview

import jakarta.persistence.*
import org.example.unifundemo.domain.user.User

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "world_view_id"])])
class Contributor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_view_id")
    val worldView: WorldView,

    @Enumerated(EnumType.STRING)
    var permission: Permission
)