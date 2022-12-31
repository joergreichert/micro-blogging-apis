package de.l.joergreichert.outintheopen.web

import de.l.joergreichert.outintheopen.mastodon.MastodonService
import de.l.joergreichert.outintheopen.twitter.TwitterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mastodon")
class MastodonController @Autowired constructor(val mastodonService: MastodonService) {

    @PostMapping("appAccessToken")
    fun appAccessToken(): String  = mastodonService.getAppAccessToken()

    @GetMapping("likes")
    fun getLikes(@RequestParam(required = false) accessToken: String? = null,
                 @RequestParam(required = false) userId: String? = null,
                 @RequestParam(required = false) targetFile: String? = null) =
        mastodonService.listLikes(accessToken, userId, targetFile)

    @GetMapping("statuses")
    fun getStatuses(@RequestParam(required = false) accessToken: String? = null,
                 @RequestParam(required = false) userId: String? = null,
                 @RequestParam(required = false) targetFile: String? = null) =
        mastodonService.listStatuses(accessToken, userId, targetFile)

    @GetMapping("followers")
    fun getFollowers(@RequestParam(required = false) accessToken: String? = null,
                     @RequestParam(required = false) userId: String? = null,
                     @RequestParam(required = false) targetFile: String? = null)  =
        mastodonService.listFollowers(accessToken, userId, targetFile)

    @GetMapping("following")
    fun getFollowing(@RequestParam(required = false) accessToken: String? = null,
                     @RequestParam(required = false) userId: String? = null,
                     @RequestParam(required = false) targetFile: String? = null) =
        mastodonService.listFollowing(accessToken, userId, targetFile)
}