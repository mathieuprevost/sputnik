package pl.touk.sputnik.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewTest {

    @Mock
    private ReviewFile file1;

    @Mock
    private ReviewFile file2;

    @Mock
    private ReviewFormatter reviewFormatter;

    private Review review;

    @BeforeEach
    void setUp() {
        review = new Review(asList(file1, file2), reviewFormatter);
    }

    @Test
    void shouldCountTotalViolationCountFromCommentsSize() {
        when(file1.getComments()).thenReturn(asList(mockComment(), mockComment()));
        when(file2.getComments()).thenReturn(singletonList(mockComment()));

        long totalViolationCount = review.getTotalViolationCount();

        assertThat(totalViolationCount).isEqualTo(3);
    }

    @Test
    void shouldCountViolationsPerSeverity() {
        Comment errorComment = mockComment(Severity.ERROR);
        Comment infoComment1 = mockComment(Severity.INFO);
        Comment infoComment2 = mockComment(Severity.INFO);
        when(file1.getComments()).thenReturn(asList(errorComment, infoComment1));
        when(file2.getComments()).thenReturn(singletonList(infoComment2));

        long totalViolationCount = review.getViolationCount(Severity.INFO);

        assertThat(totalViolationCount).isEqualTo(2);
    }

    @Test
    void shouldAddProblem() {
        review.addProblem("source", "problem");
        assertThat(review.getProblems().size()).isEqualTo(1);
    }

    @Test
    void shouldAddViolationToReviewFile() {
        Violation violation = new Violation("file1", 1, "Violation message", Severity.ERROR);
        ReviewResult reviewResult = new ReviewResult();
        reviewResult.getViolations().add(violation);

        when(file1.getReviewFilename()).thenReturn("file1");
        when(file1.getComments()).thenReturn(new ArrayList<>());
        when(reviewFormatter.formatComment("source", Severity.ERROR, "Violation message"))
                .thenReturn("Formatted comment");


        assertThat(file1.getComments()).hasSize(0);
        review.add("source", reviewResult);

        assertThat(file1.getComments()).hasSize(1);
        Comment addedComment = file1.getComments().get(0);
        assertThat(addedComment.getLine()).isEqualTo(1);
        assertThat(addedComment.getMessage()).isEqualTo("Formatted comment");
        assertThat(addedComment.getSeverity()).isEqualTo(Severity.ERROR);
    }

    private Comment mockComment() {
        return mock(Comment.class);
    }

    private Comment mockComment(Severity severity) {
        Comment comment = mock(Comment.class);
        when(comment.getSeverity()).thenReturn(severity);
        return comment;
    }

}