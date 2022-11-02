package com.aiven.simplechoose.sort

import org.junit.Test
import java.util.*
import kotlin.Comparator

class SortTest {

    @Test
    fun sortTest() {
        val list = arrayListOf<SortBean>(
            SortBean(10, 1),
            SortBean(4, 1),
            SortBean(32, null),
            SortBean(32, 43),
            SortBean(43, null),
            SortBean(43, 122),
            SortBean(54, 90),
            SortBean(12, null)
        )
        for (item in list) {
            println(item)
        }
        println("----------------------------")
//        Collections.sort(list, SortLevel(0, list.size))
//        for (item in list) {
//            println(item)
//        }
        println("----------------------------")
        Collections.sort(list, SortLevel(0, list.size))
        for (item in list) {
            println(item)
        }
        println("----------------------------")
        Collections.sort(list, SortLevel(1, list.size))
        for (item in list) {
            println(item)
        }
        list.sortWith(compareBy(nullsLast(), {it.age}))
        println("----------------------------")
        for (item in list) {
            println(item)
        }
        list.sortWith(compareByDescending(nullsFirst(), {it.age}))
        println("----------------------------")
        for (item in list) {
            println(item)
        }
    }
}

class SortLevel(private val sortBy: Int, private val size: Int): Comparator<SortBean> {
    override fun compare(o1: SortBean, o2: SortBean): Int {
        return if (sortBy == 0) {
            when {
                o1.level == null -> {
                    1
                }
                o2.level == null -> {
                    -1
                }
                o2 === o1 -> {
                    0
                }
                else -> {
                    o1.level!! - o2.level!!
                }
            }
        } else {
            when {
                o1.level == null -> {
                    1
                }
                o2.level == null -> {
                    -1
                }
                o2 === o1 -> {
                    0
                }
                else -> {
                    o2.level!! - o1.level!!
                }
            }
        }
    }
}

class SortAge(private val sortBy: Int): Comparator<SortBean> {
    override fun compare(o1: SortBean?, o2: SortBean?): Int {
        return if (sortBy == 1) {
            o2!!.age - o1!!.age
        } else {
            o1!!.age - o2!!.age
        }
    }

}