import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicInteger

fun main(): Unit = runBlocking {

    val file = File("input.txt")
    if (!file.exists()) {
        return@runBlocking
    }

    val validPasswordsCount = AtomicInteger(0)

    launch(Dispatchers.IO) {
        BufferedReader(InputStreamReader(FileInputStream(file))).use {
            it.forEachLine {
                launch {
                    if (checkIsValidPassword(it)) {
                        validPasswordsCount.incrementAndGet()
                    }
                }
            }
        }
    }.join()

    println(validPasswordsCount.get())
}

private suspend fun checkIsValidPassword(line: String): Boolean = withContext(Dispatchers.Default) {
    val symbol = line.first()
    val range: IntRange = getRangeFromLine(line)
    val password = getPasswordFromLine(line)
    if (password.count { it == symbol } in range) {
        return@withContext true
    }
    return@withContext false
}

private fun getPasswordFromLine(line: String): String {
    val (_, _, password) = line.split(' ')
    return password
}

private fun getRangeFromLine(line: String): IntRange {
    val (firstNumber, secondNumber) = line.slice(IntRange(line.indexOf(' ') + 1, line.indexOf(":") - 1)).split('-')
    return firstNumber.toInt()..secondNumber.toInt()
}
