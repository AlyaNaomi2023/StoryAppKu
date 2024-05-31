package com.example.storyappku.ui.otentikasi.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyappku.DataDummy
import com.example.storyappku.data.CustomDataRepository
import com.example.storyappku.data.model.response.RegisterResponse
import com.example.storyappku.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var customDataRepository: CustomDataRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dummyResponseSuccess = DataDummy.generateDummyResponseRegisterSuccess()
    private val dummyResponseFailed = DataDummy.generateDummyResponseRegisterFailed()
    private val dummyParamName = "Alya Naomi"
    private val dummyParamEmail = "akukamu123@gmail.com.com"
    private val dummyParamPassword = "12345678"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(customDataRepository)
    }

    @Test
    fun `when Register Success`() {
        // Arrange
        val expectedResponse = MutableLiveData<RegisterResponse>()
        expectedResponse.value = dummyResponseSuccess
        Mockito.`when`(
            customDataRepository.register(
                dummyParamName,
                dummyParamEmail,
                dummyParamPassword
            )
        ).thenReturn(expectedResponse)

        // Act
        val actualResponse =
            registerViewModel.registerUser(dummyParamName, dummyParamEmail, dummyParamPassword)
                .getOrAwaitValue()

        // Assert
        Mockito.verify(customDataRepository)
            .register(dummyParamName, dummyParamEmail, dummyParamPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(dummyResponseSuccess, actualResponse)
    }

    @Test
    fun `when Register Failed`() {
        // Arrange
        val expectedResponse = MutableLiveData<RegisterResponse>()
        expectedResponse.value = dummyResponseFailed
        Mockito.`when`(customDataRepository.register(dummyParamName, dummyParamEmail, ""))
            .thenReturn(expectedResponse)

        // Act
        val actualResponse =
            registerViewModel.registerUser(dummyParamName, dummyParamEmail, "").getOrAwaitValue()

        // Assert
        Mockito.verify(customDataRepository).register(dummyParamName, dummyParamEmail, "")
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(dummyResponseFailed, actualResponse)
    }
}
