package com.totem.ia.data.network

sealed class NetworkResult<T> {
    class Success<T>(val data: T) : NetworkResult<T>()
    class Error<T>(val message: String, val data: T? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}
