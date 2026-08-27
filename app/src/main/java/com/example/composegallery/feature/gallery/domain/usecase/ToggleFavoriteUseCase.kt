package com.example.composegallery.feature.gallery.domain.usecase

import com.example.composegallery.feature.gallery.domain.model.Photo
import com.example.composegallery.feature.gallery.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(photo: Photo) {
        val isFavorite = favoriteRepository.isFavoriteOneShot(photo.id)
        if (isFavorite) {
            favoriteRepository.removeFavorite(photo.id)
        } else {
            favoriteRepository.addFavorite(photo)
        }
    }
}
