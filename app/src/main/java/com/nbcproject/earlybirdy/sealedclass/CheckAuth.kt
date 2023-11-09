package com.nbcproject.earlybirdy.sealedclass

sealed class CheckAuth {
    object SuccessAuth : CheckAuth()
    object ElseException : CheckAuth()
}