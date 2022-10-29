package de.l.joergreichert.outintheopen.web

import de.l.joergreichert.outintheopen.twitter.TwitterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class TwitterController @Autowired constructor(val twitterService: TwitterService) {

    @PostMapping("auth")
    fun auth(): String  = twitterService.triggerAuth()

    @PostMapping("callback")
    fun callback(@RequestParam("code") code: String) {
        twitterService.storeAccessToken(code)
    }

    @GetMapping("likes")
    fun getLikes(@RequestParam(required = false) accessToken: String? = null) = twitterService.listLikes(accessToken)

    @GetMapping("followers")
    fun getFollowers(@RequestParam(required = false) accessToken: String? = null)  = twitterService.listFollowers(accessToken)

    @GetMapping("following")
    fun getFollowing(@RequestParam(required = false) accessToken: String? = null) = twitterService.listFollowing(accessToken)
}