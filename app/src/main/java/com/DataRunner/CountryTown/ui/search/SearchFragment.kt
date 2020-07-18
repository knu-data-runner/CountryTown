package com.DataRunner.CountryTown.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.DataRunner.CountryTown.*
import com.naver.maps.map.NaverMapSdk
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.main_layout.view.*
import org.json.JSONObject


class SearchFragment : Fragment() {

    private lateinit var container: ViewGroup
    private lateinit var townDataList: ArrayList<TownData>
    private lateinit var searchEdt: EditText
    private lateinit var root: View
    private lateinit var charText: String
    private val utils = Utils()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.root = inflater.inflate(R.layout.fragment_search, container, false)
        this.container = container!!
        townDataList = getTownDataList()
        searchEdt = root.editSearch
        searchEdt.addTextChangedListener(
            object :TextWatcher{
                override fun afterTextChanged(edit: Editable?) {
                }
                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                    charText = charSequence.toString()
                    search(charText)
                }
            }
        )
        root.swipe.setOnRefreshListener {
            if (charText != null) search(charText)
            root.swipe.isRefreshing = false
        }
        NaverMapSdk.getInstance(container!!.context).client =
            NaverMapSdk.NaverCloudPlatformClient(getSecret("naver", "CLIENT_ID"))
        return root
    }

    private fun search(charText: String) {
        var searchDataList: ArrayList<TownData> = ArrayList()
        if(charText != null && !charText.equals("")) {
            searchDataList.clear()
            for (i in townDataList.indices) {
                if (townDataList[i].addr.contains(charText) || townDataList[i].title.contains(charText) ||
                    townDataList[i].programType.contains(charText) || townDataList[i].programContent.contains(charText))
                    searchDataList.add(townDataList[i])
            }
        } else {
            searchDataList.clear()
        }
        val dataAdapter = getAdapter(searchDataList)
        root.result.adapter = dataAdapter
        val lm = LinearLayoutManager(activity)  // activity: 부모 Activity(Context)
        root.result.layoutManager = lm
        root.result.setHasFixedSize(true)
    }

    private fun getSecret(provider:String, keyArg:String): String {
        val assetManager = resources.assets
        val inputStream= assetManager.open("secret.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(jsonString)
        val secret = obj.getJSONObject(provider)
        return secret.getString(keyArg)
    }

    private fun getTownDataList(checkSido : String = "전국"): ArrayList<TownData> {
        val ret = utils.parsing(container!!.context, checkSido)
        return ret
    }

    private fun getAdapter(ret: ArrayList<TownData>): DataAdapter {
        val dataAdapter = DataAdapter(this, ret) { data ->
            val toDetailIntent = Intent(activity, Detail::class.java)   // activity: 부모 Activity(Context)
            val b = Bundle()
            b.putParcelable("parceledData", data)
            toDetailIntent.putExtra("bundleData", b)
            startActivity(toDetailIntent)
        }
        return dataAdapter
    }
}
