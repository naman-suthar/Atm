package com.naman.ezobooksaym

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel: ViewModel() {
    private var _atmState = MutableStateFlow(AtmMachine(0,0,0,0))
    var atmState = _atmState.asStateFlow()

    fun deposit(x2000: Int, x500:Int, x200:Int, x100:Int){
        _atmState.update {
            it.copy(
                note2000 = it.note2000+x2000,
                note500 = it.note500+x500,
                note200 = it.note200+x200,
                note100 = it.note100+x100
            )
        }
    }

    fun getBalance():Long{
        return ((atmState.value.note2000 * 2000) + (atmState.value.note500 * 500) + (atmState.value.note200 *200) + (atmState.value.note100 *100)).toLong()
    }

    fun withdraw(amount: Int): Boolean{
        val denominations = mutableMapOf(2000 to atmState.value.note2000, 500 to atmState.value.note500, 200 to atmState.value.note200, 100 to atmState.value.note100)
        val sortedDenominations = denominations.toSortedMap(compareByDescending { it })
        val dispensedNotes = mutableMapOf<Int, Int>()
        var remainingAmount = amount

        for ((note, count) in sortedDenominations) {
            if (remainingAmount >= note) {

                val notesNeeded = minOf(remainingAmount / note, count)
                remainingAmount -= notesNeeded * note

                dispensedNotes[note] = notesNeeded

                denominations[note] = count - notesNeeded
            }
        }
        Log.d(TAG, "withdraw:$denominations ")
        return if (remainingAmount > 0){
            false
        } else {
            _atmState.update {
                it.copy(
                    note2000 = denominations[2000] ?: it.note2000,
                    note500 = denominations[500] ?: it.note500,
                    note200 = denominations[200] ?:it.note200,
                    note100 = denominations[100] ?: it.note100
                )
            }
            true
        }
    }
}