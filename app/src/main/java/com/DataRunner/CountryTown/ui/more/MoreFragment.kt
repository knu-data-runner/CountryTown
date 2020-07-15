package com.DataRunner.CountryTown.ui.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.DataRunner.CountryTown.*
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {

    private lateinit var moreViewModel: MoreViewModel
    private val utils = Utils()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_more, container, false)

        val infoClickListener = View.OnClickListener { view ->
            val intent = Intent(activity, InfoActivity :: class.java)
            val res = utils.parsing("전국")
            intent.putExtra("dataList", res)
            startActivity(intent)
        }
        root.info.setOnClickListener(infoClickListener)

        val licenseClickListener = View.OnClickListener { view ->
            var intent = Intent(activity, LicenseActivity :: class.java)
            startActivity(intent)
        }
        root.license.setOnClickListener(licenseClickListener)

        val makerClickListener = View.OnClickListener { view ->
            var intent = Intent(activity, MakerActivity :: class.java)
            startActivity(intent)
        }
        root.maker.setOnClickListener(makerClickListener)

        val reviewClickListener = View.OnClickListener { view ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.DataRunner.CountryTown")
            startActivity(intent)
        }
        root.review.setOnClickListener(reviewClickListener)

        val contactClickListener = View.OnClickListener { view ->
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("knu.app.develop@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "문의드립니다.")
            }
            startActivity(intent)
        }
        root.contact.setOnClickListener(contactClickListener)
        return root
    }
}
