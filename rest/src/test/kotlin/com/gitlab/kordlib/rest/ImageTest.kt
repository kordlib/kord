package com.gitlab.kordlib.rest

import org.junit.jupiter.api.Test

class ImageTest {

    private fun getFormat(path: String): Image.Format {
        val imageType = path.split(".").last()
        return Image.Format.fromContentType("image/$imageType")
    }

    @Test
    fun `resolution test`() {
        val loader = Unit::class.java.classLoader

        val images = listOf("images/gitlab.png", "images/kord.png")
        val expectedRes = listOf(Image.Resolution(200, 200), Image.Resolution(964, 276))

        images.forEachIndexed { index, image ->
            val imageData = loader?.getResource(image)?.readBytes()!!
            val format = getFormat("images/kord.png")

            assert(Image.Resolution.fromImageData(imageData, format) == expectedRes[index])
        }
    }

}
