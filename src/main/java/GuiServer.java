
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField s1,s2,s3,s4, c1;
	Button serverChoice,clientChoice,sendAll,sendSpecified;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	HBox listItemsBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection; // make static??
	ChatInfo datapack = new ChatInfo();
	ArrayList<String> listOfClients = new ArrayList<String>();
	
	ListView<String> listItems, listItems2, listItems3;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");
		
		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});

				});
											
		});
		
		
		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");
		
		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
											primaryStage.setTitle("This is a client");
											clientConnection = new Client(data->{
							Platform.runLater(()->{
//								if(data.toString().contains("said:") || data.toString().contains("server:"))
									System.out.println(data);
									datapack = (ChatInfo) data;
									listOfClients = datapack.currentUsers;
									System.out.println("inside runlater " + datapack.message);
									System.out.println("inside runlater " + datapack.currentUsers);
									//System.out.println(data.toString());
									System.out.println("message length " + datapack.message.length());
									
									// made conditions for program to know whether to update client list or send a message
									if (datapack.currentUsers == null && datapack.message.length() > 0) {
										System.out.println("inside currentUsers null condition: "+datapack.message);
										listItems2.getItems().add(datapack.message);
										}
									else if (datapack.currentUsers != null && datapack.message.length() == 0){
										listItems3.getItems().clear();
										listItems3.getItems().add("ctrl (or command) + click to select");
										listItems3.getItems().add("multiple clients then click private chat");
										listItems3.getItems().add("Current users:");
										for(String c : listOfClients) {
											listItems3.getItems().add(c);
										}
									}
//								else
//									listItems3.getItems().add(data.toString());
								
							});
						});
							
									clientConnection.start();
		});
		
		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		
		startScene = new Scene(startPane, 800,800);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		listItems3 = new ListView<String>(); // for client list
		listItems3.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		c1 = new TextField();
		sendAll = new Button("Send All");
		sendAll.setOnAction(e->{
			datapack.message = c1.getText();
			clientConnection.send(datapack); 
			c1.clear();
			});
		
		sendSpecified = new Button("Send Private Chat");
		sendSpecified.setOnAction(e-> {
			// get selected indices from client list
			ObservableList<Integer> selectedIndices = listItems3.getSelectionModel().getSelectedIndices();
			
			// ObservableList isn't serializable, so make into ArrayList
			ArrayList<Integer> selected = new ArrayList<Integer>();
			
			for(int i : selectedIndices) {
				if(i > 2) // check if index valid
					selected.add(i);
			}
			
			System.out.println("Current indices" + selectedIndices);
			datapack.specificFlag = true;
//			ArrayList<String> specifiedUsers = new ArrayList<String>();
//			for(Integer i : selectedIndices) {
//				int index = i - 2;
//				specifiedUsers.add(listOfClients.get(index));
//				datapack.currentUsers = specifiedUsers;
//			}
			datapack.message = c1.getText();
			datapack.specifiedIndices = selected;
			clientConnection.send(datapack);
			// datapack.specificFlag = false;
			c1.clear();
		});
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		startScene.getRoot().setStyle("-fx-font-family: 'serif'");
		 
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		
		pane.setCenter(listItems);
		Scene serverScene = new Scene(pane, 500, 400);
		serverScene.getRoot().setStyle("-fx-font-family: 'serif'");
	
		return serverScene;
		
		
	}
	
	public Scene createClientGui() {
		
		listItemsBox = new HBox(30, listItems2, listItems3);
		clientBox = new VBox(10, c1,sendAll,sendSpecified,listItemsBox);
		clientBox.setStyle("-fx-background-color: blue");
		Scene clientScene = new Scene(clientBox, 500, 400);
		clientScene.getRoot().setStyle("-fx-font-family: 'serif'");
		
		return clientScene;
		
	}

}
