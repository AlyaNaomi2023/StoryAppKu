package com.example.storyappku.ui.otentikasi.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyappku.DataDummy
import com.example.storyappku.data.CustomDataRepository
import com.example.storyappku.data.model.response.LoginResponse
import com.example.storyappku.data.model.response.LoginResult
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var customDataRepository: CustomDataRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyResponseSuccess = DataDummy.generateDummyResponseLoginSuccess()
    private val dummyResponseErrorInvalidEmailFormat =
        DataDummy.generateDummyResponseLoginErrorInvalidEmailFormat()
    private val dummyResponseErrorUserNotFound =
        DataDummy.generateDummyResponseLoginErrorUserNotFound()
    private val dummyLoginResult = DataDummy.generateDummyLoginResult()
    private val dummyEmail = "akukamu123@gmail.com"
    private val dummyPassword = "12345678"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(customDataRepository)
    }

    @Test
    fun `when Login Success`() {
        // Arrange
        val expectedResponse = MutableLiveData<LoginResponse>()
        expectedResponse.value = dummyResponseSuccess
        Mockito.`when`(customDataRepository.login(dummyEmail, dummyPassword))
            .thenReturn(expectedResponse)

        // Act
        val actualResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        // Assert
        Mockito.verify(customDataRepository).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.value, actualResponse)
    }

    @Test
    fun `when Error User Not Found`() {
        // Arrange
        val expectedResponse = MutableLiveData<LoginResponse>()
        expectedResponse.value = dummyResponseErrorUserNotFound
        Mockito.`when`(customDataRepository.login("xx", "xx")).thenReturn(expectedResponse)

        // Act
        val actualResponse = loginViewModel.login("xx", "xx").getOrAwaitValue()

        // Assert
        Mockito.verify(customDataRepository).login("xx", "xx")
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(actualResponse, expectedResponse.value)
    }

    @Test
    fun `when Error Invalid Email Format`() {
        // Arrange
        val expectedResponse = MutableLiveData<LoginResponse>()
        expectedResponse.value = dummyResponseErrorInvalidEmailFormat
        Mockito.`when`(customDataRepository.login("akukamu123@gmail.com", dummyPassword))
            .thenReturn(expectedResponse)

        // Act
        val actualResponse =
            loginViewModel.login("akukamu123@gmail.com", dummyPassword).getOrAwaitValue()

        // Assert
        Mockito.verify(customDataRepository).login("akukamu123@gmail.com", dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(actualResponse, expectedResponse.value)
    }

    @Test
    fun `when Get User`() {
        // Arrange
        val expectedResponse = MutableLiveData<LoginResult>()
        expectedResponse.value = dummyLoginResult
        Mockito.`when`(customDataRepository.getUser()).thenReturn(expectedResponse)

        // Act
        val actualResponse = loginViewModel.getUser().getOrAwaitValue()

        // Assert
        Mockito.verify(customDataRepository).getUser()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(actualResponse, expectedResponse.value)
    }
}
