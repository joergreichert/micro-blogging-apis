package de.l.joergreichert.outintheopen

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.FileWriter
import java.nio.file.Paths
import kotlin.io.path.readLines

class ReverseLine {

    @Test
    @Disabled
    fun test() {
        val lines = Paths.get("file:///Users/joerg/Desktop/out-in-the-open/masto-2026-02-21-1200-books.txt").readLines().reversed()
        FileWriter("file:///Users/joerg/Desktop/out-in-the-open/masto-2026-02-21-1200-books-rev.txt").write(lines.joinToString("\n"))
    }
}