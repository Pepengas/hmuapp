package gr.hmu.hmuapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL

suspend fun fetchRss(url: String): List<RssItem> = withContext(Dispatchers.IO) {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.inputStream.use { stream ->
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(stream, null)

        var eventType = parser.eventType
        var text = ""
        var title = ""
        var link = ""
        var pubDate = ""
        val items = mutableListOf<RssItem>()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {}
                XmlPullParser.TEXT -> text = parser.text
                XmlPullParser.END_TAG -> {
                    when (tagName) {
                        "item" -> {
                            items.add(RssItem(title, pubDate, link))
                            title = ""
                            link = ""
                            pubDate = ""
                        }
                        "title" -> title = text
                        "link" -> link = text
                        "pubDate" -> pubDate = text
                    }
                }
            }
            eventType = parser.next()
        }
        items
    }
}
