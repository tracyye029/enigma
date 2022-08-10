package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author yuxinye
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();

        if (_input.hasNext("^\\*")) {
            while (_input.hasNextLine()) {
                String nextLine = _input.nextLine();
                if (nextLine.isEmpty()) {
                    _output.println();
                } else if (nextLine.charAt(0) == '*') {
                    setUp(machine, nextLine.substring(1));
                } else {
                    nextLine = nextLine.replaceAll("\\s", "");
                    String converted = machine.convert(nextLine);
                    printMessageLine(converted);
                }
            }
        } else {
            throw error("Missing *. Invalid start of the input.");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            ArrayList<Rotor> allRotors = new ArrayList<>();
            String alphabet = _config.next();
            _alphabet = new Alphabet(alphabet);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();

            while (_config.hasNext()) {
                Rotor r = readRotor();
                if (allRotors.contains(r.name())) {
                    throw new EnigmaException("Rotor is duplicated");
                }
                allRotors.add(r);
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String notches = _config.next();

            String cycles = "";
            while (_config.hasNext("\\(.*\\)")) {
                cycles = cycles + _config.next() + " ";
            }

            if (notches.charAt(0) == 'M') {
                return new MovingRotor(name,
                        new Permutation(cycles, _alphabet),
                        notches.substring(1));
            } else if (notches.charAt(0) == 'N') {
                return new FixedRotor(name,
                        new Permutation(cycles, _alphabet));
            } else if (notches.charAt(0) == 'R') {
                return new Reflector(name,
                        new Permutation(cycles, _alphabet));
            } else {
                throw new EnigmaException("Invalid rotor type.");
            }
        } catch (NoSuchElementException excp) {
            throw error("Invalid rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] rotors = new String[M.numRotors()];
        Scanner newScanner = new Scanner(settings);

        int count = 0;
        while (count < rotors.length) {
            rotors[count] = newScanner.next();
            count++;
        }
        M.insertRotors(rotors);

        String settingofRotors = newScanner.next();
        M.setRotors(settingofRotors);

        String ringSettings = "";
        if (newScanner.hasNext() && !newScanner.hasNext("\\(.*\\)")) {
            ringSettings = newScanner.next();
            M.setRingSetting(ringSettings);
        }

        String cycles = "";
        while (newScanner.hasNext("\\(.*\\)")) {
            cycles = cycles + newScanner.next() + " ";
        }

        M.setPlugboard(new Permutation(cycles, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 5) {
            if (msg.length() - i <= 5) {
                _output.print(msg.substring(i));
                _output.print('\n');
            } else {
                _output.print(msg.substring(i, i + 5) + " ");
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
