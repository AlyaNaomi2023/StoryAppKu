package com.example.storyappku.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyappku.data.model.UserDataStorePreferences
import com.example.storyappku.data.model.response.ListStoryItem
import com.example.storyappku.api.ApiService
import kotlinx.coroutines.flow.first

/**
 * Custom Paging Source untuk memuat data cerita secara halaman.
 *
 * @property apiServicePaging Layanan API untuk mendapatkan data cerita.
 * @property dataStoreRepository Penyimpanan data pengguna.
 */
class CustomDataPagingSource(
    private val apiServicePaging: ApiService,
    private val dataStoreRepository: UserDataStorePreferences
) : PagingSource<Int, ListStoryItem>() {

    /**
     * Mendapatkan kunci penyegar untuk state Paging.
     *
     * @param state State Paging saat ini.
     * @return Kunci penyegar.
     */
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /**
     * Metode untuk memuat data cerita dalam halaman.
     *
     * @param params Parameter untuk memuat halaman.
     * @return Hasil memuat data.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        // Mendapatkan posisi halaman
        val position = params.key ?: INITIAL_PAGE_INDEX

        // Mencoba memuat data dari layanan API
        return try {
            // Mendapatkan token pengguna dari penyimpanan data
            val token = dataStoreRepository.getUser().first()
            val userToken = "Bearer ${token.token}"

            // Membuat parameter kueri
            val queryParam = HashMap<String, Int>()
            queryParam["page"] = position
            queryParam["size"] = params.loadSize
            queryParam["location"] = 0

            // Memanggil layanan API untuk mendapatkan data cerita
            val responseData = apiServicePaging.getListStory(userToken, queryParam)

            // Mengembalikan hasil memuat data halaman
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            // Mengembalikan hasil kesalahan jika terjadi kesalahan
            return LoadResult.Error(e)
        }
    }

    companion object {
        // Konstanta untuk indeks halaman awal
        const val INITIAL_PAGE_INDEX = 1
    }
}
