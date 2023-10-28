package de.l.joergreichert.outintheopen.web

import de.l.joergreichert.outintheopen.twitter.TwitterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RestController
@RequestMapping("/twitter")
class TwitterController @Autowired constructor(val twitterService: TwitterService) {

    @PostMapping("appAccessToken")
    fun appAccessToken(): String  = twitterService.getAppAccessToken()

    @GetMapping("likes")
    fun getLikes(@RequestParam(required = false) accessToken: String? = null,
                 @RequestParam(required = false) userId: String? = null,
                 @RequestParam(required = false) targetFile: String? = null,
                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) since: LocalDate? = null) =
        twitterService.listLikes(accessToken, userId, targetFile, LocalDateTime.of(since, LocalTime.MIDNIGHT))

    @GetMapping("bookmarks")
    fun getBookmarks(@RequestParam(required = false) accessToken: String? = null,
                 @RequestParam(required = false) userId: String? = null,
                 @RequestParam(required = false) targetFile: String? = null,
                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) since: LocalDate? = null) =
        twitterService.listBookmarks(accessToken, userId, targetFile, LocalDateTime.of(since, LocalTime.MIDNIGHT))

    @GetMapping("followers")
    fun getFollowers(@RequestParam(required = false) accessToken: String? = null,
                     @RequestParam(required = false) userId: String? = null,
                     @RequestParam(required = false) targetFile: String? = null)  =
        twitterService.listFollowers(accessToken, userId, targetFile)

    @GetMapping("following")
    fun getFollowing(@RequestParam(required = false) accessToken: String? = null,
                     @RequestParam(required = false) userId: String? = null,
                     @RequestParam(required = false) targetFile: String? = null) =
        twitterService.listFollowing(accessToken, userId, targetFile)
}