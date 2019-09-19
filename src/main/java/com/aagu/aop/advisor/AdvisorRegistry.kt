package com.aagu.aop.advisor

interface AdvisorRegistry {
    fun registerAdvisor(ad: Advisor)
    fun getAdvisors(): List<Advisor>
}