package com.mm.foodmanagment.ui.main.postDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Comments
import kotlinx.android.synthetic.main.fragment_post_details.view.*


class PostDetailsFragment : Fragment() {
    private var viewModel: PostDetailsViewModel? = PostDetailsViewModel()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_post_details, container, false)
        val activityDashboard: DashboardActivity? = activity as DashboardActivity?

        getArgs(root)
        postsObservers(activityDashboard,root)
        refresh(activityDashboard,root)
        setListener(activityDashboard!!,root)
        return root
    }

    private fun getArgs(root: View) {
        viewModel?.run {
            postId = arguments?.getString("postId") ?: "0"
            imageType = arguments?.getString("imageType") ?: "0"
            comments = arguments?.getInt("comments") ?: 0
            Log.d("mino","$imageType")
//            if( imageType== "1"){
//                root.postImg.isVisible = true
//                root.imageSlider.isVisible = false
//            }else{
//                root.imageSlider.isVisible = true
//                root.postImg.isVisible = false
//            }
        }
    }

    private fun setListener(activityDashboard: DashboardActivity, root: View) {
        root.run {

            back.setOnClickListener {
                requireActivity().onBackPressed()
            }
            sendComment.setOnClickListener {
                if (!commentText.text.isNullOrEmpty()) {
                    viewModel!!.run {
                        sendComment(
                            Comments(
                                "",
                                "",
                                "",
                                "",
                                "",
                                postId,
                                "",
                                commentText.text.toString() ?: "",
                                activityDashboard.getCurrentDate(),
                                0
                            ), activityDashboard, root
                        )
                        getComments(activityDashboard,root)
                        getOnePost(activityDashboard,root)
                    }

                }
            }
        }

    }

    private fun postsObservers(activityDashboard: DashboardActivity?, root: View) {
        viewModel!!.getComments(activityDashboard,root)
        viewModel!!.getOnePost(activityDashboard,root)
    }

    private fun refresh(activityDashboard: DashboardActivity?, root: View) {
        root.commentsRv.viewTreeObserver.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            val scrollY: Int = root.commentsRv.scrollY //for verticalScrollView
            root.post_details_fragment.isEnabled = scrollY == 0
        })

        root.post_details_fragment.setOnRefreshListener {
            postsObservers(activityDashboard,root)
            root.post_details_fragment.isRefreshing = false
        }
    }

}
