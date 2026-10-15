package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WardrobeDao {
    @Query("SELECT * FROM wardrobe_items ORDER BY dateAdded DESC")
    fun getAllItems(): Flow<List<WardrobeItemEntity>>

    @Query("SELECT * FROM wardrobe_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): WardrobeItemEntity?

    @Query("SELECT * FROM wardrobe_items WHERE isAvailable = 1")
    suspend fun getAvailableItemsSync(): List<WardrobeItemEntity>

    @Query("SELECT * FROM wardrobe_items WHERE category = :category ORDER BY dateAdded DESC")
    fun getItemsByCategory(category: String): Flow<List<WardrobeItemEntity>>

    @Query("SELECT * FROM wardrobe_items WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun getFavoriteItems(): Flow<List<WardrobeItemEntity>>

    @Query("SELECT COUNT(*) FROM wardrobe_items")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM wardrobe_items WHERE category = :category")
    fun getCountByCategory(category: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WardrobeItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WardrobeItemEntity>): List<Long>

    @Update
    suspend fun updateItem(item: WardrobeItemEntity)

    @Delete
    suspend fun deleteItem(item: WardrobeItemEntity)

    @Query("DELETE FROM wardrobe_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("UPDATE wardrobe_items SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE wardrobe_items SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun setAvailable(id: Long, isAvailable: Boolean)

    @Query("DELETE FROM wardrobe_items")
    suspend fun clearAll()
}

@Dao
interface FavoriteOutfitDao {
    @Query("SELECT * FROM favorite_outfits ORDER BY dateSaved DESC")
    fun getAllFavorites(): Flow<List<FavoriteOutfitEntity>>

    @Query("SELECT * FROM favorite_outfits WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: Long): FavoriteOutfitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(outfit: FavoriteOutfitEntity): Long

    @Delete
    suspend fun deleteFavorite(outfit: FavoriteOutfitEntity)

    @Query("DELETE FROM favorite_outfits WHERE id = :id")
    suspend fun deleteFavoriteById(id: Long)

    @Query("UPDATE favorite_outfits SET userFeedback = :feedback WHERE id = :id")
    suspend fun updateFeedback(id: Long, feedback: Int)

    @Query("DELETE FROM favorite_outfits")
    suspend fun clearAll()
}
