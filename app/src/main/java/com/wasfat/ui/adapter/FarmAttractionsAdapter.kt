package com.wasfat.ui.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.wasfat.R
import com.wasfat.ui.pojo.Category

class FarmAttractionsAdapter(
    private val context: Activity,
    private val onClickListener: (Boolean, Category) -> Unit,
    private val categoryList: ArrayList<Category>,
    private val selectedAttractions : String
) : ArrayAdapter<Category>(context, R.layout.view_farm_attraction_list, categoryList) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.view_farm_attraction_list, null, true)

        val checkBoxText = rowView.findViewById(R.id.cbAttractionItem) as CheckBox


        if( selectedAttractions != "" && selectedAttractions.contains(categoryList[position].CategoryName)){
            checkBoxText.isChecked = true
        }

        checkBoxText.text = categoryList[position].CategoryName

        checkBoxText.setOnCheckedChangeListener { buttonView, isChecked ->
           onClickListener(isChecked,categoryList[position])
        }

        return rowView
    }
}