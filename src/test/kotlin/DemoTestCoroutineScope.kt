/*
 * MIT License
 *
 * Copyright (c) 2020. Giorgos Neokleous
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalCoroutinesApi
@ExperimentalTime
class DemoTestCoroutineScope {
    @Test
    fun `Advancing Time with TestCoroutineScope`() = runBlockingTest {
        val delay = 100.milliseconds
        val flow = operationForTimeController(delay = delay, rounds = 5)

        // observing flow and storing emissions
        val results = mutableListOf<Int>()
        launch { flow.collect { results.add(it) } }

        // assert that results is empty
        Assert.assertThat(results.isEmpty(), CoreMatchers.equalTo(true))

        // forward one emission with `delay
        advanceTimeBy(delay.toLongMilliseconds())

        // assert first item is correct
        Assert.assertThat(results.first(), CoreMatchers.equalTo(0))

        // advanced at the end of the flow
        advanceUntilIdle()

        // assert emissions
        Assert.assertThat(results, CoreMatchers.equalTo(mutableListOf(0, 1, 2, 3, 4)))
    }

    @Test
    fun `Pause and Resume Dispatcher with TestCoroutineScope`() = runBlockingTest {
        ->
        // pausing dispatcher
        pauseDispatcher()

        val stateFlow = operationForPausingAndResuming(this)

        // assert that empty string before resuming
        Assert.assertTrue(
            "Expected empty string for state.flow but found: ${stateFlow.value}",
            stateFlow.value.isEmpty()
        )

        // resuming operation
        resumeDispatcher()

        // assert that the new value matches the constant STATE_FLOW_EMISSION
        Assert.assertThat(stateFlow.value, CoreMatchers.equalTo(STATE_FLOW_EMISSION))
    }

    /**
     * A [Flow] which emits [rounds] integers value (from 0 to [rounds].
     *
     * Emits every at constant intervals depending on the value of [delay].
     */
    private fun operationForTimeController(delay: Duration, rounds: Int): Flow<Int> {
        return flow {
            repeat(rounds) {
                delay(delay)
                emit(it)
            }
        }
    }

    /**
     * A [StateFlow] which emits a string value.
     */
    private fun operationForPausingAndResuming(scope: CoroutineScope): StateFlow<String> {
        val stateFlow = MutableStateFlow("")

        scope.launch {
            stateFlow.value = STATE_FLOW_EMISSION
        }

        return stateFlow
    }
}

private const val STATE_FLOW_EMISSION = "StateFlowEmission"