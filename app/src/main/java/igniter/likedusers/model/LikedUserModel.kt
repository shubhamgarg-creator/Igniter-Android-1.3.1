package igniter.likedusers.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikedUserModel {

    @SerializedName("status_code")
    @Expose
    var statusCode = ""

    @SerializedName("status_message")
    @Expose
    var statusMessage = ""

    @SerializedName("total_pages")
    @Expose
    var totalPages = 0

    @SerializedName("used_subscription_id")
    @Expose
    var usedSubscriptionId = 0

    @SerializedName("total_likes")
    @Expose
    var totalLikes = 0

    @SerializedName("data")
    @Expose
    var profilesLikedYou : ArrayList<LikedUserDetailModel>? = null

}
