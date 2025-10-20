package org.example.unifundemo.repository

import org.example.unifundemo.domain.accounting.RevenueShare
import org.springframework.data.jpa.repository.JpaRepository

interface RevenueShareRepository : JpaRepository<RevenueShare, Long>