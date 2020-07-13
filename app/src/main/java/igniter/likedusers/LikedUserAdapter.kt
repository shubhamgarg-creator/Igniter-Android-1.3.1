package igniter.likedusers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import igniter.R
import igniter.configs.AppController
import igniter.configs.SessionManager
import igniter.interfaces.PaginationAdapterCallback
import igniter.likedusers.model.LikedUserDetailModel
import igniter.utils.ImageUtils
import igniter.views.profile.EnlargeProfileActivity
import javax.inject.Inject


class LikedUserAdapter(private val context: Context, private val mCallback: PaginationAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Inject
    lateinit var imageUtils: ImageUtils

    @Inject
    lateinit var sessionManager: SessionManager
    // View Types and Pagination variables
    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
    }

    init {
        AppController.getAppComponent().inject(this)

    }

    private var likedUserDetailModel: ArrayList<LikedUserDetailModel> = ArrayList()
    private var subscriptionId: Int = 0
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private var viewGroup: ViewGroup? = null

    var tvLikes: TextView? = null
    var tvNopes: TextView? = null
    var swipeDirection: String? = null
    var tempSwipeDirection: String = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.viewGroup=parent
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem: View = inflater.inflate(R.layout.single_swipe, parent, false)
                viewHolder = ProfilesLikedYouViewHolder(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingViewHolder(viewLoading)
            }
        }
        return viewHolder!!
    }


    override fun getItemCount(): Int {
        return likedUserDetailModel.size
    }

    fun setSubscriptionId(subsId : Int) {
        subscriptionId = subsId;
    }

    fun getLikedUser(): ArrayList<LikedUserDetailModel> {
        return likedUserDetailModel
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == likedUserDetailModel.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val likedUserDetailModel = likedUserDetailModel[position]
        when (getItemViewType(position)) {
            ITEM -> {
                val likedUserViewHolder = holder as ProfilesLikedYouViewHolder

                imageUtils.loadSliderImage(context, likedUserViewHolder.ivUserImage, likedUserDetailModel.userImage)
                if (!TextUtils.isEmpty(likedUserDetailModel.userName) && likedUserDetailModel.userAge > 0) {
                    likedUserViewHolder.tvUserNameAge.text = StringBuilder().append(likedUserDetailModel.userName).append(", ").append(likedUserDetailModel.userAge).toString()
                } else {
                    likedUserViewHolder.tvUserNameAge.text = StringBuilder().append(likedUserDetailModel.userName)
                }
                when {
                    likedUserDetailModel.userEducation.isNotEmpty() -> {
                        likedUserViewHolder.tvDesignation.text = likedUserDetailModel.userEducation
                    }
                    likedUserDetailModel.userwork.isNotEmpty() -> {
                        likedUserViewHolder.tvDesignation.text = likedUserDetailModel.userwork
                    }
                    else -> {
                        likedUserViewHolder.tvDesignation.visibility = View.GONE
                    }
                }

                if (likedUserDetailModel.is_super_likes){
                    likedUserViewHolder.superLike.visibility=View.VISIBLE
                }else{
                    likedUserViewHolder.superLike.visibility=View.GONE
                }
                likedUserViewHolder.infoImage.setOnClickListener {


                    println("subscription Id : "+subscriptionId)

                    sessionManager.isFromLikedPage = true

                    val intent = Intent(context, EnlargeProfileActivity::class.java)
                    //navType 1 is For other Profile , 0 For Own Profile
                    intent.putExtra("navType", 1)
                    intent.putExtra("subscriptionId",subscriptionId)
                    intent.putExtra("userId", likedUserDetailModel.userId.toInt())
                    context.startActivity(intent)
                    (context as Activity).overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out)
                }

                if(swipeDirection.equals("like", true) ){
                    likedUserViewHolder.tvLikes?.alpha = 1f
                    likedUserViewHolder.tvNopes?.alpha = 0f
                }else if(swipeDirection.equals("nope", true)){
                    likedUserViewHolder.tvLikes?.alpha = 0f
                    likedUserViewHolder.tvNopes?.alpha = 1f
                }else{
                    likedUserViewHolder.tvLikes?.alpha = 0f
                    likedUserViewHolder.tvNopes?.alpha = 0f
                }

                /*when {
                    swipeDirection.equals("like", true) -> {

                    }
                    swipeDirection.equals("nope", true) -> {

                    }


                }*/
            }

            LOADING -> {
                val loadingVH = holder as LoadingViewHolder

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.visibility = View.VISIBLE
                    loadingVH.mProgressBar.visibility = View.GONE

                    loadingVH.mErrorTxt.text = if (errorMsg != null)
                        errorMsg
                    else
                        context.getString(R.string.error_msg_unknown)

                } else {
                    loadingVH.mErrorLayout.visibility = View.GONE
                    loadingVH.mProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }



    /*
       Helpers - Pagination
   */

    fun add(likedUserDetail: LikedUserDetailModel) {
        likedUserDetailModel.add(likedUserDetail)
        notifyItemInserted(likedUserDetailModel.size - 1)
    }

    fun removeAll(){
        likedUserDetailModel.clear()
        notifyDataSetChanged()
    }

    fun addAll(likedUserDetailModel: ArrayList<LikedUserDetailModel>?) {
        for (likedUserDetail in likedUserDetailModel!!) {
            add(likedUserDetail)
        }
    }

    public fun remove(likedUserDetail: LikedUserDetailModel?) {
        val position = likedUserDetailModel.indexOf(likedUserDetail)
        if (position > -1) {
            likedUserDetailModel.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(LikedUserDetailModel())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = likedUserDetailModel.size - 1
        val tripStatusModel = getItem(position)

        if (tripStatusModel != null) {
            likedUserDetailModel.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): LikedUserDetailModel? {
        return likedUserDetailModel[position]
    }



    inner class ProfilesLikedYouViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var ivUserImage = view.findViewById<ImageView>(R.id.iv_user_image)
        var tvUserNameAge = view.findViewById<TextView>(R.id.tv_user_name_age)
        var tvDesignation = view.findViewById<TextView>(R.id.tv_designation)
        var superLike = view.findViewById<ImageView>(R.id.superlike)
        var infoImage = view.findViewById<ImageView>(R.id.infoimage)
        var tvLikes = view.findViewById<TextView>(R.id.like_tv)
        var tvNopes = view.findViewById<TextView>(R.id.nope_tv)

        init {

        }

    }

    /**
     * Loading ViewHolder
     */
    protected inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val mProgressBar: ProgressBar = itemView.findViewById(R.id.loadmore_progress)
        val mRetryBtn: ImageButton = itemView.findViewById(R.id.loadmore_retry)
        val mErrorTxt: TextView = itemView.findViewById(R.id.loadmore_errortxt)
        val mErrorLayout: LinearLayout = itemView.findViewById(R.id.loadmore_errorlayout)


        init {
            mRetryBtn.setOnClickListener(this)
            mErrorLayout.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.loadmore_retry, R.id.loadmore_errorlayout -> {
                    showRetry(false, null)
                    mCallback.retryPageLoad()
                }
            }
        }
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    fun showRetry(show: Boolean, errorMsg: String?) {
        retryPageLoad = show
        notifyItemChanged(likedUserDetailModel.size - 1)

        if (errorMsg != null) this.errorMsg = errorMsg
    }



}