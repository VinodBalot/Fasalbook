package com.wasfat.ui.activity.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wasfat.R
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory.getClient
import com.wasfat.ui.activity.BuyActivity
import com.wasfat.ui.activity.EventCategoryActivity
import com.wasfat.ui.activity.SellActivity
import com.wasfat.ui.adapter.BannerAdapter
import com.wasfat.ui.pojo.BannerResponse
import com.wasfat.ui.pojo.BannerResponseItem
import com.wasfat.utils.Constants
import com.wasfat.utils.ProgressDialog
import kotlinx.android.synthetic.main.fragment_home.*
import me.relex.circleindicator.CircleIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel
    var adapter: PagerAdapter? = null
    private var currentPage = 0

    private val NUM_PAGES = 0
    var bannerList: ArrayList<BannerResponseItem> = ArrayList()
    private var indicator: CircleIndicator? = null
    var viewpager: ViewPager? = null
    var llSell: LinearLayout? = null
    var llBuy: LinearLayout? = null
    var llEvent: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        callBannerImageAPI()
        initializeView(view)
        return view
    }

    private fun callBannerImageAPI() {
        ProgressDialog.showProgressDialog(requireActivity())
        val myMap = HashMap<String, String>()
        myMap["languageId"] = Constants.LANGUAGE
        val apiService1 = getClient()!!.create(RestApi::class.java)
        val call1: Call<BannerResponse> = apiService1.getAllBanner(myMap)
        call1.enqueue(object : Callback<BannerResponse?> {
            override fun onResponse(
                call: Call<BannerResponse?>,
                response: Response<BannerResponse?>
            ) {
                if (response.body() != null) {
                    ProgressDialog.hideProgressDialog()
                    bannerList = response.body()!!
                    if (bannerList.isNotEmpty()) {
                        setAdapter()
                    }
                }
            }

            override fun onFailure(
                call: Call<BannerResponse?>,
                t: Throwable
            ) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }

    private fun setAdapter() {
        adapter = BannerAdapter(activity, bannerList)
        viewpager!!.adapter = adapter
        llSell!!.setOnClickListener {
            SellActivity.startActivity(requireActivity(), null, false)
        }
        llBuy!!.setOnClickListener {
            BuyActivity.startActivity(requireActivity(), null, false)
        }
        llEvent!!.setOnClickListener{
            EventCategoryActivity.startActivity(requireActivity(),null,false)
        }

        indicator!!.setViewPager(viewpager)
        viewpager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrolled(
                arg0: Int,
                arg1: Float,
                arg2: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val pageCount: Int = bannerList.size
                    if (currentPage === 0) {
                        viewpager!!.setCurrentItem(pageCount - 1, false)
                    } else if (currentPage === pageCount - 1) {
                        viewpager!!.setCurrentItem(0, false)
                    }
                }
            }
        })
        val handler = Handler()
        val update = Runnable {
            if (currentPage === NUM_PAGES) {
                currentPage = 0
            }
            viewpager!!.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 1000, 1000)
    }

    private fun initializeView(view: View) {
        viewpager = view.findViewById(R.id.pager) as ViewPager
        llSell = view.findViewById(R.id.llSell) as LinearLayout
        llBuy = view.findViewById(R.id.llBuy) as LinearLayout
        llEvent = view.findViewById(R.id.llEvent) as LinearLayout
        indicator = view.findViewById(R.id.indicator) as CircleIndicator
    }

    override fun onClick(view: View?) {

        when (requireView().id) {

        }

    }

}
