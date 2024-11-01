package com.farizz.dicodingevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.farizz.dicodingevent.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)
        val settingsViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(SettingsViewModel::class.java)
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        binding.rvFinishedEvent.layoutManager = LinearLayoutManager(activity)

        binding.progressBar.visibility = View.VISIBLE

        eventViewModel.events.observe(viewLifecycleOwner, { eventResponse ->
            val eventAdapter = EventAdapter(eventResponse.listEvents ?: emptyList())
            binding.rvFinishedEvent.adapter = eventAdapter
            binding.progressBar.visibility = View.GONE
        })

        eventViewModel.getEvents(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}