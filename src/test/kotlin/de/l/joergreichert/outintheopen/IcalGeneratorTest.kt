package de.l.joergreichert.outintheopen

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.StreetAddress
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
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
    fun testGenerateIcsFromDataModel() {
        val events = mutableListOf<Event>()
        events.add(Event(
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
        ))

        events.add(Event(
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
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 5, 9, 30, 0),
            to = LocalDateTime.of(2024, 11, 5, 17, 30, 0),
            title = "Reusing data: Bridging the distance between data consumers and producers",
            location = Location(
                name = "Academy Building, Raum Telders",
                        street = "Rapenburg", houseNumber = "73",
                        zipCode = "2311 GJ", city = "Leiden"
            ),
            link = "https://www.universiteitleiden.nl/en/events/2024/11/reusing-data-bridging-the-distance-between-data-consumers-and-producers"
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 5, 14, 0, 0),
            to = LocalDateTime.of(2024, 11, 5, 14, 45, 0),
            location = Location(online = true),
            title = "CorrelCompact: Mission Datenqualität - vom Rohmaterial zum Datengold",
            link = "https://www.correlaid.org/veranstaltungen/correlcompact-datenqualit%C3%A4t"
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11,5, 15, 0, 0),
            to = LocalDateTime.of(2024, 11,5, 17, 0, 0),
            location = Location(online = true),
            title = "Civic Data Lab - Mit Daten überzeugen: Wirkung erfolgreich kommunizieren",
            link = "https://www.correlaid.org/veranstaltungen/cdl-wirkung-kommunizieren"
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 6, 15, 0, 0),
            to = LocalDateTime.of(2024, 11, 6, 18, 0, 0),
            location = Location(online = true),
            title = "Civic Data Lab - Gemeinsam Machen 3: Infos und Beratung fürs Ankommen in Deutschland: Faktenbasiert, Vernetzt, Lösungsorientiert",
            link = "https://www.correlaid.org/veranstaltungen/cdl-gemeinsam-machen-3"
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 12, 12, 0, 0),
            to = LocalDateTime.of(2024, 11, 12, 12, 45, 0),
            location = Location(online = true),
            title = "Civic Data Lab - Espresso Talk: Your Emotional City",
            link = "https://www.correlaid.org/veranstaltungen/cdl-espressotalk-staedte"
        ))
        events.add(Event(
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
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 12, 10, 0, 0),
            to = LocalDateTime.of(2024, 11, 12, 11, 30, 0),
            title = "Open-Data-Netzwerktreffen",
            location = Location(online = true),
            registration = Registration(
                required = true,
                link = "https://www.bertelsmann-stiftung.de/de/open-data-netzwerk"
            ),
            link = "https://www.bertelsmann-stiftung.de/de/unsere-projekte/daten-fuer-die-gesellschaft/projektnachrichten/das-kommunale-open-data-netzwerktreffen#c222695"
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 13, 10, 0, 0),
            to = LocalDateTime.of(2024, 11, 13, 12, 30, 0),
            location = Location(online = true),
            title = "Aarhus Digital: Public Participation Portals in Environmental Matters",
            link = "https://www.ufu.de/aarhus-digital-webinar/"
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 16, 0, 0, 0),
            to = LocalDateTime.of(2024, 11, 17, 0, 0, 0),
            location = Location(city = "Hannover"),
            registration = Registration(
                required = true,
                link = "https://cloud.okfn.de/apps/forms/s/iq4fbzdL7CW39aS8HBjA7Xam"
            ),
            title = "Umweltdatenwerkstatt",
            link = "https://datenschule.de/workshops/umweltdatenwerkstatt/"
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 11, 19, 11, 30, 0),
            to = LocalDateTime.of(2024, 11, 19, 12, 15, 0),
            location = Location(online = true),
            title = "CorrelCompact: Data Storytelling - Daten sprechen lassen!",
            link = "https://www.correlaid.org/veranstaltungen/correlcompact-datastorytelling"
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
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
        ))
        events.add(Event(
            from = LocalDateTime.of(2024, 12, 3, 14, 0,0),
            to = LocalDateTime.of(2024, 12, 3, 14, 45, 0),
            location = Location(online = true),
            title = "CorrelCompact: Diskriminierung durch Daten und Algorithmen",
            link = "https://www.correlaid.org/veranstaltungen/correlcompact-datenethik"
        ))
        val actual = events.joinToString("\n") { generateEventLink(it) }
        assertEquals(expected(), actual)
    }

    fun generateEventLink(event: Event) = "* ${dateStr(event)}, ${locationStr(event.location)}: **${event.title}**\n  * ${event.link}"

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
        val calendar = Calendar()
            .withDefaults()
            .withProdId("-//Out in the Open Oktober 2024//iCal4j 1.0//EN")
        val base64 = ""
        return "<a title='Kalendereintrag ${event.title}' download='event.ics' href=\"data:text/calendar;base64,${base64}\">&#x1F4C5;</a>"
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
                links.add("<a title='Kalendereintrag ${event.summary.get().value}' download='event.ics' href=\"data:text/calendar;base64,${base64}\">&#x1F4C5;</a>")
            }
        }
        FileWriter(outpath).use { out ->
            out.write("""
                <html>
                    <body>
                        ${links.joinToString("\n")}
                    </body>
                </html>
            """.trimIndent())
        }
    }
}