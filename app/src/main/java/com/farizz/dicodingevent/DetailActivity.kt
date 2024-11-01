package com.farizz.dicodingevent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.farizz.dicodingevent.data.response.DetailResponse
import com.farizz.dicodingevent.data.response.Event
import com.farizz.dicodingevent.data.retrofit.ApiConfig
import com.farizz.dicodingevent.database.FavoriteEvent
import com.farizz.dicodingevent.database.FavoriteEventDatabase
import com.farizz.dicodingevent.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var favoriteEventDatabase: FavoriteEventDatabase
    private var isFavorite = false

    private lateinit var binding: ActivityDetailBinding
    private lateinit var favoriteButton: ImageButton

    private var eventId: Int = 0
    private var currentEvent: Event? = null

    private var registrationUrl: String? = null

    private lateinit var progressBar: ProgressBar

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteEventDatabase = FavoriteEventDatabase.getDatabase(this)
        favoriteButton = binding.btnFavorite
        progressBar = binding.progressBarDetail

        val pref = SettingPreferences.getInstance(applicationContext.dataStore)
        settingsViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(SettingsViewModel::class.java)

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            val nightMode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
                AppCompatDelegate.setDefaultNightMode(nightMode)

                recreate()
            }
        }

        eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId != -1) {
            fetchEventDetail(eventId)
        } else {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show()
        }

        binding.btnRegister.setOnClickListener {
            registrationUrl?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            } ?: Toast.makeText(this, "Registration URL not available", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            isFavorite = favoriteEventDatabase.favoriteEventDao().isFavorite(eventId)
            updateFavoriteButton()
        }

        favoriteButton.setOnClickListener {
            if (isFavorite) {
                deleteFavorite()
            } else {
                insertToFavorite()
            }
        }
    }

    private fun fetchEventDetail(eventId: Int) {
        progressBar.visibility = View.VISIBLE
        ApiConfig.getApiService().getEventDetails(eventId).enqueue(object :
            Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    displayEventDetail(response.body()?.event)
                } else {
                    Toast.makeText(this@DetailActivity, "Failed to load event details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DetailActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayEventDetail(event: Event?) {
        event?.let {
            currentEvent = it
            binding.tvEventName.text = it.name

            binding.tvEventDescription.text = HtmlCompat.fromHtml(
                it.description ?: "",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            binding.tvEventTime.text = "${it.beginTime} - ${it.endTime}"
            binding.tvEventOwner.text = it.ownerName

            val remainingQuota = (it.quota ?: 0) - (it.registrants ?: 0)
            binding.tvEventQuota.text = "Sisa Quota: $remainingQuota"

            registrationUrl = it.link

            if (!isFinishing && !isDestroyed) {
                Glide.with(this).load(it.mediaCover).into(binding.imgEventCover)
            }
        } ?: run {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    private fun insertToFavorite() {
        favoriteButton.isEnabled = false
        currentEvent?.let {
            val favoriteEvent = FavoriteEvent(eventId, it.name.toString(), it.imageLogo, it.description)
            lifecycleScope.launch {
                favoriteEventDatabase.favoriteEventDao().addFavorite(favoriteEvent)
                isFavorite = true
                updateFavoriteButton()
                Toast.makeText(this@DetailActivity, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                favoriteButton.isEnabled = true
            }
        }
    }

    private fun deleteFavorite() {
        lifecycleScope.launch {
            val favoriteEvent = favoriteEventDatabase.favoriteEventDao().getFavoriteById(eventId)
            favoriteEvent?.let {
                favoriteEventDatabase.favoriteEventDao().removeFavorite(it)
                isFavorite = false
                updateFavoriteButton()
                Toast.makeText(this@DetailActivity, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this@DetailActivity, "Event tidak ditemukan di favorit", Toast.LENGTH_SHORT).show()
            }
            favoriteButton.isEnabled = true
        }
    }

    private fun updateFavoriteButton() {
        favoriteButton.setImageResource(
            if (isFavorite) R.drawable.favorite else R.drawable.favorite_border
        )
    }
}
