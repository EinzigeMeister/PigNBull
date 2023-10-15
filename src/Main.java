import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class Main extends Frame implements ActionListener {
    TextField guessField;
    JButton guessButton, newGameButton, showWord, newGameRandomButton, rules;
    JTextArea tryLabel;
    JLabel[][] neverUsed;
    static private List<String> dict = new ArrayList();
    public static void loadDict(){
        try{
            File dictFile = new File("src/dict.txt");
            Scanner dictReader = new Scanner(dictFile);
            while(dictReader.hasNextLine()){
                String nextWord = dictReader.nextLine();
                if(nextWord.length()==5 || nextWord.length()==4) dict.add(nextWord);
            }
        } catch(FileNotFoundException e){
            System.out.println("oops");
        }
    }
    static private String word;
    static private int tries;
    public static String getWord(){
        return word;
    }
    public static void setWord(String wordSubmit){
        word = wordSubmit;
    }
    public static int getTries(){
        return tries;
    }
    public static void setTries(int numTries){
        tries = numTries;
    }
    public Main() {
        loadDict();
        JFrame frame = new JFrame("Pigs and Bulls");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLayout(new BorderLayout());
        Panel panel = new Panel();
        guessField = new TextField("Press New Game"); //guessField.getText();
        guessField.setEditable(false);
        guessButton = new JButton("Guess");
        guessButton.setEnabled(false);
        guessButton.addActionListener(this);
        showWord = new JButton("Show Word");
        showWord.addActionListener(this);
        showWord.setEnabled(false);
        rules = new JButton("rules");
        rules.addActionListener(this);
        panel.add(guessField);
        panel.add(guessButton);
        panel.add(showWord);
        panel.add(rules);
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        tryLabel = new JTextArea(20, 1);
        tryLabel.setEditable(false);
        tryLabel.setText("Tries:\tP   B");
        frame.getContentPane().add(tryLabel, BorderLayout.WEST);
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(this);
        newGameButton.setEnabled(true);
        newGameRandomButton = new JButton("New Game, random word");
        newGameRandomButton.addActionListener(this);
        newGameRandomButton.setEnabled(true);
        Panel newGamePanel = new Panel();
        newGamePanel.add(newGameButton);
        newGamePanel.add(newGameRandomButton);
        frame.getContentPane().add(newGamePanel, BorderLayout.SOUTH);
        Panel panel2 = new Panel(new GridLayout(6, 5));
        neverUsed = new JLabel[6][5];
        for (int x =0; x<5;x++) {
            for (int y = 0; y < 6; y++) {
                int charVal = 97 + 6 * x + y;
                if (charVal - 97 > 25) break;
                neverUsed[y][x] = new JLabel((char) (charVal) + "");
                panel2.add(neverUsed[y][x]);
            }
        }
        frame.getContentPane().add(panel2, BorderLayout.EAST);
        Panel centerPanel = new Panel(new GridLayout(2, 1));
        JLabel centerLabel = new JLabel("Never tried letters ---->");
        centerPanel.add(centerLabel);
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    public void newGame(boolean useRandomWord){
        for (int x =0; x<5;x++) {
            for (int y = 0; y < 6; y++) {
                int charVal = 97 + 6 * x + y;
                if (charVal - 97 > 25) break;
                neverUsed[y][x].setText((char) (charVal) + "");
            }
        }
        tryLabel.setText("Tries:\tP   B");
        setTries(0);
        guessButton.setEnabled(true);
        showWord.setEnabled(true);
        guessField.setEditable(true);
        guessField.setText("Enter secret word");
        play(useRandomWord);
    }
    public void actionPerformed(ActionEvent e) {
        //Read rules
        if(e.getSource() == rules){
            Random rand = new Random();
            String randomWord = dict.get(rand.nextInt(dict.size()));
            String ruleText = "When you select the New Game, have a friend enter a valid word.\n" +
                              "If you don't have any friends, or they keep choosing the word \""+randomWord+",\"\n" +
                              "select New Game, random word and I'll choose one of my liking.\n" +
                              "Guess what the word could be, and I'll let you know how many \n" +
                              "You got in the right spot, or just exist in the answer.\n" +
                              "Each pig (P) means there is a letter in your guess in the wrong spot\n" +
                              "Each bull (B) means there is a letter in your guess in the right spot.";
            JOptionPane.showMessageDialog(null, ruleText);
        }
        //New Game
        else if (e.getSource() == newGameButton) {
            newGame(false);
        }
        //New game with random word from dictionary
        else if (e.getSource() == newGameRandomButton) {
            newGame(true);
        }
        //Forget the word?
        else if (e.getSource() == showWord) {
            if (guessField.getText().equalsIgnoreCase("EricGetsToSeeTis")) JOptionPane.showMessageDialog(null, getWord());
            else JOptionPane.showMessageDialog(null, "Not telling! The word has " + getWord().length() +" letters.");
        }
        //Submitting Guess
        else if (e.getSource() == guessButton) {
            String guess = guessField.getText().toLowerCase();
            boolean validWord = checkWord(guess, false);
            if (guess.length() != getWord().length()) {
                validWord = false;
                JOptionPane.showMessageDialog(null, "Your guess must be the same length as the word");
            }
            if (validWord) {
                setTries(getTries() + 1);
                //Find Pigs & Bulls
                int pigs = 0, bulls = 0;
                for (int x = 0; x < guess.length(); x++) {
                    for (int y = 0; y < word.length(); y++) {
                        if (guess.charAt(x) == word.charAt(y)) {
                            if (x == y) bulls++;
                            else pigs++;
                        }
                    }
                }
                tryLabel.append("\n" + getTries() + ". " + guess + "\t" + pigs + "   " + bulls);
                for (int x = 0; x < guess.length(); x++) {
                    int charVal = guess.charAt(x) - 97;
                    neverUsed[charVal % 6][charVal / 6].setText("");
                }
                if (guess.equals(getWord())) {
                    endGame();
                }
            }
        }

    }
    public static void main(String[] args) {
        new Main();
    }
    /*
     * @param   useRandomWord   determines whether user entered word or random word generated
     */
    public void play(Boolean useRandomWord) {
        boolean validWord;
        String wordSubmit;
        if(useRandomWord){
            //Draws a random secret word from the dictionary
            do {
                Random rand = new Random();
                wordSubmit = dict.get(rand.nextInt(dict.size()));
                validWord = checkWord(wordSubmit, true);
            } while (!validWord);
        }
        else {
            do {
                // Get the secret word from the user.
                wordSubmit = JOptionPane.showInputDialog("Please enter your word");
                validWord = checkWord(wordSubmit, false);
            } while (!validWord);
        }
        setWord(wordSubmit.toLowerCase());
        guessField.setText("guess");
    }
    public void endGame(){
        if (getTries()==1) JOptionPane.showMessageDialog(null, "Lucky guess... or you cheated");
        //else if(getTries()==99) JOptionPane.showMessageDialog(null, "You're either not trying or never gonna get it.");
        else JOptionPane.showMessageDialog(null, "Congratulations! You completed the game in " + getTries() + " tries.");
        guessButton.setEnabled(false);
        guessField.setEditable(false);
        showWord.setEnabled(false);
        guessField.setText("Play Again?");
    }
    /*
     * @param   word            a word that is input by the user.
     * @param   useRandomWord   determines whether user entered word or random word generated
     * @returns chk             a boolean representing a valid or invalid entry
     */
    public static boolean checkWord(String word, boolean useRandomWord){
        //must be 4-5 letters
        String errorText="";
        if(Objects.equals(word, null)){
            errorText+="You must enter a word.\n";
            JOptionPane.showMessageDialog(null, errorText);
            return false;
        }
        boolean chk = true;
        word=word.toLowerCase();
        if(!dict.contains(word)){
            errorText+="You must choose a word in the dictionary I have. \n";
            chk = false;
        }
        if (word.length() < 4 || word.length() > 5) {
            errorText+="Your word must be 4 or 5 characters.\n";
            chk = false;
        }
        //letters can't repeat
        for (int x = 0; x < word.length() - 1; x++) {
            for (int y = x + 1; y < word.length(); y++) {
                if (word.charAt(x) == word.charAt(y)) {
                    errorText+="You cannot use the same letter multiple times.\n";
                    chk = false;
                    x=word.length(); y=word.length();
                }
            }
        }
        //can only contain letters
        boolean lettersOnly = word.matches("[a-z]+");
        if (!lettersOnly){
            errorText+="Only use real letters.\n";
            chk = false;
        }
        //displays all errors if it's not valid
        if(!chk && !useRandomWord) JOptionPane.showMessageDialog(null, errorText);
        return chk;
    }
}