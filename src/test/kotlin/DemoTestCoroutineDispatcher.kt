import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
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
        val flow = operation(delay = delay, rounds = 5)

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

    private fun operation(delay: Duration, rounds: Int): Flow<Int> {
        return flow {
            repeat(rounds) {
                delay(delay)
                emit(it)
            }
        }
    }
}