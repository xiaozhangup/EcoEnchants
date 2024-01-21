package com.willfp.ecoenchants.utils

import com.google.common.collect.Maps
import org.jetbrains.annotations.NotNull
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

abstract class Baffle {
    abstract fun resetAll()
    abstract fun reset(id: String)
    abstract fun next(id: String)
    abstract fun hasNext(id: String, update: Boolean): Boolean
    fun reset() {
        reset("*")
    }

    fun next() {
        next("*")
    }

    fun hasNext(): Boolean {
        return hasNext("*")
    }

    fun hasNext(id: String): Boolean {
        return hasNext(id, true)
    }

    companion object {
        @NotNull
        fun of(duration: Long, timeUnit: TimeUnit): Baffle {
            return BaffleTime(timeUnit.toMillis(duration))
        }

        @NotNull
        fun of(count: Int): Baffle {
            return BaffleCounter(count)
        }
    }

    class BaffleTime(private val millis: Long) : Baffle() {
        private val data = Maps.newConcurrentMap<String, Long>()
        private var globalTime: Long = 0

        fun nextTime(id: String): Long {
            val result: Long = if (id == "*") {
                globalTime + millis - System.currentTimeMillis()
            } else {
                data.getOrDefault(id, 0L) + millis - System.currentTimeMillis()
            }
            return if (result >= 0) result else 0L
        }

        override fun resetAll() {
            data.clear()
            globalTime = 0L
        }

        override fun reset(id: String) {
            if (id == "*") {
                globalTime = 0L
            } else {
                data.remove(id)
            }
        }

        override fun next(id: String) {
            if (id == "*") {
                globalTime = System.currentTimeMillis()
            } else {
                data[id] = System.currentTimeMillis()
            }
        }

        override fun hasNext(id: String, update: Boolean): Boolean {
            val time = if (id == "*") {
                globalTime
            } else {
                data.getOrDefault(id, 0L)
            }
            return if (time + millis < System.currentTimeMillis()) {
                if (update) {
                    next(id)
                }
                true
            } else {
                false
            }
        }
    }

    class BaffleCounter(private val count: Int) : Baffle() {
        private val data = Maps.newConcurrentMap<String, Int>()
        private val globalCount = AtomicInteger()

        override fun resetAll() {
            data.clear()
            globalCount.set(0)
        }

        override fun reset(id: String) {
            if (id == "*") {
                globalCount.set(0)
            } else {
                data.remove(id)
            }
        }

        override fun next(id: String) {
            if (id == "*") {
                globalCount.incrementAndGet()
            } else {
                data[id] = data.computeIfAbsent(id) { 0 } + 1
            }
        }

        override fun hasNext(id: String, update: Boolean): Boolean {
            return if (id == "*") {
                if (globalCount.get() < count) {
                    if (update) {
                        globalCount.incrementAndGet()
                    }
                    false
                } else {
                    if (update) {
                        globalCount.set(0)
                    }
                    true
                }
            } else {
                val i: Int = data.getOrDefault(id, 0)
                return if (i < count) {
                    if (update) {
                        data[id] = i + 1
                    }
                    false
                } else {
                    if (update) {
                        data[id] = 0
                    }
                    true
                }
            }
        }
    }
}