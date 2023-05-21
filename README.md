# java-chat-app
Java Chat App using local Server/Client socket communication

To run this application, you must have Maven installed on your device. Guides for installing Maven here: https://www.baeldung.com/install-maven-on-windows-linux-mac

Download the repository and open the root folder containing the pom.xml file, src folder, and target folder in your terminal. Then run command "mvn compile exec:java"
to run the application. Maven must be properly installed on your device for this command to run. 

The first screen you will see has two buttons, one "Server" button and one "Client" button. Since this project is only meant to run locally on your system, you must run 
multiple instances of the application at the same time. To run multiple instances in the terminal, open new tabs in the same directory (Command+T or Ctrl+T). 
Your first instance you will click the "Server" button to start the local server. For all of your other instances, you will click the "Client" buttons to make client 
instances which connect to the local server. 
