package de.l.joergreichert.outintheopen.web

import de.l.joergreichert.outintheopen.bluesky.BlueskyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/bluesky")
class BlueskyController @Autowired constructor(val blueskyService: BlueskyService) {

    @PostMapping("appAccessToken")
    fun appAccessToken() = blueskyService.getAppAccessToken()

    @GetMapping("profile")
    fun getProfile(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
    ) =
        blueskyService.getProfile(accessToken, userId)

    @GetMapping("likes")
    fun getLikes(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) limit: Int? = 50,
        @RequestParam(required = false) cursor: String? = null,
        @RequestParam(required = false) targetFile: String? = null,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) since: LocalDate? = null
    ) =
        blueskyService.listLikes(accessToken, userId, targetFile, limit, cursor, since)

    @GetMapping("statuses")
    fun getStatuses(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) limit: Int? = 50,
        @RequestParam(required = false) cursor: String? = null,
        @RequestParam(required = false) targetFile: String? = null,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) since: LocalDate? = null
    ) =
        blueskyService.listStatuses(accessToken, userId, targetFile, limit, cursor, since)

    @GetMapping("followers")
    fun getFollowers(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) limit: Int? = 100,
        @RequestParam(required = false) cursor: String? = null,
        @RequestParam(required = false) targetFile: String? = null
    ) =
        blueskyService.listFollowers(accessToken, userId, limit, cursor, targetFile)

    @GetMapping("following")
    fun getFollowing(
        @RequestParam(required = false) accessToken: String? = null,
        @RequestParam(required = false) userId: String? = null,
        @RequestParam(required = false) limit: Int? = 100,
        @RequestParam(required = false) cursor: String? = null,
        @RequestParam(required = false) targetFile: String? = null
    ) =
        blueskyService.listFollowing(accessToken, userId, limit, cursor, targetFile)
}