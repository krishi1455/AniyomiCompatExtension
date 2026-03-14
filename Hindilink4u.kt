package com.yourname

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class HindiLinks4uProvider : MainAPI() {
    override var mainUrl = "https://hindilinks4u.codes"
    override var name = "HindiLinks4u"
    override val hasMainPage = true
    override var lang = "hi" // hindi
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    // step 1: search
    override suspend fun search(query: String): List<SearchResponse> {
        // most of these sites use ?s=query or /search/query
        val url = "$mainUrl/?s=$query"
        val doc = app.get(url).document
        
        // you need to inspect element on the site to find the css selector
        // example: div.result-item article
        return doc.select("div.post-cards article").mapNotNull {
            val title = it.select("a").attr("title")
            val href = it.select("a").attr("href")
            val poster = it.select("img").attr("src")
            
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }

    // step 2: load details
    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.select("h1.entry-title").text()
        val poster = doc.select("img.poster").attr("src")
        val desc = doc.select("div.entry-content p").text()
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = desc
        }
    }

    // step 3: load links (the hard part)
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document
        // find the iframe sources
        doc.select("iframe").forEach { iframe ->
            val sourceUrl = iframe.attr("src")
            // use built-in extractors for things like doodstream, mixdrop, etc.
            loadExtractor(sourceUrl, callback, subtitleCallback)
        }
        return true
    }
}
