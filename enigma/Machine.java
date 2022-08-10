package enigma;

import java.util.Collection;
import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author yuxinye
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _rotors = new ArrayList<>();
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        ArrayList<String> allRotorNames = new ArrayList<>();
        for (Rotor rotor : _allRotors) {
            allRotorNames.add(rotor.name());
        }
        _rotors = new ArrayList<>();
        for (String r : rotors) {
            if (!allRotorNames.contains(r)) {
                throw new EnigmaException("Can't find the rotor");
            } else if (_rotors.contains(r)) {
                throw new EnigmaException("Rotor already in the slot");
            }

            for (Rotor rotor : _allRotors) {
                if (rotor.name().equals(r)) {
                    _rotors.add(rotor);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Rotor setting has wrong length");
        }
        for (int i = 1; i < numRotors(); i++) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw new EnigmaException("No matching character in Alphabet");
            }
            _rotors.get(i).set(setting.charAt(i - 1));
        }
    }

    /** Set optional ring settings.
     * RINGSETTING is a string of ring settings.  */
    void setRingSetting(String ringSetting) {
        if (ringSetting.length() != numRotors() - 1) {
            throw new EnigmaException("Ring setting has wrong length");
        }
        for (int i = 1; i < _numRotors; i++) {
            if (!_alphabet.contains(
                    ringSetting.charAt(i - 1))) {
                throw new EnigmaException(
                        "Ringsetting's character not in Alphabet");
            }
            _rotors.get(i).setRing(ringSetting.charAt(i - 1));
        }
    }


    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        for (int i = 0; i < _numRotors; i++) {
            if (_rotors.get(i).rotates()) {
                if (i == _numRotors - 1) {
                    _rotors.get(i).advance();
                } else if (i < _numRotors - 1 && _rotors.get(i + 1).atNotch()) {
                    _rotors.get(i).advance();
                    _rotors.get(i + 1).advance();
                    i++;
                }
            }
        }

        c = _plugboard.permute(c);
        for (int i = _numRotors - 1; i >= 0; i--) {
            c = _rotors.get(i).convertForward(c);
        }
        for (int i = 1; i < _numRotors; i++) {
            c = _rotors.get(i).convertBackward(c);
        }
        c = _plugboard.invert(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String newMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            int converted = convert(_alphabet.toInt(msg.charAt(i)));
            newMsg = newMsg + _alphabet.toChar(converted);
        }
        return newMsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private final int _numRotors;
    /** Number of pawls. */
    private final int _pawls;
    /** All rotors in the machine. */
    private ArrayList<Rotor> _rotors;
    /** All available rotors. */
    private Collection<Rotor> _allRotors;
    /** A plugboard. */
    private Permutation _plugboard;
}
