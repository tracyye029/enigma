package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author yuxinye
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    private Alphabet al = new Alphabet("ABCDE");

    private Permutation perm1 = new Permutation("(BAC) (D)", al);
    private Permutation perm2 = new Permutation("(BACDE)", al);
    private Permutation perm3 = new Permutation("", al);

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        perm1.permute('F');
        perm3.permute('F');
        perm1.invert('F');
        perm3.invert('F');
    }

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkPermuteChar() {
        assertEquals('A', perm3.permute('A'));
        assertEquals('B', perm3.permute('B'));
        assertEquals('C', perm3.permute('C'));
        assertEquals('D', perm3.permute('D'));
        assertEquals('E', perm3.permute('E'));

        assertEquals('A', perm1.permute('B'));
        assertEquals('C', perm1.permute('A'));
        assertEquals('B', perm1.permute('C'));
        assertEquals('D', perm1.permute('D'));
        assertEquals('E', perm1.permute('E'));
    }

    @Test
    public void checkPermuteInt() {
        assertEquals(0, perm3.permute(0));
        assertEquals(1, perm3.permute(1));
        assertEquals(2, perm3.permute(2));
        assertEquals(3, perm3.permute(3));
        assertEquals(4, perm3.permute(4));
        assertEquals(4, perm3.permute(-1));
        assertEquals(3, perm3.permute(-2));
        assertEquals(2, perm3.permute(-3));
        assertEquals(0, perm3.permute(5));

        assertEquals(2, perm1.permute(0));
        assertEquals(0, perm1.permute(1));
        assertEquals(1, perm1.permute(2));
        assertEquals(3, perm1.permute(3));
        assertEquals(4, perm1.permute(4));
        assertEquals(4, perm1.permute(-1));
        assertEquals(3, perm1.permute(-2));
        assertEquals(1, perm1.permute(-3));
        assertEquals(2, perm1.permute(5));

    }

    @Test
    public void checkInvertChar() {
        assertEquals('A', perm3.invert('A'));
        assertEquals('B', perm3.invert('B'));
        assertEquals('C', perm3.invert('C'));
        assertEquals('D', perm3.invert('D'));
        assertEquals('E', perm3.invert('E'));

        assertEquals('B', perm1.invert('A'));
        assertEquals('A', perm1.invert('C'));
        assertEquals('C', perm1.invert('B'));
        assertEquals('D', perm1.invert('D'));
        assertEquals('E', perm1.invert('E'));
    }

    @Test
    public void checkInvertInt() {
        assertEquals(0, perm3.invert(0));
        assertEquals(1, perm3.invert(1));
        assertEquals(2, perm3.invert(2));
        assertEquals(3, perm3.invert(3));
        assertEquals(4, perm3.invert(4));
        assertEquals(0, perm3.invert(5));
        assertEquals(4, perm3.invert(-1));
        assertEquals(2, perm3.invert(-3));

        assertEquals(1, perm1.invert(0));
        assertEquals(2, perm1.invert(1));
        assertEquals(0, perm1.invert(2));
        assertEquals(3, perm1.invert(3));
        assertEquals(4, perm1.invert(4));
        assertEquals(1, perm1.invert(5));
        assertEquals(4, perm1.invert(-1));
        assertEquals(0, perm1.invert(-3));
    }



    @Test
    public void checkSize() {
        assertEquals(5, perm1.size());
        assertEquals(5, perm2.size());
        assertEquals(5, perm3.size());
    }

    @Test
    public void checkDerangement() {
        assertEquals(false, perm1.derangement());
        assertEquals(true, perm2.derangement());
        assertEquals(false, perm3.derangement());
    }

    @Test
    public void checkAlphabet() {
        assertEquals(al, perm1.alphabet());
        assertEquals(al, perm2.alphabet());
        assertEquals(al, perm3.alphabet());
    }



}
