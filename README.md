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

## 🧩 High-level Components (TODO) <a id="high-level-components"></a>
Find the back-ends main 3-5 components below. <br>What is their role?
How are they correlated? Reference the main class, file, or function in the README text
with a link.

### 🧑‍🤝‍🧑 Lobby 

The Lobby is  

### 🎮 Game

The Game is responsible for 

### 🫀 Keep Alive

All methods connected to the keep alive feature handle idle users or ones that close the tab during the game. While not a component per-se, it still is a essential feature to understand. Users get "kicked" from the game and their co-players are informed of this. This means redistribution of the roles (SPIER/GUESSER) or the host rights in a game if necessary and handling the case if a player remains alone in game. Keep Alive checks liveness of the player beginning after the login/signup.


## 🚀 Launch & Development <a id="launch--development"></a>

These are the steps a new developer joining the team would
have to take to get started with the application.

### Prerequisites
TODO


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


#### Tests

Run the tests with: `npm run test`

> For macOS user running into a 'fsevents' error: https://github.com/jest-community/vscode-jest/issues/423

#### Build

To build the app, run `npm run build` <br>

### External dependencies

Both the client and the server have to be running for the application to behave as expected.
#### Database (TODO) 

### Releases TODO idk about this

## 🚙 Roadmap TODO <a id="roadmap"></a>

* *Hard game mode*: When one person guesses correctly, the round is over
* *More metrics*: Allow the leaderboard to be filterable by other metrics (f.ex. fasted guessed)
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

 
