package de.l.joergreichert.outintheopen

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Recur
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.RRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.FileWriter
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.comparisons.compareBy
import kotlin.comparisons.thenComparator

class IcalGeneratorTest {

    data class Location(
        val online: Boolean = false,
        val name: String? = null,
        val street: String? = null,
        val houseNumber: String? = null,
        val city: String? = null,
        val zipCode: String? = null,
        val lat: Double? = null,
        val lon: Double? = null,
        val onlineLink: String? = null
    )

    data class Registration(
        val required: Boolean,
        val from: LocalDateTime? = null,
        val to: LocalDateTime? = null,
        val link: String? = null
    )

    data class Event(
        val category: String? = null,
        val from: LocalDateTime,
        val to: LocalDateTime,
        val location: Location,
        val registration: Registration? = null,
        val title: String,
        val link: String,
        val comments: List<String> = mutableListOf()
    )

    @Test
    @Disabled
    fun testGenerateAugust2025IcsFromDataModel() {
        val events = createAugust2025Events()
        val actual = events.sortedBy { it.from }.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    @Test
    @Disabled
    fun testGenerateRecurrentIcsFromDataModel() {
        val events = recurrentEventsMap().entries.sortedBy { it.key }.map { it.value }
        val actual = events.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    @Test
    @Disabled
    fun testGenerateCompleteYear2025IcsFromDataModel() {
        val events = createJanuary2025Events()
        events.addAll(createFebruary2025Events())
        events.addAll(createMarch2025Events())
        events.addAll(createApril2025Events())
        events.addAll(createMay2025Events())
        events.addAll(createJune2025Events())
        events.addAll(createJuly2025Events())
        events.addAll(createAugust2025Events())
        val df = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm")
        val eventMap = events.groupBy { df.format(it.from) + "__" + it.title }.toMap()
        val distinctEvents = eventMap.keys.mapNotNull { key -> eventMap[key]?.firstOrNull() }
        val actual = distinctEvents.sortedBy { df.format(it.from) + "__" + it.title }.joinToString("\n") { generateEventLink(it) }
        FileWriter(System.getProperty("user.dir") + "/docs/ics/events.ics").use { out ->
            out.write(createIcsForMonth("Out In The Open", events))
        }
        assertEquals(expected(), actual)
    }

    private fun createIcsForMonth(monthTitle: String, events: List<Event>): String {
        val calendar = events.fold(
            Calendar().withDefaults().withProdId("-//${monthTitle}//iCal4j 1.0//EN")
        ) { cal, event -> cal.withComponent(createCalendarComponent(event)); cal }
        val calendarWithCodeforEvents = recurrentEvents()
            .fold(calendar) { cal, event -> cal.withComponent(event) }
        return calendarWithCodeforEvents.toString()
    }

    private fun createAugust2025Events(): List<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 5, 20, 0, 0),
                to = LocalDateTime.of(2025, 8, 5, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true,
                    lat = 52.5129735,
                    lon = 13.4201313
                ),
                title = "Netzpolitisches Grillen",
                link = "https://digitalegesellschaft.de/2025/07/netzpolitisches-grillen-2/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 7, 18, 0, 0),
                to = LocalDateTime.of(2025, 8, 7, 21, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 7, 19, 31, 0),
                to = LocalDateTime.of(2025, 8, 7, 19, 31, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://bits-und-baeume.org/bbb/community"
                ),
                title = "Bits und Bäume Community Treffen",
                link = "https://bits-und-baeume.org/termine/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 9, 14, 0, 0),
                to = LocalDateTime.of(2025, 8, 9, 18, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    lat = 52.5129735,
                    lon = 13.4201313
                ),
                title = "Linux install Party - endof10",
                link = "https://c-base.org/calendar/#view=month&date=2025-08-01&event=5cff058c-d236-40b3-8116-bdc374fcb34a"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 13, 18, 0, 0),
                to = LocalDateTime.of(2025, 8, 13, 19, 0, 0),
                location = Location(online = true),
                title = "Datawrapper maps: Deep dive",
                link = "https://streamyard.com/watch/y55ugdhB6uqE"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 15, 16, 0, 0),
                to = LocalDateTime.of(2025, 8, 17, 16, 0, 0),
                location = Location(
                    name = "Bitwäscherei",
                    street = "Neue Hard",
                    houseNumber = "12",
                    zipCode = "8005",
                    city = "Zürich (Schweiz)",
                    lon = 8.5204024,
                    lat = 47.3870316,
                ),
                title = "Jugend hackt Zürich 2025",
                link = "https://anmeldung.jugendhackt.org/schweiz/jhzh2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 16, 9, 30, 0),
                to = LocalDateTime.of(2025, 8, 17, 17, 0, 0),
                location = Location(
                    name = "Hochschule Bonn-Rhein-Sieg",
                    street = "Grantham-Allee",
                    houseNumber = "20",
                    zipCode = "53757",
                    city = "Sankt Augustin",
                    lat = 50.779560200000006,
                    lon = 7.182170128730925
                ),
                title = "FrOSCon",
                link = "https://froscon.org"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 21, 14, 30, 0),
                to = LocalDateTime.of(2025, 8, 21, 16, 30, 0),
                location = Location(online = true),
                title = "Teil 4 der Workshopreihe Sovereign. Sustainable. Digital.: Wie grün ist unsere Cloud? Nachhaltige Infrastruktur, Rechenzentren",
                link = "https://www.bundesumweltministerium.de/veranstaltung/bmukn-community-nachhaltige-digitalisierung-teil-4-der-workshopreihe-sovereign-sustainable-digital"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 21, 12, 0, 0),
                to = LocalDateTime.of(2025, 8, 21, 12, 30, 0),
                location = Location(online = true),
                title = "CDL Espresso Talk | Quereinstieg in Data Science: Von der Soziologie zu Daten für die Zivilgesellschaft",
                link = "https://correlaid.org/veranstaltungen/espresso_quereinstieg_datascience?viewType=list"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 21, 15, 0, 0),
                to = LocalDateTime.of(2025, 8, 25, 12, 0, 0),
                location = Location(
                    name = "Jugendzeltplatz Bonn",
                    street = "Venner Str.",
                    houseNumber = "54",
                    zipCode = "53177",
                    city = "Bonn",
                    lat = 50.681084,
                    lon = 7.133399
                ),
                title = "Hack'n'Sun",
                link = "https://teckids.org/blog/2025/03/hacknsun/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 23, 10, 0, 0),
                to = LocalDateTime.of(2025, 8, 24, 17, 0, 0),
                location = Location(
                    name = "Hannover Congress Centrum (HCC)",
                    street = "Theodor-Heuss-Platz",
                    houseNumber = "1-3",
                    zipCode = "30175",
                    city = "Hannover",
                    lat = 52.37739626292196,
                    lon = 9.769216275667079
                ),
                title = "Maker Faire Hannover",
                link = "https://maker-faire.de/hannover/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 25, 18, 0, 0),
                to = LocalDateTime.of(2025, 8, 31, 22, 0, 0),
                location = Location(
                    name = "LABOR e.V. Hackspace Bochum",
                    street = "Alleestraße",
                    houseNumber = "50",
                    zipCode = "44793",
                    city = "Bochum",
                    lat = 51.4809426,
                    lon = 7.2086028
                ),
                title = "20 Jahre Labor",
                link = "https://wiki.das-labor.org/w/20_jahre_labor"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 26, 11, 30, 0),
                to = LocalDateTime.of(2025, 8, 26, 12, 30, 0),
                location = Location(online = true),
                title = "CorrelCompact | Open Data für Non-Profits: Schätze finden und nutzen",
                link = "https://correlaid.org/veranstaltungen/cc25-opendata?viewType=list"
            )
        )


        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 26, 10, 0, 0),
                to = LocalDateTime.of(2025, 8, 26, 11, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "SCS-Standards in der Praxis: Von der Umsetzung zur Zertifizierung",
                link = "https://events.sovereigncloudstack.org/webinar/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 26, 18, 0, 0),
                to = LocalDateTime.of(2025, 8, 26, 19, 0, 0),
                location = Location(online = true),
                title = "Getting started with Datawrapper",
                link = "https://streamyard.com/watch/W5dzprKiUXGT"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 26, 19, 30, 0),
                to = LocalDateTime.of(2025, 8, 26, 21, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "OSM Radinfra-Mapathon #3",
                link = "https://osmcal.org/event/3842/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 27, 9, 0, 0),
                to = LocalDateTime.of(2025, 8, 31, 18, 0, 0),
                location = Location(
                    name = "Zeltplatz Gunzenberg an der Talsperre Pöhl",
                    street = "Hauptstraße",
                    houseNumber = "38",
                    zipCode = "08543",
                    city = "Pöhl",
                    lat = 50.53879295,
                    lon = 12.185235895847025
                ),
                title = "thereisno.camp",
                link = "https://thereisno.camp"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 27, 10, 0, 0),
                to = LocalDateTime.of(2025, 8, 28, 15, 30, 0),
                location = Location(
                    name = "Hochschule Merseburg (Hauptgebäude)",
                    street = "Eberhard-Leibnitz-Straße",
                    houseNumber = "2",
                    zipCode = "06217",
                    city = "Merseburg",
                    lat = 51.3436547,
                    lon = 11.974483708704984
                ),
                title = "Merseburger Digitaltage 2025",
                link = "https://www.merseburger-digitaltage.de/de/startseite-mdt.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 28, 11, 0, 0),
                to = LocalDateTime.of(2025, 8, 28, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "openCode Connect August 2025: Sovereign Cloud Stack – Die Basis für Digitale Souveränität in der Cloud",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-august-2025-4785"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 8, 28, 12, 0, 0),
                to = LocalDateTime.of(2025, 8, 31, 12, 0, 0),
                location = Location(
                    name = "Jugendraum",
                    street = "K",
                    houseNumber = "61",
                    zipCode = "56459",
                    city = "Todtenberg, Rotenhain, Westerburg",
                    lat = 50.60378,
                    lon = 7.88126
                ),
                title = "WAMP - Das Camp im Westerwald",
                link = "https://thereisno.camp"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 9, 5, 12, 0, 0),
                to = LocalDateTime.of(2025, 9, 7, 12, 0, 0),
                location = Location(
                    name = "La Grange e.V.",
                    street = "Gingster Chaussee",
                    houseNumber = "6",
                    zipCode = "18528",
                    city = "Bergen auf Rügen",
                    lat = 54.424704,
                    lon = 13.415565
                ),
                title = "InselChaos 2025",
                link = "https://inselchaos.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 9, 5, 10, 0, 0),
                to = LocalDateTime.of(2025, 9, 6, 15, 0, 0),
                location = Location(
                    name = "Erich-Brost-Institut für Internationalen Journalismus auf dem Campus Nord der TU Dortmund",
                    street = "Otto-Hahn-Straße",
                    houseNumber = "2",
                    zipCode = "44227",
                    city = "Dortmund",
                    lat = 51.491149199999995,
                    lon = 7.415581198534301
                ),
                title = "SciCAR 2025",
                link = "https://netzwerkrecherche.org/wir-vernetzen/scicar/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 9, 12, 13, 30, 0),
                to = LocalDateTime.of(2025, 9, 14, 15, 30, 0),
                location = Location(
                    name = "Piloty-Gebäude (S2|02), TU Darmstadt",
                    street = "Hochschulstraße",
                    houseNumber = "10",
                    zipCode = "64289",
                    city = "Darmstadt",
                    lat = 49.877509149999995,
                    lon = 8.654546299588523
                ),
                title = "Meta-Rhein-Main-Chaos-Days (MRMCD) 2025",
                link = "https://2025.mrmcd.net/de/"
            )
        )
        return events
    }

    private fun createJuly2025Events(): List<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 9, 0, 0),
                to = LocalDateTime.of(2025, 7, 5, 18, 0, 0),
                location = Location(
                    name = "Pfadfinderheim Welfenhof",
                    street = "III. Koppelweg",
                    houseNumber = "6",
                    zipCode = "38518",
                    city = "Gifhorn",
                    lat = 52.46289915,
                    lon = 10.604584416286588
                ),
                title = "Hacken Open Air 2::25",
                link = "https://hackenopenair.de/index.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 9, 31, 0),
                to = LocalDateTime.of(2025, 7, 1, 10, 0, 0),
                location = Location(
                    online = true
                ),
                title = "Open Data trifft Open Learning: Mit OER zur Datenkompetenz in der Verwaltungswissenschaft",
                link = "https://gi.de/veranstaltung/open-data-trifft-open-learning"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 10, 0, 0),
                to = LocalDateTime.of(2025, 7, 1, 16, 30, 0),
                location = Location(
                    name = "Startplatz Köln",
                    street = "Im Mediapark",
                    houseNumber = "5",
                    zipCode = "50670",
                    city = "Köln",
                    lat = 50.9486808,
                    lon = 6.9447581
                ),
                title = "Klimaaktiv-Event – Barcamp kommunaler Klimaschutz",
                link = "https://difu.de/veranstaltungen/2025-07-01/klimaaktiv-event-barcamp-kommunaler-klimaschutz"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 20, 0, 0),
                to = LocalDateTime.of(2025, 7, 1, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true,
                    lat = 52.5129735,
                    lon = 13.4201313
                ),
                title = "148. Netzpolitischer Abend",
                link = "https://digitalegesellschaft.de/2025/06/148-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 10, 0, 0),
                to = LocalDateTime.of(2025, 7, 1, 18, 0, 0),
                location = Location(
                    name = "IHK Nürnberg für Mittelfranken",
                    street = "Hauptmarkt",
                    houseNumber = "25",
                    zipCode = "90403",
                    city = "Nürnberg",
                    lat = 49.45459912324225,
                    lon = 11.076739083649812
                ),
                title = "Ein Tag der digitalen Souveränität: Impulse, Use Cases und Diskussionen zur Bedeutung von Open Source für den digitalen Staat",
                link = "https://egovernment-podcast.com/event/ein-tag-zur-digitalen-souveraenitaet/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 3, 14, 0, 0),
                to = LocalDateTime.of(2025, 7, 3, 14, 45, 0),
                location = Location(
                    online = true
                ),
                title = "Wissen kompakt um 2 – openDesk: Auch für Kommunalverwaltungen?!",
                link = "https://egovernment-podcast.com/event/wissen-kompakt-um-2-opendesk-auch-fuer-kommunalverwaltungen/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 3, 14, 0, 0),
                to = LocalDateTime.of(2025, 7, 3, 18, 0, 0),
                location = Location(
                    name = "Bürgerhaus Bennohaus",
                    street = "Bennostraße",
                    houseNumber = "5",
                    zipCode = "48155",
                    city = "Münster",
                    lat = 51.95370355787202,
                    lon = 7.653066354481723
                ),
                title = "Open Data für Kulturveranstaltungen",
                link = "https://www.eventbrite.de/e/open-data-fur-kulturveranstaltungen-registrierung-1241916438499"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 3, 19, 31, 0),
                to = LocalDateTime.of(2025, 7, 3, 19, 31, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://bits-und-baeume.org/bbb/community"
                ),
                title = "Bits und Bäume Community Treffen",
                link = "https://bits-und-baeume.org/termine/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 4, 9, 15, 0),
                to = LocalDateTime.of(2025, 7, 6, 17, 30, 0),
                location = Location(
                    name = "Kulturhaus Eidelstedt",
                    street = "Alte Elbgaustraße",
                    houseNumber = "12",
                    zipCode = "22523",
                    city = "Hamburg",
                    lat = 53.6073818,
                    lon = 9.9032184
                ),
                title = "Chaos Feminist Convention",
                link = "https://events.haecksen.org/cfc25/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 4, 17, 30, 0),
                to = LocalDateTime.of(2025, 7, 6, 12, 0, 0),
                location = Location(
                    name = "Jugendherberge Otto-Moericke-Turm Konstanz",
                    street = "Zur Allmannshöhe",
                    houseNumber = "16",
                    zipCode = "78464",
                    city = "Konstanz",
                    lat = 47.6871632,
                    lon = 9.2033462
                ),
                title = "CorrelCon 2025 + 10 Jahre CorrelAid",
                link = "https://correlaid.org/veranstaltungen/correlcon2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 4, 16, 0, 0),
                to = LocalDateTime.of(2025, 7, 6, 18, 0, 0),
                location = Location(
                    name = "Aaccelerator",
                    street = "Blezingerstraße",
                    houseNumber = "15",
                    zipCode = "73430",
                    city = "Aalen",
                    lat = 48.8544329,
                    lon = 10.0906347
                ),
                title = "FAT25 - Always watching",
                link = "https://hackwerk.fun/start"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 5, 9, 30, 0),
                to = LocalDateTime.of(2025, 7, 5, 20, 0, 0),
                location = Location(
                    name = "Universität Tübingen - Gebäude auf dem Sand",
                    street = "Sand",
                    houseNumber = "1",
                    zipCode = "72076",
                    city = "Tübingen",
                    lat = 48.534420,
                    lon = 9.071120
                ),
                title = "Tübix - Tübinger Linuxtag",
                link = "https://www.tuebix.org/2025/programm/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 5, 10, 0, 0),
                to = LocalDateTime.of(2025, 7, 6, 18, 30, 0),
                location = Location(
                    name = "Forum der Zukunft",
                    street = "Museumsinsel",
                    houseNumber = "1",
                    zipCode = "80538",
                    city = "München",
                    lat = 48.131401197649865,
                    lon = 11.585583531781538
                ),
                title = "Festival der Zukunft",
                link = "https://www.1e9.community/festival-der-zukunft/2025/deutsch"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 5, 14, 0, 0),
                to = LocalDateTime.of(2025, 7, 5, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true,
                    lat = 52.5129735,
                    lon = 13.4201313
                ),
                title = "Wege ins Fediverse – Gemeinsam ins freie soziale Netzwerk",
                link = "https://bits-und-baeume.org/termine/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 8, 11, 0, 0),
                to = LocalDateTime.of(2025, 7, 8, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "Die FITKO stellt vor: BayKommun, DigitalMarkt & BayKoNet – Erfolgreicher digitaler Rollout in Bayern",
                link = "https://egovernment-podcast.com/event/infoveranstaltung-die-fitko-stellt-vor-baykommun-digitalmarkt-baykonet-erfolgreicher-digitaler-rollout-in-bayern/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 8, 10, 30, 0),
                to = LocalDateTime.of(2025, 7, 9, 19, 30, 0),
                location = Location(
                    online = true
                ),
                title = "The Tech We People Online Summit",
                link = "https://okfn.org/en/events/the-tech-people-want-online-summit/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 9, 13, 30, 0),
                to = LocalDateTime.of(2025, 7, 9, 21, 0, 0),
                location = Location(
                    name = "ProjectTogether gGmbH",
                    street = "Karl-Liebknecht-Straße",
                    houseNumber = "34",
                    zipCode = "10178",
                    city = "Berlin",
                    online = true,
                    lat = 52.525793499485445,
                    lon = 13.415033412619891,
                ),
                title = "Zweite bundesweite Werkstatt der Mutigen",
                link = "https://egovernment-podcast.com/event/zweite-bundesweite-werkstatt-der-mutigen/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 9, 16, 0, 0),
                to = LocalDateTime.of(2025, 7, 9, 18, 0, 0),
                location = Location(
                    name = "Pöge Haus",
                    street = "Hedwigstraße",
                    houseNumber = "20",
                    zipCode = "04315",
                    city = "Leipzig",
                    lat = 51.34755700691633,
                    lon = 12.403512220789537
                ),
                title = "Digitalpolitischer Bits & Bäume-Sommerabend",
                link = "https://bits-und-baeume.org/posts/sommerabend_2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 9, 20, 0, 0),
                to = LocalDateTime.of(2025, 7, 9, 21, 30, 0),
                location = Location(online = true),
                title = "Open Transport Meetup: Holger Bruch - Deutschlandweiter GTFS Feed aus DELFI NeTEx Daten",
                link = "https://hackmd.okfn.de/opentransportmeetup"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 10, 11, 0, 0),
                to = LocalDateTime.of(2025, 7, 10, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "openCode Connect Juli 2025: GA-Lotse – Zukunftsfähige Verwaltungsdigitalisierung für Gesundheitsämter",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-juli-2025-4564"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 11, 12, 30, 0),
                to = LocalDateTime.of(2025, 7, 11, 17, 30, 0),
                location = Location(
                    name = "Bundestag, Marie-Elisabeth-Lüders-Haus",
                    street = "Adele-Schreiber-Krieger-Straße",
                    houseNumber = "1",
                    zipCode = "10117",
                    city = "Berlin",
                    lat = 52.5201606,
                    lon = 13.378282843021069
                ),
                title = "(Selbst-)Verteidigung der Zivilgesellschaft",
                link = "https://www.dielinkebt.de/service/termine/detail/konferenz-zur-verteidigung-der-zivilgesellschaft/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 11, 15, 0, 0),
                to = LocalDateTime.of(2025, 7, 13, 14, 30, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Transitous Hack Weekend",
                link = "https://github.com/public-transport/transitous/wiki/Transitous-Hack-Weekend-Berlin,-July-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 11, 18, 0, 0),
                to = LocalDateTime.of(2025, 7, 11, 21, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 15, 16, 0, 0),
                to = LocalDateTime.of(2025, 7, 15, 17, 30, 0),
                location = Location(online=true),
                title = "energieXchange | Bürger:innenenergie – Power to the People ",
                link = "https://qlee.idloom.events/energieXchange-B%C3%BCrgerenergie"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 16, 15, 30, 0),
                to = LocalDateTime.of(2025, 7, 16, 17, 0, 0),
                location = Location(
                    online = true
                ),
                title = "strg+c[ut]: Bürgerdaten für digitale Zwillinge – Chancen und Perspektiven",
                link = "https://egovernment-podcast.com/event/strgcut-buergerdaten-fuer-digitale-zwillinge-chancen-und-perspektiven/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 16, 14, 0, 0),
                to = LocalDateTime.of(2025, 7, 23, 1, 0, 0),
                location = Location(
                    name = "Hylkedam",
                    street = "Hylkedamvej",
                    houseNumber = "54",
                    zipCode = "5591",
                    city = "Gelsted (Dänemark)",
                    lat = 55.385601,
                    lon = 9.939074
                ),
                title = "Bornhack",
                link = "https://bornhack.dk/bornhack-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 17, 10, 0, 0),
                to = LocalDateTime.of(2025, 7, 17, 11, 0, 0),
                location = Location(online = true),
                title = "Nutzung von freien Geodaten zur Parkraumanalyse",
                link = "https://www.mundialis.de/effiziente-parkraumanalyse-mit-freien-geodaten-einladung-zum-webinar/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 17, 18, 0, 0),
                to = LocalDateTime.of(2025, 7, 20, 13, 30, 0),
                location = Location(
                    name = "Zeltplatz Messerschmidmühle",
                    street = "Messerschmidmühle",
                    houseNumber = "St 2127",
                    zipCode = "94157",
                    city = "Maresberg",
                    lat = 48.77162885652271,
                    lon = 13.457468561517288,

                    ),
                title = "VVoid Camp 2025",
                link = "https://www.vvoid.camp"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 19, 10, 0, 0),
                to = LocalDateTime.of(2025, 7, 20, 12, 0, 0),
                location = Location(
                    name = "Burg Husen",
                    street = "Syburger Dorfstr.",
                    houseNumber = "135",
                    zipCode = "44265",
                    city = "Dortmund",
                    lat = 51.41896774541706,
                    lon = 7.505246300056001
                ),
                title = "Hack an der Ruhr 2025",
                link = "https://hadr.un-hack-bar.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 21, 17, 0, 0),
                to = LocalDateTime.of(2025, 7, 21, 18, 30, 0),
                location = Location(
                    online = true
                ),
                title = "Community Workshop: Open Data? Challenge Accepted!",
                link = "https://correlaid.org/veranstaltungen/cw-opendata?viewType=list"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 22, 19, 30, 0),
                to = LocalDateTime.of(2025, 7, 22, 21, 0, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://osmvideo.cloud68.co/user/chr-g7r-xz3"
                ),
                title = "OSM-Verkehrswende #69",
                link = "https://wiki.openstreetmap.org/wiki/Verkehrswende-Meetup/Meetup_2025-07-22"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 24, 13, 0, 0),
                to = LocalDateTime.of(2025, 7, 27, 20, 0, 0),
                location = Location(
                    name = "Jugendcamp des See-Campingplatz",
                    street = "Seestraße",
                    houseNumber = "11",
                    zipCode = "63533",
                    city = "Mainhausen",
                    lat = 50.022455,
                    lon = 9.016447
                ),
                title = "SeeZeit 2025",
                link = "https://ccc-ffm.de/2025/05/seezeit/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 25, 9, 30, 0),
                to = LocalDateTime.of(2025, 7, 25, 12, 30, 0),
                location = Location(
                    online = true
                ),
                title = "Open Data Einführungsworkshop",
                link = "https://correlaid.org/veranstaltungen/workshop-open-data-1?viewType=list"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 26, 13, 0, 0),
                to = LocalDateTime.of(2025, 7, 27, 13, 0, 0),
                location = Location(
                    name = "Westspitze Tübingen",
                    street = "Eisenbahnstraße",
                    houseNumber = "1",
                    zipCode = "72072",
                    city = "Tübingen",
                    lat = 48.51625024119991,
                    lon = 9.063219241258452,
                ),
                title = "Tübinger Tage der digitalen Freiheit",
                link = "https://tdf.cttue.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 29, 11, 30, 0),
                to = LocalDateTime.of(2025, 7, 29, 12, 30, 0),
                location = Location(
                    online = true
                ),
                title = "CorrelCompact | Data Storytelling: Daten sprechen lassen!",
                link = "https://correlaid.org/veranstaltungen/correlcompact-storytelling-29-7?viewType=list"
            )
        )
        return events
    }

    private fun createJune2025Events(): List<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 3, 20, 0, 0),
                to = LocalDateTime.of(2025, 6, 3, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true,
                    lat = 52.5129735,
                    lon = 13.4201313
                ),
                title = "147. Netzpolitischer Abend",
                link = "https://digitalegesellschaft.de/2025/05/147-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 5, 11, 0, 0),
                to = LocalDateTime.of(2025, 6, 5, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "openCode Connect Juni 2025: Umweltdaten transparent machen – das bietet die App UmweltNAVI",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-juni-2025-4475"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 4, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 5, 16, 0,0),
                location = Location(
                    name = "bUm – Raum für solidarisches Miteinander",
                    street = "Paul-Lincke-Ufer",
                    houseNumber = "21",
                    zipCode = "10999",
                    city = "Berlin",
                    lon = 13.4296611,
                    lat = 52.4937932
                ),
                title = "Weizenbaum Conference 2025: Empowering People in Online Spaces",
                link = "https://www.weizenbaum-conference.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 10, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 13, 22, 0,0),
                location = Location(
                    name = "Neues Rathaus",
                    street = "Martin-Luther-Ring",
                    houseNumber = "4-6",
                    zipCode = "04109",
                    city = "Leipzig",
                    lon = 12.372519385027744,
                    lat = 51.3362747589957
                ),
                title = "Dataweek",
                link = "https://2025.dataweek.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 12, 10, 30, 0),
                to = LocalDateTime.of(2025, 6, 12, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "Linked Data im Mobilitätskontext: NOVA-Daten als Open Data nutzen",
                link = "https://linked-open-data.event.sbb.ch"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 4, 13, 0, 0),
                to = LocalDateTime.of(2025, 6, 4, 14, 0, 0),
                location = Location(
                    online = true
                ),
                title = "GIS@Lunch-Webinar: Open Data-Angebot des BKG",
                link = "https://www.gdi-suedhessen.de/gislunch-webinar-open-data-angebot-des-bkg/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 7, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 7, 17, 0, 0),
                location = Location(
                    name = "Lehngericht Augustusburg",
                    street = "Markt",
                    houseNumber = "14",
                    zipCode = "09573",
                    city = "Augustusburg",
                    online = true,
                    lat = 50.814184600000004,
                    lon = 13.099928202926208
                ),
                title = "Kartenwerkstatt Augustusburg",
                link = "https://www.aufweiterflur.org/event-details/kartenwerkstatt-mit-openstreetmap"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 4, 9, 15, 0),
                to = LocalDateTime.of(2025, 7, 6, 17, 30, 0),
                location = Location(
                    name = "Kulturhaus Eidelstedt",
                    street = "Alte Elbgaustraße",
                    houseNumber = "12",
                    zipCode = "22523",
                    city = "Hamburg",
                    lat = 53.6073818,
                    lon = 9.9032184
                ),
                title = "Chaos Feminist Convention",
                link = "https://events.haecksen.org/cfc25/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 24, 19, 30, 0),
                to = LocalDateTime.of(2025, 6, 24, 21, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "OSM Radinfra-Mapathon #2",
                link = "https://osmcal.org/event/3692/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 19, 14, 0, 0),
                to = LocalDateTime.of(2025, 6, 22, 14, 0, 0),
                location = Location(
                    name = "Zentrum für Kunst und Medien (ZKM)",
                    street = "Lorenzstr.",
                    houseNumber = "15",
                    zipCode = "76133",
                    city = "Karlsruhe",
                    lat = 49.0020695,
                    lon = 8.383668296343833
                ),
                title = "23. Gulaschprogrammiernacht (GPN)",
                link = "https://entropia.de/GPN23"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 18, 9, 0, 0),
                to = LocalDateTime.of(2025, 6, 18, 22, 0, 0),
                location = Location(
                    name = "Umweltforum",
                    street = "Pufendorfstraße",
                    houseNumber = "11",
                    zipCode = "10249",
                    city = "Berlin",
                    lat = 52.520961,
                    lon = 13.4382144
                ),
                title = "CityLAB Sommerkonferenz 2025",
                link = "https://citylab-berlin.org/de/events/citylab-sommerkonferenz-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 27, 9, 0, 0),
                to = LocalDateTime.of(2025, 6, 28, 19, 0, 0),
                location = Location(
                    name = "farm (Shedhalle)",
                    street = "Bücklestraße",
                    houseNumber = "3",
                    zipCode = "78467",
                    city = "Konstanz",
                    lat = 47.676813100000004,
                    lon = 9.169326478244056
                ),
                title = "Hack and Harvest Hackathon",
                link = "https://correlaid.org/veranstaltungen/hack-and-harvest-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 4, 17, 30, 0),
                to = LocalDateTime.of(2025, 7, 6, 12, 0, 0),
                location = Location(
                    name = "Jugendherberge Otto-Moericke-Turm Konstanz",
                    street = "Zur Allmannshöhe",
                    houseNumber = "16",
                    zipCode = "78464",
                    city = "Konstanz",
                    lat = 47.6871632,
                    lon = 9.2033462
                ),
                title = "CorrelCon 2025 + 10 Jahre CorrelAid",
                link = "https://correlaid.org/veranstaltungen/correlcon2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 16, 11, 0, 0),
                to = LocalDateTime.of(2025, 6, 16, 16, 0, 0),
                location = Location(
                    name = "MEDIAN Hotel Hannover Lehrte",
                    street = "Zum Blauen See",
                    houseNumber = "3",
                    zipCode = "31275",
                    city = "Lehrte",
                    lat = 52.3880356,
                    lon = 9.9696623
                ),
                title = "Offene KI in der Schule",
                link = "https://www.wikimedia.de/veranstaltungen/offene-ki-in-der-schule/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 5, 18, 0, 0),
                to = LocalDateTime.of(2025, 6, 5, 21, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 13, 18, 0, 0),
                to = LocalDateTime.of(2025, 6, 13, 22, 0, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.4984142,
                    lon = 13.3810486
                ),
                title = "Populismus, Desinformation und Manipulation: Wie retten wir valides Wissen im Netz?",
                link = "https://www.wikimedia.de/veranstaltungen/populismus-desinformation-und-manipulation/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 27, 7, 0, 0),
                to = LocalDateTime.of(2025, 6, 27, 19, 0, 0),
                location = Location(
                    name = "an verschiedenen Orten",
                    online = true
                ),
                title = "Digitaltag 2025",
                link = "https://digitaltag.eu"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 5, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 5, 16, 0, 0),
                location = Location(
                    name = "ecos work spaces",
                    street = "Gottorpstraße",
                    houseNumber = "8",
                    zipCode = "26122",
                    city = "Oldenburg",
                    lat = 53.1420309,
                    lon = 8.217251048461254
                ),
                title = "14. Betrieblichen Umweltinformationssystem-(BUIS)-Tage: Smarte und Nachhaltige Infrastrukturen",
                link = "https://buis-tage25.vlba.net"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 11, 18, 0, 0),
                to = LocalDateTime.of(2025, 6, 11   , 19, 0, 0),
                location = Location(
                    online = true
                ),
                title = "STEM GIrls 2025: Green IT: Wie kann ich meinen Beitrag zu einer nachhaltigen IT leisten?",
                link = "https://junge.gi.de/veranstaltung/green-it-wie-kann-ich-meinen-beitrag-zu-einer-nachhaltigen-it-leisten"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 4, 16, 0, 0),
                to = LocalDateTime.of(2025, 7, 6, 18, 0, 0),
                location = Location(
                    name = "Aaccelerator",
                    street = "Blezingerstraße",
                    houseNumber = "15",
                    zipCode = "73430",
                    city = "Aalen",
                    lat = 48.8544329,
                    lon = 10.0906347
                ),
                title = "FAT25 - Always watching",
                link = "https://hackwerk.fun/start"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 9, 0, 0),
                to = LocalDateTime.of(2025, 7, 5, 18, 0, 0),
                location = Location(
                    name = "Pfadfinderheim Welfenhof",
                    street = "III. Koppelweg",
                    houseNumber = "6",
                    zipCode = "38518",
                    city = "Gifhorn",
                    lat = 52.46289915,
                    lon = 10.604584416286588
                ),
                title = "Hacken Open Air 2::25",
                link = "https://hackenopenair.de/index.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 15, 14, 30, 0),
                to = LocalDateTime.of(2025, 6, 17, 17, 30, 0),
                location = Location(
                    name = "Kulturbrauerei",
                    street = "Schönhauser Allee",
                    houseNumber = "36-39",
                    zipCode = "10435",
                    city = "Berlin",
                    online = true,
                    lat = 52.5392251,
                    lon = 13.4136688
                ),
                title = "Berlin Buzzwords",
                link = "https://2025.berlinbuzzwords.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 20, 17, 0, 0),
                to = LocalDateTime.of(2025, 6, 20, 22, 0, 0),
                location = Location(
                    name = "ScaDS.AI Dresden/Leipzig",
                    street = "Humboldtstr.",
                    houseNumber = "25",
                    zipCode = "04105",
                    city = "Leipzig",
                    lat = 51.3466504,
                    lon = 12.3740462
                ),
                title = "Lange Nacht der Wissenschaften am ScaDS.AI Dresden/Leipzig",
                link = "https://scads.ai/event/lange-nacht-der-wissenschaften/long-night-of-science-leipzig-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 19, 13, 0, 0),
                to = LocalDateTime.of(2025, 6, 20, 16, 0, 0),
                location = Location(
                    name = "Brandenburg Museum für Zukunft, Gegenwart und Geschichte",
                    street = "Am Neuen Markt",
                    houseNumber = "9",
                    zipCode = "14467",
                    city = "Potsdam",
                    lat = 52.3959751,
                    lon = 13.0566354
                ),
                title = "QUADRIGA Jahrestagung 2025 und Barcamp \"Data Literacy\"",
                link = "https://www.quadriga-dk.de/de/quadriga-jahresveranstaltung-2025/call"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 23, 16, 0, 0),
                to = LocalDateTime.of(2025, 6, 23, 18, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "Over the fence: Digitale Souveränität und Open Source in Europa",
                link = "https://ak-oss.gi.de/veranstaltung/detail/over-the-fence-digitale-souveraenitaet-und-open-source-in-europa"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 5, 19, 31, 0),
                to = LocalDateTime.of(2025, 6, 5, 19, 31, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://bbb.tu-dresden.de/rooms/qje-7si-xu1-lul/join"
                ),
                title = "Bits und Bäume Community Treffen",
                link = "https://discourse.bits-und-baeume.org/t/2025-06-05-online-community-treffen-19-31-uhr/1625"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 26, 10, 30, 0),
                to = LocalDateTime.of(2025, 6, 26, 12, 45, 0),
                location = Location(
                    online = true,
                ),
                title = "Digital Souverän – Jetzt erst recht",
                link = "https://www.digitaler-staat.online/events/digitalsouveraen/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 19, 18, 0, 0),
                to = LocalDateTime.of(2025, 6, 19, 22, 0, 0),
                location = Location(
                    name = "Mitten in Hamburg (die genaue Location erhaltet Ihr nach Anmeldung)",
                    city = "Hamburg",
                    lat = 53.550341,
                    lon = 10.000654
                ),
                title = "Public Sector & Friends // Hamburg",
                link = "https://egovernment-podcast.com/event/govtech-goes-public-sector-friends-hamburg-volume-3-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 18, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 18, 17, 0, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.4984142,
                    lon = 13.3810486
                ),
                title = "Barcamp Open Science",
                link = "https://www.barcamp-open-science.eu"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 1, 10, 0, 0),
                to = LocalDateTime.of(2025, 7, 1, 16, 30, 0),
                location = Location(
                    name = "Startplatz Köln",
                    street = "Im Mediapark",
                    houseNumber = "5",
                    zipCode = "50670",
                    city = "Köln",
                    lat = 50.9486808,
                    lon = 6.9447581
                ),
                title = "Klimaaktiv-Event – Barcamp kommunaler Klimaschutz",
                link = "https://difu.de/veranstaltungen/2025-07-01/klimaaktiv-event-barcamp-kommunaler-klimaschutz"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 25, 20, 0, 0),
                to = LocalDateTime.of(2025, 6, 25, 21, 30, 0),
                location = Location(online = true),
                title = "Open Transport Meetup: Michael Lorenzen (VBB) - DELFI GTFS-RT",
                link = "https://hackmd.okfn.de/opentransportmeetup"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 9, 20, 0, 0),
                to = LocalDateTime.of(2025, 7, 9, 21, 30, 0),
                location = Location(online = true),
                title = "Open Transport Meetup: Holger Bruch - Deutschlandweiter GTFS Feed aus DELFI NeTEx Daten",
                link = "https://hackmd.okfn.de/opentransportmeetup"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 14, 14, 0, 0),
                to = LocalDateTime.of(2025, 6, 14, 18, 0, 0),
                location = Location(
                    name = "KI-Ideenwerkstatt für Umweltschutz",
                    street = "Rollbergstr.",
                    houseNumber = "28A",
                    zipCode = "12053",
                    city = "Berlin",
                    lat = 52.4790412,
                    lon = 13.4319106,
                ),
                title = "KI-Ideenwerkstatt beim Langen Tag der StadtNatur",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/ki-ideenwerkstatt-beim-langen-tag-der-stadtnatur/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 18, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 18, 12, 0, 0),
                location = Location(
                    name = "Museum für Naturkunde Berlin,",
                    street = "Invalidenstraße",
                    houseNumber = "43",
                    zipCode = "10115",
                    city = "Berlin",
                    online = true,
                    lat = 52.5304903,
                    lon = 13.3791152
                ),
                title = "KI trifft Biodiversitätsforschung: Datengewinnung aus Sammlungsetiketten",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/ki-trifft-biodiversitaetsforschung-datengewinnung-aus-sammlungsetiketten/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 6, 15, 0, 0),
                to = LocalDateTime.of(2025, 6, 8, 17, 0, 0),
                location = Location(
                    name = "Jugendhaus Riedberg",
                    street = "Friedrich-Dessauer-Straße",
                    houseNumber = "4-6",
                    zipCode = "60438",
                    city = "Frankfurt am Main",
                    lat = 50.1786142,
                    lon = 8.625099866085272
                ),
                title = "Jugend Hackt Frankfurt",
                link = "https://jugendhackt.org/events/frankfurt/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 5, 9, 0, 0),
                to = LocalDateTime.of(2025, 6, 5, 19, 0, 0),
                location = Location(
                    name = "Festsaal Kreuzberg",
                    street = "Am Flutgraben",
                    houseNumber = "2",
                    zipCode = "12435",
                    city = "Berlin",
                    lat = 52.49682315,
                    lon = 13.451555564938815,
                    online = true
                ),
                title = "Creative Bureaucracy Festival",
                link = "https://creativebureaucracy.org/de/festival"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 11, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 11, 14, 0, 0),
                location = Location(
                    name = "Bundesministerium für Umwelt, Klimaschutz, Naturschutz und nukleare Sicherheit (BMUKN)",
                    street = "Stresemannstraße",
                    houseNumber = "128",
                    zipCode = "10117",
                    city = "Berlin",
                    lat = 52.50802195,
                    lon = 13.378533577137322
                ),
                title = "Green-IT-Fachtagung \"Open Source und Blauer Engel Software\"",
                link = "https://www.umweltbundesamt.de/service/termine/green-it-fachtagung-open-source-blauer-engel"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 16, 19, 0, 0),
                to = LocalDateTime.of(2025, 6, 16, 22, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin/events/307800527/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 13, 15, 30, 0),
                to = LocalDateTime.of(2025, 6, 13, 22, 0, 0),
                location = Location(
                    name = "Neues Rathaus",
                    street = "Martin-Luther-Ring",
                    houseNumber = "4-6",
                    zipCode = "04109",
                    city = "Leipzig",
                    lon = 12.372519385027744,
                    lat = 51.3362747589957
                ),
                title = "Jugend hackt Hackday: Sensorik fürs Wohnzimmer",
                link = "https://anmeldung.jugendhackt.org/leipzig/data-week-25/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 17, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 17, 11, 30, 0),
                location = Location(online = true),
                title = "18. Open Data Netzwerktreffen",
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/das-kommunale-open-data-netzwerktreffen"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 7, 10, 11, 0, 0),
                to = LocalDateTime.of(2025, 7, 10, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "openCode Connect Juli 2025: GA-Lotse – Zukunftsfähige Verwaltungsdigitalisierung für Gesundheitsämter",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-juli-2025-4564"
            )
        )
        return events
    }

    private fun createMay2025Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 16, 9, 0, 0),
                to = LocalDateTime.of(2025, 5, 16, 16, 0, 0),
                location = Location(
                    name = "ALICE Rooftop & Garden",
                    street = "Kantstraße",
                    houseNumber = "17",
                    zipCode = "10623",
                    city = "Berlin",
                    lat = 52.5060665,
                    lon = 13.3245821
                ),
                title = "Berlin Open Data Day 2025",
                link = "https://daten.berlin.de/artikel/berlin-open-data-day-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 9, 11, 0, 0),
                to = LocalDateTime.of(2025, 5, 9, 12, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "Open Access handgemacht – die Entwicklung des Journal Kommunikation@Gesellschaft, ein Modell für andere?",
                link = "https://blog.adlr.link/2025/04/17/online-workshop-reihe-fid-media-bites-startet/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 12, 15, 0, 0),
                to = LocalDateTime.of(2025, 5, 12, 16, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "COMMUNIA Salon: Unfair licensing practices",
                link = "https://communia-association.org/event/communia-salon-unfair-licensing-practices/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 12, 10, 0, 0),
                to = LocalDateTime.of(2025, 5, 12, 12, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "Vernetzungsforum: Institutionelle Repositorien und Forschungsdaten",
                link = "https://www.ibi.hu-berlin.de/de/forschung/infomanagement/events/prooarde-forschungsdaten/vernetzungsforum-institutionelle-repositorien-und-forschungsdaten"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 15, 16, 0, 0),
                to = LocalDateTime.of(2025, 5, 15, 17, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Open Access meets KI",
                link = "https://enable-oa.org/news/open-access-meets-ki"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 23, 14, 0, 0),
                to = LocalDateTime.of(2025, 5, 23, 15, 40, 0),
                location = Location(
                    online = true,
                ),
                title = "Open Access as a Business Model: Practical Insights and Disciplinary Comparisons",
                link = "https://events.gwdg.de/event/1077/overview"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 22, 9, 30, 0),
                to = LocalDateTime.of(2025, 5, 22, 16, 30, 0),
                location = Location(
                    name = "Internationales Caritas-Zentrum Sülz",
                    street = "Zülpicher Str.",
                    houseNumber = "273b",
                    zipCode = "50937",
                    city = "Köln",
                    lat = 50.92206535,
                    lon = 6.922585712580955
                ),
                title = "Civic Data Camp - Das Barcamp für Civic Data Explorer",
                link = "https://civic-data.de/barcamp/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 8, 11, 0, 0),
                to = LocalDateTime.of(2025, 5, 8, 12, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "openCode Connect Mai: Connected Urban Twins – Das Energiewende-Dashboard als Erfolgsbeispiel interkommunaler Kooperation",
                link = "https://egovernment-podcast.com/event/opencode-connect-mai-connected-urban-twins-das-energiewende-dashboard-als-erfolgsbeispiel-interkommunaler-kooperation/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 6, 20, 0, 0),
                to = LocalDateTime.of(2025, 5, 6, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true,
                    lat = 52.5129735,
                    lon = 13.4201313
                ),
                title = "146. Netzpolitischer Abend – Digitalpolitische Aspekte des Koalitionsvertrages",
                link = "https://digitalegesellschaft.de/2025/04/146-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 29, 18, 0, 0),
                to = LocalDateTime.of(2025, 6, 1, 16, 0, 0),
                location = Location(
                    name = "Villa Ritter",
                    street = "Juravorstadt",
                    houseNumber = "36",
                    zipCode = "2502",
                    city = "Biel (Schweiz)",
                    lat = 47.1442554,
                    lon = 7.249968954367201
                ),
                title = "Chaos Singularity (CoSin) 2025",
                link = "https://cosin.ch/de/"
            )
        )

        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 23, 16, 0, 0),
                to = LocalDateTime.of(2025, 5, 25, 23, 59, 0),
                location = Location(
                    name = "dezentrale e.V.",
                    street = "Eisenbahnstraße",
                    houseNumber = "9 (Hinterhaus)",
                    zipCode = "04315",
                    online = true,
                    lat = 51.346432,
                    lon = 12.3962347
                ),
                title = "jetzt9 Geekend",
                link = "https://tickets.chaos.jetzt/jetzt9/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 21, 12, 30, 0),
                to = LocalDateTime.of(2025, 5, 22, 14, 30, 0),
                location = Location(
                    name = "Hochschule Merseburg",
                    street = "Eberhard-Leibnitz-Straße",
                    houseNumber = "2",
                    zipCode = "06217",
                    city = "Merseburg",
                    online = true,
                ),
                title = "Umweltinformationssysteme 2025",
                link = "https://fa-ui.gi.de/veranstaltung/umweltinformationssysteme-2025-gesellschaftliche-transformation-durch-umweltinformationssysteme-digitale-innovationen-fuer-eine-nachhaltige-zukunft"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 19, 16, 0, 0),
                to = LocalDateTime.of(2025, 5, 19, 18, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "Intelligente Zukunft: Open Source KI und Ethik im Fokus",
                link = "https://ak-oss.gi.de/veranstaltung/detail/intelligente-zukunft-open-source-ki-und-ethik-im-fokus"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 14, 12, 0, 0),
                to = LocalDateTime.of(2025, 5, 14, 13, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "KI in Digital Health",
                link = "https://fg-digital-health.gi.de/veranstaltung/themensalon-digital-health-zum-thema-ki-in-digital-health-stand-der-technik-herausforderungen-und-potenziale-anhand-von-beispielen-aus-forschung-und-praxis"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 16, 10, 0, 0),
                to = LocalDateTime.of(2025, 5, 16, 11, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "Open judicial data, AI, and transparency in the digital age",
                link = "https://dataeuropaacademy.clickmeeting.com/webinar-open-judicial-data-ai-and-transparency-in-the-digital-age-/register"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 13, 10, 0, 0),
                to = LocalDateTime.of(2025, 5, 15, 12, 45, 0),
                location = Location(
                    online = true,
                ),
                title = "Interoperable Europe Academy Seasonal School",
                link = "https://interoperable-europe.ec.europa.eu/collection/interoperable-europe-academy/event/seasonal-school-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 6, 10, 0, 0),
                to = LocalDateTime.of(2025, 5, 6, 11, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "The value of EU vocabularies for data spaces",
                link = "https://op.europa.eu/en/web/endorse/follow-up-events"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 7, 10, 0, 0),
                to = LocalDateTime.of(2025, 6, 7, 17, 0, 0),
                location = Location(
                    name = "Lehngericht Augustusburg",
                    street = "Markt",
                    houseNumber = "14",
                    zipCode = "09573",
                    city = "Augustusburg",
                    online = true,
                ),
                title = "Kartenwerkstatt Augustusburg",
                link = "https://www.aufweiterflur.org/event-details/kartenwerkstatt-mit-openstreetmap"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 16, 19, 0, 0),
                to = LocalDateTime.of(2025, 5, 18, 13, 0, 0),
                location = Location(
                    name = "Linuxhotel",
                    street = "Antonienallee",
                    houseNumber = "3",
                    zipCode = "45279",
                    city = "Essen",
                    lat = 51.43071555,
                    lon = 7.112465469856769
                ),
                title = "FOSSGIS-OSM-Communitytreffen im Linuxhotel",
                link = "https://www.fossgis.de/wiki/FOSSGIS_OSM_Communitytreffen_2025_Nummer_23"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 15, 19, 0, 0),
                to = LocalDateTime.of(2025, 5, 15, 22, 0, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.4984142,
                    lon = 13.3810486
                ),
                title = "tech from below #11",
                link = "https://www.wikimedia.de/veranstaltungen/tech-from-below-11/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 8, 18, 0, 0),
                to = LocalDateTime.of(2025, 5, 8, 21, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert-3/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 27, 9, 30, 0),
                to = LocalDateTime.of(2025, 5, 27, 13, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Biobasierte Kunststoffe - Praxisworkshop",
                link = "https://www.dbfz.de/praxisworkshop-biobasierte-kunststoffe/start"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 18, 11, 0, 0),
                to = LocalDateTime.of(2025, 5, 18   , 18, 0, 0),
                location = Location(
                    name = "Am Karlsruher Schloss und stadtweit",
                    street = "Schlossbezirk",
                    houseNumber = "1",
                    zipCode = "76131",
                    city = "Karlsruhe",
                    lat = 49.0135248,
                    lon = 8.40435918703854
                ),
                title = "Underständ the Länd (im Rahmen vom Wissenschaftsfestival EFFEKTE)",
                link = "https://ok-lab-karlsruhe.de/projekte/effekte25/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 8, 11, 0, 0),
                to = LocalDateTime.of(2025, 5, 8, 12, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Eins für Alle: Das Energiewende-Dashboard als Erfolgsbeispiel interkommunaler Kooperation",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-mai-2025-3399"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 7, 9, 30, 0),
                to = LocalDateTime.of(2025, 5, 7, 16, 30, 0),
                location = Location(
                    name = "Leopoldina",
                    street = "Jägerberg",
                    houseNumber = "1",
                    zipCode = "06108",
                    city = "Halle (Saale)",
                    online = true,
                ),
                title = "KEDi Convention",
                link = "https://www.kedi-dena.de/veranstaltungen/kedi-convention-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 10, 13, 0, 0),
                to = LocalDateTime.of(2025, 5, 10, 19, 0, 0),
                location = Location(
                    name = "Max-Planck-Campus",
                    street = "Am Mühlenberg",
                    houseNumber = "1",
                    zipCode = "14476",
                    city = "Potsdam",
                    lat = 52.41562675,
                    lon = 12.970000479384487
                ),
                title = "Potsdamer Tag der Wissenschaft",
                link = "https://potsdamertagderwissenschaften.de/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 5, 9, 15, 0),
                to = LocalDateTime.of(2025, 5, 7, 17, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Energietage 2025",
                link = "https://www.energietage.de/kongress/gesamtprogramm.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 5, 18, 0, 0),
                to = LocalDateTime.of(2025, 5, 7, 16, 0, 0),
                location = Location(
                    name = "Kongresszentrum Konzerthaus Freiburg",
                    street = "Konrad-Adenauer-Platz",
                    houseNumber = "1",
                    zipCode = "79098",
                    city = "Freiburg",
                    lon = 7.842418268753468,
                    lat = 47.995716099999996,
                ),
                title = "2. ÖPNV-Zukunftskongress 2025",
                link = "https://www.zukunftsnetzwerk-oepnv.de/aktuelles/veranstaltungen/2-oepnv-zukunftskongress-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 6, 14, 30, 0),
                to = LocalDateTime.of(2025, 5, 6, 16, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Teil 1 der Workshopreihe Sovereign. Sustainable. Digital.: Das Fediverse und seine sozialen Medien",
                link = "https://www.bmuv.de/veranstaltung/teil-1-der-workshopreihe-sovereign-sustainable-digital-das-fediverse-und-seine-sozialen-medien"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 10, 11, 0, 0),
                to = LocalDateTime.of(2025, 5, 10, 19, 0, 0),
                location = Location(
                    name = "Einstein Center Digital Future (ECDF)",
                    street = "Wilhelmstraße",
                    houseNumber = "67",
                    zipCode = "10117",
                    city = "Berlin",
                    online = false,
                    lat = 52.5183956,
                    lon = 13.3806021
                ),
                title = "Beyond Education: Die Bildungskonferenz von Jugend hackt",
                link = "https://okfn.de/blog/2025/04/beyond-education-die-bildungskonferenz-von-jugend-hackt/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 9, 17, 0, 0),
                to = LocalDateTime.of(2025, 5, 11, 15, 45, 0),
                location = Location(
                    name = "Universität Erfurt",
                    street = "Nordhäuser Str.",
                    houseNumber = "63",
                    zipCode = "99089",
                    city = "Erfurt",
                    online = false,
                    lat = 50.9909272,
                    lon = 11.009547426126073
                ),
                title = "CORRECTIV.Lokal Konferenz 2025",
                link = "https://correctiv.org/lokal/konferenz/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 8, 18, 30, 0),
                to = LocalDateTime.of(2025, 5, 24, 21, 15, 0),
                location = Location(
                    name = "Impact Hub",
                    street = "Trompeterstraße",
                    houseNumber = "5",
                    zipCode = "01069",
                    city = "Dresden",
                    online = false,
                    lat = 51.046278,
                    lon = 13.7348812
                ),
                title = "Dear Future – Dresdner Nachhaltigkeitsfestival",
                link = "https://dearfuturedresden.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 22, 10, 0, 0),
                to = LocalDateTime.of(2025, 5, 22, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "Wikidata für Kulturerbeeinrichtungen",
                link = "https://www.digis-berlin.de/veranstaltungen/workshops/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 26, 12, 30, 0),
                to = LocalDateTime.of(2025, 5, 28, 17, 0, 0),
                location = Location(
                    name = "STATION Berlin",
                    street = "Luckenwalder Straße",
                    houseNumber = "4-6",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.49868925,
                    lon = 13.374857573182297
                ),
                title = "re:publica 25",
                link = "https://re-publica.com/de/news/save-date-republica-25-26-28-mai-2025-station-berlin"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 6, 4, 13, 0, 0),
                to = LocalDateTime.of(2025, 6, 4, 14, 0, 0),
                location = Location(
                    online = true
                ),
                title = "GIS@Lunch-Webinar: Open Data-Angebot des BKG",
                link = "https://www.gdi-suedhessen.de/gislunch-webinar-open-data-angebot-des-bkg/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 16, 14, 0, 0),
                to = LocalDateTime.of(2025, 5, 16, 15, 30, 0),
                location = Location(
                    online = true
                ),
                title = "Against the Odds: How to Collect Social Media Data",
                link = "https://nfdi4culture.de/news/against-the-odds-how-to-collect-social-media-data.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 12, 18, 0, 0),
                to = LocalDateTime.of(2025, 5, 12, 20, 0, 0),
                location = Location(
                    name = "Presseclub Concordia",
                    street = "Bankgasse",
                    houseNumber = "8",
                    zipCode = "A-1010",
                    city = "Wien (Österreich)",
                    online = true,
                    lat = 48.2102509,
                    lon = 16.3628968
                ),
                title = "Her mit den Daten: Informationsfreiheit in der Praxis. Unter Druck – Erfahrungen aus Ungarn mit Direkt36.hu",
                link = "https://concordia.at/her-mit-den-daten-informationsfreiheit-in-der-praxis-unter-druck-erfahrungen-aus-ungarn-mit-direkt36-hu/"
            )
        )
        val comparator = compareBy<Event> { it.from }.thenComparator({ a, b -> compareValues(a.title, b.title) })
        return events.sortedWith(comparator).toMutableList()
    }

    private fun createApril2025Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 1, 20, 0, 0),
                to = LocalDateTime.of(2025, 4, 1, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true
                ),
                title = "145. Netzpolitischer Abend",
                link = "https://digitalegesellschaft.de/2025/03/145-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 3, 8, 0, 0),
                to = LocalDateTime.of(2025, 4, 3, 14, 0, 0),
                location = Location(online = true),
                title = "Girls Day Online Events",
                link = "https://www.girls-day.de/Radar?lat=0&lon=0&locality=&keywords=programmieren&sortBy=&sortDesc=false&page=1&pageSize=30&radius=20&gkz=&initiativeId=0&categoryIds=&minimumFreePlaces=1&maximumFreePlaces=-1&minimumParticipantsAge=0&onlySubscribable=false&onlyAccessible=false&onlyEmbedded=false&onlyVirtual=true&onlyNonvirtual=false&organizerId=0&freePlaces=mindestens+1&locationType=digital+%2F+vom+Rechner+aus&warmStart=true&for=event"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 3, 8, 0, 0),
                to = LocalDateTime.of(2025, 4, 3, 12, 30, 0),
                location = Location(
                    name = "Freie Universität Berlin - Zuse-Institut Berlin (ZIB)",
                    street = "Takustraße",
                    houseNumber = "9",
                    zipCode = "14195",
                    city = "Berlin",
                    lat = 52.4559809,
                    lon = 13.297162523268483
                ),
                title = "Girls Day am ZIB",
                link = "https://www.zib.de/event/girlsday-am-zib-sei-dabei"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 3, 10, 0, 0),
                to = LocalDateTime.of(2025, 4, 3, 18, 30, 0),
                location = Location(
                    name = "Technologiestiftung Berlin - CityLAB",
                    street = "Platz der Luftbrücke",
                    houseNumber = "4",
                    zipCode = "12101",
                    city = "Berlin",
                    lat = 52.4838796,
                    lon = 13.3885778
                ),
                title = "Girls Day 2025 im CityLAB – Zukunftsjobs mit Daten!",
                link = "https://citylab-berlin.org/de/events/zukunftstag-2025-im-citylab-tauche-ein-in-die-welt-der-daten/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 4, 9, 30, 0),
                to = LocalDateTime.of(2025, 4, 5, 18, 30, 0),
                location = Location(
                    name = "Rathaus Moers",
                    street = "Rathausplatz",
                    houseNumber = "1",
                    zipCode = "47441",
                    city = "Moers",
                    lat = 51.4532487,
                    lon = 6.626686
                ),
                title = "10 Jahre Hackday Moers",
                link = "https://www.moers.de/veranstaltungen/hackday-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 4, 18, 0, 0),
                to = LocalDateTime.of(2025, 4, 6, 13, 0, 0),
                location = Location(
                    name = "temporärhaus",
                    street = "Augsburger Straße",
                    houseNumber = "23–25",
                    zipCode = "89231",
                    city = "Neu-Ulm",
                    lat = 52.49682315,
                    lon = 13.451555564938815
                ),
                title = "FemNetzCon 2025",
                link = "https://de.wikipedia.org/wiki/Wikipedia:WikiProjekt_FemNetz/FemNetzCon_2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 7, 14, 0, 0),
                to = LocalDateTime.of(2025, 4, 7, 15, 0, 0),
                location = Location(online = true),
                title = "CorrelCompact: Data Storytelling - Daten sprechen lassen!",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-storytelling-25-1"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 8, 9, 0, 0),
                to = LocalDateTime.of(2025, 4, 8, 12, 0, 0),
                location = Location(online = true),
                title = "AI Impact - KI und Nachhaltigkeit",
                link = "https://www.erwachsenenbildung-ekhn.de/programm/kw/bereich/kursdetails/kurs/2025-D-033/kursname/Online%20AI%20Impact%20-%20KI%20und%20Nachhaltigkeit/kategorie-id/5/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 8, 11, 0, 0),
                to = LocalDateTime.of(2025, 4, 8, 12, 0, 0),
                location = Location(online = true),
                title = "Ope Parl Data: Public Meeting #4",
                link = "https://opendata.ch/de/events/open-parl-data-public-meeting-4/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 9, 0, 0),
                to = LocalDateTime.of(2025, 4, 9, 16, 0, 0),
                location = Location(
                    name = "Kaisersaal",
                    street = "Futterstraße",
                    houseNumber = "15/16",
                    zipCode = "99084",
                    city = "Erfurt",
                    lat = 50.97936970000001,
                    lon = 11.033602232239485,
                ),
                title = "4. Open Data Barcamp 2025",
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/4-open-data-barcamp-2025-einladungsseite"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 9, 30, 0),
                to = LocalDateTime.of(2025, 4, 9, 11, 0, 0),
                location = Location(online = true),
                title = "KI in Pflege und Betreuung am Beispiel von CareMates",
                link = "https://www.charta28.de/kommunikationsorte/ki-in-der-sozialwirtschaft"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 12, 30, 0),
                to = LocalDateTime.of(2025, 4, 9, 15, 0, 0),
                location = Location(
                    online = true
                ),
                title = "Einsatz und Anwendung kontrollierter Vokabulare und Normdaten",
                link = "https://www.digis-berlin.de/workshop-kontrollierte-vokabulare/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 14, 30, 0),
                to = LocalDateTime.of(2025, 4, 9, 17, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "Sovereign. Sustainable. Digital.: Digitale Souveränität nachhaltig stärken",
                link = "https://www.bmuv.de/veranstaltung/auftaktveranstaltung-der-workshopreihe-sovereign-sustainable-digital-digitale-souveraenitaet-nachhaltig-staerken"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 17, 30, 0),
                to = LocalDateTime.of(2025, 4, 9, 20, 30, 0),
                location = Location(
                    name = "KI-Ideenwerkstatt für Umweltschutz",
                    street = "Rollbergstr.",
                    houseNumber = "28A",
                    zipCode = "12053",
                    city = "Berlin",
                    lat = 52.4790412,
                    lon = 13.4319106,
                ),
                title = "Rethink Recycling: KI in der Praxis",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/themenabend-rethink-recycling-ki-in-der-praxis/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 20, 0, 0),
                to = LocalDateTime.of(2025, 4, 9, 21, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Wir verteidigen die Informationsfreiheit",
                link = "https://europe-calling.de/webinar/informationsfreiheit/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 10, 9, 30, 0),
                to = LocalDateTime.of(2025, 4, 10, 18, 30, 0),
                location = Location(
                    name = "Weizenbaum-Institut",
                    street = "Hardenbergstr.",
                    houseNumber = "32",
                    zipCode = "10623",
                    city = "Berlin",
                    lon = 13.3292227,
                    lat = 52.5081411
                ),
                title = "Who Owns Free Knowledge? Examining Power, Platformization, and the Promise of the Commons",
                link = "https://www.weizenbaum-institut.de/conference-who-owns-free-knowledge/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 10, 8, 30, 0),
                to = LocalDateTime.of(2025, 4, 11, 15, 0, 0),
                location = Location(
                    name = "Park Hyatt Wien",
                    street = "Am Hof",
                    houseNumber = "2",
                    zipCode = "1010",
                    city = "Wien",
                    lon = 16.3679689,
                    lat = 48.210653,
                ),
                title = "Data Excellence Konferenz 2025",
                link = "https://www.adv.at/events/adv-data-excellence-konferenz-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 11, 16, 0, 0),
                to = LocalDateTime.of(2025, 4, 13, 15, 0, 0),
                location = Location(
                    name = "TU Dresden – Fakultät Informatik, Andreas-Pfitzmann-Bau",
                    street = "Nöthnitzer Str.",
                    houseNumber = "46",
                    zipCode = "01187",
                    city = "Dresden",
                    lon = 13.723108960907332,
                    lat = 51.02546065,
                ),
                title = "Jugend Hackt Dresden",
                link = "https://jugendhackt.org/events/dresden/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 14, 16, 0, 0),
                to = LocalDateTime.of(2025, 4, 14, 18, 0, 0),
                location = Location(
                    online = true
                ),
                title = "Unabhängig im digitalen Zeitalter: Digitale Souveränität durch Open Source",
                link = "https://ak-oss.gi.de/veranstaltung/information/unabhaengig-im-digitalen-zeitalter-digitale-souveraenitaet-durch-open-source"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 14, 18, 0, 0),
                to = LocalDateTime.of(2025, 4, 14, 21, 0, 0),
                location = Location(
                    online = true,
                    name = "TMF e.V.",
                    street = "Charlottenstraße",
                    houseNumber = "42",
                    zipCode = "10117",
                    city = "Berlin",
                    lon = 13.3903278,
                    lat = 52.5185963,
                ),
                title = "Health IT-Talk April: Bürgerzentrierte Gesundheitsdaten",
                link = "https://www.eventbrite.de/e/health-it-talk-april-burgerzentrierte-gesundheitsdaten-tickets-1299431708219"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 14, 19, 0, 0),
                to = LocalDateTime.of(2025, 4, 14, 22, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin/events/306775584/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 14, 19, 0, 0),
                to = LocalDateTime.of(2025, 4, 14, 22, 0, 0),
                location = Location(
                    name = "Lichtspiel / Kinemathek",
                    street = "Sandrainstrasse",
                    houseNumber = "3",
                    zipCode = "3007",
                    city = "Bern",
                    lon = 7.4421399,
                    lat = 46.9404989,
                ),
                title = "Netzpolitischer Abend zu Digitale Integrität",
                link = "https://www.digitale-gesellschaft.ch/event/netzpolitischer-abend-zu-digitale-integritaet/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 16, 12, 30, 0),
                to = LocalDateTime.of(2025, 4, 16, 13, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Mit App und Citizen Science zur besseren, individuellen Allergievorhersage",
                link = "https://www.technologiestiftung-berlin.de/veranstaltungen/soup-science-pollen-im-anflug"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 17, 11, 0, 0),
                to = LocalDateTime.of(2025, 4, 17, 12, 0, 0),
                location = Location(
                    online = true
                ),
                title = "openCode Connect April 2025: Das KERN Design-System – der Open-Source-Baukasten für barrierefreie und intuitive Verwaltungsleistungen",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-april-2025-2702"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 18, 15, 0, 0),
                to = LocalDateTime.of(2025, 4, 21, 15, 30, 0),
                location = Location(
                    name = "Kampnagel",
                    street = "Jarrestraße",
                    houseNumber = "20",
                    zipCode = "22303",
                    city = "Hamburg",
                    lon = 10.02217216661041,
                    lat = 53.5833009,
                ),
                title = "Easterhegg 2025",
                link = "https://eh22.easterhegg.eu"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 23, 19, 0, 0),
                to = LocalDateTime.of(2025, 4, 23, 22, 0, 0),
                location = Location(
                    name = "Hackquarter des Chaos Computer Club Frankfurt e.V.",
                    street = "Hohenstaufenstraße",
                    houseNumber = "8",
                    zipCode = "60327",
                    city = "Frankfurt am Main",
                    lon = 8.6565254,
                    lat = 50.1087657,
                ),
                title = "Erste Heimautomatisierung User Group",
                link = "https://ccc-ffm.de/2025/04/mi-23-04-19-uhr-erste-heimautomatisierung-user-group/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 26, 14, 0, 0),
                to = LocalDateTime.of(2025, 4, 26, 16, 0, 0),
                location = Location(
                    online = true,
                    name = "101LAB",
                    street = "Skalitzer Str.",
                    houseNumber = "100",
                    zipCode = "10997",
                    city = "Berlin",
                    lon = 13.428466,
                    lat = 52.4997077,
                ),
                title = "Großer Kickoff zur 101cloud Genossenschaftsgründung",
                link = "https://101lab.it/101cloud/geno.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 29, 12, 30, 0),
                to = LocalDateTime.of(2025, 4, 29, 15, 30, 0),
                location = Location(
                    online = true
                ),
                title = "Gemeinsam Machen 4 | Chatbot, LLM, RAG oder doch klassisches Wiki?",
                link = "https://app.guestoo.de/public/event/8fcfd801-ba78-4893-b009-101485852f5d?lang=de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 29, 19, 30, 0),
                to = LocalDateTime.of(2025, 4, 29, 21, 30, 0),
                location = Location(
                    online = true
                ),
                title = "OSM Radinfra-Mapping-Abend",
                link = "https://wiki.openstreetmap.org/wiki/Verkehrswende-Meetup#Meetups"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 5, 9, 15, 0),
                to = LocalDateTime.of(2025, 5, 7, 17, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Energietage 2025",
                link = "https://www.energietage.de/kongress/gesamtprogramm.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 5, 18, 0, 0),
                to = LocalDateTime.of(2025, 5, 7, 16, 0, 0),
                location = Location(
                    name = "Kongresszentrum Konzerthaus Freiburg",
                    street = "Konrad-Adenauer-Platz",
                    houseNumber = "1",
                    zipCode = "79098",
                    city = "Freiburg",
                    lon = 7.842418268753468,
                    lat = 47.995716099999996,
                ),
                title = "2. ÖPNV-Zukunftskongress 2025",
                link = "https://www.zukunftsnetzwerk-oepnv.de/aktuelles/veranstaltungen/2-oepnv-zukunftskongress-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 6, 14, 30, 0),
                to = LocalDateTime.of(2025, 5, 6, 16, 30, 0),
                location = Location(
                    online = true,
                ),
                title = "Teil 1 der Workshopreihe Sovereign. Sustainable. Digital.: Das Fediverse und seine sozialen Medien",
                link = "https://www.bmuv.de/veranstaltung/teil-1-der-workshopreihe-sovereign-sustainable-digital-das-fediverse-und-seine-sozialen-medien"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 10, 11, 0, 0),
                to = LocalDateTime.of(2025, 5, 10, 19, 0, 0),
                location = Location(
                    name = "Einstein Center Digital Future (ECDF)",
                    street = "Wilhelmstraße",
                    houseNumber = "67",
                    zipCode = "10117",
                    city = "Berlin",
                    online = false,
                    lat = 52.5183956,
                    lon = 13.3806021
                ),
                title = "Beyond Education: Die Bildungskonferenz von Jugend hackt",
                link = "https://okfn.de/blog/2025/04/beyond-education-die-bildungskonferenz-von-jugend-hackt/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 5, 9, 17, 0, 0),
                to = LocalDateTime.of(2025, 5, 11, 15, 45, 0),
                location = Location(
                    name = "Universität Erfurt",
                    street = "Nordhäuser Str.",
                    houseNumber = "63",
                    zipCode = "99089",
                    city = "Erfurt",
                    online = false,
                    lat = 50.9909272,
                    lon = 11.009547426126073
                ),
                title = "CORRECTIV.Lokal Konferenz 2025",
                link = "https://correctiv.org/lokal/konferenz/"
            )
        )

        return events.sortedBy { it.from }.toMutableList()
    }

    private fun createMarch2025Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 1, 0, 0, 0),
                to = LocalDateTime.of(2025, 3, 8, 23, 59, 59),
                location = Location(online = true),
                title = "Open Data Day 2025",
                link = "https://opendataday.org/de/events/2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 1, 9, 0, 0),
                to = LocalDateTime.of(2025, 3, 1, 16, 0, 0),
                location = Location(
                    name = "IT-Referat der Stadt München, Qubes Gebäude",
                    street = "Agnes-Pockels-Bogen",
                    houseNumber = "33",
                    zipCode = "80992",
                    city = "München",
                    lat = 48.17402935,
                    lon = 11.533678297485846
                ),
                title = "Open Data Day 2025 in München",
                link = "https://muenchen.digital/veranstaltungen/open-data-day-2025.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 1, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 1, 21, 0, 0),
                location = Location(
                    name = "Aktivitetshuset",
                    street = "Norderstraße",
                    houseNumber = "49",
                    zipCode = "24939",
                    city = "Flensburg",
                    lon = 9.431249601609665,
                    lat = 54.791591785985844
                ),
                title = "Open Data Day Flensburg 2025",
                link = "https://opendataday-flensburg.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 3, 11, 0, 0),
                to = LocalDateTime.of(2025, 3, 3, 17, 30, 0),
                location = Location(
                    name = "bUm – Raum für solidarisches Miteinander",
                    street = "Paul-Lincke-Ufer",
                    houseNumber = "21",
                    zipCode = "10999",
                    city = "Berlin",
                    lon = 13.4296611,
                    lat = 52.4937932
                ),
                title = "Prototype Fund: Demo Day der Förderrunde 16",
                link = "https://prototypefund.de/demo-day/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 4, 20, 0, 0),
                to = LocalDateTime.of(2025, 3, 4, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true
                ),
                title = "144. Netzpolitischer Abend",
                link = "https://digitalegesellschaft.de/2025/02/144-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 5, 9, 0, 0),
                to = LocalDateTime.of(2025, 3, 5, 15, 0, 0),
                location = Location(
                    name = "FH Technikum Wien",
                    street = "Höchstädtplatz",
                    houseNumber = "6",
                    zipCode = "1200",
                    city = "Wien",
                    lon = 16.3774409,
                    lat = 48.2391664
                ),
                title = "Open Data Expo 2025",
                link = "https://digitales.wien.gv.at/open-data-expo-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 5, 18, 0, 0),
                to = LocalDateTime.of(2025, 3, 5, 19, 30, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.4984142,
                    lon = 13.3810486
                ),
                title = "Künstliche Intelligenz und Urheberrecht",
                link = "https://www.wikimedia.de/veranstaltungen/monsters-of-law-kuenstliche-intelligenz-und-urheberrecht/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 6, 14, 0, 0),
                to = LocalDateTime.of(2025, 3, 6, 16, 0, 0),
                location = Location(online = true),
                title = "Einführung in die digitale Erschließung",
                link = "https://www.digis-berlin.de/einfuehrung-in-die-digitale-erschliessung-am-6-3/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 6, 18, 0, 0),
                to = LocalDateTime.of(2025, 3, 6, 21, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 6, 17, 30, 0),
                to = LocalDateTime.of(2025, 3, 6, 21, 0, 0),
                location = Location(
                    name = "Weizenbaum-Institut",
                    street = "Hardenbergstr.",
                    houseNumber = "32",
                    zipCode = "10623",
                    city = "Berlin",
                    lon = 13.3292227,
                    lat = 52.5081411
                ),
                title = "Bits & Bäume Policy Lab - Digitale Souveränität für eine wehrhafte Demokratie",
                link = "https://www.weizenbaum-institut.de/news/detail/bits-baeume-policy-lab-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 6, 19, 31, 0),
                to = LocalDateTime.of(2025, 3, 6, 20, 30, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://bbb.tu-dresden.de/rooms/qje-7si-xu1-lul/join"
                ),
                title = "Bits & Bäume Community Vernetzungstreffen",
                link = "https://discourse.bits-und-baeume.org/t/2025-03-06-online-community-treffen-19-31-uhr/1602"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 7, 19, 0, 0),
                to = LocalDateTime.of(2025, 3, 9, 21, 0, 0),
                location = Location(
                    name = "Jugendzentrum Nord (JuNo)",
                    street = "Lintforter Straße",
                    houseNumber = "132",
                    zipCode = "47445",
                    city = "Moers",
                    online = false,
                    lat = 51.489544949999996,
                    lon = 6.607030129498574,
                ),
                title = "Community-Hackday: Starke Frauen, Brot und Rosen",
                link = "https://osmcal.org/event/3436/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 10, 19, 0, 0),
                to = LocalDateTime.of(2025, 3, 10, 22, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin/events/306493396/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 11, 9, 0, 0),
                to = LocalDateTime.of(2025, 3, 13, 16, 0, 0),
                location = Location(
                    name = "Charlemagne Building",
                    street = "Rue de la Loi - Wetstraat",
                    houseNumber = "170",
                    zipCode = "1040",
                    city = "Brüssel",
                    lon = 4.3802985,
                    lat = 50.8433926,
                ),
                title = "New Techniques and Technologies for official Statistics (NTTS)",
                link = "https://cros.ec.europa.eu/ntts2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 11, 17, 0, 0),
                to = LocalDateTime.of(2025, 3, 11, 19, 0, 0),
                location = Location(
                    name = "SCADS.AI Dresden/Leipzig",
                    street = "Humboldtstr.",
                    houseNumber = "25",
                    zipCode = "04105",
                    city = "Leipzig",
                    online = false,
                    lat = 51.3466504,
                    lon = 12.3740462,
                ),
                title = "Interactive Meetup: AI Insights and Workshop on local LLMs",
                link = "https://scads.ai/event/meetup/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 11, 18, 0, 0),
                to = LocalDateTime.of(2025, 3, 11, 19, 30, 0),
                location = Location(online = true),
                title = "ChatGPT: Wie veränderst du die Wissenschaft?",
                link = "https://www.weizenbaum-institut.de/veranstaltungen/detailseite/chatgpt-wie-veraenderst-du-die-wissenschaft/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 12, 17, 30, 0),
                to = LocalDateTime.of(2025, 3, 12, 19, 0, 0),
                location = Location(online = true),
                title = "Künstliche Intelligenz im freiwilligen sozialen Engagement",
                link = "https://www.charta28.de/kommunikationsorte/ki-in-der-sozialwirtschaft"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 13, 11, 0, 0),
                to = LocalDateTime.of(2025, 3, 13, 16, 30, 0),
                location = Location(
                    name = "Deutscher Sparkassen- und Giroverband",
                    street = " Charlottenstraße",
                    houseNumber = "47",
                    zipCode = "10117",
                    city = "Berlin",
                    lat = 52.5160623,
                    lon = 13.3906284
                ),
                title = "#D21talk – Digitale Gesellschaft 2030: Gerechtigkeit, Teilhabe und Verantwortung",
                link = "https://initiatived21.de/veranstaltungen/d21talk-digitale-gesellschaft-2030-gerechtigkeit-teilhabe-und-verantwortung"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 13, 11, 30, 0),
                to = LocalDateTime.of(2025, 3, 13, 12, 30, 0),
                location = Location(online = true),
                title = "CorrelCompact: KI-Kickstart - Grundlagen und Chancen für Non-Profits",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-ki-25-1"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 13, 16, 0, 0),
                to = LocalDateTime.of(2025, 3, 13, 17, 0, 0),
                location = Location(online = true),
                title = "Civic Coding-Forum: Ethischer KI-Einsatz - Erfolgsmodelle aus der Praxis",
                link = "https://forum-ethischer-ki-einsatz.anmeldung-events.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 14, 17, 0, 0),
                to = LocalDateTime.of(2025, 3, 15, 20, 0, 0),
                location = Location(
                    name = "House of Living Labs, FZI | Forschungszentrum Informatik Karlsruhe",
                    street = "Haid-und-Neu-Straße",
                    houseNumber = "10-14",
                    zipCode = "76131",
                    city = "Karlsruhe",
                    lat = 49.012037649999996,
                    lon = 8.424695475549498
                ),
                title = "Open Data HackDays 2025 in Karlsruhe",
                link = "https://karlsruhe.digital/2025/02/open-data-hackdays-karlsruhe/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 14, 17, 0, 0),
                to = LocalDateTime.of(2025, 3, 15, 18, 0, 0),
                location = Location(
                    name = "Bertelsmann Stiftung Berlin",
                    street = "Werderscher Markt",
                    houseNumber = "6",
                    zipCode = "10117",
                    city = "Berlin",
                    lat = 52.5158087,
                    lon = 13.3979948
                ),
                title = "Datendialog",
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/data-science/projektnachrichten/agenda-datendialog"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 19, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 19, 13, 0, 0),
                location = Location(
                    name = "KI-Servicezentrum Berlin-Brandenburg am HPI, Raum: HE.52",
                    street = "Prof.-Dr.-Helmert-Straße",
                    houseNumber = "2-3",
                    zipCode = "14482",
                    city = "Potsdam",
                    lat = 52.3933919,
                    lon = 13.13156397679563
                ),
                title = "KI-Workshop Lokales Retrieval-Augmented-Generation-System: Effizientes Abfragen von Informationen aus Dokumenten",
                link = "https://www.eventbrite.de/e/effizientes-abfragen-von-informationen-aus-dokumenten-tickets-1219326160369"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 20, 11, 0, 0),
                to = LocalDateTime.of(2025, 3, 20, 12, 0, 0),
                location = Location(online = true),
                title = "openCode Connect März 2025",
                link = "https://opencode.de/de/aktuelles/opencode-connect-am-27-02-2520"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 21, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 22, 17, 0, 0),
                location = Location(
                    name = "Landwirtschaftliches Zentrum Liebegg",
                    street = "Liebegg",
                    houseNumber = "1",
                    zipCode = "5722",
                    city = "Gränichen (Aargau, Schweiz)",
                    lat = 47.3397455,
                    lon = 8.119092826447094
                ),
                title = "Open Farming Hackdays",
                link = "https://www.farming-hackdays.ch"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 21, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 22, 17, 0, 0),
                location = Location(
                    name = "Aula Schulhaus Bleicherain",
                    street = "Angelrainstrasse",
                    houseNumber = "19",
                    zipCode = "5600",
                    city = "Lenzburg (Aargau, Schweiz)",
                    lat = 47.3867937,
                    lon = 8.17474378762245
                ),
                title = "Smart Regio Lab Lenzburg Seetal 2025",
                link = "https://opendata.ch/de/events/smart-regio-lab-lenzburg-seetal-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 22, 9, 0, 0),
                to = LocalDateTime.of(2025, 3, 23, 18, 0, 0),
                location = Location(
                    name = "Technische Universität Chemnitz",
                    street = "Straße der Nationen",
                    houseNumber = "62",
                    zipCode = "09111",
                    city = "Chemnitz",
                    lat = 50.813566,
                    lon = 12.929715
                ),
                title = "Chemnitzer Linux-Tage",
                link = "https://chemnitzer.linux-tage.de/2025/de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 25, 11, 30, 0),
                to = LocalDateTime.of(2025, 3, 25, 12, 30, 0),
                location = Location(online = true),
                title = "CorrelCompact: Mission Datenqualität - vom Rohmaterial zum Datengold",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-datenqualitaet-25-1"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 25, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 25, 20, 0, 0),
                location = Location(
                    name = "Weizenbaum-Institut",
                    street = "Hardenbergstr.",
                    houseNumber = "32",
                    zipCode = "10623",
                    city = "Berlin",
                    lon = 13.3292227,
                    lat = 52.5081411
                ),
                title = "Yes, we are open! Künstliche Intelligenz verantwortungsbewusst gestalten",
                link = "https://www.weizenbaum-institut.de/veranstaltungen/detailseite/yes-we-are-open-kuenstliche-intelligenz-verantwortungsbewusst-gestalten/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 26, 9, 0, 0),
                to = LocalDateTime.of(2025, 3, 26, 15, 0, 0),
                location = Location(
                    name = "Katholische Hochschule für Sozialwesen Berlin",
                    street = "Köpenicker Allee",
                    houseNumber = "39-57",
                    zipCode = "10318",
                    city = "Berlin",
                    lat = 51.4176519,
                    lon = 9.6535408
                ),
                title = "Fachtagung: Soziale Dienstleistungen im digitalen Zeitalter: KI als Werkzeug zur Transformation",
                link = "https://www.khsb-berlin.de/de/node/990254"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 26, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 29, 16, 45, 0),
                location = Location(
                    name = "Schloss Münster",
                    street = "Schlossplatz",
                    houseNumber = "2",
                    zipCode = "48149",
                    city = "Münster",
                    lat = 51.96360715,
                    lon = 7.613135742994505
                ),
                title = "FOSSGIS",
                link = "https://fossgis-konferenz.de/2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 27, 11, 0, 0),
                to = LocalDateTime.of(2025, 3, 27, 12, 0, 0),
                location = Location(online = true),
                title = "openCode Community Call",
                link = "https://opencode.de/de/aktuelles/events/community-call-2-2524"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 28, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 29, 17, 0, 0),
                location = Location(
                    name = "Colosseum Kino",
                    street = "Gleimstraße",
                    houseNumber = "31",
                    zipCode = "10437",
                    city = "Berlin",
                    lat = 52.5477163,
                    lon = 13.4121477
                ),
                title = "transform_D Summit 2025",
                link = "https://www.deutsche-stiftung-engagement-und-ehrenamt.de/d-s-e-e-de-summit/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 29, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 29, 23, 59, 59),
                location = Location(name = "diverse"),
                title = "Tag des offenen Hackspace",
                link = "https://events.ccc.de/2025/02/28/tag-des-offenen-hackspace-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 29, 11, 0, 0),
                to = LocalDateTime.of(2025, 3, 29, 21, 45, 0),
                location = Location(
                    name = "Festsaal Kreuzberg",
                    street = "Am Flutgraben",
                    houseNumber = "2",
                    zipCode = "12435",
                    city = "Berlin",
                    lat = 52.49682315,
                    lon = 13.451555564938815
                ),
                title = "Acht Jahre Prototype Fund",
                link = "https://prototypefund.de/acht-jahre/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 29, 19, 30, 0),
                to = LocalDateTime.of(2025, 3, 29, 21, 0, 0),
                location = Location(online = true),
                title = "Verkehrswende-Meetup",
                link = "https://wiki.openstreetmap.org/wiki/Verkehrswende-Meetup#Meetups"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 3, 8, 0, 0),
                to = LocalDateTime.of(2025, 4, 3, 14, 0, 0),
                location = Location(online = true),
                title = "Girls'Day Online Events",
                link = "https://www.girls-day.de/Radar?lat=0&lon=0&locality=&keywords=programmieren&sortBy=&sortDesc=false&page=1&pageSize=30&radius=20&gkz=&initiativeId=0&categoryIds=&minimumFreePlaces=1&maximumFreePlaces=-1&minimumParticipantsAge=0&onlySubscribable=false&onlyAccessible=false&onlyEmbedded=false&onlyVirtual=true&onlyNonvirtual=false&organizerId=0&freePlaces=mindestens+1&locationType=digital+%2F+vom+Rechner+aus&warmStart=true&for=event"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 3, 8, 0, 0),
                to = LocalDateTime.of(2025, 4, 3, 12, 30, 0),
                location = Location(
                    name = "Freie Universität Berlin - Zuse-Institut Berlin (ZIB)",
                    street = "Takustraße",
                    houseNumber = "9",
                    zipCode = "14195",
                    city = "Berlin",
                    lat = 52.4559809,
                    lon = 13.297162523268483
                ),
                title = "Girls'Day am ZIB",
                link = "https://www.zib.de/event/girlsday-am-zib-sei-dabei"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 3, 10, 0, 0),
                to = LocalDateTime.of(2025, 4, 3, 18, 30, 0),
                location = Location(
                    name = "Technologiestiftung Berlin - CityLAB",
                    street = "Platz der Luftbrücke",
                    houseNumber = "4",
                    zipCode = "12101",
                    city = "Berlin",
                    lat = 52.4838796,
                    lon = 13.3885778
                ),
                title = "Girls Day 2025 im CityLAB – Zukunftsjobs mit Daten!",
                link = "https://citylab-berlin.org/de/events/zukunftstag-2025-im-citylab-tauche-ein-in-die-welt-der-daten/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 4, 9, 30, 0),
                to = LocalDateTime.of(2025, 4, 5, 18, 30, 0),
                location = Location(
                    name = "Rathaus Moers",
                    street = "Rathausplatz",
                    houseNumber = "1",
                    zipCode = "47441",
                    city = "Moers",
                    lat = 51.4532487,
                    lon = 6.626686
                ),
                title = "10 Jahre Hackday Moers",
                link = "https://www.moers.de/veranstaltungen/hackday-2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 4, 18, 0, 0),
                to = LocalDateTime.of(2025, 4, 6, 13, 0, 0),
                location = Location(
                    name = "temporärhaus",
                    street = "Augsburger Straße",
                    houseNumber = "23–25",
                    zipCode = "89231",
                    city = "Neu-Ulm",
                    lat = 52.49682315,
                    lon = 13.451555564938815
                ),
                title = "FemNetzCon 2025",
                link = "https://de.wikipedia.org/wiki/Wikipedia:WikiProjekt_FemNetz/FemNetzCon_2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 7, 14, 0, 0),
                to = LocalDateTime.of(2025, 4, 7, 15, 0, 0),
                location = Location(online = true),
                title = "CorrelCompact: Data Storytelling - Daten sprechen lassen!",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-storytelling-25-1"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 8, 9, 0, 0),
                to = LocalDateTime.of(2025, 4, 8, 12, 0, 0),
                location = Location(online = true),
                title = "AI Impact - KI und Nachhaltigkeit",
                link = "https://www.erwachsenenbildung-ekhn.de/programm/kw/bereich/kursdetails/kurs/2025-D-033/kursname/Online%20AI%20Impact%20-%20KI%20und%20Nachhaltigkeit/kategorie-id/5/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 8, 11, 0, 0),
                to = LocalDateTime.of(2025, 4, 8, 12, 0, 0),
                location = Location(online = true),
                title = "Ope Parl Data: Public Meeting #4",
                link = "https://opendata.ch/de/events/open-parl-data-public-meeting-4/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 9, 0, 0),
                to = LocalDateTime.of(2025, 4, 9, 16, 0, 0),
                location = Location(
                    name = "Kaisersaal",
                    street = "Futterstraße",
                    houseNumber = "15/16",
                    zipCode = "99084",
                    city = "Erfurt",
                    lat = 50.97936970000001,
                    lon = 11.033602232239485,
                ),
                title = "4. Open Data Barcamp 2025",
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/4-open-data-barcamp-2025-einladungsseite"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 17, 30, 0),
                to = LocalDateTime.of(2025, 4, 9, 20, 30, 0),
                location = Location(
                    name = "KI-Ideenwerkstatt für Umweltschutz",
                    street = "Rollbergstr.",
                    houseNumber = "28A",
                    zipCode = "12053",
                    city = "Berlin",
                    lat = 52.4790412,
                    lon = 13.4319106,
                ),
                title = "Rethink Recycling: KI in der Praxis",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/themenabend-rethink-recycling-ki-in-der-praxis/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 9, 9, 30, 0),
                to = LocalDateTime.of(2025, 4, 9, 11, 0, 0),
                location = Location(online = true),
                title = "KI in Pflege und Betreuung am Beispiel von CareMates",
                link = "https://www.charta28.de/kommunikationsorte/ki-in-der-sozialwirtschaft"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 4, 10, 9, 30, 0),
                to = LocalDateTime.of(2025, 4, 10, 18, 30, 0),
                location = Location(
                    name = "Weizenbaum-Institut",
                    street = "Hardenbergstr.",
                    houseNumber = "32",
                    zipCode = "10623",
                    city = "Berlin",
                    lon = 13.3292227,
                    lat = 52.5081411
                ),
                title = "Who Owns Free Knowledge? Examining Power, Platformization, and the Promise of the Commons",
                link = "https://www.weizenbaum-institut.de/conference-who-owns-free-knowledge/"
            )
        )
        return events
    }

    private fun createFebruary2025Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 1, 9, 0, 0),
                to = LocalDateTime.of(2025, 2, 2, 17, 0, 0),
                location = Location(
                    name = "Université libre de Bruxelles (ULB) Solbosch Campus",
                    street = "Avenue Franklin Roosevelt",
                    houseNumber = "50",
                    zipCode = "1050",
                    city = "Brüssel",
                    online = true,
                    onlineLink = "https://live.fosdem.org/",
                    lon = 4.436951259577029,
                    lat = 50.468529174564935
                ),
                title = "Free and Open Source Developers European Meeting (FOSDEM 2025)",
                link = "https://fosdem.org/2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 4, 9, 0, 0),
                to = LocalDateTime.of(2025, 2, 4, 11, 0, 0),
                location = Location(online = true),
                title = "Annotierte Daten im Umweltbereich – Lessons Learned aus dem Projekt LabelledGreenData4All",
                link = "https://www.igd.fraunhofer.de/de/veranstaltungen/labelledgreendata4all-community-building-event.html"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 4, 20, 0, 0),
                to = LocalDateTime.of(2025, 2, 4, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true
                ),
                title = "143. Netzpolitischer Abend",
                link = "https://digitalegesellschaft.de/2025/01/143-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 4, 10, 0, 0),
                to = LocalDateTime.of(2025, 2, 4, 14, 0, 0),
                location = Location(
                    name = "Einstein Center Digital Future (ECDF)",
                    street = "Wilhelmstraße",
                    houseNumber = "67",
                    zipCode = "10117",
                    city = "Berlin",
                    online = false,
                    lat = 52.5183956,
                    lon = 13.3806021
                ),
                title = "Ist die deutsche Wissenschaftslandschaft ein starker Motor für Open Science?",
                link = "https://www.ibi.hu-berlin.de/de/forschung/infomanagement/events/open-science-als-handlungsfeld-fuer-wissenschaftliche-einrichtungen"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 6, 18, 0, 0),
                to = LocalDateTime.of(2025, 2, 6, 21, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 6, 18, 0, 0),
                to = LocalDateTime.of(2025, 2, 6, 21, 0, 0),
                location = Location(
                    name = "Bitwäscherei",
                    street = "Neue Hard",
                    houseNumber = "12",
                    zipCode = "8005",
                    city = "Zürich (Schweiz)",
                    lon = 8.5204024,
                    lat = 47.3870316,
                ),
                title = "Jugendlab",
                link = "https://www.digitale-gesellschaft.ch/jugend-hackt-und-jugendlab-in-der-schweiz/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 6, 19, 31, 0),
                to = LocalDateTime.of(2025, 2, 6, 20, 30, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://bbb.tu-dresden.de/rooms/qje-7si-xu1-lul/join"
                ),
                title = "Bits & Bäume Community Vernetzungstreffen",
                link = "https://discourse.bits-und-baeume.org/tag/vernetzungstreffen"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 10, 9, 0, 0),
                to = LocalDateTime.of(2025, 2, 14, 15, 0, 0),
                location = Location(
                    online = true,
                ),
                title = "forschungsdaten.info Love Data Week 2025",
                link = "https://forschungsdaten.info/fdm-im-deutschsprachigen-raum/love-data-week-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 10, 19, 0, 0),
                to = LocalDateTime.of(2025, 2, 10, 22, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin/events/306008661/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 11, 14, 0, 0),
                to = LocalDateTime.of(2025, 2, 11, 15, 30, 0),
                location = Location(online = true),
                title = "Offenheit reicht nicht aus – Auf dem Weg zu einer lebendigen Datenkultur",
                link = "https://blogs.fu-berlin.de/open-access-berlin/2024/12/16/quo-vadis-4-offene-datenkultur/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 11, 10, 0, 0),
                to = LocalDateTime.of(2025, 2, 11, 12, 0, 0),
                location = Location(online = true),
                title = "17. Open Data Netzwerktreffen",
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/das-kommunale-open-data-netzwerktreffen"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 13, 11, 0, 0),
                to = LocalDateTime.of(2025, 2, 13, 12, 0, 0),
                location = Location(online = true),
                title = "Barrierefrei unterwegs: Wo stehen wir bei der Umsetzung des BehiG im Open Data-Bereich und welche Fortschritte gibt es bei Aufzugstörungen?",
                link = "https://barrierefrei-open-data-aufzugsdaten.event.sbb.ch"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 13, 18, 0, 0),
                to = LocalDateTime.of(2025, 2, 13, 21, 0, 0),
                location = Location(
                    name = "Weizenbaum-Institut",
                    street = "Hardenbergstr.",
                    houseNumber = "32",
                    zipCode = "10623",
                    city = "Berlin",
                    lon = 13.3292227,
                    lat = 52.5081411
                ),
                title = "Watching You – Die Welt von Palantir und Alex Karp",
                link = "https://www.weizenbaum-institut.de/veranstaltungen/detailseite/screening-watching-you-die-welt-von-palantir-und-alex-karp/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 13, 19, 0, 0),
                to = LocalDateTime.of(2025, 2, 13, 22, 0, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.4984142,
                    lon = 13.3810486
                ),
                title = "tech from below - 10. Treffen",
                link = "https://techfrombelow.de/2025-02-13/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 14, 18, 0, 0),
                to = LocalDateTime.of(2025, 2, 16, 21, 0, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false,
                    lat = 52.4984142,
                    lon = 13.3810486
                ),
                title = "Berlinale Edit-a-thon 2025",
                link = "https://www.wikimedia.de/veranstaltungen/berlinale-edit-a-thon-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 14, 19, 0, 0),
                to = LocalDateTime.of(2025, 2, 16, 21, 0, 0),
                location = Location(
                    name = "Jugendzentrum Nord (JuNo)",
                    street = "Lintforter Straße",
                    houseNumber = "132",
                    zipCode = "47445",
                    city = "Moers",
                    online = false,
                    lat = 51.489544949999996,
                    lon = 6.607030129498574,
                ),
                title = "I Love Free Software Day Community-Hackday",
                link = "https://osmcal.org/event/3312/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 14, 18, 0, 0),
                to = LocalDateTime.of(2025, 2, 16, 21, 0, 0),
                location = Location(
                    name = "Geofabrik",
                    street = "Amalienstraße",
                    houseNumber = "44",
                    zipCode = "76133",
                    city = "Karlsruhe",
                    online = false,
                    lat = 49.009606,
                    lon = 8.3902072,
                ),
                title = "Karlsruhe Hack Weekend February 2025",
                link = "https://wiki.openstreetmap.org/wiki/Karlsruhe_Hack_Weekend_February_2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 18, 18, 0, 0),
                to = LocalDateTime.of(2025, 2, 27, 19, 10, 0),
                location = Location(online = true),
                title = "Data Reuse Days 2025",
                link = "https://www.wikidata.org/wiki/Event:Data_Reuse_Days_2025"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 19, 20, 0, 0),
                to = LocalDateTime.of(2025, 2, 19, 21, 30, 0),
                location = Location(online = true),
                title = "Open Transport Meetup: Mobility Database and Canonical GTFS Schedule Validator",
                link = "https://github.com/transportkollektiv/meetup/wiki#next-otm-wednesday-2025-02-19-8pm-cet-isabelle-de-robert-mobility-database-and-canonical-gtfs-schedule-validator-jonah-br%C3%BCchert-and-felix-g%C3%BCndling-transitous"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 20, 12, 0, 0),
                to = LocalDateTime.of(2025, 2, 20, 12, 45, 0),
                location = Location(online = true),
                title = "Espresso Talk: Aufbruch ins (Un)Bekannte: reale Mehrwerte durch strukturierte Daten schaffen - am Beispiel FörderFunke",
                link = "https://community.civic-data.de/s/willkommens-space/calendar/entry/view?id=273"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 21, 10, 0, 0),
                to = LocalDateTime.of(2025, 2, 22, 16, 0, 0),
                location = Location(
                    name = "OpenSource Science B.V.",
                    street = "Etnastraat",
                    houseNumber = "20",
                    zipCode = "4814AA",
                    city = "Breda (Niederlande)",
                    online = true,
                    lat = 51.5926406,
                    lon = 4.7693296,
                ),
                title = "Foss FEST 2025: International Hackathon",
                link = "https://os-sci.com/event/foss-fest-2025-international-hackathon-14/register"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 25, 17, 30, 0),
                to = LocalDateTime.of(2025, 2, 25, 20, 30, 0),
                location = Location(
                    name = "KI-Ideenwerkstatt für Umweltschutz",
                    street = "Rollbergstr.",
                    houseNumber = "28A",
                    zipCode = "12053",
                    city = "Berlin",
                    lat = 51.5926406,
                    lon = 4.7693296,
                ),
                title = "Unterwegs in die Kreislaufgesellschaft: Wie kann KI Bürger*innen dabei helfen, Stoffkreisläufe zu schließen?",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/themenabend-unterwegs-in-die-kreislaufgesellschaft/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 25, 19, 30, 0),
                to = LocalDateTime.of(2025, 2, 25, 21, 0, 0),
                location = Location(online = true),
                title = "Verkehrswende-Meetup",
                link = "https://wiki.openstreetmap.org/wiki/Verkehrswende-Meetup#Meetups"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 27, 11, 0, 0),
                to = LocalDateTime.of(2025, 2, 27, 12, 0, 0),
                location = Location(online = true),
                title = "openCode Connect Februar 2025",
                link = "https://opencode.de/de/aktuelles/events/opencode-connect-februar-2025-2298"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 27, 14, 0, 0),
                to = LocalDateTime.of(2025, 2, 27, 15, 0, 0),
                location = Location(online = true),
                title = "CorrelCompact: So lügt man mit Statistik",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-statistik-25-1?viewType=list"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 28, 19, 0, 0),
                to = LocalDateTime.of(2025, 3, 1, 21, 0, 0),
                location = Location(
                    name = "Casinotheater Winterthur",
                    street = "Stadthausstrasse",
                    houseNumber = "119",
                    zipCode = "8400",
                    city = "Winterthur (Schweiz)",
                    lat = 47.49970345,
                    lon = 8.726622163758583,
                ),
                title = "Winterkongress der Digitalen Gesellschaft Schweiz",
                link = "https://winterkongress.ch/2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 28, 19, 0, 0),
                to = LocalDateTime.of(2025, 3, 1, 21, 0, 0),
                location = Location(
                    name = "WIR-Haus",
                    street = "Wilhelmstraße",
                    houseNumber = "189",
                    zipCode = "42489",
                    city = "Wülfrath",
                    lon = 4.436951259577029,
                    lat = 50.468529174564935
                ),
                title = "Hack im Pott",
                link = "https://hackimpott.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 1, 0, 0, 0),
                to = LocalDateTime.of(2025, 3, 8, 23, 59, 59),
                location = Location(online = true),
                title = "Open Data Day 2025",
                link = "https://opendataday.org/de/events/2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 1, 10, 0, 0),
                to = LocalDateTime.of(2025, 3, 8, 21, 0, 0),
                location = Location(
                    name = "Aktivitetshuset",
                    street = "Norderstraße",
                    houseNumber = "49",
                    zipCode = "24939",
                    city = "Flensburg",
                    lon = 9.431249601609665,
                    lat = 54.791591785985844
                ),
                title = "Open Data Day Flensburg 2025",
                link = "https://opendataday-flensburg.de"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 5, 9, 0, 0),
                to = LocalDateTime.of(2025, 3, 5, 15, 0, 0),
                location = Location(
                    name = "FH Technikum Wien",
                    street = "Höchstädtplatz",
                    houseNumber = "6",
                    zipCode = "1200",
                    city = "Wien",
                    lon = 16.3774409,
                    lat = 48.2391664
                ),
                title = "Open Data Expo 2025",
                link = "https://digitales.wien.gv.at/open-data-expo-2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 3, 6, 14, 0, 0),
                to = LocalDateTime.of(2025, 3, 6, 16, 0, 0),
                location = Location(online = true),
                title = "Einführung in die digitale Erschließung“",
                link = "https://www.digis-berlin.de/einfuehrung-in-die-digitale-erschliessung-am-6-3/"
            )
        )
        return events
    }

    private fun createJanuary2025Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 13, 19, 0, 0),
                to = LocalDateTime.of(2025, 1, 13, 22, 0, 0),
                location = Location(
                    name = "WikiBär Wikipedia",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin/events/304875890/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 16, 9, 0, 0),
                to = LocalDateTime.of(2025, 1, 16, 18, 0, 0),
                location = Location(
                    name = "Wilhelm von Humboldt-Saal im Haus Unter den Linden der Staatsbibliothek zu Berlin",
                    street = "Unter den Linden",
                    houseNumber = "8",
                    zipCode = "10117",
                    city = "Berlin",
                    online = true,
                    lon = 13.391620476395673,
                    lat = 52.51753889200077
                ),
                title = "Initiative News-Infographics-Analytics-Maps (NIAM 2025)",
                link = "https://news-infographics-analytics-maps.media"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 22, 9, 0, 0),
                to = LocalDateTime.of(2025, 1, 22, 17, 0, 0),
                location = Location(online = true),
                title = "Staat in die Zukunft",
                link = "https://staat-in-die-zukunft.de/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 23, 9, 0, 0),
                to = LocalDateTime.of(2025, 1, 23, 12, 0, 0),
                location = Location(online = true),
                title = "Klimaatlas BW – Release Veranstaltung",
                link = "https://www.fortbildung-klimawandel.de/klimaatlas/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 23, 10, 0, 0),
                to = LocalDateTime.of(2025, 1, 23, 11, 30, 0),
                location = Location(online = true),
                title = "Indikatoren nachhaltiger urbaner Mobilität",
                link = "https://difu.de/veranstaltungen/2025-01-23/indikatoren-nachhaltiger-urbaner-mobilitaet"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 27, 11, 0, 0),
                to = LocalDateTime.of(2025, 1, 27, 15, 0, 0),
                location = Location(
                    name = "Ständehaus Merseburg",
                    street = "Oberaltenburg",
                    houseNumber = "2",
                    zipCode = "06217",
                    city = "Merseburg",
                    lon = 11.999473754588223,
                    lat = 51.35999505476602
                ),
                title = "Feierlicher Launch des Portals umwelt.info",
                link = "https://www.umweltbundesamt.de/service/termine/feierlicher-launch-des-portals-umweltinfo"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 28, 11, 0, 0),
                to = LocalDateTime.of(2025, 1, 28, 12, 0, 0),
                location = Location(online = true),
                title = "Open Parl Data: Public Meeting #3",
                link = "https://opendata.ch/events/open-parl-data-public-meeting-3/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 28, 19, 30, 0),
                to = LocalDateTime.of(2025, 1, 28, 21, 0, 0),
                location = Location(
                    online = true,
                    onlineLink = "https://osmvideo.cloud68.co/user/chr-g7r-xz3"
                ),
                title = "OSM-Verkehrswende #65: Kacper Goliński talks about veloplanner.com",
                link = "https://wiki.openstreetmap.org/wiki/Verkehrswende-Meetup/Meetup_2025-01-28"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 31, 9, 0, 0),
                to = LocalDateTime.of(2025, 1, 31, 18, 0, 0),
                location = Location(online = true),
                title = "EU Open Source Policy Summit 2025",
                link = "https://summit.openforumeurope.org"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 1, 9, 0, 0),
                to = LocalDateTime.of(2025, 2, 2, 17, 0, 0),
                location = Location(
                    name = "Université libre de Bruxelles (ULB) Solbosch Campus",
                    street = "Avenue Franklin Roosevelt",
                    houseNumber = "50",
                    zipCode = "1050",
                    city = "Brüssel",
                    online = true,
                    onlineLink = "https://live.fosdem.org/",
                    lon = 4.436951259577029,
                    lat = 50.468529174564935
                ),
                title = "Free and Open Source Developers European Meeting (FOSDEM 2025)",
                link = "https://fosdem.org/2025/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 11, 14, 0, 0),
                to = LocalDateTime.of(2025, 2, 11, 15, 30, 0),
                location = Location(online = true),
                title = "Offenheit reicht nicht aus – Auf dem Weg zu einer lebendigen Datenkultur",
                link = "https://blogs.fu-berlin.de/open-access-berlin/2024/12/16/quo-vadis-4-offene-datenkultur/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 2, 11, 10, 0, 0),
                to = LocalDateTime.of(2025, 2, 11, 12, 0, 0),
                location = Location(online = true),
                title = "17. Open Data Netzwerktreffen",
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/das-kommunale-open-data-netzwerktreffen"
            )
        )
        return events
    }

    private fun createDecember2024Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 3, 14, 0, 0),
                to = LocalDateTime.of(2024, 12, 3, 14, 45, 0),
                location = Location(online = true),
                title = "CorrelCompact: Diskriminierung durch Daten und Algorithmen",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-datenethik"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 3, 20, 0, 0),
                to = LocalDateTime.of(2024, 12, 3, 22, 0, 0),
                location = Location(
                    name = "c-base",
                    street = "Rungestraße",
                    houseNumber = "20",
                    zipCode = "10179",
                    city = "Berlin",
                    online = true
                ),
                title = "142. Netzpolitischer Abend",
                link = "https://digitalegesellschaft.de/2024/11/142-netzpolitischer-abend/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 4, 14, 15, 0),
                to = LocalDateTime.of(2024, 12, 4, 16, 15, 0),
                location = Location(
                    name = "Paul-Löbe-Haus, Sitzungssaal E.600",
                    street = "Konrad-Adenauer-Straße",
                    houseNumber = "1",
                    zipCode = "10557",
                    city = "Berlin",
                    online = true
                ),
                title = "Anhörung zum Thema „Open Source“",
                link = "https://www.bundestag.de/ausschuesse/a23_digitales/Anhoerungen/1024966-1024966"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 5, 9, 0, 0),
                to = LocalDateTime.of(2024, 12, 5, 18, 0, 0),
                location = Location(
                    name = "WikiMUC",
                    street = "Angertorstraße",
                    houseNumber = "3",
                    zipCode = "80469",
                    city = "München",
                    online = true
                ),
                title = "Federated Queries Workshop",
                link = "https://de.wikipedia.org/wiki/Wikipedia:WikiMUC/Federated_Queries_Workshop/Agenda"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 9, 19, 0, 0),
                to = LocalDateTime.of(2024, 12, 9, 20, 0, 0),
                location = Location(
                    name = "Wikimedia Deutschland e. V.",
                    street = "Tempelhofer Ufer",
                    houseNumber = "23-24",
                    zipCode = "10963",
                    city = "Berlin",
                    online = false
                ),
                title = "Monsters of Law: Rechtsgeschichte der Wikipedia",
                link = "https://www.wikimedia.de/veranstaltungen/rechtsgeschichte-der-wikipedia/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 10, 15, 0, 0),
                to = LocalDateTime.of(2024, 12, 10, 17, 0, 0),
                location = Location(online = true),
                title = "Wikidata for legal historians",
                link = "https://www.lhlt.mpg.de/events/40121/2078412"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 12, 9, 30, 0),
                to = LocalDateTime.of(2024, 12, 12, 17, 0, 0),
                location = Location(
                    name = "Universitätsclub Bonn",
                    street = "Konviktstraße",
                    houseNumber = "9",
                    zipCode = "53113",
                    city = "Bonn",
                    online = true
                ),
                title = "Where2B - Die Open-Source-GIS-Konferenz",
                link = "https://where2b-conference.com"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 16, 17, 0, 0),
                to = LocalDateTime.of(2024, 12, 16, 18, 0, 0),
                location = Location(online = true),
                title = "Prototype Fund Fragestunde",
                link = "https://mastodon.social/@PrototypeFund/113635049516024292"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 27, 10, 30, 0),
                to = LocalDateTime.of(2024, 12, 30, 18, 30, 0),
                location = Location(
                    name = "Congress Center Hamburg (CCH)",
                    street = "Congressplatz",
                    houseNumber = "1",
                    zipCode = "20355",
                    city = "Hamburg",
                    online = true
                ),
                title = "38C3",
                link = "https://fahrplan.events.ccc.de/congress/2024/fahrplan/schedule/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2025, 1, 16, 9, 0, 0),
                to = LocalDateTime.of(2025, 1, 16, 18, 0, 0),
                location = Location(
                    name = "Wilhelm von Humboldt-Saal im Haus Unter den Linden der Staatsbibliothek zu Berlin",
                    street = "Unter den Linden",
                    houseNumber = "8",
                    zipCode = "10117",
                    city = "Berlin",
                    online = true
                ),
                title = "Initiative News-Infographics-Analytics-Maps (NIAM 2025)",
                link = "https://news-infographics-analytics-maps.media"
            )
        )
        return events
    }

    private fun createNovember2024Events(): MutableList<Event> {
        val events = mutableListOf<Event>()
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 1, 10, 0, 0),
                to = LocalDateTime.of(2024, 11, 3, 20, 0, 0),
                location = Location(
                    name = "Jugendzentrum Nord",
                    street = "Lintforter Straße",
                    houseNumber = "132",
                    city = "Moers",
                    zipCode = "47445",
                    lat = 51.4894376,
                    lon = 6.6069422,
                ),
                title = "Community Hackday",
                link = "https://www.codeforniederrhein.de/2024/10/09/community-hackday-am-1-3-november-im-juno/"
            )
        )

        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 4, 10, 30, 0),
                to = LocalDateTime.of(2024, 11, 6, 13, 30, 0),
                location = Location(
                    online = true,
                    name = "Wiener Stadt- und Landesarchiv, Gasometer D",
                    street = "Guglgasse",
                    houseNumber = "14",
                    zipCode = "1110",
                    city = "Wien"
                ),
                title = "MediaWiki Users and Developers Conference 2024",
                link = "https://conference.knowledge.wiki/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 5, 9, 30, 0),
                to = LocalDateTime.of(2024, 11, 5, 17, 30, 0),
                title = "Reusing data: Bridging the distance between data consumers and producers",
                location = Location(
                    name = "Academy Building, Raum Telders",
                    street = "Rapenburg", houseNumber = "73",
                    zipCode = "2311 GJ", city = "Leiden"
                ),
                link = "https://www.universiteitleiden.nl/en/events/2024/11/reusing-data-bridging-the-distance-between-data-consumers-and-producers"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 5, 14, 0, 0),
                to = LocalDateTime.of(2024, 11, 5, 14, 45, 0),
                location = Location(online = true),
                title = "CorrelCompact: Mission Datenqualität - vom Rohmaterial zum Datengold",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-datenqualit%C3%A4t"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 5, 15, 0, 0),
                to = LocalDateTime.of(2024, 11, 5, 17, 0, 0),
                location = Location(online = true),
                title = "Civic Data Lab - Mit Daten überzeugen: Wirkung erfolgreich kommunizieren",
                link = "https://www.correlaid.org/veranstaltungen/cdl-wirkung-kommunizieren"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 6, 15, 0, 0),
                to = LocalDateTime.of(2024, 11, 6, 18, 0, 0),
                location = Location(online = true),
                title = "Civic Data Lab - Gemeinsam Machen 3: Infos und Beratung fürs Ankommen in Deutschland: Faktenbasiert, Vernetzt, Lösungsorientiert",
                link = "https://www.correlaid.org/veranstaltungen/cdl-gemeinsam-machen-3"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 7, 9, 0, 0),
                to = LocalDateTime.of(2024, 11, 7, 17, 30, 0),
                location = Location(
                    name = "Zukunft – Umwelt – Gesellschaft (ZUG) gGmbH",
                    street = "Stresemannstraße",
                    houseNumber = "69-71",
                    zipCode = "10963",
                    city = "Berlin"
                ),
                title = "AI Conference: Mit Künstlicher Intelligenz zu mehr Nachhaltigkeit?",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/ai-conference-mit-kuenstlicher-intelligenz-zu-mehr-nachhaltigkeit/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 7, 18, 0, 0),
                to = LocalDateTime.of(2024, 11, 7, 21, 0, 0),
                location = Location(
                    name = "WikiBär",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin"
                ),
                title = "Jugend editiert",
                link = "https://www.wikimedia.de/veranstaltungen/jugend-editiert/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 8, 10, 0, 0),
                to = LocalDateTime.of(2024, 11, 8, 17, 0, 0),
                location = Location(
                    name = "Moritzbastei",
                    street = "Kurt-Masur-Platz",
                    houseNumber = "1",
                    zipCode = "04109",
                    city = "Leipzig"
                ),
                title = "Public Service Lab Day 2024",
                link = "https://www.eventbrite.de/e/public-service-lab-day-2024-tickets-941917569807"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 8, 18, 0, 0),
                to = LocalDateTime.of(2024, 11, 9, 22, 45, 0),
                location = Location(
                    name = "Audimax der Uni Lübeck",
                    street = "Mönkhofer Weg",
                    houseNumber = "245",
                    zipCode = "23562",
                    city = "Lübeck"
                ),
                title = "Nights of Open Knowledge (NooK)",
                link = "https://events.ccc.de/2024/07/17/nook/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 11, 19, 0, 0),
                to = LocalDateTime.of(2024, 11, 11, 22, 0, 0),
                location = Location(
                    name = "WikiBär",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin"
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin/events/304012491/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 11, 19, 0, 0),
                to = LocalDateTime.of(2024, 11, 11, 21, 0, 0),
                location = Location(
                    name = "Impact Hub",
                    street = "Kaiserstr",
                    houseNumber = "97",
                    zipCode = "76131",
                    city = "Karlsruhe"
                ),
                title = "Code for Karlsruhe",
                link = "https://ok-lab-karlsruhe.de/mitmachen/#schedule"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 12, 12, 0, 0),
                to = LocalDateTime.of(2024, 11, 12, 12, 45, 0),
                location = Location(online = true),
                title = "Civic Data Lab - Espresso Talk: Your Emotional City",
                link = "https://www.correlaid.org/veranstaltungen/cdl-espressotalk-staedte"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 12, 14, 0, 0),
                to = LocalDateTime.of(2024, 11, 12, 21, 0, 0),
                location = Location(
                    name = "KINDL - Zentrum für zeitgenössische Kunst GmbH",
                    street = "Am Sudhaus",
                    houseNumber = "3",
                    zipCode = "12053",
                    city = "Berlin"
                ),
                title = "Festival der KI-Ideenwerkstatt für Umweltschutz",
                link = "https://www.ki-ideenwerkstatt.de/veranstaltungen/festival-der-ki-ideenwerkstatt-fuer-umweltschutz/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 12, 10, 0, 0),
                to = LocalDateTime.of(2024, 11, 12, 11, 30, 0),
                title = "Open-Data-Netzwerktreffen",
                location = Location(online = true),
                registration = Registration(
                    required = true,
                    link = "https://www.bertelsmann-stiftung.de/de/open-data-netzwerk"
                ),
                link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/das-kommunale-open-data-netzwerktreffen#c222695"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 13, 10, 0, 0),
                to = LocalDateTime.of(2024, 11, 13, 12, 30, 0),
                location = Location(online = true),
                title = "Aarhus Digital: Public Participation Portals in Environmental Matters",
                link = "https://www.ufu.de/aarhus-digital-webinar/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 13, 10, 30, 0),
                to = LocalDateTime.of(2024, 11, 13, 17, 30, 0),
                location = Location(
                    name = "Unperfekthaus Essen",
                    street = "Friedrich-Ebert-Straße",
                    houseNumber = "18-20",
                    zipCode = "45127",
                    city = "Essen"
                ),
                registration = Registration(
                    required = true,
                    to = LocalDateTime.of(2024, 11, 4, 0, 0, 0),
                    link = "https://civicrm.eine-welt-netz-nrw.de/civicrm/event/register?id=6&reset=1"
                ),
                title = "Bits und Bäume NRW Vernetzungstreffen",
                link = "https://nrw.bits-und-baeume.org"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 15, 15, 0, 0),
                to = LocalDateTime.of(2024, 11, 17, 16, 30, 0),
                location = Location(
                    name = "Amerikahaus",
                    street = "Karolinenplatz",
                    houseNumber = "3",
                    zipCode = "80333",
                    city = "München"
                ),
                title = "Jugend hackt",
                link = "https://jugendhackt.org/events/muenchen/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 16, 0, 0, 0),
                to = LocalDateTime.of(2024, 11, 17, 0, 0, 0),
                location = Location(city = "Hannover"),
                registration = Registration(
                    required = true,
                    link = "https://cloud.okfn.de/apps/forms/s/iq4fbzdL7CW39aS8HBjA7Xam"
                ),
                title = "Umweltdatenwerkstatt",
                link = "https://datenschule.de/workshops/umweltdatenwerkstatt/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 19, 11, 30, 0),
                to = LocalDateTime.of(2024, 11, 19, 12, 15, 0),
                location = Location(online = true),
                title = "CorrelCompact: Data Storytelling - Daten sprechen lassen!",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-datastorytelling"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 21, 19, 0, 0),
                to = LocalDateTime.of(2024, 11, 21, 22, 0, 0),
                location = Location(
                    name = "Karl der Grosse",
                    street = "Kirchgasse",
                    houseNumber = "14",
                    zipCode = "8001",
                    city = "Zürich"
                ),
                title = "Netzpolitischer Abend zu \"Datenethik in der Schweiz\"",
                link = "https://www.digitale-gesellschaft.ch/event/netzpolitischer-abend-zu-datenethik-in-der-schweiz/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 23, 9, 0, 0),
                to = LocalDateTime.of(2024, 11, 23, 16, 30, 0),
                registration = Registration(
                    required = true,
                    link = "https://oknrw.de/veranstaltungen/anmeldung/"
                ),
                location = Location(
                    name = "Bergische Volkshochschule",
                    street = "Auer Schulstraße",
                    houseNumber = "20",
                    zipCode = "42103",
                    city = "Wuppertal"
                ),
                title = "OKNRW–Barcamp 2024",
                link = "https://oknrw.de/veranstaltungen/offene-kommunen-nrw-2024/"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 11, 25, 17, 0, 0),
                to = LocalDateTime.of(2024, 11, 25, 21, 0, 0),
                location = Location(
                    name = "Anwendungslabor für Künstliche Intelligenz und Big Data am Umweltbundesamt",
                    street = "Alte Messe",
                    houseNumber = "6",
                    zipCode = "04103",
                    city = "Leipzig"
                ),
                title = "Workshop umwelt.info, Teil 1",
                link = "https://mvp.umwelt.info/sites/default/files/2024-10/Einladung_Workshop_umwelt.info_Leipzig.pdf"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 2, 17, 0, 0),
                to = LocalDateTime.of(2024, 12, 2, 21, 0, 0),
                location = Location(
                    name = "Anwendungslabor für Künstliche Intelligenz und Big Data am Umweltbundesamt",
                    street = "Alte Messe",
                    houseNumber = "6",
                    zipCode = "04103",
                    city = "Leipzig"
                ),
                title = "Workshop umwelt.info, Teil 2",
                link = "https://mvp.umwelt.info/sites/default/files/2024-10/Einladung_Workshop_umwelt.info_Leipzig.pdf"
            )
        )
        events.add(
            Event(
                from = LocalDateTime.of(2024, 12, 3, 14, 0, 0),
                to = LocalDateTime.of(2024, 12, 3, 14, 45, 0),
                location = Location(online = true),
                title = "CorrelCompact: Diskriminierung durch Daten und Algorithmen",
                link = "https://www.correlaid.org/veranstaltungen/correlcompact-datenethik"
            )
        )
        return events
    }

    fun generateEventLink(event: Event) =
        "* ${dateStr(event)}, ${locationStr(event.location)}: **${event.title}** ${calLink(event)}\n  * ${event.link}"

    private fun dateStr(event: Event): String {
        val weekDayFrom = event.from.dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.GERMAN)
        val sameDay = !event.to.toLocalDate().isAfter(event.from.toLocalDate())
        val dayFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
        val dayFromStr = "$weekDayFrom, ${dayFormat.format(event.from)}"
        val timeFromStr = if (event.from.hour != 0) ", ${timeFormat.format(event.from)}" else ""
        val firstDayStr = dayFromStr + timeFromStr
        val secondDayStr = if (sameDay) {
            if (event.from.hour != 0 && event.to.hour != 0) {
                "-${timeFormat.format(event.to)}"
            } else ""
        } else {
            val weekDayTo = event.to.dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.GERMAN)
            val dayToStr = "$weekDayTo, ${dayFormat.format(event.to)}"
            val timeToStr = if (event.to.hour != 0) ", ${timeFormat.format(event.to)}" else ""
            ", bis $dayToStr$timeToStr"
        }
        return firstDayStr + secondDayStr
    }

    private fun locationStr(location: Location): String {
        return if (location.online && location.city == null) {
            "online"
        } else {
            if (location.city != null) {
                val cityStr = "${location.zipCode?.let { "$it " } ?: ""}${location.city}"
                val streetStr = "${location.street ?: ""}${location.houseNumber?.let { " $it" } ?: ""}"
                val addressStr = "${if (streetStr.isNotEmpty()) streetStr.let { "$it, " } else ""}$cityStr"
                "${location.name?.let { "$it, " } ?: ""}$addressStr${if (location.online) " und auch online" else ""}"
            } else ""
        }
    }

    private fun calLink(event: Event): String {
        val vevent = createCalendarComponent(event)
        return calLink(event.title, vevent)
    }

    private fun calLink(title: String, vevent: CalendarComponent): String {
        val calendar = Calendar()
            .withDefaults()
            .withProdId("-//${title}//iCal4j 1.0//EN")
            .withComponent(vevent)
            .fluentTarget
        val base64 = Base64.getEncoder().encodeToString(calendar.toString().toByteArray())
        return "<a title='Kalendereintrag ${title}' download='event.ics' href=\"data:text/calendar;base64,${base64}\">&#x1F4C5;</a>"
    }

    private fun createCalendarComponent(event: Event): CalendarComponent {
        val location = net.fortuna.ical4j.model.property.Location(locationStr(event.location))
        val link = net.fortuna.ical4j.model.property.Link(event.link)
        val description = net.fortuna.ical4j.model.property.Description(event.comments.joinToString("; "))
        var vevent = VEvent(event.from, event.to, event.title)
            .withProperty(link)
            .withProperty(description)
            .withProperty(location)
        if (event.location.lat != null && event.location.lon != null) {
            val geo = net.fortuna.ical4j.model.property.Geo(
                BigDecimal.valueOf(event.location.lat), BigDecimal.valueOf(event.location.lon))
            vevent = vevent.withProperty(geo)
        }
        return vevent.fluentTarget as CalendarComponent
    }

    private fun recurrentEventsMap(): Map<String, Event> {
        val events = mutableMapOf<String, Event>()
        events.put("Berlin",
            Event(
                from = LocalDateTime.of(2025, 7, 21, 19, 0, 0),
                to = LocalDateTime.of(2025, 7, 21, 22, 0, 0),
                location = Location(
                    name = "WikiBär",
                    street = "Köpenicker Straße",
                    houseNumber = "45",
                    zipCode = "10179",
                    city = "Berlin"
                ),
                title = "Code for Berlin",
                link = "https://www.meetup.com/ok-lab-berlin"
            )
        )
        events.put("Bielefeld",
            Event(
                from = LocalDateTime.of(2025, 7, 3, 18, 30, 0),
                to = LocalDateTime.of(2025, 7, 3, 21, 0, 0),
                location = Location(
                    name = "Innovation Office",
                    street = "Alter Markt",
                    houseNumber = "13",
                    zipCode = "33602",
                    city = "Bielefeld",
                    lat = 52.020834315056845,
                    lon = 8.532432121531627
                ),
                title = "Code for Bielefeld",
                link = "https://codefor.de/bielefeld/"
            )
        )
        events.put("Flensburg",
            Event(
                from = LocalDateTime.of(2025, 7, 2, 18, 0, 0),
                to = LocalDateTime.of(2025, 7, 2, 21, 0, 0),
                location = Location(
                    name = "Aktivitetshuset",
                    street = "Norderstraße",
                    houseNumber = "49",
                    zipCode = "24939",
                    city = "Flensburg",
                    lon = 9.431249601609665,
                    lat = 54.791591785985844
                ),
                title = "OKLab Flensburg",
                link = "https://codefor.de/flensburg/"
            )
        )
        events.put("Karlsruhe",
            Event(
                from = LocalDateTime.of(2025, 7, 21, 19, 0, 0),
                to = LocalDateTime.of(2025, 7, 21, 21, 0, 0),
                location = Location(
                    name = "Impact Hub",
                    street = "Kaiserstr",
                    houseNumber = "97",
                    zipCode = "76131",
                    city = "Karlsruhe"
                ),
                title = "Code for Karlsruhe",
                link = "https://ok-lab-karlsruhe.de/mitmachen/#schedule"
            )
        )
        events.put("Köln",
            Event(
                from = LocalDateTime.of(2025, 7, 7, 19, 0, 0),
                to = LocalDateTime.of(2025, 7, 7, 22, 0, 0),
                location = Location(
                    name = "Wikipedia Lokal K",
                    street = "Hackländerstr",
                    houseNumber = "2",
                    zipCode = "50825",
                    city = "Köln",
                    lon = 6.910391,
                    lat = 50.9556011
                ),
                title = "Code for Cologne",
                link = "https://www.meetup.com/de-DE/codeforcologne/"
            )
        )
        events.put("Leipzig",
            Event(
                from = LocalDateTime.of(2025, 7, 2, 19, 0, 0),
                to = LocalDateTime.of(2025, 7, 2, 22, 0, 0),
                location = Location(
                    name = "Basislager Coworking Leipzig",
                    street = "Peterssteinweg",
                    houseNumber = "14",
                    zipCode = "04107",
                    city = "Leipzig",
                    lon = 12.3735399,
                    lat = 51.3320744
                ),
                title = "OKLab Leipzig",
                link = "https://www.meetup.com/de-DE/oklab-leipzig/"
            )
        )
        events.put("Münster",
            Event(
                from = LocalDateTime.of(2025, 7, 1, 19, 30, 0),
                to = LocalDateTime.of(2025, 7, 1, 22, 0, 0),
                location = Location(
                    name = "Cafe SpecOps network",
                    street = "Aegidiimarkt",
                    houseNumber = "5",
                    zipCode = "48155",
                    city = "Münster",
                    lon = 7.6224732,
                    lat = 51.9607162
                ),
                title = "Code for Münster",
                link = "https://www.meetup.com/de-DE/code-for-munster/"
            )
        )
        events.put("Niederrhein",
            Event(
                from = LocalDateTime.of(2025, 7, 1, 20, 0, 0),
                to = LocalDateTime.of(2025, 7, 1, 22, 0, 0),
                location = Location(online = true),
                title = "Code for Niederrhein",
                link = "https://www.codeforniederrhein.de/termine/"
            )
        )
        events.put("temporärhaus",
            Event(
                from = LocalDateTime.of(2025, 7, 16, 19, 30, 0),
                to = LocalDateTime.of(2025, 7, 16, 22, 0, 0),
                location = Location(
                    name = "temporärhaus",
                    street = "Augsburgerstr",
                    houseNumber = "23-25",
                    zipCode = "89231",
                    city = "Neu-Ulm",
                    lon = 13.439250348721544,
                    lat = 52.50267706293607
                ),
                title = "temporärhaus: Open Data Monday",
                link = "https://temporaerhaus.de/termine-und-oeffnungszeiten/"
            )
        )
        return events
    }


    private fun recurrentEvents(): List<CalendarComponent> {
        val recurrentEventsMap = recurrentEventsMap()
        val events = mutableListOf<CalendarComponent>()
        val everyFirstMonday =
            RRule(Recur<LocalDateTime>("FREQ=MONTHLY;INTERVAL=1;BYDAY=MO;UNTIL=20251231"))
        val everyTuesday = RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=1;BYDAY=TU;UNTIL=20251231"))
        val everyWednesday = RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=1;BYDAY=WE;UNTIL=20251231"))
        val everyThursday = RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=1;BYDAY=TH;UNTIL=20251231"))
        val secondAndForthTuesday = RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=2;BYDAY=TU;UNTIL=20251231"))
        val thirdMonday =
            RRule(Recur<LocalDateTime>("FREQ=MONTHLY;INTERVAL=1;BYDAY=MO;UNTIL=20251231"))
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Berlin")!!
            ).withProperty(thirdMonday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Bielefeld")!!
            ).withProperty(everyThursday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Flensburg")!!
            ).withProperty(everyWednesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Karlsruhe")!!
            ).withProperty(thirdMonday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Köln")!!
            ).withProperty(everyFirstMonday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Leipzig")!!
            ).withProperty(everyWednesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Münster")!!
            ).withProperty(everyTuesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("Niederrhein")!!
            ).withProperty(everyTuesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                recurrentEventsMap.get("temporärhaus")!!
            ).withProperty(secondAndForthTuesday).fluentTarget as CalendarComponent
        )
        return events
    }

    fun expected() = """
* Freitag, 01.11.2024, 10:00, bis Sonntag, 03.11.2024, 20:00, Jugendzentrum Nord, Lintforter Straße 132, 47445 Moers: **Community Hackday**
  * https://www.codeforniederrhein.de/2024/10/09/community-hackday-am-1-3-november-im-juno/
* Montag, 04.11.2024, 10:30, bis Mittwoch, 06.11.2024, 13:30, Wiener Stadt- und Landesarchiv, Gasometer D, Guglgasse 14, 1110 Wien und auch online: **MediaWiki Users and Developers Conference 2024**
  * https://conference.knowledge.wiki/
* Dienstag, 05.11.2024, 09:30-17:30, Academy Building, Raum Telders, Rapenburg 73, 2311 GJ Leiden: **Reusing data: Bridging the distance between data consumers and producers**
  * https://www.universiteitleiden.nl/en/events/2024/11/reusing-data-bridging-the-distance-between-data-consumers-and-producers
* Dienstag, 05.11.2024, 14:00-14:45, online: **CorrelCompact: Mission Datenqualität - vom Rohmaterial zum Datengold**
  * https://www.correlaid.org/veranstaltungen/correlcompact-datenqualit%C3%A4t
* Dienstag, 05.11.2024, 15:00-17:00, online: **Civic Data Lab - Mit Daten überzeugen: Wirkung erfolgreich kommunizieren**
  * https://www.correlaid.org/veranstaltungen/cdl-wirkung-kommunizieren
* Mittwoch, 06.11.2024, 15:00-18:00, online: **Civic Data Lab - Gemeinsam Machen 3: Infos und Beratung fürs Ankommen in Deutschland: Faktenbasiert, Vernetzt, Lösungsorientiert**
  * https://www.correlaid.org/veranstaltungen/cdl-gemeinsam-machen-3
* Donnerstag, 07.11.2024, 09:00-17:30, Zukunft – Umwelt – Gesellschaft (ZUG) gGmbH, Stresemannstraße 69-71, 10963 Berlin: **AI Conference: Mit Künstlicher Intelligenz zu mehr Nachhaltigkeit?**
  * https://www.ki-ideenwerkstatt.de/veranstaltungen/ai-conference-mit-kuenstlicher-intelligenz-zu-mehr-nachhaltigkeit/
* Donnerstag, 07.11.2024, 18:00-21:00, WikiBär, Köpenicker Straße 45, 10179 Berlin: **Jugend editiert**
  * https://www.wikimedia.de/veranstaltungen/jugend-editiert/
* Freitag, 08.11.2024, 10:00-17:00, Moritzbastei, Kurt-Masur-Platz 1, 04109 Leipzig: **Public Service Lab Day 2024**
  * https://www.eventbrite.de/e/public-service-lab-day-2024-tickets-941917569807
* Freitag, 08.11.2024, 18:00, bis Samstag, 09.11.2024, 22:45, Audimax der Uni Lübeck, Mönkhofer Weg 245, 23562 Lübeck: **Nights of Open Knowledge (NooK)**
  * https://events.ccc.de/2024/07/17/nook/
* Montag, 11.11.2024, 19:00-22:00, WikiBär, Köpenicker Straße 45, 10179 Berlin: **Code for Berlin**
  * https://www.meetup.com/ok-lab-berlin/events/304012491/
* Montag, 11.11.2024, 19:00-21:00, Impact Hub, Kaiserstr 97, 76131 Karlsruhe: **Code for Karlsruhe**
  * https://ok-lab-karlsruhe.de/mitmachen/#schedule
* Dienstag, 12.11.2024, 12:00-12:45, online: **Civic Data Lab - Espresso Talk: Your Emotional City**
  * https://www.correlaid.org/veranstaltungen/cdl-espressotalk-staedte
* Dienstag, 12.11.2024, 14:00-21:00, KINDL - Zentrum für zeitgenössische Kunst GmbH, Am Sudhaus 3, 12053 Berlin: **Festival der KI-Ideenwerkstatt für Umweltschutz**
  * https://www.ki-ideenwerkstatt.de/veranstaltungen/festival-der-ki-ideenwerkstatt-fuer-umweltschutz/
* Dienstag, 12.11.2024, 10:00-11:30, online: **Open-Data-Netzwerktreffen**
  * https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/das-kommunale-open-data-netzwerktreffen#c222695
* Mittwoch, 13.11.2024, 10:00-12:30, online: **Aarhus Digital: Public Participation Portals in Environmental Matters**
  * https://www.ufu.de/aarhus-digital-webinar/
* Mittwoch, 13.11.2024, 10:30-17:30, Unperfekthaus Essen, Friedrich-Ebert-Straße 18-20, 45127 Essen: **Bits und Bäume NRW Vernetzungstreffen**
  * https://nrw.bits-und-baeume.org
* Freitag, 15.11.2024, 15:00, bis Sonntag, 17.11.2024, 16:30, Amerikahaus, Karolinenplatz 3, 80333 München: **Jugend hackt**
  * https://jugendhackt.org/events/muenchen/
* Samstag, 16.11.2024, bis Sonntag, 17.11.2024, Hannover: **Umweltdatenwerkstatt**
  * https://datenschule.de/workshops/umweltdatenwerkstatt/
* Dienstag, 19.11.2024, 11:30-12:15, online: **CorrelCompact: Data Storytelling - Daten sprechen lassen!**
  * https://www.correlaid.org/veranstaltungen/correlcompact-datastorytelling
* Donnerstag, 21.11.2024, 19:00-22:00, Karl der Grosse, Kirchgasse 14, 8001 Zürich: **Netzpolitischer Abend zu "Datenethik in der Schweiz"**
  * https://www.digitale-gesellschaft.ch/event/netzpolitischer-abend-zu-datenethik-in-der-schweiz/
* Samstag, 23.11.2024, 09:00-16:30, Bergische Volkshochschule, Auer Schulstraße 20, 42103 Wuppertal: **OKNRW–Barcamp 2024**
  * https://oknrw.de/veranstaltungen/offene-kommunen-nrw-2024/
* Montag, 25.11.2024, 17:00-21:00, Anwendungslabor für Künstliche Intelligenz und Big Data am Umweltbundesamt, Alte Messe 6, 04103 Leipzig: **Workshop umwelt.info, Teil 1**
  * https://mvp.umwelt.info/sites/default/files/2024-10/Einladung_Workshop_umwelt.info_Leipzig.pdf
* Montag, 02.12.2024, 17:00-21:00, Anwendungslabor für Künstliche Intelligenz und Big Data am Umweltbundesamt, Alte Messe 6, 04103 Leipzig: **Workshop umwelt.info, Teil 2**
  * https://mvp.umwelt.info/sites/default/files/2024-10/Einladung_Workshop_umwelt.info_Leipzig.pdf
* Dienstag, 03.12.2024, 14:00-14:45, online: **CorrelCompact: Diskriminierung durch Daten und Algorithmen**
  * https://www.correlaid.org/veranstaltungen/correlcompact-datenethik
    """.trimIndent()

    @Test
    @Disabled
    fun testGenerateCalendarLinksFromIcs() {
        val path = "C:\\Users\\Joerg\\Downloads\\icals.ics"
        val outpath = "C:\\Users\\Joerg\\Desktop\\icals.html"
        val links = mutableListOf<String>()
        FileInputStream(path).use { fis ->
            val cal = CalendarBuilder().build(fis)

            for (event in cal.getComponents<VEvent>()) {
                val calendar = Calendar()
                    .withDefaults()
                    .withProdId("-//Out in the Open September 2024//iCal4j 1.0//EN")
                calendar.withComponent(event)
                val base64 = Base64.getEncoder().encodeToString(calendar.toString().toByteArray())
                links.add("<a title='Kalendereintrag ${event.summary.value}' download='event.ics' href=\"data:text/calendar;base64,${base64}\">&#x1F4C5;</a>")
            }
        }
        FileWriter(outpath).use { out ->
            out.write(
                """
                <html>
                    <body>
                        ${links.joinToString("\n")}
                    </body>
                </html>
            """.trimIndent()
            )
        }
    }
}