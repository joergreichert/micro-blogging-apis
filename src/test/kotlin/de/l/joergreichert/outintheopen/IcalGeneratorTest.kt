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
    fun testGenerateNovember2024IcsFromDataModel() {
        val events = createNovember2024Events()
        val actual = events.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    @Test
    @Disabled
    fun testGenerateDecember2024IcsFromDataModel() {
        val events = createDecember2024Events()
        val actual = events.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    @Test
    @Disabled
    fun testGenerateJanuary2025IcsFromDataModel() {
        val events = createJanuary2025Events()
        val actual = events.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    @Test
    @Disabled
    fun testGenerateFebruary2025IcsFromDataModel() {
        val events = createFebruary2025Events()
        val actual = events.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    @Test
    //@Disabled
    fun testGenerateMarch2025IcsFromDataModel() {
        val events = createMarch2025Events()
        val actual = events.joinToString("\n") { generateEventLink(it) }
        FileWriter(System.getProperty("user.dir") + "/docs/ics/events.ics").use { out ->
            out.write(createIcsForMonth("Out In The Open Feburar 2025", events))
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
        val calendar = Calendar()
            .withDefaults()
            .withProdId("-//${event.title}//iCal4j 1.0//EN")
            .withComponent(vevent)
            .fluentTarget
        val base64 = Base64.getEncoder().encodeToString(calendar.toString().toByteArray())
        return "<a title='Kalendereintrag ${event.title}' download='event.ics' href=\"data:text/calendar;base64,${base64}\">&#x1F4C5;</a>"
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

    private fun recurrentEvents(): List<CalendarComponent> {
        val events = mutableListOf<CalendarComponent>()
        val everySecondMonday =
            RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=2;BYDAY=MO;UNTIL=20251231"))
        val everyTuesday = RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=1;BYDAY=TU;UNTIL=20251231"))
        val everyWednesday = RRule(Recur<LocalDateTime>("FREQ=WEEKLY;INTERVAL=1;BYDAY=WE;UNTIL=20251231"))
        events.add(
            createCalendarComponent(
                Event(
                    from = LocalDateTime.of(2025, 3, 5, 18, 0, 0),
                    to = LocalDateTime.of(2025, 3, 5, 21, 0, 0),
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
            ).withProperty(everyWednesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                Event(
                    from = LocalDateTime.of(2025, 3, 3, 19, 0, 0),
                    to = LocalDateTime.of(2025, 3, 3, 22, 0, 0),
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
            ).withProperty(everySecondMonday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                Event(
                    from = LocalDateTime.of(2025, 3, 5, 19, 0, 0),
                    to = LocalDateTime.of(2025, 3, 5, 22, 0, 0),
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
            ).withProperty(everyWednesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                Event(
                    from = LocalDateTime.of(2025, 3, 4, 19, 30, 0),
                    to = LocalDateTime.of(2025, 3, 4, 22, 0, 0),
                    location = Location(
                        name = "Café Drei:klang",
                        street = "Wolbeckerstr",
                        houseNumber = "36",
                        zipCode = "48155",
                        city = "Münster",
                        lon = 7.6398118,
                        lat = 51.9576369
                    ),
                    title = "Code for Münster",
                    link = "https://www.meetup.com/de-DE/code-for-munster/"
                )
            ).withProperty(everyTuesday).fluentTarget as CalendarComponent
        )
        events.add(
            createCalendarComponent(
                Event(
                    from = LocalDateTime.of(2025, 3, 4, 20, 0, 0),
                    to = LocalDateTime.of(2025, 3, 4, 22, 0, 0),
                    location = Location(online = true),
                    title = "Code for Niederrhein",
                    link = "https://www.codeforniederrhein.de/termine/"
                )
            ).withProperty(everyTuesday).fluentTarget as CalendarComponent
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