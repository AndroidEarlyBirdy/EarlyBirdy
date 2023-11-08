package com.nbcproject.earlybirdy.sealedclass

sealed class CheckAuth {
    object SuccessAuth : CheckAuth()
    object InvalidCredential : CheckAuth()
    object ElseException : CheckAuth()
}