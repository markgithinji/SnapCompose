package com.example.composegallery.feature.gallery.ui.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlurHashDecoderTest {

    @Test
    fun decode_validHash_returnsBitmap() {
        val hash = "LKO2?U%2Tw=w]~RBVZRi};RPxuwH"
        val bitmap = BlurHashDecoder.decode(hash, 20, 12)

        assertThat(bitmap).isNotNull()
        assertThat(bitmap?.width).isEqualTo(20)
        assertThat(bitmap?.height).isEqualTo(12)
    }

    @Test
    fun decode_invalidHash_returnsNull() {
        val hash = "abc"
        val bitmap = BlurHashDecoder.decode(hash, 20, 12)

        assertThat(bitmap).isNull()
    }

    @Test
    fun decode_nullHash_returnsNull() {
        val bitmap = BlurHashDecoder.decode(null, 20, 12)

        assertThat(bitmap).isNull()
    }
}
