package com.example.movies.presentation.profile.model.state

import android.net.Uri

interface ProfileState {
    val name: String
    val photoUri: Uri
}