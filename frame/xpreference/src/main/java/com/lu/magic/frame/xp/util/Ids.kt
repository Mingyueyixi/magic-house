package com.lu.magic.frame.xp.util

import java.util.concurrent.atomic.AtomicInteger

class Ids {
    companion object {
        private var requestAtomic = AtomicInteger()
        fun genRequestId(): String {
            return "${requestAtomic.incrementAndGet()}"
        }
    }
}