package org.example.project.data.repo

object QuotesRepository {

    private val quotes = listOf(
        "The only way to do great work is to love what you do and stay curious every single day.",
        "It does not matter how slowly you go as long as you do not stop moving forward.",
        "Success is not final, failure is not fatal, it is the courage to continue that counts.",
        "Believe you can and you are halfway there, but discipline carries you the rest of the way.",
        "The future belongs to those who believe in the beauty of their dreams and act on them.",
        "Do not watch the clock, do what it does and keep going until the work is truly done.",
        "The best time to plant a tree was twenty years ago, the second best time is right now.",
        "Your limitation is only your imagination, so type boldly and learn from every mistake.",
        "Push yourself because no one else is going to do it for you when motivation fades away.",
        "Great things never come from comfort zones, they come from showing up again and again.",
        "Dream it, wish it, do it, then repeat the cycle until the habit becomes who you are.",
        "Success does not just find you, you have to go out there and earn it with patience.",
        "The harder you work for something, the greater you will feel when you finally achieve it.",
        "Do not stop when you are tired, stop when you are done and proud of the effort you gave.",
        "Wake up with determination and go to bed with satisfaction after a day well spent.",
        "Do something today that your future self will thank you for, even if it feels small now.",
        "Little things make big days, and consistent practice turns small gains into real skill.",
        "It is going to be hard, but hard does not mean impossible, it means worth the attempt.",
        "Do not wait for opportunity, create it by preparing quietly while others make excuses.",
        "Sometimes we are tested not to show our weaknesses but to discover our true strength.",
        "The key to success is to focus on goals, not obstacles, and keep your hands moving.",
        "Dream bigger, do bigger, and remember that progress is still progress no matter the pace.",
        "Do not be afraid to give up the good to go for the great that waits beyond comfort.",
        "The secret of getting ahead is getting started, even when perfection is not within reach.",
        "Quality is not an act, it is a habit built one careful repetition at a time every day.",
    )

    fun getRandomQuote(): String {
        return quotes.random()
    }
}
