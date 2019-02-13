# CZ3004 MDP 2018 S1 Group 11

## Android application for ShadowBotz

### About
The Android mobile application acts as the remote controller for Robot. It is capable of bi-directional data transfer between Android device and Robot. The data being transferred are command such as start and stop, waypoint, the current location of the robot and arrows in text strings (JSON). This application can initiate scanning, selection and establish a connection with a nearby Bluetooth device. When the connection between the mobile application and Raspberry Pi drops, the mobile application will automatically re-establish the connection.
In addition, the mobile application allows the user to manoeuvre the robot through an interactive user interface. Alternatively, the robot can navigate through the map autonomously. The mobile application will display real-time status of the robot. The embedded 2D grid map allows the user to determine the current location of the robot, enter the pre-defined waypoint and the starting coordinates. Grid map can be updated automatically or manually by toggling auto-update switch.
Furthermore, users can use buttons to issue commands that are stored as a persistent string. These strings are defined by the user and will be restored automatically after the mobile application restarts. Upon identification of arrows in the arena by Raspberry Pi camera, the mobile application will display found arrows in the grid map.

### Transmitting Strings over Bluetooth
When reading, it will constantly listen to the InputStream and will then write the data to the connected OutputStream. String messages that are sent and received from Android mobile application will be saved in a database. The database adopted database is Realm. As compared to SQLite, Realm has a better insert, count and query performance. With these performance improvements, the Android mobile application will be able to display the current status of the arena using the map descriptor string and transmits String messages with minimal delay.

### Graphic User Interface (GUI) Design


1. Interactive Control of Robot Movement
The application interface contains directional arrows which allow the user to remotely control the movement of the robot by the serial communication link established. Additionally, the application runs in landscape mode, which allows the user to use both hands to operate simultaneously without losing the screen estate for grid view while controlling the robot.

2. User Interface indicates the current status of the robot
The current status of the robot will be constantly updated like a “live feed” with notable messages when there are any movement changes to the robot. (e.g: “Moving Forward” to “Idling”)
3. Initialization of Starting Point and WayPoint coordinates.
Users can select the start point and waypoint coordinates by selecting the “Set Waypoint” & “Set Robot Position” button and proceed to touch the location on the displayed map grid. The application will save the data and display the points accordingly.

4. Bluetooth Message Log
The Bluetooth Message Log works as a messaging application to view and send messages to other devices through the established Bluetooth connection.

5. Gyroscope Functionality
The application allows for advanced control of the robot using the gyroscope sensors of the Android device. Users will be able to remotely control the robot’s movement by tilting the device in any desired direction.

6. Competitive/Non-Competitive Mode Toggle
The user can toggle between competitive mode and non-competitive mode user interface. When the competitive mode is enabled, the only relevant user interface that are required for the leaderboard run will be displayed e.g. initialization of waypoint and robot’s position, start and stop command for both exploration and fastest path.
In the non-competitive mode, the user will be able to use an interactive controller to manoeuvre the Robot e.g. Directional Pad and Rotation by the gyroscope (by tilting phone left and right). With this user interface segregation, the user will be able to perform the necessary setup swimmingly for the leaderboard run.
