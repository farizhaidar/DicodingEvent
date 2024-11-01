package com.farizz.dicodingevent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.farizz.dicodingevent.database.FavoriteEvent
import com.farizz.dicodingevent.database.FavoriteEventDatabase
import com.farizz.dicodingevent.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteEventDatabase: FavoriteEventDatabase
    private val favoriteEvents = mutableListOf<FavoriteEvent>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteEventDatabase = FavoriteEventDatabase.getDatabase(requireContext())

        favoriteAdapter = FavoriteAdapter(favoriteEvents) { favoriteEvent ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_EVENT_ID, favoriteEvent.id)
            }
            startActivity(intent)
        }

        binding.rvFavoriteEvents.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        observeFavoriteEvents()

        val pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)
        val settingsViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(SettingsViewModel::class.java)
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun observeFavoriteEvents() {
        lifecycleScope.launch {
            favoriteEventDatabase.favoriteEventDao().getAllFavorites().collect { favorites ->
                favoriteEvents.clear()
                favoriteEvents.addAll(favorites)
                favoriteAdapter.notifyDataSetChanged()
                binding.progressBarFav.visibility = View.GONE
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
