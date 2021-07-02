package com.andres_k.lib.test

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created on 2021/07/02.
 *
 * @author Kevin Andres
 */
class DummyTest {
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @Disabled("only used to test CD workflows")
    fun drawText() {
        assertEquals(1, 2)
    }
}