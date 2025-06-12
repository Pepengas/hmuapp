package gr.hmu.hmuapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.Connection


private const val NEWS_URL = "https://ee.hmu.gr/news_gr/"

suspend fun fetchNews(): List<RssItem> = withContext(Dispatchers.IO) {
    val doc = Jsoup.connect(NEWS_URL)
        .userAgent("Mozilla/5.0")
        .timeout(10_000)
        .method(Connection.Method.GET)
        .get()
    val items = mutableListOf<RssItem>()
    for (titleDiv in doc.select("div.contenttitle")) {
        val linkEl = titleDiv.selectFirst("a") ?: continue
        val title = linkEl.text().trim()
        if (title.isBlank()) continue
        val link = linkEl.absUrl("href")
        val parent = titleDiv.parent()
        val date = parent?.selectFirst("div.date")?.text()?.trim().orEmpty()
        items.add(RssItem(title, date, link))
    }
    items
}