package com.DataRunner.CountryTown

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DataAdapter(
    val context: Fragment,                  // HomeFragment
    val townDataList: ArrayList<TownData>,  // Data 객체 list
    val itemClick: (TownData) -> Unit)      // Data 객체 클릭시 실행되는 lambda 식
    : RecyclerView.Adapter<DataAdapter.Holder>() {

    /**
     * 각 Data 객체를 감싸는 Holder
     * bind 가 자동 호출되며 데이터가 매핑된다.
     * @author jungwoo
     */
    inner class Holder(itemView: View, itemClick: (TownData) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val dataSido = itemView.findViewById<TextView>(R.id.sido)
        val dataSigungu = itemView.findViewById<TextView>(R.id.sigungu)
        val dataTitle = itemView.findViewById<TextView>(R.id.title)
        val dataprogramType = itemView.findViewById<TextView>(R.id.programType)
        val dataprogramContent = itemView.findViewById<TextView>(R.id.programContent)
        val dataDistance = itemView.findViewById<TextView>(R.id.distance)
        val dataImg = itemView.findViewById<ImageView>(R.id.main_img)

        fun bind (townData: TownData, context: Fragment) {
            dataSido.text = townData.sido
            dataSigungu.text = townData.sigungu
            dataTitle.text = townData.title
            dataprogramType.text = townData.programType
            dataprogramContent.text = townData.programContent
            dataDistance.text = if (townData.distance == null) "" else townData.distance + "km | "

            // Set loading image
            Glide.with(itemView)
                .load(R.drawable.loading_spinningwheel)
                .into(dataImg)

            // Set image
            val storage = Firebase.storage
            var storageRef = storage.reference
            storageRef.child("img/town/" + townData.townId + "_1.png").downloadUrl.addOnSuccessListener {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(itemView)
                    .load(it)
                    .into(dataImg)
            }.addOnFailureListener {
                // Handle any errors
                storageRef.child("img/town/" + townData.townId + "_1.PNG").downloadUrl.addOnSuccessListener {
                    Glide.with(itemView)
                        .load(it)
                        .into(dataImg)
                }
            }
            itemView.setOnClickListener { itemClick(townData) }
        }
    }

    /**
     * 화면을 최초로 로딩하여 만들어진 View 가 없는 경우, xml 파일을 inflate 하여 ViewHolder 생성
     * @author jungwoo
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.data_item, parent, false)
        return Holder(view, itemClick)
    }

    /**
     * onCreateViewHolder 에서 만든 view 와 실제 입력되는 각각의 데이터 연결
     * @author jungwoo
     */
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(townDataList[position], context)
    }

    /**
     * RecyclerView 로 만들어지는 item 의 총 개수 반환
     * @author jungwoo
     */


    fun getSido(i: Int): Any? {
        return townDataList[i].sido
    }
    fun getTitle(i: Int): Any? {
        return townDataList[i].title
    }
    fun getProgramType(i: Int): Any? {
        return townDataList[i].programType
    }
    fun getProgramContent(i: Int): Any? {
        return townDataList[i].programContent
    }

    override fun getItemCount(): Int {
        return townDataList.size
    }


}