package enigma;
import java.util.List;
import java.util.ArrayList;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author yuxinye
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        String newCycles = cycles.trim();
        newCycles = newCycles.replace(")(", " ").replace(
                "(", "").replace(")", "");
        String[] temp = newCycles.split(" ");

        _cycles = new ArrayList<>();
        for (String t:temp) {
            _cycles.add(t);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        if (_cycles.isEmpty()) {
            return p;
        }

        char c = _alphabet.toChar(wrap(p));
        char nextChar;
        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).length(); j++) {
                if (_cycles.get(i).charAt(j) == c) {
                    if (j == _cycles.get(i).length() - 1) {
                        nextChar = _cycles.get(i).charAt(0);
                    } else {
                        nextChar = _cycles.get(i).charAt(j + 1);
                    }
                    return _alphabet.toInt(nextChar);
                }
            }
        }
        return wrap(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        if (_cycles.isEmpty()) {
            return c;
        }

        char p = _alphabet.toChar(wrap(c));
        char lastChar;
        for (int i = 0; i < _cycles.size(); i++) {
            for (int j = 0; j < _cycles.get(i).length(); j++) {
                if (_cycles.get(i).charAt(j) == p) {
                    if (j == 0) {
                        lastChar = _cycles.get(i).charAt(
                                _cycles.get(i).length() - 1);
                    } else {
                        lastChar = _cycles.get(i).charAt(j - 1);
                    }
                    return _alphabet.toInt(lastChar);
                }
            }
        }
        return wrap(c);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        return _alphabet.toChar(permute(index));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _alphabet.toInt(c);
        return _alphabet.toChar(invert(index));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int count = 0;

        for (int i = 0; i < _cycles.size(); i++) {
            count += _cycles.get(i).length();
            if (_cycles.get(i).length() == 1) {
                return false;
            }
        }

        return count == _alphabet.size() || count != 0;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** A list of all cycles. */
    private List<String> _cycles;
}
