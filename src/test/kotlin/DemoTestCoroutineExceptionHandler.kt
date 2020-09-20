import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class DemoTestCoroutineExceptionHandler {
    @Test
    fun `Assert Exception with Coroutines`() = runBlockingTest {
        // handler will catch Exceptions
        val exceptionHandler = TestCoroutineExceptionHandler()

        launch(exceptionHandler) { operation().collect() }

        // asserting that first uncaught exception is CustomException
        Assert.assertThat(
            exceptionHandler.uncaughtExceptions.first(),
            CoreMatchers.instanceOf(CustomException::class.java)
        )
    }

    /**
     * A flow which throws a [CustomException]
     */
    private fun operation() = flow<Unit> {
        throw CustomException()
    }

    private class CustomException : Throwable("Operation failed..")
}