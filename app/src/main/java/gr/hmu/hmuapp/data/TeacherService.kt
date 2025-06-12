package gr.hmu.hmuapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.Connection

const val TEACHERS_URL = "https://ee.hmu.gr/meli-dep/"

private fun parseTeacherDetails(url: String): Triple<String, String, String> {
    return try {
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .header("Accept-Language", "el,en;q=0.9")
            .ignoreHttpErrors(true)
            .timeout(10_000)
            .method(Connection.Method.GET)
            .followRedirects(true)
            .get()

        val phone = doc.selectFirst("a[href^=tel], p:matches((?i)τηλ|phone)")?.text()?.trim().orEmpty()
        val email = doc.selectFirst("a[href^=mailto]")?.attr("href")?.removePrefix("mailto:")
            ?: doc.selectFirst("p:matches((?i)e.?mail)")?.text()?.substringAfter(":")?.trim().orEmpty()
        val interests = doc.selectFirst("p:matches(Ερευνητικ|Research)")?.text()?.substringAfter(":")?.trim().orEmpty()
        Triple(phone, email, interests)
    } catch (e: Exception) {
        Triple("", "", "")
    }
}

private fun parseTeacherDetails(url: String): Triple<String, String, String> {
    return try {
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .header("Accept-Language", "el,en;q=0.9")
            .ignoreHttpErrors(true)
            .timeout(10_000)
            .method(Connection.Method.GET)
            .followRedirects(true)
            .get()

        val phone = doc.selectFirst("a[href^=tel], p:matches((?i)τηλ|phone)")?.text()?.trim().orEmpty()
        val email = doc.selectFirst("a[href^=mailto]")?.attr("href")?.removePrefix("mailto:")
            ?: doc.selectFirst("p:matches((?i)e.?mail)")?.text()?.substringAfter(":")?.trim().orEmpty()
        val interests = doc.selectFirst("p:matches(Ερευνητικ|Research)")?.text()?.substringAfter(":")?.trim().orEmpty()
        Triple(phone, email, interests)
    } catch (e: Exception) {
        Triple("", "", "")
    }
}

suspend fun fetchTeachers(): List<Teacher> = withContext(Dispatchers.IO) {
    val doc = try {
        Jsoup.connect(TEACHERS_URL)
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

    val teachers = mutableListOf<Teacher>()

    // Try to parse common patterns for teacher listings
    val elements = doc.select("article, div.teacher, div.elementor-widget-container")
    for (el in elements) {
        val name = el.selectFirst("h2, h3, h4, strong")?.text()?.trim().orEmpty()
        if (name.isBlank()) continue
        val title = el.selectFirst("p:matches((?i)prof|καθηγη|επικουρ|Λέκτορ), span:matches((?i)prof|καθηγη|επικουρ|Λέκτορ)")?.text()?.trim().orEmpty()
        val department = el.selectFirst("p:matches((?i)department|τμήμα)")?.text()?.trim().orEmpty()
        val link = el.selectFirst("a[href]")?.absUrl("href").orEmpty()
        val (phone, email, interests) = if (link.isNotEmpty()) parseTeacherDetails(link) else Triple("", "", "")
        teachers.add(Teacher(name, title, department, phone, email, interests, link))
    }

    teachers
}

