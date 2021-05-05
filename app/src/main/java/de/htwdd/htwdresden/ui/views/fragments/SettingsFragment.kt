package de.htwdd.htwdresden.ui.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.databinding.FragmentSettingsBinding
import de.htwdd.htwdresden.ui.models.*
import de.htwdd.htwdresden.ui.viewmodels.fragments.SettingsViewModel
import de.htwdd.htwdresden.utils.extensions.error
import de.htwdd.htwdresden.utils.extensions.getViewModel
import de.htwdd.htwdresden.utils.extensions.verbose
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder

class SettingsFragment : Fragment() {

    private val viewModel by lazy { getViewModel<SettingsViewModel>() }
    private val cph by lazy { CryptoSharedPreferencesHolder.instance }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        DataBindingUtil.inflate<FragmentSettingsBinding>(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        ).apply {
            settingsModel = viewModel.model
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        viewModel.apply {
            try {
                val packageInfo = context?.packageManager?.getPackageInfo(context?.packageName ?: "de.htwdd.htwdresden", 0)
                verbose("${packageInfo?.versionName}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    verbose("${packageInfo?.longVersionCode}")
                } else {
                    @Suppress("DEPRECATION")
                    verbose("${packageInfo?.versionCode}")
                }

            } catch (e: Exception) {
                error(e)
            }

            onImprintClick {
                findNavController()
                    .navigate(
                        R.id.action_to_web_view_page_fragment,
                        bundleOf(
                            WebViewFragment.BUNDLE_ARG_URL to "file:///android_asset/HTW-Impressum.html",
                            "title" to getString(R.string.imprint)
                        )
                    )
            }

            onDataProtectionClick {
                //            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                findNavController()
                    .navigate(
                        R.id.action_to_web_view_page_fragment,
                        bundleOf(
                            WebViewFragment.BUNDLE_ARG_URL to "file:///android_asset/HTW-Datenschutz.html",
                            "title" to getString(R.string.data_protection)
                        )
                    )
//            } else {
//                findNavController()
//                    .navigate(
//                        R.id.action_to_web_view_page_fragment,
//                        bundleOf(
//                            WebViewFragment.BUNDLE_ARG_URL to "file:///android_asset/HTW-Datenschutz_dark.html",
//                            "title" to getString(R.string.data_protection)
//                        )
//                    )
//            }
            }

            onDeleteAllDataClick {
                MaterialDialog(requireContext()).show {
                    title(R.string.delete_all_saved_data)
                    message(R.string.delete_all_saved_data_question)
                    positiveButton(R.string.general_delete) {
                        cph.clear()
                        deleteAllTimetable()
                        findNavController().navigate(R.id.onboarding_page_fragment)
                    }
                    negativeButton(R.string.general_cancel)
                }
            }

            onResetEventsClick {
                MaterialDialog(requireContext()).show {
                    title(R.string.show_hidden_elective_lectures_title)
                    message(R.string.show_hidden_elective_lecture_question)
                    positiveButton(R.string.general_reset) {
                        activity?.let {
                            val getAllTimetables = getAllTimetables()
                            getAllTimetables.forEach {
                                it.isHidden = false
                                TimetableRealm().updateAsync(it) {}
                            }
                            Toast.makeText(
                                it,
                                R.string.show_hidden_elective_lecture_message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    negativeButton(R.string.general_cancel)
                }
            }

            onStudyGroupClick {
                findNavController().navigate(R.id.action_to_study_group_page_fragment)
            }

            onLoginClick {
                findNavController().navigate(R.id.action_to_login_page_fragment)
            }
        }
    }
}