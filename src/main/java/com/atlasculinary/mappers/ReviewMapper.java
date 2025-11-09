package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AddReviewRequest;
import com.atlasculinary.dtos.UpdateReviewRequest;
import com.atlasculinary.dtos.ReviewReplyRequest;
import com.atlasculinary.dtos.ReviewDto;
import com.atlasculinary.entities.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "reviewerAccount.accountId", target = "reviewerAccountId")
    @Mapping(
            target = "reviewerUsername",
            expression = "java(" +
                    "review != null && review.getReviewerAccount() != null " +
                    "? com.atlasculinary.utils.NameUtil.resolveName(" +
                    "review.getReviewerAccount().getFullName(), " +
                    "review.getReviewerAccount().getEmail()) " +
                    ": \"Anonymous\"" +
                    ")"
    )
    @Mapping(source = "restaurant.restaurantId", target = "restaurantId")
    @Mapping(source = "dish.dishId", target = "dishId")
    ReviewDto toDto(Review review);

    List<ReviewDto> toDtoList(List<Review> reviewList);

    Review toEntity(AddReviewRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateReviewRequest request, @MappingTarget Review targetEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReply(ReviewReplyRequest request, @MappingTarget Review targetEntity);

}