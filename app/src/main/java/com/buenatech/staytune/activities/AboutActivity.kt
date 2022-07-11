package com.buenatech.staytune.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.Preference
import com.buenatech.staytune.BuildConfig
import com.buenatech.staytune.R
import com.buenatech.staytune.databinding.ActivityAboutBinding
import java.lang.Exception

class AboutActivity : BaseActivity() {
    private lateinit var binding: ActivityAboutBinding


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPersistentActionBar(binding.appBarLayout.toolbar)
        setToolbarTitle(R.string.activity_about)
    }

    companion object {
        const val ABOUT_DEVELOPER_EMAIL = "harimoradiya123@gmail.com"
        const val ABOUT_HARI = "https://www.instagram.com/hari_moradiya1610/"
        const val ABOUT_GAURANG = "https://www.instagram.com/gaurang_dhameliya/"

        class AboutFragment: BasePreference() {

            override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
                setPreferencesFromResource(R.xml.xml_about_main, rootKey)
            }

            override fun onStart() {
                super.onStart()

                findPreference<Preference>("KEY_SHARE")
                    ?.setOnPreferenceClickListener {

                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                            var shareMessage = "\nGet more done with the Timely mobile app\n\n"
                            shareMessage =
                                """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            """.trimIndent()
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "choose one"))
                        }
                        catch (e: Exception) {
                            //e.toString();
                        }

                        true

                    }
                findPreference < Preference >("KEY_RATE")
                        ?.setOnPreferenceClickListener {

                            val uri = Uri.parse("market://details?id=" + requireContext().packageName)
                            val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

                            var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                            flags = if (Build.VERSION.SDK_INT >= 21) {
                                flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                            } else {
                                flags or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            goToMarketIntent.addFlags(flags)

                            try {
                                startActivity(goToMarketIntent)
                            } catch (e: ActivityNotFoundException) {
                                val intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName))

                                startActivity(intent)
                            }
                            true
                }
//
                findPreference<Preference>("KEY_FEEDBACK")
                    ?.setOnPreferenceClickListener {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("mailto:${ABOUT_DEVELOPER_EMAIL}")
                        }

                        if (intent.resolveActivity(requireContext().packageManager) != null)
                            startActivity(intent)
                        true
                    }

//
                findPreference<Preference>("KEY_HARI")
                    ?.setOnPreferenceClickListener {
                        CustomTabsIntent.Builder().build()
                            .launchUrl(requireContext(), Uri.parse(ABOUT_HARI))

                        true
                    }
                findPreference<Preference>("KEY_GAURANG")
                    ?.setOnPreferenceClickListener {
                        CustomTabsIntent.Builder().build()
                            .launchUrl(requireContext(), Uri.parse(ABOUT_GAURANG))

                        true
                    }
            }
        }
    }
}