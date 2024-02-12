package pl.touk.sputnik.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        assertThat(review.getProblems()).containsExactly("source: problem");
    }

    @Test
    void shouldAdd() {
        ReviewResult reviewResult = mock(ReviewResult.class);
        Violation violation = mock(Violation.class);
        when(reviewResult.getViolations()).thenReturn(singletonList(violation));
        when(violation.getFilenameOrJavaClassName()).thenReturn("file1");
        when(file1.getReviewFilename()).thenReturn("file1");

        review.add("source", reviewResult);

        assertThat(file1.getComments()).hasSize(1);
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