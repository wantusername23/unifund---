package org.example.unifundemo.domain.worldview

import jakarta.persistence.*

@Entity
class Character(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    var description: String, // 캐릭터 설정

    @Lob
    @Column(columnDefinition = "TEXT")
    var relationships: String, // 다른 캐릭터와의 관계도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_view_id")
    val worldView: WorldView
)