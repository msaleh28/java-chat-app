import java.io.Serializable;
import java.util.ArrayList;

import javafx.collections.ObservableList;


public class ChatInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String username;
	String message;
	boolean specificFlag = false;
	ArrayList<String> currentUsers;
	ArrayList<Integer> specifiedIndices;
}
