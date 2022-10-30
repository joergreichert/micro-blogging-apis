package de.l.joergreichert.outintheopen.web

import de.l.joergreichert.outintheopen.twitter.TwitterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class TwitterController @Autowired constructor(val twitterService: TwitterService) {

    @PostMapping("appAccessToken")
    fun appAccessToken(): String  = twitterService.getAppAccessToken()

    @GetMapping("likes")
    fun getLikes(@RequestParam(required = false) accessToken: String? = null,
                 @RequestParam(required = false) userId: String? = null,
                 @RequestParam(required = false) targetFile: String? = null) =
        twitterService.listLikes(accessToken, userId, targetFile)

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