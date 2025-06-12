package gr.hmu.hmuapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.Connection


private const val NEWS_URL = "https://ee.hmu.gr/news_gr/"

suspend fun fetchNews(): List<RssItem> = withContext(Dispatchers.IO) {
    val doc = try {
        Jsoup.connect(NEWS_URL)
            .userAgent("Mozilla/5.0")
            .referrer("https://www.google.com")
            .header("Accept-Language", "el,en;q=0.9")
            .ignoreHttpErrors(true)
            .timeout(10_000)
            .method(Connection.Method.GET)
            .followRedirects(true)
            .get()
    } catch (e: Exception) {
        return@withContext emptyList()
    }

    // Try to locate an RSS feed link first for more reliable parsing
    val rssLink = doc.selectFirst("link[type=application/rss+xml]")?.absUrl("href")
    if (!rssLink.isNullOrBlank()) {
        return@withContext fetchRss(rssLink)
    }

    val items = mutableListOf<RssItem>()

    // Prefer modern <article> markup if present
    val articles = doc.select("article")
    if (articles.isNotEmpty()) {
        for (article in articles) {
            val linkEl = article.selectFirst("h1 a, h2 a, h3 a, a")
            val title = linkEl?.text()?.trim().orEmpty()
            if (title.isBlank()) continue
            val date = article.selectFirst("time, .date")?.text()?.trim().orEmpty()
            val link = linkEl.absUrl("href")
            items.add(RssItem(title, date, link))
        }
        return@withContext items
    }

    // Fallback to older div based markup
    for (titleDiv in doc.select("div.contenttitle")) {
        val linkEl = titleDiv.selectFirst("a")
        val title = linkEl?.text()?.trim()?.takeIf { it.isNotBlank() }
            ?: titleDiv.text().trim()
        if (title.isBlank()) continue
        val link = linkEl?.absUrl("href").orEmpty()
        val parent = titleDiv.parent()
        val date = parent?.selectFirst("div.date")?.text()?.trim().orEmpty()
        items.add(RssItem(title, date, link))
    }

    items
}