package com.farizz.dicodingevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.farizz.dicodingevent.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
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

        binding.rvUpcomingEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.progressBar.visibility = View.VISIBLE

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        eventViewModel.events.observe(viewLifecycleOwner) { eventResponse ->
            val eventAdapter = EventAdapter(eventResponse.listEvents ?: emptyList())
            binding.rvUpcomingEvent.adapter = eventAdapter
            binding.progressBar.visibility = View.GONE
        }

        eventViewModel.getEvents(1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
