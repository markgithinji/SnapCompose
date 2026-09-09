package com.example.composegallery.feature.home.fakes

import com.example.composegallery.core.common.StringProvider

class FakeStringProvider : StringProvider {
    override fun get(resId: Int, vararg args: Any): String {
        return if (args.isEmpty()) "Fake string" else "Fake string with args"
    }
}
