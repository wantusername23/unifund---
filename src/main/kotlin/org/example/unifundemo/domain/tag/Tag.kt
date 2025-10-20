package org.example.unifundemo.domain.tag

import jakarta.persistence.*

@Entity
class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val name: String
)