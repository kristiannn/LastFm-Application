package com.neno.lastfmapp.modules.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.neno.lastfmapp.R
import com.neno.lastfmapp.modules.utils.AccountManager
import com.neno.lastfmapp.modules.utils.fragments.SecondaryFragment
import org.koin.android.ext.android.inject


class SettingsFragment : SecondaryFragment(), AdapterView.OnItemSelectedListener
{
    private lateinit var themesSpinner: Spinner
    private val accountManager: AccountManager by inject()
    private val themes = listOf(R.style.LightTheme, R.style.DarkMutedTheme)
    private val themesHash by lazy {
        hashMapOf<String, Int>(
            Pair(getString(R.string.theme_light), R.style.LightTheme),
            Pair(getString(R.string.theme_dark_muted), R.style.DarkMutedTheme)
        )
    }

    override fun toolbarTitle(): String = getString(R.string.settings)

    override fun currentNavigationUser(): String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.settings_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        if (view == null) return
        accountManager.setCurrentTheme(themesHash.values.elementAt(position))
        activity?.recreate()
    }

    override fun onNothingSelected(parent: AdapterView<*>?)
    {
    }

    private fun setupViews(view: View)
    {
        themesSpinner = view.findViewById(R.id.spinnerThemes)
        val themesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, themesHash.keys.toList())
        val selectedTheme =
            themesHash.keys.indexOf(themesHash.filter { accountManager.getCurrentTheme() == it.value }.keys.first())

        themesAdapter.setDropDownViewResource(R.layout.menu_dropdown_item);
        themesSpinner.adapter = themesAdapter
        themesSpinner.setSelection(selectedTheme, false)
        themesSpinner.onItemSelectedListener = this
    }
}