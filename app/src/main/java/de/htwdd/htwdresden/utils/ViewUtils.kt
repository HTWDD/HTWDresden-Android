package de.htwdd.htwdresden.utils

import android.content.Context
import android.widget.TextView
import de.htwdd.htwdresden.R

object ViewUtils {
    fun setBeginAndEndTime(
        beginView: TextView,
        endView: TextView,
        rowNumber: Int,
        context: Context
    ) {
        context.resources.apply {
            when (rowNumber) {
                1 -> {
                    beginView.text = getString(R.string.lesson1_begin_time)
                    endView.text = getString(R.string.lesson1_end_time)
                }
                3 -> {
                    beginView.text = getString(R.string.lesson2_begin_time)
                    endView.text = getString(R.string.lesson2_end_time)
                }
                5 -> {
                    beginView.text =
                        getString(R.string.lesson3_begin_time)
                    endView.text = getString(R.string.lesson3_end_time)
                }
                7 -> {
                    beginView.text =
                        getString(R.string.lesson4_begin_time)
                    endView.text = getString(R.string.lesson4_end_time)
                }
                9 -> {
                    beginView.text =
                        getString(R.string.lesson5_begin_time)
                    endView.text = getString(R.string.lesson5_end_time)
                }
                11 -> {
                    beginView.text =
                        getString(R.string.lesson6_begin_time)
                    endView.text = getString(R.string.lesson6_end_time)
                }
                13 -> {
                    beginView.text =
                        getString(R.string.lesson7_begin_time)
                    endView.text = getString(R.string.lesson7_end_time)
                }
                15 -> {
                    beginView.text =
                        getString(R.string.lesson8_begin_time)
                    endView.text = getString(R.string.lesson8_end_time)
                }
            }
        }
    }
}