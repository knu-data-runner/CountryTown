package com.DataRunner.CountryTown.ui.arround

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.DataRunner.CountryTown.R

class ArroundFragment : Fragment() {

    private lateinit var arroundViewModel: ArroundViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        arroundViewModel =
            ViewModelProvider(this).get(ArroundViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_arround, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        arroundViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
