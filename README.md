# I Spy With My Little Eye

![Image](./LogoHeader.png) 

By translating the childhood-favorite to the web, I Spy With My Little Eye is an exciting game where you can explore the
world with your friends online.
Check out the front-end implementation [here](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_client).

## 📖 Table of Contents

1. [✨ Introduction](#introduction)
2. [🦋 Technologies](#technologies)
3. [🧩 High-level Components](#high-level-components)
4. [🚀 Launch & Development](#launch--development)
5. [🚙 Roadmap](#roadmap)
6. [👩‍💻 Authors](#authors)
7. [🫂 Acknowledgments](#acknowledgments)
8. [©️ License](#license)

## ✨ Introduction <a name="introduction"></a>

I Spy With My Little Eye aims to rekindle childhood memories - Having an online version of this beloved game allows us
to explore the world with our friends online in a fun and exciting way.

## 🦋 Technologies <a id="technologies"></a>

During the development of the back-end, we used the following technologies:

* [Java](https://www.java.com/de/download/manual.jsp) - Programming language used in the server
* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot Framework used in the server
* [Axios API](https://axios-http.com/docs/api_intro) - Used for REST-based communication
* [Stomp](https://stomp-js.github.io/stomp-websocket/) - Used for websocket communication with the client
* [JPA/Hibernate]() - Used for the persistance of players
* [Google cloud](https://cloud.google.com/?hl=en) - Handles the deployment

## 🧩 High-level Components <a id="high-level-components"></a>
Find the back-ends main 3-5 components below. <br>What is their role?
How are they correlated? Reference the main class, file, or function in the README text
with a link.

### 👤 PlayerService

The [PlayerService](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/PlayerService.java) is responsible for managing all player related parameters. It allows creation and log in of players, is the center of the [Keep Alive](#keepalive) feature and allows the players to edit their user information. The PlayerService also has access to the [PlayerRepository](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/repository/PlayerRepository.java), which communicates with the database. Without the PlayerService it wouldn't be possible to have logged in players that can play the game and look at their stats and profiles afterwards.

### 🧑‍🤝‍🧑 Lobby 

The [Lobby](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/Lobby.java) class is responsible for storing all information about players and game settings that are needed to start a game. This means the lobby stores the participating players (up to 10), the host of the game, the amount of rounds to be played (2 - 20) and how long a round should take (1 - 4 minutes). The Lobby therefore is an important component, as it creates the game with the necessary information it collected before. It can be addressed via the Lobbyservice.

### 🎲 Game

The [Game](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/Game.java) class is responsible for storing all game related parameters. This includes the information about the game that was handed over from the Lobby but also the points of each player. The game distributes the roles (SPIER/GUESSER), controls the timing of rounds and checks the guesses for correctness (+ awards points if necessary). The Game therefore is an important component, because it controls a single game during the whole process. It can be reached using the Gameservice.

### 🎮 GameStompController

The [GameStompController](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/GameStompController.java) is in charge of handling all interactions between clients and the server that have to be synchronized during the game. This includes sending information about the round (location and color of the object), the guesses and hints and the start/end of a round. It interacts via the [GameService](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/GameService.java) with the Game that needs to be addressed.

### 🫀 Keep Alive <a id="keepalive"></a>

All methods connected to the [keep alive feature](https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server/blob/d1d28d86c4bf328cf9a34d58c75b87c132180aef/src/main/java/ch/uzh/ifi/hase/soprafs23/service/PlayerService.java#L129) handle idle users or ones that close the tab during the game. While not a component per-se, it still is a essential feature to understand. Users get "kicked" from the game and their co-players are informed of this. This means redistribution of the roles (SPIER/GUESSER) or the host rights in a game if necessary and handling the case if a player remains alone in game. Keep Alive checks liveness of the player beginning after the login/signup.


## 🚀 Launch & Development <a id="launch--development"></a>

These are the steps a new developer joining the team would
have to take to get started with the application.

### Prerequisites
As the server is written in Java, make sure to have a new version installed. 
Also get the server code

```bash
git clone https://github.com/sopra-fs23-group-09/SpyWithMyLittleEye_server.git
```
and open the project with an IDE of your choice.


### Commands to build and run the project locally

#### Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.


#### Test

```bash
./gradlew test
```

### External dependencies

Both the client and the server have to be running for the application to behave as expected.

#### Database (TODO) 

### Releases TODO idk about this

## 🚙 Roadmap TODO <a id="roadmap"></a>

* *Hard game mode*: When one person guesses correctly, the round is over
* *Edit profile picture*
* *Make keyword checking more flexible*: Allow synonyms of words by using an API for synonyms

## 👩‍💻 Authors <a id="authors"></a>


* [Nina Emmermann](https://github.com/ninanni) - server
* [Claudia Anna Narang-Keller](https://github.com/cnaran) - server
* [Loubna Dia-Eddine](https://github.com/Loubnadia) - client
* [Ana Thereza Schiemer](https://github.com/athzsc) - client
* [Xena Wacker](https://github.com/xayreen) - client

## 🫂 Acknowledgments <a id="acknowledgments"></a>

We want to thank our Teaching Assistant [Sheena Lang](https://github.com/SheenaGit) for guiding us through the course.
We also couldn't have made it without our ✨ceremonies✨ 🍣✨ 🍱 🫶 after each Milestone.

## ©️ License <a id="license"></a>

This project is licensed under the GNU GPLv3 License. 

 
