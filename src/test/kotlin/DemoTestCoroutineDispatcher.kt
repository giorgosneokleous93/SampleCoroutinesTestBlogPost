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
class DemoTestCoroutineDispatcher {
    @Test
    fun `Advancing Time with TestCoroutineDispatcher`() = runBlockingTest {
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
    fun `Pause and Resume Dispatcher with TestCoroutineDispatcher`() = runBlockingTest {
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