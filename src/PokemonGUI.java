import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PokemonGUI implements ActionListener { // Guess the pokemon
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel northPanel;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JLabel southLabel;
    private JLabel displayPreviousAnswer;
    private JLabel displayImage; // still missing image
    private JLabel answerStreakCounter;
    private JLabel chancesRemaining;
    private JTextField inputAnswer;

    private JButton submitAnswer;
    private JButton skipAnswer; // doesn't add correct streak counter; deduct one life out of three
    private String pokemonName;
    private int random;
    private int answerStreak = 0;
    private int lives = 3;

    public static void main(String[] args) {
        PokemonGUI p = new PokemonGUI();
    }
    public PokemonGUI() {
        setUpGUI();
        pullFromAPI();
    }

    private void setUpGUI() {
        System.out.println("gui");
        mainFrame = new JFrame();
        mainPanel = new JPanel(new BorderLayout());
        northPanel = new JPanel();
        centerPanel = new JPanel();
        southPanel = new JPanel();
        chancesRemaining = new JLabel("Chances remaining: 3"); // north
        answerStreakCounter = new JLabel("Answer Streak: 0"); // north
        displayImage = new JLabel(); // center
        southLabel = new JLabel("Input answer here:"); // south, to show "input answer here:"
        displayPreviousAnswer = new JLabel(); // north
        inputAnswer = new JTextField(); // south
        submitAnswer = new JButton("Submit"); // south
        submitAnswer.addActionListener(this);
        skipAnswer = new JButton("Skip"); // south
        skipAnswer.addActionListener(this);

        mainFrame.setSize(1000, 800);
        int borderSize = 50;

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        northPanel.setLayout(new GridLayout(2,2));
        northPanel.add(answerStreakCounter);
        northPanel.add(chancesRemaining);
        northPanel.add(displayPreviousAnswer);

        centerPanel.add(displayImage);

        southPanel.setLayout(new GridLayout(1,4));
        southPanel.add(southLabel);
        southPanel.add(inputAnswer);
        southPanel.add(submitAnswer);
        southPanel.add(skipAnswer);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);


    }

    private void pullFromAPI ()  {
        // pokemon gui: display pokemon + info, if there's time, make a pokemon battle game
        random = 0;
        int pastValue = 0;
        random = ((int)(Math.random()*1025) + 1);
        while (pastValue == random){
            random = ((int)(Math.random()*1025) + 1);
        }
        pastValue = random;
        String output = "idk";
        String totalJson = "";
        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + random + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) { // not successful if condition

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
//                System.out.println(output);
                totalJson += output;
            }

            conn.disconnect();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONParser parser = new JSONParser();
//        int i = 10;
        //System.out.println(str);
        try {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) parser.parse(totalJson);
//            System.out.println(jsonObject);
//            System.out.println("Name: " + jsonObject.get("name"));
            pokemonName = (String) jsonObject.get("name");

        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            System.out.println("adding image");
            addImage();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void addImage() throws IOException {
        try {
            String path = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + random + ".png";
            //path = "https://i.pinimg.com/originals/07/16/ba/0716ba54fe3b77b3a5b0b16c7bc33389.png";

            System.out.println("image path: " + path);
            URL url = new URL(path);
            BufferedImage inputImageBuff = ImageIO.read(url.openStream());

            if (inputImageBuff != null) {
                System.out.println("image is not null yay");
//                inputImage = new ImageIcon(inputImageBuff.getScaledInstance(800, 700, Image.SCALE_SMOOTH));

                ImageIcon inputImage = new ImageIcon(inputImageBuff.getScaledInstance(500, 500, Image.SCALE_SMOOTH));
            // = new JLabel();
                if (inputImage != null) {
//                    imageLabel = new JLabel(inputImage);
                    System.out.println("adding image to label");
                    displayImage = new JLabel(inputImage);

                } else {
//                    imageLabel =new JLabel(new ImageIcon(ErrorImage.getScaledInstance(800, 589, Image.SCALE_SMOOTH)));
                    System.out.println("AHHHHHHHHH");
                }
                System.out.println("updating the gui stuff");
                centerPanel.removeAll();
                centerPanel.repaint();

                centerPanel.add(displayImage);
                mainPanel.add(centerPanel, BorderLayout.CENTER);

            }
            else{
//                imageLabel =new JLabel(new ImageIcon(ErrorImage.getScaledInstance(800, 589, Image.SCALE_SMOOTH)));
                System.out.println("AHHHHHH 2");
            }

        } catch (IOException e) {
            System.out.println(e);
        }
        mainFrame.setVisible(true);

    }

    public void actionPerformed(ActionEvent e){
        Object buttonClicked = e.getSource();
        if (lives > 0) {
//        System.out.println("BUTTON CLICKED");
            if (buttonClicked == submitAnswer) {
                String answer = inputAnswer.getText();
                //            System.out.println("answer detected");
                if (answer.equals(pokemonName)) {
                    answerStreak++;
                    answerStreakCounter.setText("Answer Streak: " + String.valueOf(answerStreak));
                    displayPreviousAnswer.setText("Correct! The name of the previous Pokémon is " + pokemonName);
                    inputAnswer.setText("");
                    pullFromAPI();
                } else {
                    //                System.out.println("\"" + answer + "\"");
                    //                System.out.println("\"" + pokemonName + "\"");
                    lives--;
                    chancesRemaining.setText("Chances remaining: " + String.valueOf(lives));
                    if (lives == 0) {
                        displayPreviousAnswer.setText("GAME OVER!!! The name of the previous Pokémon is " + pokemonName);
                        inputAnswer.setText("");
                    } else {
                        displayPreviousAnswer.setText("The name of the previous Pokémon is " + pokemonName);
                        inputAnswer.setText("");
                        pullFromAPI();
                    }
                }
            }
            if (buttonClicked == skipAnswer) {
                lives--;
                chancesRemaining.setText("Chances remaining: " + String.valueOf(lives));
                if (lives == 0) {
                    displayPreviousAnswer.setText("GAME OVER!!! The name of the previous Pokémon is " + pokemonName);
                    inputAnswer.setText("");
                } else {
                    displayPreviousAnswer.setText("The name of the previous Pokémon is " + pokemonName);
                    inputAnswer.setText("");
                    pullFromAPI();
                }
            }
        }
    }
}

