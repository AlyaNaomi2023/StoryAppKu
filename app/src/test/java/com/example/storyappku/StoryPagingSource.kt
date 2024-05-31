package com.example.storyappku

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyappku.data.model.response.ListStoryItem

/**
 * PagingSource for stories.
 */
class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {

    companion object {
        /**
         * Create a snapshot of [PagingData] from a list of story items.
         */
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    /**
     * Get the refresh key for the paging state.
     */
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    /**
     * Load function for loading pages of data.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        // For simplicity, returning an empty page with a single page index
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = 1)
    }
}
