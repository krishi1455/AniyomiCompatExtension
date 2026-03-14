import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.TvType

class Hindilink4u : MainAPI() {

    override var name = "Hindilinks4u"
    override var mainUrl = "https://hindilinks4u.codes"
    override var lang = "en"

    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )
}