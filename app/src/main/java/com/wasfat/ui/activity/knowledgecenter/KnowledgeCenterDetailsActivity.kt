package com.wasfat.ui.activity.knowledgecenter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfat.R
import com.wasfat.databinding.ActivityKnowledgeCenterDetailsBinding
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.UserIdea

class KnowledgeCenterDetailsActivity : BaseBindingActivity() {

    var binding: ActivityKnowledgeCenterDetailsBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var newsCuttingList: ArrayList<String> = ArrayList()

    var imageListRVAdapter: ImageListRVAdapter? = null
    var newsCuttingRVAdapter: ImageListRVAdapter? = null

    lateinit var  userIdea : UserIdea

    companion object {

        fun startActivity(activity: Activity, userIdea : UserIdea, isClear: Boolean) {
            val intent = Intent(activity, KnowledgeCenterDetailsActivity::class.java)
            intent.putExtra("userIdea", userIdea)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_knowledge_center_details)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        //Getting product from parent
        userIdea = (intent.getSerializableExtra("userIdea") as? UserIdea)!!
    }

    override fun initializeObject() {

        binding!!.textTitle.text = userIdea.Title

        binding!!.txtIdeaTitle.text = userIdea.Title
        //binding!!.txtIdeaDetails.text = userIdea.Details

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding!!.txtIdeaDetails.text =
                Html.fromHtml(userIdea.Details, Html.FROM_HTML_MODE_COMPACT);
        } else {
            binding!!.txtIdeaDetails.text = Html.fromHtml(userIdea.Details);
        }

        setAdapter()
    }

    private fun setAdapter() {

        //Image list Adapter

        userIdea.ImageList.forEach {
            if(it.ImageName.isNotEmpty()){
                val image = it.Path + "/" + it.ImageName
                imageList.add(image)
            }
        }

        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding!!.rvImageList.layoutManager = layoutManager1
        binding!!.rvImageList.setHasFixedSize(true)

        imageListRVAdapter = ImageListRVAdapter(this,onClickListener,imageList)
        binding!!.rvImageList.adapter = imageListRVAdapter

        if(imageListRVAdapter!!.itemCount == 0){
            binding!!.txtNoImageFound.visibility = View.VISIBLE
            binding!!.rvImageList.visibility = View.GONE
        }else{
            binding!!.txtNoImageFound.visibility = View.GONE
            binding!!.rvImageList.visibility = View.VISIBLE
        }

        //News Cutting Adapter

        userIdea.NewsCuttings.forEach {
            if(it.ImageName.isNotEmpty()){
                val image = it.Path + "/" + it.ImageName
                newsCuttingList.add(image)
            }
        }

        val layoutManager2 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding!!.rvNewsCuttingList.layoutManager = layoutManager2
        binding!!.rvNewsCuttingList.setHasFixedSize(true)

        newsCuttingRVAdapter = ImageListRVAdapter(this,onClickListener,newsCuttingList)
        binding!!.rvNewsCuttingList.adapter = newsCuttingRVAdapter

        if(newsCuttingRVAdapter!!.itemCount == 0){
            binding!!.txtNoNewsFound.visibility = View.VISIBLE
            binding!!.rvNewsCuttingList.visibility = View.GONE
        }else{
            binding!!.txtNoNewsFound.visibility = View.GONE
            binding!!.rvNewsCuttingList.visibility = View.VISIBLE
        }


    }

    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btnSubmit.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.btnSubmit ->{
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}