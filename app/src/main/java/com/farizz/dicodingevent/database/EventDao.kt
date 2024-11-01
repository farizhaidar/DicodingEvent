package com.farizz.dicodingevent.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_events WHERE id = :eventId")
    suspend fun getFavoriteById(eventId: Int): FavoriteEvent?

    @Delete
    suspend fun removeFavorite(favoriteEvent: FavoriteEvent)

    @Query("SELECT EXISTS(SELECT * FROM favorite_events WHERE id = :eventId)")
    suspend fun isFavorite(eventId: Int): Boolean

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): Flow<List<FavoriteEvent>>
}