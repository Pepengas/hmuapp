package gr.hmu.hmuapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

suspend fun fetchNews(): List<RssItem> = withContext(Dispatchers.IO) {
    val doc = Jsoup.connect("https://ee.hmu.gr/news_gr/").get()
    val items = mutableListOf<RssItem>()
    for (titleDiv in doc.select("div.contenttitle")) {
        val linkEl = titleDiv.selectFirst("a")
        val title = linkEl?.text()?.trim() ?: ""
        val link = linkEl?.absUrl("href") ?: ""
        val parent = titleDiv.parent()
        val date = parent?.selectFirst("div.date")?.text()?.trim() ?: ""
        items.add(RssItem(title, date, link))
    }
    items
}
