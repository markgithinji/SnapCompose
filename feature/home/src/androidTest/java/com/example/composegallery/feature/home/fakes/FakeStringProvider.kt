package com.example.composegallery.feature.home.fakes

import com.example.composegallery.core.common.StringProvider

class FakeStringProvider : StringProvider {
    override fun get(resId: Int, vararg args: Any): String {
        return "Fake string"
    }
}
