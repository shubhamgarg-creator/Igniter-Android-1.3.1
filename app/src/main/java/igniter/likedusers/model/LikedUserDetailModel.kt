package igniter.likedusers.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikedUserDetailModel{
    @SerializedName("user_id")
    @Expose
    var userId = 0
    @SerializedName("first_name")
    @Expose
    var userName = ""
    @SerializedName("user_age")
    @Expose
    var userAge = 0
    @SerializedName("college")
    @Expose
    var userEducation = ""
    @SerializedName("work")
    @Expose
    var userwork = ""
    @SerializedName("is_super_liked")
    @Expose
    var is_super_likes = false
    @SerializedName("location")
    @Expose
    var location = ""
    @SerializedName("user_image_url")
    @Expose
    var userImage = ""
}
