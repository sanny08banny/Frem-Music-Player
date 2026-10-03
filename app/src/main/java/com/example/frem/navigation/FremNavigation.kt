package com.example.frem.navigation


sealed class FremDestination(
    val route: String
) {

    data object Music : FremDestination(
        "music"
    )

    data object NowPlaying : FremDestination(
        "now_playing"
    )

    data object Albums : FremDestination(
        "albums"
    )

    data object Artists : FremDestination(
        "artists"
    )

    data object Library : FremDestination(
        "library"
    )

    data object Profile : FremDestination(
        "profile"
    )

    data object Search : FremDestination(
        "search"
    )
}