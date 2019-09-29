package controllers;

import events.SwitchSceneEvent;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import models.*;
import views.ChunkCellFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ChunkView extends Controller {

    @FXML ListView<Chunk> chunksListView;

    @FXML TextArea searchResult;

    private Synthesizer synthesizer;

    @FXML
    public void initialize() {
        ChunkManager.getInstance();
        chunksListView.setItems(ChunkManager.getInstance().getItems());
        chunksListView.setCellFactory(new ChunkCellFactory());
        ChunkManager.getInstance().getItems().addListener(new ListChangeListener<Chunk>() {
            @Override
            public void onChanged(Change<? extends Chunk> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        chunksListView.getSelectionModel().select(c.getFrom());
                    }
                }
            }
        });

        synthesizer = new EspeakSynthesizerBuilder().setVoice(EspeakSynthesizer.Voice.DEFAULT).build();
        // TODO - Load Wikit Result
        try {
            FileReader result = new FileReader(new File("temp/search.txt"));
            String string = new String();
            int i;
            while ((i = result.read()) != -1) {
                string = string.concat(Character.toString((char) i));
            }
            string = string.trim();
            searchResult.setText(string);

//            System.out.println("\n\nResult: "+string+"\n\n");
//            searchResult.setText("An apple is a sweet, edible fruit produced by an apple tree (Malus domestica). Apple " +
//                    "trees are cultivated worldwide and are the most widely grown species in the genus Malus. The tree " +
//                    "originated in Central Asia, where its wild ancestor, Malus sieversii, is still found today. Apples " +
//                    "have been grown for thousands of years in Asia and Europe and were brought to North America by " +
//                    "European colonists. Apples have religious and mythological significance in many cultures, including" +
//                    " Norse, Greek and European Christian traditions."); // TODO - Remove
        } catch (IOException e) {
            e.printStackTrace();
            // TODO - Handle exception
        }
    }

    @FXML public void pressBack() {
        if (!ChunkManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    String.format("If you go back your Snippets will not be saved. Do you wish to continue?"),
                    ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                listener.handle(new SwitchSceneEvent(this, "/SearchView.fxml"));
            }
        } else {
            listener.handle(new SwitchSceneEvent(this, "/SearchView.fxml"));
        }
    }

    @FXML public void pressPreview() {
        if (checkWords(searchResult.getSelectedText())) {
            // TODO - Word count validation (20-40)
            // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button)
            synthesizer.preview(searchResult.getSelectedText());
        }
    }

    @FXML public void pressSaveSnippet() {
        if (checkWords(searchResult.getSelectedText())) {
            // TODO - Word count validation (20-40)
            ChunkBuilder chunkBuilder = ChunkManager.getInstance().getBuilder();
            chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
            ChunkManager.getInstance().create(chunkBuilder);
        }
    }

    private boolean checkWords(String string) {
        StringTokenizer tokenizer = new StringTokenizer(string);
        if (tokenizer.countTokens() > 40) {
            System.out.println("Popup: more than 40 words");

            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select less than 40 words to synthesize text");
            alert.showAndWait();

            return false;
        }
        return true;
    }

    @FXML public void pressPlayback() {
        recursivePlayback(chunksListView.getItems().iterator());
    }

    private void recursivePlayback(Iterator<Chunk> iterator) {
        if (iterator.hasNext()) {
            Chunk chunk = iterator.next();

            chunksListView.getSelectionModel().select(chunk);

            File audioFile = new File(chunksListView.getSelectionModel().getSelectedItem().getFolder(), "audio.wav");

            Media media = new Media(audioFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setAutoPlay(true);
            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Ended?");
                    recursivePlayback(iterator);
                }
            });
        }
    }

    @FXML public void pressPreviewSnippet() {
        // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button, click on a different snippet)
        File audioFile = new File(chunksListView.getSelectionModel().getSelectedItem().getFolder(), "audio.wav");

        Media media = new Media(audioFile.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setAutoPlay(true);
    }

    @FXML public void pressDelete() {
        ChunkManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
    }

    @FXML public void pressNext() {
        if (ChunkManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please make a snippet to continue");
            alert.showAndWait();
            return;
        }
        listener.handle(new SwitchSceneEvent(this, "/NameView.fxml"));
    }
}
