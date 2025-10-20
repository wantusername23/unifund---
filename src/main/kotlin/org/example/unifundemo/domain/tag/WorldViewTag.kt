package org.example.unifundemo.domain.tag

import jakarta.persistence.*
import org.example.unifundemo.domain.worldview.WorldView

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["world_view_id", "tag_id"])])
class WorldViewTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_view_id")
    val worldView: WorldView,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag
)