package org.example.project.data

object QuotesData {
    val quotes = listOf(
        "The only way to do great work is to love what you do. If you haven't found it yet, keep looking. Don't settle.",
        "Innovation distinguishes between a leader and a follower. Think different and challenge the status quo.",
        "Life is what happens to you while you're busy making other plans. Live in the moment and cherish every day.",
        "The future belongs to those who believe in the beauty of their dreams. Never stop pursuing your passions.",
        "Success is not final, failure is not fatal: it is the courage to continue that counts. Perseverance is key.",
        "The only impossible journey is the one you never begin. Take that first step towards your goals today.",
        "In the middle of difficulty lies opportunity. Challenges are just opportunities in disguise.",
        "It does not matter how slowly you go as long as you do not stop. Progress, not perfection, is what matters.",
        "The way to get started is to quit talking and begin doing. Action speaks louder than words.",
        "Don't watch the clock; do what it does. Keep going. Time will pass regardless of what you do.",
        "Believe you can and you're halfway there. Confidence is the first step to achieving anything.",
        "The secret of getting ahead is getting started. Procrastination is the enemy of progress.",
        "You miss 100% of the shots you don't take. Take risks and embrace opportunities.",
        "The best time to plant a tree was 20 years ago. The second best time is now. Start today.",
        "Your limitation—it's only your imagination. Push yourself beyond what you think is possible.",
        "Great things never come from comfort zones. Step outside your comfort zone to achieve greatness.",
        "Dream it. Wish it. Do it. Turn your dreams into reality through hard work and determination.",
        "Success doesn't just find you. You have to go out and get it. Be proactive in pursuing your goals.",
        "The harder you work for something, the greater you'll feel when you achieve it. Hard work pays off.",
        "Dream bigger. Do bigger. Set ambitious goals and work tirelessly to achieve them."
    )
    
    fun getRandomQuote(): String {
        return quotes.random()
    }
    
    fun getQuoteForWords(wordCount: Int): String {
        val words = "The quick brown fox jumps over the lazy dog. ".repeat(wordCount / 9 + 1)
        return words.split(" ").take(wordCount).joinToString(" ")
    }
}


