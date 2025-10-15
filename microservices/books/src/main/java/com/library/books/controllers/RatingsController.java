package com.library.books.controllers;

import com.library.books.api.RatingsApi;
import com.library.books.services.RatingService;
import com.library.dtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RatingsController implements RatingsApi {

    private final RatingService ratingService;

    @Override
    public ResponseEntity<PaginatedRatingResponseDTO> getRatingsByFilters(Integer bookId, Integer userProfileId, Integer min, Integer max, Integer page) {
        log.info("REST request to get ratings by filters - bookId: {}, userProfileId: {}, min: {}, max: {}, page: {}",
                 bookId, userProfileId, min, max, page);
        PaginatedRatingResponseDTO response = ratingService.getRatingsByFilters(bookId, userProfileId, min, max, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RatingResponseDTO> createRating(RatingRequestDTO ratingRequestDTO) {
        log.info("REST request to create Rating for book: {}", ratingRequestDTO.getBookId());
        RatingResponseDTO response = ratingService.createRating(ratingRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<RatingResponseDTO> getRatingById(Integer id) {
        log.info("REST request to get Rating by ID: {}", id);
        RatingResponseDTO response = ratingService.getRatingById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RatingResponseDTO> updateRating(Integer id, RatingRequestDTO ratingRequestDTO) {
        log.info("REST request to update Rating: {}", id);
        RatingResponseDTO response = ratingService.updateRating(id, ratingRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteRating(Integer id) {
        log.info("REST request to delete Rating: {}", id);
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
