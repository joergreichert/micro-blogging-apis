package de.l.joergreichert.outintheopen.web

import de.l.joergreichert.outintheopen.mastodon.MastodonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/mastodon")
class MastodonController @Autowired constructor(val mastodonService: MastodonService) {

    @PostMapping("appAccessToken")
    fun appAccessToken(): String = mastodonService.getAppAccessToken()

    @GetMapping("codeLoginUrl")
    fun codeLoginUrl(): String = mastodonService.getCodeLoginUrl()

    @GetMapping("userAppAccessToken")
    fun userAppAccessTokenForCode(@RequestParam code: String): String =
        mastodonService.getUserAppAccessTokenForCode(code)

    @GetMapping("likes")
    fun getLikes(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) targetFile: String? = null,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) since: LocalDate? = null
    ) =
        mastodonService.listLikes(accessToken, userId, targetFile, since)

    @GetMapping("statuses")
    fun getStatuses(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) targetFile: String? = null,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) since: LocalDate? = null
    ) =
        mastodonService.listStatuses(accessToken, userId, targetFile, since)

    @GetMapping("followers")
    fun getFollowers(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) targetFile: String? = null
    ) =
        mastodonService.listFollowers(accessToken, userId, targetFile)

    @GetMapping("following")
    fun getFollowing(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) targetFile: String? = null
    ) =
        mastodonService.listFollowing(accessToken, userId, targetFile)
}