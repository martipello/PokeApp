package com.sealstudios.pokemonApp.ui.util

import kotlinx.coroutines.*
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertThat
import org.junit.Test

class CoroutineTest {
    @Test
    fun getValueFromCoroutine() {
        var sum: Int

        println("sum function called")

        runBlocking {
            sum = sumAsync().await()
            println("sum = $sum")
            assertThat(
                sum,
                IsEqual.equalTo(3)
            )
        }

    }

    private suspend fun funA() = CoroutineScope(Dispatchers.IO).async {
        delay(3000)
        println("fun a finished")
        return@async 1
    }.await()

    private suspend fun funB() = CoroutineScope(Dispatchers.IO).async {
        delay(3000)
        println("fun b finished")
        return@async 2
    }.await()

    private fun sumAsync() = CoroutineScope(Dispatchers.IO).async {
        println("call fun a")
        val a = funA()
        println("call fun b")
        val b = funB()
        return@async a + b
    }
}
