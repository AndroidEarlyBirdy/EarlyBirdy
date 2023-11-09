package com.nbcproject.earlybirdy.sealedclass

sealed class CheckChangePassword {
    object ChangeFailed : CheckChangePassword()
}