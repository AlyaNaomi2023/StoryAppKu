package com.example.storyappku.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyappku.DataDummy
import com.example.storyappku.MainDispatcherRule
import com.example.storyappku.data.CustomDataRepository
import com.example.storyappku.data.model.response.ListStoryItem
import com.example.storyappku.data.model.response.StoryResponse
import com.example.storyappku.getOrAwaitValue
import com.example.storyappku.ui.story.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var customDataRepository: CustomDataRepository
    private lateinit var mainViewModel: MainViewModel
    private val dummyStory = DataDummy.generateDummyListStoryItem()
    private val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
    private val dummyAddStoryResponse = DataDummy.generateDummyAddStoryResponse()
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(customDataRepository)
    }

    @Test
    fun `when Get Story Success`() = runTest {
        // Prepare empty data and LiveData
        val emptyData: PagingData<ListStoryItem> = PagingData.empty()
        val expectedEmptyStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedEmptyStory.value = emptyData

        // Mock the behavior of getAllStory on customDataRepository
        Mockito.`when`(customDataRepository.getAllStory(token)).thenReturn(expectedEmptyStory)

        // Call the actual method
        val actualEmptyStory: PagingData<ListStoryItem> =
            mainViewModel.getAllStory(token).getOrAwaitValue()

        // Verify the results using Mockito.verify
        Mockito.verify(customDataRepository).getAllStory(token)

        // Assertions
        Assert.assertNotNull(actualEmptyStory)

        val differEmpty = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differEmpty.submitData(actualEmptyStory)
        advanceUntilIdle()

        Assert.assertEquals(0, differEmpty.snapshot().size)
    }

    @Test
    fun `when Get List Story Maps Success`() = runTest {
        // Prepare expected response and LiveData
        val expectedResponse = MutableLiveData<StoryResponse>()
        expectedResponse.postValue(dummyStoryResponse)
        Mockito.`when`(customDataRepository.getListMapsStory(token)).thenReturn(expectedResponse)

        // Call the actual method
        val actualResponse = mainViewModel.getListMapsStory(token).getOrAwaitValue()

        // Verify the results using Mockito.verify
        Mockito.verify(customDataRepository).getListMapsStory(token)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.value, actualResponse)
    }

    @Test
    fun `when Get List Story Maps Fails - Network Error`() = runTest {
        // Prepare expected error
        val expectedError = Throwable("Network error")
        Mockito.`when`(customDataRepository.getListMapsStory(token)).thenAnswer { throw expectedError }

        // Try to call the actual method and catch the expected error
        try {
            mainViewModel.getListMapsStory(token).getOrAwaitValue()
            Assert.fail("Exception not thrown")
        } catch (e: Throwable) {
            Assert.assertEquals(expectedError, e)
        }

        // Verify the results using Mockito.verify
        Mockito.verify(customDataRepository).getListMapsStory(token)
    }

    @Test
    fun `when Paging Data Loaded Successfully`() = runTest {
        // Prepare expected data and LiveData
        val expectedData: PagingData<ListStoryItem> = PagingData.from(dummyStory)
        val expectedLiveData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedLiveData.value = expectedData

        // Mock the behavior of getAllStory on customDataRepository
        Mockito.`when`(customDataRepository.getAllStory(token)).thenReturn(expectedLiveData)

        // Call the actual method
        val actualData: PagingData<ListStoryItem> = mainViewModel.getAllStory(token).getOrAwaitValue()

        // Verify the results using Mockito.verify
        Mockito.verify(customDataRepository).getAllStory(token)

        // Assertions
        Assert.assertNotNull(actualData)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)
        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot().get(0))
    }

    @Test
    fun `when Paging Data Loading Fails - Network Error`() = runTest {
        // Prepare expected error
        val expectedError = Throwable("Network error")
        Mockito.`when`(customDataRepository.getAllStory(token)).thenAnswer { throw expectedError }

        // Try to call the actual method and catch the expected error
        try {
            mainViewModel.getAllStory(token).getOrAwaitValue()
            Assert.fail("Exception not thrown")
        } catch (e: Throwable) {
            Assert.assertEquals(expectedError, e)
        }

        // Verify the results using Mockito.verify
        Mockito.verify(customDataRepository).getAllStory(token)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
