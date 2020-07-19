package com.DataRunner.CountryTown.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.DataRunner.CountryTown.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search.view.layoutText
import kotlinx.android.synthetic.main.main_layout.view.*


class SearchFragment : Fragment() {

    private lateinit var container: ViewGroup
    private lateinit var townDataList: ArrayList<TownData>
    private lateinit var searchEdt: EditText
    private lateinit var root: View
    private lateinit var charText: String
    private val utils = Utils()
    private val gpsUtils = GpsUtils()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.root = inflater.inflate(R.layout.fragment_search, container, false)
        this.container = container!!
        townDataList = utils.getTownDataList(requireContext())
        searchEdt = root.editSearch
        searchEdt.addTextChangedListener(
            object :TextWatcher{
                override fun afterTextChanged(edit: Editable?) {
                }
                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                    charText = charSequence.toString()
                    val searchDataList = utils.search(townDataList, arrayOf(charText), "user")
                    if (searchDataList.size != 0) {
                        layoutText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
                    } else {
                        layoutText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    utils.setAdapter(requireContext(), root.result, searchDataList)
                }
            }
        )
        root.swipe.setOnRefreshListener {
            if (charText != null) utils.search(townDataList, arrayOf(charText), "user")
            root.swipe.isRefreshing = false
        }
        return root
    }
}
