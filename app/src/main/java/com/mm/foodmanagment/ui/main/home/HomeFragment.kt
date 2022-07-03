package com.mm.foodmanagment.ui.main.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.ui.main.addPost.models.Post
import com.mm.foodmanagment.ui.main.home.adapter.PostsAdapter
import com.mm.foodmanagment.ui.main.profile.ChatActivity
import com.mm.foodmanagment.ui.main.users.UsersActivity
import com.mm.foodmanagment.utils.GlideCircleLoader
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.userImg
import kotlinx.android.synthetic.main.fragment_profile.view.*


class HomeFragment : Fragment() {
    private var viewModel: HomeViewModel? = HomeViewModel()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val activityDashboard: DashboardActivity? = activity as DashboardActivity?
        activityDashboard?.getUserData().let {
            GlideCircleLoader(requireContext()).loadImage(it?.userImg.toString(), root.userImg)
            root.usernameTV.text = "Hello ${it?.username}, Try Direct Message Now!!"
        }

        postsObservers(activityDashboard,root)
        refresh(activityDashboard,root)
        setListener(root)
        return root
    }


    private fun setListener(root: View) {
        root.run {
            addPostBtn.setOnClickListener {
//                findNavController().navigate(R.id.action_navigation_home_to_navigation_chat)
                requireActivity().startActivity(Intent(requireContext(),ChatActivity::class.java))
            }

            cardClicked .setOnClickListener {
                requireContext().startActivity(Intent(requireContext(),UsersActivity::class.java))
            }
        }

    }

    private fun postsObservers(activityDashboard: DashboardActivity?, root: View) {
        Log.d(TAG,"ob: ${viewModel!!.getPostsList().value?.size}")
        viewModel!!.getPosts(activityDashboard,root)
        viewModel!!.getPostsList().observe(requireActivity(), androidx.lifecycle.Observer {
            val adapter = PostsAdapter(requireActivity().applicationContext,it ,activityDashboard?.getUserData(),object : PostsAdapter.ClickListener{
                override fun onItemClick(v: View, model: Post) {
                        v.findNavController().navigate(
                            R.id.action_navigation_home_to_navigation_post_details,
                            Bundle().apply {
                            putString("postId", model.postID ?: "0")
                            putInt("comments", model.comments ?: 0)
                            putString("imageType", model.imageType ?: "0")
                        })
                }

                override fun onUserClick(v: View, model: Post) {
                    v.findNavController().navigate(
                        R.id.action_navigation_home_to_navigation_profile,
                        Bundle().apply {
                            putString("uid", model.uid ?: "0")
                        })
                }

                override fun onOptionsClick(v: View, model: Post) {

                }

            })
            root.postsRv.adapter = adapter
            adapter.notifyDataSetChanged()
            root.postsRv.setHasFixedSize(true)
        })

    }

    private fun refresh(activityDashboard: DashboardActivity?, root: View) {
        root.postsRv.viewTreeObserver.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            val scrollY: Int = root.postsRv.scrollY //for verticalScrollView
            root.home_fragment.isEnabled = scrollY == 0
        })

        root.home_fragment.setOnRefreshListener {
            postsObservers(activityDashboard,root)
            root.home_fragment.isRefreshing = false
        }

    }


    override fun onResume() {
        super.onResume()
    }

}
