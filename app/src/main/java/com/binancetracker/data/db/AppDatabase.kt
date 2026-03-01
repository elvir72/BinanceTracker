package com.binancetracker.data.db

import androidx.room.*
import com.binancetracker.data.model.FavoriteSymbol
import com.binancetracker.data.model.RecentSymbol
import kotlinx.coroutines.flow.Flow

@Dao
interface SymbolDao {
    @Query("SELECT * FROM favorite_symbols ORDER BY addedAt DESC")
    fun getFavorites(): Flow<List<FavoriteSymbol>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(symbol: FavoriteSymbol)

    @Delete
    suspend fun removeFavorite(symbol: FavoriteSymbol)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_symbols WHERE symbol = :symbol)")
    suspend fun isFavorite(symbol: String): Boolean

    @Query("SELECT * FROM recent_symbols ORDER BY visitedAt DESC LIMIT 20")
    fun getRecentSymbols(): Flow<List<RecentSymbol>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(symbol: RecentSymbol)

    @Query("DELETE FROM recent_symbols WHERE symbol NOT IN (SELECT symbol FROM recent_symbols ORDER BY visitedAt DESC LIMIT 20)")
    suspend fun pruneRecent()
}

@Database(
    entities = [FavoriteSymbol::class, RecentSymbol::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun symbolDao(): SymbolDao
}
