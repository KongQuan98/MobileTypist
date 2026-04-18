package org.example.project.data.repo

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mobiletypist.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

object WordsRepository {

    private var cachedWords: WordPack? = null

    suspend fun getWordPack(): WordPack {
        if (cachedWords != null) return cachedWords!!

        val bytes = Res.readBytes("files/typing_words_en.json")
        val jsonString = bytes.decodeToString()

        cachedWords = Json.decodeFromString(jsonString)
        return cachedWords!!
    }

    suspend fun getRandomWords(
        difficulty: Difficulty,
        count: Int
    ): List<String> {

        val pack = getWordPack()

        val baseList = when (difficulty) {
            Difficulty.EASY -> pack.levels.easy
            Difficulty.MEDIUM -> pack.levels.medium
            Difficulty.HARD -> pack.levels.hard
        }

        return baseList.shuffled().take(count)
    }

    @OptIn(InternalResourceApi::class)
    suspend fun loadJson(fileName: String): String {
        val bytes = readResourceBytes(fileName)
        return bytes.decodeToString()
    }
}

enum class Difficulty(val value: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard"),
}

@Serializable
data class WordPack(
    val language: String,
    val levels: WordLevels
)

@Serializable
data class WordLevels(
    val easy: List<String>,
    val medium: List<String>,
    val hard: List<String>
)
