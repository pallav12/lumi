package com.desktop.lumi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform