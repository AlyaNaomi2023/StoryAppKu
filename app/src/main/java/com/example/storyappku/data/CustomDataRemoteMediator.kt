package com.example.storyappku.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyappku.data.model.UserDataStorePreferences
import com.example.storyappku.data.model.database.CustomStoryDatabase
import com.example.storyappku.data.model.database.RemoteKeys
import com.example.storyappku.data.model.response.ListStoryItem
import com.example.storyappku.api.ApiService
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class CustomDataRemoteMediator(
    private val database: CustomStoryDatabase,
    private val apiService: ApiService,
    private val dataStoreRepository: UserDataStorePreferences
) : RemoteMediator<Int, ListStoryItem>() {

    // Konstanta untuk indeks halaman awal
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    // Metode untuk menginisialisasi mediasi data jarak jauh
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    // Metode untuk memuat data jarak jauh
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        // Mendapatkan nomor halaman berdasarkan tipe pembebanan
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        // Menjalankan operasi mediasi data jarak jauh
        try {
            val token = dataStoreRepository.getUser().first()
            val userToken = "Bearer ${token.token}"

            // Membuat parameter kueri
            val queryParam = HashMap<String, Int>()
            queryParam["page"] = page
            queryParam["size"] = state.config.pageSize
            queryParam["location"] = 0

            // Memanggil layanan API untuk mendapatkan data cerita
            val responseData = apiService.getListStory(userToken, queryParam)

            // Menentukan apakah halaman terakhir sudah tercapai
            val endOfPaginationReached = responseData.listStory.isEmpty()

            // Menjalankan transaksi dengan database
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Menghapus kunci jarak jauh dan semua cerita pada pembaruan data
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAllStory()
                }

                // Menentukan kunci sebelumnya dan berikutnya
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                // Menyusun kunci jarak jauh untuk setiap entitas cerita
                val keys = responseData.listStory.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                // Menyimpan kunci jarak jauh dan menyisipkan cerita ke dalam database
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertStory(responseData.listStory)
            }

            // Mengembalikan hasil mediasi sukses
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            // Mengembalikan hasil mediasi error jika terjadi kesalahan
            return MediatorResult.Error(exception)
        }
    }

    // Metode untuk mendapatkan kunci jarak jauh untuk item terakhir
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    // Metode untuk mendapatkan kunci jarak jauh untuk item pertama
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    // Metode untuk mendapatkan kunci jarak jauh yang paling dekat dengan posisi saat ini
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}
