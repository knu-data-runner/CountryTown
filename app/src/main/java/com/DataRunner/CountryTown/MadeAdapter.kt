package com.DataRunner.CountryTown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MadeAdapter (val context: Context, val madeList: ArrayList<Made_Data>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.madeby_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val date_name = view.findViewById<TextView>(R.id.made_name)
        val data_email = view.findViewById<TextView>(R.id.made_email)
        val data_git= view.findViewById<TextView>(R.id.made_git)
        val data_aff= view.findViewById<TextView>(R.id.made_aff)
        val data_pic= view.findViewById<ImageView>(R.id.made_img)


        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val data = madeList[position]
        date_name.text = data.made_name
        data_email.text = data.made_email
        data_git.text = data.made_git
        data_aff.text = data.made_aff
        Glide.with(view).load(data.made_pic).into(data_pic)

        return view
    }

    override fun getItem(position: Int): Any {
        return madeList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return madeList.size
    }
}