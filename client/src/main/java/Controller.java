import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ListView;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    ListView serverFilesList;

    @FXML
    ListView clientFilesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(()  -> {
                try{
                    AbstractMessage message = Network.getMessage();
                    if (message instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) message;
                        Files.write(Paths.get("client_storage/" + fileMessage.getFileName()), fileMessage.getData(), StandardOpenOption.CREATE);
                        refreshFilesLists();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    Network.stop();
                }
        });
        t.setDaemon(true);
        t.start();
        refreshFilesLists();
    }

    public static void updateUI(Runnable r){
        if(Platform.isFxApplicationThread())
            r.run();
        else
            Platform.runLater(r);
    }

    public void refreshFilesLists(){
        updateUI(() -> {
            try {
                clientFilesList.getItems().clear();
                serverFilesList.getItems().clear();
                Files.list(Paths.get("client_storage"))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> clientFilesList.getItems().add(o));
                Files.list(Paths.get("server_storage"))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> serverFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void upload(ActionEvent actionEvent) throws IOException{
        if(clientFilesList.getSelectionModel().getSelectedItem() != null){
            if(clientFilesList.getSelectionModel().getSelectedItems() != null) {
                Network.sendMessage(new FileMessage(Paths.get("client_storage/" + clientFilesList.getSelectionModel().getSelectedItem().toString())));
                refreshFilesLists();
            }
        }
    }

    public void deleteFromClient(ActionEvent actionEvent) {
        if(clientFilesList.getSelectionModel().getSelectedItem() != null){
            try {
                Files.delete(Paths.get("client_storage/" + clientFilesList.getSelectionModel().getSelectedItem().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void download(ActionEvent actionEvent) {
        if (serverFilesList.getSelectionModel().getSelectedItem() != null) {
            CommandMessage commandMessage = new CommandMessage(serverFilesList.getSelectionModel().getSelectedItem().toString(), CommandType.DOWNLOAD);
            Network.sendMessage(commandMessage);
        }
    }

    public void deleteFromServer(ActionEvent actionEvent) {
        if(serverFilesList.getSelectionModel().getSelectedItem() != null){
            CommandMessage commandMessage = new CommandMessage(serverFilesList.getSelectionModel().getSelectedItem().toString(), CommandType.DELETE);
            Network.sendMessage(commandMessage);
        }
    }

    public void refresh(ActionEvent actionEvent) {
        refreshFilesLists();
    }
}
